/*
 * Copyright 2011 Witoslaw Koczewsi <wi@koczewski.de>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <http://www.gnu.org/licenses/>.
 */
package ilarkesto.persistence.file;

import ilarkesto.base.Bytes;
import ilarkesto.base.Env;
import ilarkesto.core.base.RuntimeTracker;
import ilarkesto.core.persistance.ACachingEntityDatabase;
import ilarkesto.core.persistance.AEntity;
import ilarkesto.core.persistance.EntityDoesNotExistException;
import ilarkesto.core.persistance.Transaction;
import ilarkesto.di.Context;
import ilarkesto.di.app.AApplication;
import ilarkesto.integration.git.Git;
import ilarkesto.integration.git.GitProject;
import ilarkesto.io.AFileStorage;
import ilarkesto.io.IO;
import ilarkesto.json.JsonMapper;
import ilarkesto.json.JsonMapper.TypeResolver;
import ilarkesto.webapp.AWebApplication;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

// TODO background task which checks if transactions are open (running) too long
public abstract class AJsonFilesEntityDatabase extends ACachingEntityDatabase {

	private AFileStorage storage;
	private GitProject git;

	private ThreadLocal<Transaction> tlTransaction = new ThreadLocal<Transaction>();
	private Collection<Transaction> transactions = new ArrayList<Transaction>();
	private int transactionNumberCounter = 0;

	protected abstract AEntityJsonFileUpgrades createUpgrader();

	protected abstract List<Class<? extends ilarkesto.core.persistance.AEntity>> getEntityTypes();

	protected abstract TypeResolver createTypeResolver();

	public AJsonFilesEntityDatabase() {
		AApplication application = AApplication.get();
		storage = application.getFileStorage().getSubStorage("entities");
		git = new GitProject(new Git(), new File(application.getApplicationDataDir()));
		load();
	}

	private void load() {
		int dataVersion = loadVersion();
		AEntityJsonFileUpgrades upgrader = createUpgrader();
		int softwareVersion = upgrader.getSoftwareVersion();

		if (dataVersion > softwareVersion)
			throw new IllegalStateException("Data version " + dataVersion + " is bigger then softwareVersion "
					+ softwareVersion);

		log.info("Loading entities from", storage, "| data-version", dataVersion, "| software-version", softwareVersion);
		RuntimeTracker rt = new RuntimeTracker();
		upgrader.upgradeEntitiesDir(storage.getFile(null), dataVersion);
		TypeResolver typeResolver = createTypeResolver();
		for (Class<? extends AEntity> type : getEntityTypes()) {
			int count = 0;
			File dir = storage.getFile(type.getSimpleName());
			File[] files = dir.listFiles();
			if (files != null) {
				for (File file : files) {
					if (!file.isFile()) continue;
					if (!file.getName().endsWith(".json")) continue;
					upgrader.upgradeEntity(file, type, dataVersion);
					if (!file.exists()) continue;
					AEntity entity;
					try {
						entity = JsonMapper.deserialize(file, type, typeResolver);
					} catch (Exception ex) {
						throw new RuntimeException("Loading entity failed: " + file, ex);
					}
					cache.add(entity);
					count++;
				}
			}
			log.info("   ", type.getSimpleName(), count);
		}

		saveVersion(softwareVersion);

		log.info(cache.size(), "entities loaded in", rt.getRuntimeFormated());

	}

	private void saveVersion(int version) {
		IO.writeFile(getVersionFile(), String.valueOf(version), IO.UTF_8);
	}

	private int loadVersion() {
		File versionFile = getVersionFile();
		if (!versionFile.exists()) return 0;
		return Integer.parseInt(IO.readFile(versionFile, IO.UTF_8).trim());
	}

	private File getVersionFile() {
		return storage.getFile("version.txt");
	}

	@Override
	protected void onUpdate(Collection<AEntity> modified, Collection<String> deleted,
			Map<String, Map<String, String>> modifiedPropertiesByEntityIds, Runnable callback) {
		if ((modified == null || modified.isEmpty()) && (deleted == null || deleted.isEmpty())) return;
		List<File> files = new ArrayList<File>();
		RuntimeTracker rt = new RuntimeTracker();
		int saveCount = 0;
		Collection<AEntity> created = new ArrayList<AEntity>();
		if (modified != null) {
			for (AEntity entity : modified) {
				File file = getFile(entity);
				if (!file.exists()) {
					created.add(entity);
				}
				files.add(file);
				log.debug("Saving entity:", entity.getClass().getSimpleName(), file.getName(), "in", file.getParent());
				try {
					JsonMapper.serialize(entity, file);
				} catch (IOException ex) {
					throw new RuntimeException("Writing entity to file failed: " + file + " -> " + entity, ex);
				}
				saveCount++;
			}
		}
		int deleteCount = 0;
		if (deleted != null) {
			for (String id : deleted) {
				AEntity entity;
				try {
					entity = cache.get(id);
				} catch (EntityDoesNotExistException ex) {
					continue;
				}
				File file = getFile(entity);
				files.add(file);
				log.debug("Deleting entity", entity.getClass().getSimpleName(), file);
				IO.delete(file);
				deleteCount++;
			}
		}
		log.info("Entity changes saved:", rt.getRuntimeFormated(), "(" + saveCount, "saved,", deleteCount, "deleted)");

		if (git.isInitialized()) {
			git.addAll();
			git.commit(Context.get().toString());
		}

		AWebApplication webApplication = AWebApplication.get();
		webApplication.deleteFromClients(deleted);
		webApplication.sendToAllIfTracking((Collection) modified);
		webApplication.sendToAll((Collection) created);

		if (callback != null) callback.run();
	}

	private File getFile(AEntity entity) {
		return storage.getFile(entity.getClass().getSimpleName() + "/" + entity.getId() + ".json");
	}

	@Override
	public synchronized Transaction getTransaction() {
		Transaction transaction = tlTransaction.get();
		if (transaction == null) {
			int no = ++transactionNumberCounter;
			transaction = new Transaction(this, "#" + no + " (" + Thread.currentThread().getName() + ")", false, true);
			transactions.add(transaction);
			tlTransaction.set(transaction);
		}
		return transaction;
	}

	@Override
	public synchronized void onTransactionFinished(Transaction transaction) {
		Transaction current = tlTransaction.get();
		if (current == null) {
			if (transaction == null) return;
			throw new IllegalStateException("Current transaction == null. Finished: " + transaction);
		}
		if (current != transaction)
			throw new IllegalStateException("Transaction is not current transaction: " + transaction);
		tlTransaction.set(null);
		transactions.remove(transaction);
	}

	@Override
	public synchronized boolean isTransactionWithChangesOpen() {
		for (Transaction transaction : transactions) {
			if (!transaction.isEmpty()) return true;
		}
		return false;
	}

	@Override
	public String createInfo() {
		StringBuilder sb = new StringBuilder();

		sb.append("\nEntities size on disk: ")
				.append(new Bytes(Env.get().getFileSize(storage.getFile(null))).toRoundedString()).append("\n");

		sb.append("\nEntity counts:\n");
		for (Map.Entry<Class, Integer> entry : cache.countEntities().entrySet()) {
			sb.append("* ").append(entry.getKey().getSimpleName()).append(": ").append(entry.getValue()).append("\n");
		}

		if (git.isInitialized()) sb.append("\nGit status: ").append(git.status());

		return sb.toString();
	}

}
