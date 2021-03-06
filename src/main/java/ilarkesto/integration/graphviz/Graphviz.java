/*
 * Copyright 2011 Witoslaw Koczewsi <wi@koczewski.de>, Artjom Kochtchi
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package ilarkesto.integration.graphviz;

import ilarkesto.base.Proc;
import ilarkesto.base.Sys;
import ilarkesto.core.logging.Log;
import ilarkesto.io.IO;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Graphviz {

	private static final Log LOG = Log.get(Graphviz.class);

	public static final String OUTPUT_TYPE_PS = "ps";
	public static final String OUTPUT_TYPE_SVG = "svg";
	public static final String OUTPUT_TYPE_GIF = "gif";
	public static final String OUTPUT_TYPE_PNG = "png";
	public static final String OUTPUT_TYPE_IMAP = "imap";

	public static File createPng(String sourceCode) {
		return processDot(sourceCode, OUTPUT_TYPE_PNG);
	}

	public static File processDot(String sourceCode, String outputType) {
		File sourceFile;
		try {
			sourceFile = File.createTempFile("ilarkesto-graph_", ".dot");
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		IO.writeFile(sourceFile, sourceCode, Sys.getFileEncoding());
		try {
			return processDot(sourceFile, outputType);
		} catch (Exception ex) {
			throw new RuntimeException("Processing dot failed -> " + sourceCode, ex);
		}
	}

	public static File processDot(File sourceFile, String outputType) {
		File outputFile;
		try {
			outputFile = File.createTempFile("ilarkesto-graph_", "." + outputType);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}

		Proc proc = new Proc("dot");
		proc.addParameter("-T" + outputType);
		proc.addParameter(sourceFile.getAbsolutePath());
		proc.addParameter("-o");
		proc.addParameter(outputFile.getAbsolutePath());

		try {
			String output = proc.execute();
			LOG.debug("Output:", output);
		} finally {
			sourceFile.delete();
		}

		return outputFile;
	}

	public static void write(File file, String sourceCode, String outputType) {
		IO.createDirectory(file.getParentFile());
		BufferedOutputStream out;
		try {
			out = new BufferedOutputStream(new FileOutputStream(file));
		} catch (FileNotFoundException ex) {
			throw new RuntimeException(ex);
		}
		write(out, sourceCode, outputType);
		try {
			out.close();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static void write(OutputStream outputStream, String sourceCode, String outputType) {
		File file;
		try {
			file = processDot(sourceCode, outputType);
		} catch (Exception ex) {
			throw new RuntimeException("Processing dot failed -> " + sourceCode, ex);
		}
		IO.copyFile(file, outputStream);
		file.delete();
	}
}
