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
package ilarkesto.integration;

import ilarkesto.core.base.Str;
import ilarkesto.core.logging.Log;
import ilarkesto.io.DummyReader;
import ilarkesto.io.IO;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import bsh.ConsoleInterface;
import bsh.EvalError;
import bsh.Interpreter;
import bsh.TargetError;

public class BeanshellExecutor {

	protected final Log log = Log.get(getClass());

	private Interpreter interpreter;
	private StringBuilder preScript;

	public BeanshellExecutor() {}

	protected void onBeforeExecute(String script) {}

	public BeanshellExecutor put(String name, Object value) {
		try {
			interpreter.set(name, value);
		} catch (EvalError ex) {
			throw new RuntimeException(ex);
		}
		return this;
	}

	public void addPackageImports(Class... types) {
		for (Class type : types) {
			addPreScript("import " + type.getPackage().getName() + ".*;");
		}
	}

	public void addPreScript(String script) {
		preScript.append(script).append("\n");
	}

	public synchronized String executeScript(String script) {
		Console console = new Console();
		interpreter = new Interpreter(console);
		preScript = new StringBuilder();

		onBeforeExecute(script);

		script = script.trim();
		if (preScript != null) {
			try {
				interpreter.eval(preScript.toString());
			} catch (EvalError ex) {
				throw new RuntimeException("Evaluating pre-script failed:\n---\n" + preScript + "\n---\n", ex);
			}
		}

		String output = null;
		try {
			interpreter.eval(script);
			output = console.toString();
		} catch (Exception ex) {
			if (ex instanceof TargetError) return Str.formatException(((TargetError) ex).getTarget());
			output = Str.formatException(ex);
		}
		log.warn("BeanSchell-Script executed:\n", script, "\n\n --- Output --->\n\n", output);
		return output;
	}

	class Console implements ConsoleInterface {

		private StringStream stringOutputStream;
		private PrintStream out;
		private DummyReader in;

		public Console() {
			stringOutputStream = new StringStream();
			out = new PrintStream(stringOutputStream);
			in = new DummyReader();
		}

		@Override
		public Reader getIn() {
			return in;
		}

		@Override
		public PrintStream getOut() {
			return out;
		}

		@Override
		public PrintStream getErr() {
			return getOut();
		}

		@Override
		public void println(Object s) {
			getOut().println(s);
		}

		@Override
		public void print(Object s) {
			getOut().print(s);
		}

		@Override
		public void error(Object s) {
			getErr().println(s);
		}

		@Override
		public String toString() {
			out.flush();
			return stringOutputStream.toString();
		}

	}

	class StringStream extends OutputStream {

		private StringBuilder sb = new StringBuilder();
		private List<Integer> buffer = new ArrayList<Integer>();

		@Override
		public void write(int b) throws IOException {
			buffer.add(Integer.valueOf(b));
			if (b == 10) flush();
		}

		@Override
		public void flush() throws IOException {
			if (!buffer.isEmpty()) {
				byte[] bytes = new byte[buffer.size()];
				for (int i = 0; i < bytes.length; i++) {
					bytes[i] = buffer.get(i).byteValue();
				}
				sb.append(new String(bytes, IO.UTF_8));
				buffer = new ArrayList<Integer>();
			}
			super.flush();
		}

		@Override
		public String toString() {
			return sb.toString().trim();
		}
	}

}
