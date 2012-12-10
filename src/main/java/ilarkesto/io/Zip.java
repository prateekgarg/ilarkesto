package ilarkesto.io;

import ilarkesto.io.zip.Deflater;
import ilarkesto.io.zip.ZipEntry;
import ilarkesto.io.zip.ZipFile;
import ilarkesto.io.zip.ZipOutputStream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

public class Zip {

	public static void unzip(File zipfile, File destinationDir, UnzipObserver observer) {
		ZipFile zf;
		try {
			zf = new ZipFile(zipfile);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		if (observer != null) observer.onFileCountAvailable(zf.size());
		try {
			Enumeration entries = zf.entries();
			while (entries.hasMoreElements()) {
				if (observer != null && observer.isAbortRequested()) return;
				ZipEntry ze = (ZipEntry) entries.nextElement();
				String name = ze.getName();
				name = name.replace((char) 129, '\u00FC');
				name = name.replace((char) 154, '\u00DC');
				name = name.replace((char) 148, '\u00F6');
				name = name.replace((char) 153, '\u00D6');
				name = name.replace((char) 132, '\u00E4');
				name = name.replace((char) 142, '\u00C4');
				name = name.replace((char) 225, '\u00DF');
				if (ze.isDirectory()) continue;
				File f = new File(destinationDir.getPath() + "/" + name);
				if (observer != null) observer.onFileBegin(f);
				try {
					IO.createDirectory(f.getParentFile());
					BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(f));
					InputStream in = zf.getInputStream(ze);
					IO.copyData(new BufferedInputStream(in), out);
					out.close();
					long lastModified = ze.getTime();
					if (lastModified >= 0) IO.setLastModified(f, lastModified);
				} catch (Exception ex) {
					if (observer == null) throw new RuntimeException(ex);
					observer.onFileError(f, ex);
				}
				if (observer != null) observer.onFileEnd(f);
			}
		} finally {
			try {
				zf.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static interface UnzipObserver {

		void onFileCountAvailable(int count);

		void onFileBegin(File f);

		void onFileEnd(File f);

		void onFileError(File f, Throwable ex);

		boolean isAbortRequested();

	}

	public static void zip(File zipfile, File... files) {
		zip(zipfile, files, null);
	}

	public static void zip(File zipfile, File[] files, FileFilter filter) {
		zip(zipfile, files, filter, null);
	}

	public static void zip(File zipfile, File[] files, FileFilter filter, ZipObserver observer) {
		if (zipfile.exists()) IO.delete(zipfile);
		IO.createDirectory(zipfile.getParentFile());
		File tempFile = new File(zipfile.getPath() + "~");

		try {
			zip(new FileOutputStream(tempFile), files, filter, observer);
		} catch (FileNotFoundException ex) {
			throw new RuntimeException(ex);
		}

		if (observer != null && observer.isAbortRequested()) {
			IO.delete(tempFile);
		} else {
			IO.move(tempFile, zipfile);
		}
	}

	public static void zip(OutputStream os, File... files) {
		zip(os, files, null, null);
	}

	public static void zip(OutputStream os, File[] files, FileFilter filter, ZipObserver observer) {
		ZipOutputStream zipout;
		try {
			zipout = new ZipOutputStream(new BufferedOutputStream(os));
			zipout.setLevel(Deflater.BEST_COMPRESSION);
			for (int i = 0; i < files.length; i++) {
				if (!files[i].exists()) continue;
				addZipEntry(zipout, "", files[i], filter, observer);
			}
			zipout.close();
		} catch (Exception ex) {
			throw new RuntimeException("Zipping files failed.", ex);
		}
	}

	public static void addZipEntry(ZipOutputStream zipout, String zippath, File f, FileFilter filter,
			ZipObserver observer) {
		if (filter != null && !filter.accept(f)) return;
		if (observer != null) {
			if (observer.isAbortRequested()) return;
			observer.onFileBegin(f);
		}
		if (f.isDirectory()) {
			File[] fa = f.listFiles();
			for (int i = 0; i < fa.length; i++) {
				addZipEntry(zipout, zippath + f.getName() + "/", fa[i], filter, observer);
			}
		} else {
			try {
				BufferedInputStream in = new BufferedInputStream(new FileInputStream(f));
				ZipEntry entry = new ZipEntry(zippath + f.getName());
				zipout.putNextEntry(entry);
				IO.copyData(in, zipout);
				in.close();
				zipout.closeEntry();
			} catch (Exception ex) {
				if (observer == null) { throw new RuntimeException("Zipping " + f + " failed.", ex); }
				observer.onFileError(f, ex);
			}
		}
		if (observer != null) observer.onFileEnd(f);
	}

	public static void unzip(File zipfile, File destinationDir) {
		unzip(zipfile, destinationDir, null);
	}

	public static interface ZipObserver {

		void onFileBegin(File f);

		void onFileEnd(File f);

		void onFileError(File f, Throwable ex);

		boolean isAbortRequested();

	}

}
