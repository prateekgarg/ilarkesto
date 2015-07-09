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
import ilarkesto.mda.legacy.model.ParameterModel;

import java.util.Collection;

public abstract class AJavaCodeGenerator {

	protected abstract void print(String text);

	public AJavaCodeGenerator s(String... ss) {
		boolean first = true;
		for (String s : ss) {
			if (first) {
				first = false;
			} else {
				print(" ");
			}
			print(s);
		}
		return this;
	}

	public AJavaCodeGenerator parameterNames(Collection<ParameterModel> parameters) {
		boolean first = true;
		for (ParameterModel parameter : parameters) {
			if (first) {
				first = false;
			} else {
				s(", ");
			}
			s(parameter.getName());
		}
		return this;
	}

	public AJavaCodeGenerator parameterDeclaration(Collection<ParameterModel> parameters) {
		boolean first = true;
		for (ParameterModel parameter : parameters) {
			if (first) {
				first = false;
			} else {
				s(", ");
			}
			s(parameter.getType(), parameter.getName());
		}
		return this;
	}

	public AJavaCodeGenerator annotationOverride() {
		return ln("    @Override");
	}

	public AJavaCodeGenerator annotation(String type, String... parameters) {
		s("@" + type);
		if (parameters != null) {
			s("(");
			for (String parameter : parameters) {
				s("\"" + parameter + "\"");
			}
			s(")");
		}
		ln();
		return this;
	}

	public AJavaCodeGenerator ln(String... ss) {
		s(ss);
		s("\n");
		return this;
	}

	public AJavaCodeGenerator javadoc(String text) {
		ln("/**");
		ln(" * ", text);
		ln("**/");
		return this;
	}

	public AJavaCodeGenerator sU(String s) {
		return s(Str.uppercaseFirstLetter(s));
	}

	public void comment(String s) {
		s("    // --- ").s(s).s(" ---").ln();
	}

	public void section(String description) {
		ln();
		ln("    // -----------------------------------------------------------");
		ln("    // - " + description);
		ln("    // -----------------------------------------------------------");
	}

}
