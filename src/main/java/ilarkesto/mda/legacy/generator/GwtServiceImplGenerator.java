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
import ilarkesto.gwt.server.AGwtServiceImpl;
import ilarkesto.mda.legacy.model.GwtServiceModel;
import ilarkesto.mda.legacy.model.MethodModel;

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
		ln(");");

		ln();
		s("    public", service.getDtoClassName(), method.getName() + "(int conversationNumber");
		ln(") {");
		ln("        log.debug(\"Handling service call: " + method.getName() + "\");");
		ln("        WebSession session = (WebSession) getSession();");
		ln("        synchronized (session) {");
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
		ln("                on" + Str.uppercaseFirstLetter(method.getName()) + "(conversation);");
		ln("                onServiceMethodExecuted(context);");
		ln("            } catch (Throwable ex) {");
		ln("                handleServiceMethodException(conversationNumber, \"" + method.getName() + "\", ex);");
		ln("            }");
		ln("            return (" + service.getDtoClassName() + ") conversation.popNextData();");
		ln("        }");
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
