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
import ilarkesto.mda.legacy.model.GwtServiceModel;
import ilarkesto.mda.legacy.model.MethodModel;
import ilarkesto.mda.legacy.model.ParameterModel;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

public class GwtServiceInterfaceGenerator extends AClassGenerator {

	private GwtServiceModel service;

	public GwtServiceInterfaceGenerator(GwtServiceModel service) {
		super();
		this.service = service;
	}

	@Override
	protected void writeContent() {
		for (MethodModel method : service.getMethods()) {
			ln();
			s("    " + service.getDtoClassName(), method.getName() + "(int conversationNumber");
			for (ParameterModel param : method.getParameters()) {
				s(",", param.getType(), param.getName());
			}
			ln(");");
		}
	}

	@Override
	protected void writeBeforeClassDefinition() {
		annotation(RemoteServiceRelativePath.class.getName(), Str.lowercaseFirstLetter(service.getName()));
	}

	@Override
	protected String getSuperclass() {
		return RemoteService.class.getName();
	}

	@Override
	protected String getName() {
		return service.getName() + "Service";
	}

	@Override
	protected String getPackage() {
		return service.getPackageName().replace(".server", ".client");
	}

	@Override
	protected boolean isInterface() {
		return true;
	}

	@Override
	protected boolean isOverwrite() {
		return true;
	}

}
