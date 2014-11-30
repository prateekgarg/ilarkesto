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
package ilarkesto.io;

import ilarkesto.core.base.Str;
import ilarkesto.core.logging.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public abstract class ACsvWriter<R> {

	private Log log = Log.get(getClass());

	private List<AColumn> columns;

	protected abstract void createColumns();

	protected abstract Iterable<R> getRecords();

	public final void write(CsvWriter csv) {
		onWrite(csv);
		onWriteCompleted();
	}

	protected void onWriteCompleted() {}

	protected void onWrite(CsvWriter csv) {
		columns = new ArrayList<AColumn>();
		createColumns();
		List<String> headers = new ArrayList<String>();
		for (AColumn column : columns) {
			headers.add(column.getName());
		}
		if (isHeadersEnabled()) csv.writeHeaders(headers);

		Iterable<R> records = getRecords();
		for (R record : records) {
			for (AColumn column : columns) {
				String value;
				try {
					value = column.getCsvValue(record);
				} catch (Exception ex) {
					handleExceptionOnGetValue(ex);
					csv.writeField("");
					continue;
				}
				csv.writeField(value);
			}
			csv.closeRecord();
		}

		csv.close();
	}

	protected boolean isHeadersEnabled() {
		return false;
	}

	public final void write(Writer out, char separator) {
		CsvWriter csv = new CsvWriter(out);
		csv.setSeparator(separator);
		write(csv);
	}

	public final void write(File file, String charset, char separator) throws IOException {
		log.info("Writing", file.getAbsolutePath());
		IO.createDirectory(file.getParentFile());
		write(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), charset)), separator);
	}

	public final void write(File file) throws IOException {
		write(file, getDefaultCharset(), getDefaultSeparator());
	}

	protected String getDefaultCharset() {
		return IO.UTF_8;
	}

	protected char getDefaultSeparator() {
		return ';';
	}

	protected void handleExceptionOnGetValue(Exception ex) {
		throw new RuntimeException(ex);
	}

	protected final void addColumn(AColumn column) {
		columns.add(column);
	}

	public abstract class AColumn {

		public abstract String getName();

		public abstract Object getValue(R record);

		public String getCsvValue(R record) {
			String ret = Str.format(getValue(record));
			if (ret == null) return null;
			int lengthAutocut = getLengthAutocut();
			if (lengthAutocut > 0 && ret.length() > lengthAutocut) ret = Str.cutRight(ret, lengthAutocut);
			return ret;
		}

		protected int getLengthAutocut() {
			return -1;
		}

	}

}
