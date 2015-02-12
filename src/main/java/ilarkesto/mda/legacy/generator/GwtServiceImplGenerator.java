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

import ilarkesto.core.base.RuntimeTracker;
import ilarkesto.core.base.Str;
import ilarkesto.gwt.server.AGwtServiceImpl;
import ilarkesto.mda.legacy.model.GwtServiceModel;
import ilarkesto.mda.legacy.model.MethodModel;
import ilarkesto.mda.legacy.model.ParameterModel;

import java.util.LinkedHashSet;
import java.util.Set;

public class GwtServiceImplGenerator extends AClassGenerator {

	private GwtServiceModel service;

	public GwtServiceImplGenerator(GwtServiceModel service) {
		super();
		this.service = service;
	}

	@Override
	protected void writeContent() {
		for (MethodModel method : service.getMethods()) {
			writeMethodContent(method);
		}
	}

	private void writeMethodContent(MethodModel method) {
		ln();
		s("    protected abstract void on" + Str.uppercaseFirstLetter(method.getName())
				+ "(GwtConversation conversation");
		for (ParameterModel param : method.getParameters()) {
			s(",", param.getType(), param.getName());
		}
		ln(");");

		ln();
		s("    public", service.getDtoClassName(), method.getName() + "(int conversationNumber");
		for (ParameterModel param : method.getParameters()) {
			s(",", param.getType(), param.getName());
		}
		ln(") {");
		boolean ping = method.getName().equals("ping");
		if (!ping) {
			ln("        " + RuntimeTracker.class.getName(), "rt = new", RuntimeTracker.class.getName() + "();");
			ln("        log.debug(\"Handling service call: " + method.getName() + "\");");
		}
		ln("        WebSession session = (WebSession) getSession();");
		if (method.isSync()) {
			ln("        synchronized (getSyncObject()) {");
		}
		ln("            GwtConversation conversation = null;");
		ln("            try {");
		ln("                conversation = (GwtConversation) session.getGwtConversation(conversationNumber);");
		ln("            } catch (Throwable ex) {");
		ln("                log.info(\"Getting conversation failed:\", conversationNumber);");
		ln("                " + service.getDtoClassName() + " dto = new " + service.getDtoClassName() + "();");
		ln("                dto.addError(new ilarkesto.gwt.client.ErrorWrapper(ex));");
		ln("                return dto;");
		ln("            }");
		ln("            ilarkesto.di.Context context = ilarkesto.di.Context.get();");
		ln("            context.setName(\"gwt-srv:" + method.getName() + "\");");
		ln("            context.bindCurrentThread();");
		ln("            try {");
		s("                on" + Str.uppercaseFirstLetter(method.getName()) + "(conversation");
		for (ParameterModel param : method.getParameters()) {
			s(",", param.getName());
		}
		ln(");");
		ln("                onServiceMethodExecuted(context);");
		ln("            } catch (Throwable ex) {");
		ln("                handleServiceMethodException(conversationNumber, \"" + method.getName() + "\", ex);");
		ln("            }");
		if (!ping) {
			String paramsString = "";
			for (ParameterModel param : method.getParameters()) {
				paramsString += ", " + param.getName();
			}
			ln("            if (rt.getRuntime() > getMaxServiceCallExecutionTime(\"" + method.getName() + "\")) {");
			ln("                log.warn(\"ServiceCall served in\", rt.getRuntimeFormated(),\"" + method.getName()
					+ "\"" + paramsString + ");");
			ln("            } else {");
			ln("                log.info(\"ServiceCall served in\", rt.getRuntimeFormated(),\"" + method.getName()
					+ "\"" + paramsString + ");");
			ln("            }");
		}
		ln("            return (" + service.getDtoClassName() + ") conversation.popNextData();");
		if (method.isSync()) {
			ln("        }");
		}
		ln("    }");
	}

	@Override
	protected String getSuperclass() {
		return AGwtServiceImpl.class.getName();
	}

	@Override
	protected Set<String> getSuperinterfaces() {
		Set<String> ret = new LinkedHashSet<String>();
		ret.addAll(super.getSuperinterfaces());
		ret.add(service.getClientPackageName() + "." + service.getName() + "Service");
		return ret;
	}

	@Override
	protected String getName() {
		return "G" + service.getName() + "ServiceImpl";
	}

	@Override
	protected String getPackage() {
		return service.getPackageName();
	}

	@Override
	protected boolean isInterface() {
		return false;
	}

	@Override
	protected boolean isAbstract() {
		return true;
	}

	@Override
	protected boolean isOverwrite() {
		return true;
	}

}
