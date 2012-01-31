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
package ilarkesto.integration.couchdb;

import ilarkesto.core.json.JsonObject;
import ilarkesto.core.jsondb.AJsonDb;
import ilarkesto.core.jsondb.DocumentReference;
import ilarkesto.core.jsondb.JsonDbException;
import ilarkesto.core.logging.Log;
import ilarkesto.io.IO;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CouchDb extends AJsonDb {

	private static Log log = Log.get(CouchDb.class);

	private String url;

	public CouchDb(String url) {
		super();
		this.url = url;
	}

	@Override
	public JsonObject loadDocumentById(String id) {
		log.debug("Loading document:", id);
		String json = IO.downloadUrlToString(url + id);
		if (json == null) return null;
		return new JsonObject(json);
	}

	@Override
	public List<DocumentReference> listAllDocuments() {
		JsonObject all = loadDocumentById("_all_docs");
		List<JsonObject> rows = all.getArrayOfObjects("rows");
		List<DocumentReference> references = new ArrayList<DocumentReference>(rows.size());
		for (JsonObject row : rows) {
			String id = row.getString("id");
			references.add(new DocumentReference(id));
		}
		return references;
	}

	@Override
	public void saveDocument(JsonObject document) {
		String id = getId(document);
		log.info("Saving document:", id, "\n" + document.toFormatedString());
		doHttpRequest(id, "PUT", document.toFormatedString());
	}

	@Override
	public void deleteDocument(JsonObject document) {
		String id = getId(document);
		log.info("Deleting document:", id, "\n" + document.toFormatedString());
		String rev = getRevision(document);
		doHttpRequest(id + "?rev=" + rev, "DELETE", null);
	}

	private String doHttpRequest(String path, String method, String content) {
		String urlString = this.url + path;
		URL url;
		try {
			url = new URL(urlString);
		} catch (MalformedURLException ex) {
			throw new JsonDbException("Malformed URL: " + urlString, ex);
		}
		HttpURLConnection connection;
		try {
			connection = (HttpURLConnection) url.openConnection();
		} catch (IOException ex) {
			throw new JsonDbException("Connecting failed:" + urlString, ex);
		}
		connection.setDoOutput(true);
		try {
			connection.setRequestMethod(method);
		} catch (ProtocolException ex) {
			throw new JsonDbException("Unsupported HTTP method: " + method, ex);
		}

		OutputStream outputStream;
		try {
			outputStream = connection.getOutputStream();
		} catch (IOException ex) {
			throw new JsonDbException("Writing failed: " + urlString, ex);
		}
		PrintWriter out;
		String encoding = IO.UTF_8;
		try {
			out = new PrintWriter(new OutputStreamWriter(outputStream, encoding));
		} catch (UnsupportedEncodingException ex) {
			throw new JsonDbException("Unsupported encoding: " + encoding, ex);
		}
		if (content != null) out.println(content);
		IO.closeQuiet(out);

		InputStream in;
		try {
			in = connection.getInputStream();
		} catch (IOException ex) {
			throw new JsonDbException("Reading failed: " + urlString, ex);
		}
		String ret = IO.readToString(in, encoding);
		IO.closeQuiet(in);
		return ret;
	}

	private String getRevision(JsonObject document) {
		return document.getString("_rev");
	}

	private String getId(JsonObject document) {
		return document.getString("_id");
	}

}
