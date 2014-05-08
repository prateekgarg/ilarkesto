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
package ilarkesto.mda.legacy.generator;

import ilarkesto.core.base.Str;
import ilarkesto.gwt.client.AServiceCall;
import ilarkesto.mda.legacy.model.GwtServiceModel;
import ilarkesto.mda.legacy.model.MethodModel;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

public class GwtServiceCallGenerator extends AClassGenerator {

	private GwtServiceModel service;
	private MethodModel method;

	public GwtServiceCallGenerator(GwtServiceModel service, MethodModel method) {
		super();
		this.service = service;
		this.method = method;
	}

	@Override
	protected void writeContent() {
		ln();
		annotationOverride();
		s("    protected void onExecute(int conversationNumber");
		// TODO parameters
		ln(",", AsyncCallback.class.getName(), "callback) {");
		ln("        " + getServiceClassName() + "Async", "service = (" + getServiceClassName() + "Async) getService("
				+ getServiceClassName() + ".class);");
		s("        service." + method.getName() + "(conversationNumber");
		// TODO parameters
		ln(", callback);");
		ln("    }");
	}

	@Override
	protected void writeBeforeClassDefinition() {
		annotation(RemoteServiceRelativePath.class.getName(), Str.lowercaseFirstLetter(service.getName()));
	}

	@Override
	protected String getSuperclass() {
		return AServiceCall.class.getName();
	}

	@Override
	protected String getName() {
		return Str.uppercaseFirstLetter(method.getName()) + "ServiceCall";
	}

	private String getServiceClassName() {
		return service.getClientPackageName() + "." + service.getName() + "Service";
	}

	@Override
	protected String getPackage() {
		String s = service.getClientPackageName();
		int idx = s.indexOf(".client.");
		s = s.substring(0, idx + 8);
		return s + method.getPackageName();
	}

	@Override
	protected boolean isInterface() {
		return false;
	}

	@Override
	protected boolean isAbstract() {
		return false;
	}

	@Override
	protected boolean isOverwrite() {
		return true;
	}

}
