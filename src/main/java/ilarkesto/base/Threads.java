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
package ilarkesto.base;

import java.util.Map;

public class Threads {

	public static void main(String[] args) {
		System.out.println(getAllThreadsInfo());
	}

	public static StackTraceIdentificator stackTraceIdentificator = new StackTraceIdentificator();

	public static String getAllThreadsInfo() {
		return getAllThreadsInfo("\n  * ");
	}

	public static String getAllThreadsInfo(String separator) {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<Thread, StackTraceElement[]> entry : Thread.getAllStackTraces().entrySet()) {
			sb.append(separator);
			Thread thread = entry.getKey();
			StackTraceElement[] stackTrace = entry.getValue();
			Object identification = identifyStackTrace(stackTrace);
			if (identification == null) identification = "?";
			sb.append("[").append(identification).append("]").append(" ");
			sb.append(formatThread(thread)).append(" ").append(formatStackTrace(stackTrace));

		}
		return sb.toString();
	}

	private static Object identifyStackTrace(StackTraceElement[] stackTrace) {
		return stackTraceIdentificator.identify(stackTrace);
	}

	private static String formatStackTrace(StackTraceElement[] stackTrace) {
		StringBuilder sb = new StringBuilder();
		if (stackTrace == null) return sb.toString();
		for (StackTraceElement element : stackTrace) {
			String className = element.getClassName();
			// int idx = className.lastIndexOf('.');
			// if (idx > 0) className = className.substring(idx + 1);
			sb.append(" @ ").append(className).append(".").append(element.getMethodName() + "():")
					.append(element.getLineNumber());
		}
		return sb.toString();
	}

	public static String formatThread(Thread thread) {
		StringBuilder sb = new StringBuilder();
		sb.append(thread.getName());
		sb.append(" ").append(thread.getState());
		sb.append(":").append(thread.getPriority());
		return sb.toString();
	}

	public static String formatThreadWithStrackTrace(Thread thread) {
		StringBuilder sb = new StringBuilder();
		sb.append(formatThread(thread));
		sb.append(formatStackTrace(thread.getStackTrace()));
		return sb.toString();
	}

	public static class StackTraceIdentificator {

		public String identify(StackTraceElement[] stackTrace) {
			if (stackTrace == null) return "";
			for (int i = stackTrace.length - 1; i >= 0; i--) {
				StackTraceElement element = stackTrace[i];
				String description = identify(element, element.getClassName() + "." + element.getMethodName() + "()");
				if (description != null) return description;
			}
			return null;
		}

		protected String identify(StackTraceElement element, String call) {
			if (call.contains("Reference$ReferenceHandler.run()")) return "JVM";
			if (call.contains("Threads.main()")) return "JVM";
			if (call.contains("Finalizer$FinalizerThread.run()")) return "JVM";
			if (call.contains("DefaultLogRecordHandler$1.run()")) return "LOG";
			if (call.contains("UNIXProcess.waitForProcessExit()")) return "SYS";
			if (call.contains("GC$Daemon.run()")) return "JVM";

			if (call.contains("org.apache.catalina.core.ContainerBase$ContainerBackgroundProcessor.run()"))
				return "TOMCAT";
			if (call.contains("org.apache.tomcat.util.net.JIoEndpoint$Acceptor.run()")) return "TOMCAT";
			if (call.contains("org.apache.catalina.core.StandardServer.await()")) return "TOMCAT";

			if (call.contains("ilarkesto.base.Proc$StreamGobbler.run()")) return "PROC";
			return null;
		}

	}
}
