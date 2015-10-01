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
import ilarkesto.mda.legacy.model.ParameterModel;

import com.google.gwt.core.client.GWT;
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
		ln("    private static", getServiceClassName() + "Async", "service;");

		ln();
		for (ParameterModel param : method.getParameters()) {
			ln("    " + param.getType(), param.getName() + ";");
		}

		ln();
		s("    public", getName() + "(");
		boolean first = true;
		for (ParameterModel param : method.getParameters()) {
			if (first) {
				first = false;
			} else {
				s(", ");
			}
			s(param.getType(), param.getName());
		}
		ln(") {");
		for (ParameterModel param : method.getParameters()) {
			ln("        this." + param.getName(), "=", param.getName() + ";");
		}
		ln("    }");

		ln();
		annotationOverride();
		ln("    protected void onExecute(int conversationNumber,",
			AsyncCallback.class.getName() + "<" + service.getDtoClassName() + ">", "callback) {");
		ln("        if (service==null) {");
		ln("            service = (" + getServiceClassName() + "Async) " + GWT.class.getName() + ".create("
				+ getServiceClassName() + ".class);");
		ln("            initializeService(service, \"" + Str.lowercaseFirstLetter(service.getName()) + "\");");
		ln("        }");
		s("        service." + method.getName() + "(conversationNumber");
		for (ParameterModel param : method.getParameters()) {
			s(",", param.getName());
		}
		ln(", callback);");
		ln("    }");

		if (method.isDispensable()) {
			ln();
			annotationOverride();
			ln("    public boolean isDispensable() {");
			ln("        return " + method.isDispensable() + ";");
			ln("    }");
		}

		ln();
		annotationOverride();
		ln("    public String toString() {");
		ln("        return \"" + method.getName() + "\";");
		ln("    }");
	}

	@Override
	protected void writeBeforeClassDefinition() {
		annotation(RemoteServiceRelativePath.class.getName(), Str.lowercaseFirstLetter(service.getName()));
	}

	@Override
	protected String getSuperclass() {
		return AServiceCall.class.getName() + "<" + service.getDtoClassName() + ">";
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
		String ret = service.getClientPackageName();
		String methodPackage = method.getPackageName();
		if (!Str.isBlank(methodPackage)) ret += "." + methodPackage;
		return ret;
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
