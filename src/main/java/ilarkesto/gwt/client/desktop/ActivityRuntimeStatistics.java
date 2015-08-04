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
package ilarkesto.gwt.client.desktop;

import java.util.LinkedList;
import java.util.List;

public class ActivityRuntimeStatistics {

	private List<ServiceCallInfo> serviceCalls = new LinkedList<ActivityRuntimeStatistics.ServiceCallInfo>();
	private List<ActivityInfo> activities = new LinkedList<ActivityRuntimeStatistics.ActivityInfo>();

	public ActivityInfo addActivity(String name, long onStartRuntime) {
		ActivityInfo activity = new ActivityInfo(name, onStartRuntime);
		activities.add(0, activity);
		return activity;
	}

	public void addServiceCall(String name, long rtCall, long rtData, long rtHandler) {
		if (name.toLowerCase().equals("ping")) return;
		serviceCalls.add(0, new ServiceCallInfo(name, rtCall, rtData, rtHandler));
	}

	public List<ServiceCallInfo> getServiceCalls() {
		return serviceCalls;
	}

	public List<ActivityInfo> getActivities() {
		return activities;
	}

	public static class ActivityInfo {

		private String name;
		private long onStartRuntime;
		private long onRequiredServiceCallReturnRuntime = -1;
		private long onResumeRuntime = -1;

		public ActivityInfo(String name, long onStartRuntime) {
			super();
			this.name = name;
			this.onStartRuntime = onStartRuntime;
		}

		public void setOnResumeRuntime(long onResumeRuntime) {
			this.onResumeRuntime = onResumeRuntime;
		}

		public void setOnRequiredServiceCallReturnRuntime(long onRequiredServiceCallReturnRuntime) {
			this.onRequiredServiceCallReturnRuntime = onRequiredServiceCallReturnRuntime;
		}

		@Override
		public String toString() {
			return name + " " + onStartRuntime + "ms " + onRequiredServiceCallReturnRuntime + "ms " + onResumeRuntime
					+ "ms";
		}

		public String toHtml() {
			StringBuilder sb = new StringBuilder();
			sb.append(name);
			sb.append(" ( ");
			sb.append(colorizeRuntime(onStartRuntime));
			sb.append(" / ");
			sb.append(colorizeRuntime(onRequiredServiceCallReturnRuntime));
			sb.append(" / ");
			sb.append(colorizeRuntime(onResumeRuntime));
			sb.append(" )");
			return sb.toString();
		}

	}

	public static class ServiceCallInfo {

		private String name;
		private long rtCall;
		private long rtData;
		private long rtHandler;

		public ServiceCallInfo(String name, long rtCall, long rtData, long rtHandler) {
			super();
			this.name = name;
			this.rtCall = rtCall;
			this.rtData = rtData;
			this.rtHandler = rtHandler;
		}

		@Override
		public String toString() {
			return name + " " + rtCall + "ms " + rtData + "ms " + rtHandler + "ms";
		}

		public String toHtml() {
			StringBuilder sb = new StringBuilder();
			sb.append(name);
			sb.append(" ( ");
			sb.append(colorizeRuntime(rtCall));
			sb.append(" / ");
			sb.append(colorizeRuntime(rtData));
			sb.append(" / ");
			sb.append(colorizeRuntime(rtHandler));
			sb.append(" )");
			return sb.toString();
		}
	}

	private static String colorizeRuntime(long runtime) {
		return colorizeRuntime(runtime, 500, 3000);
	}

	private static String colorizeRuntime(long runtime, long yellow, long red) {
		String color = "green";
		if (runtime >= yellow) color = "orange";
		if (runtime >= red) color = "red";
		String sRuntime = String.valueOf(runtime);
		if (runtime < 0) {
			color = "grey";
			sRuntime = "-";
		}
		return "<span style='color:" + color + ";'>" + sRuntime + "</span>";
	}

}
