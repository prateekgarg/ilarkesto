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

import ilarkesto.base.Str;
import ilarkesto.core.time.Date;
import ilarkesto.core.time.DateAndTime;
import ilarkesto.core.time.Time;
import ilarkesto.gwt.client.editor.ABooleanEditorModel;
import ilarkesto.gwt.client.editor.ADateAndTimeEditorModel;
import ilarkesto.gwt.client.editor.ADateEditorModel;
import ilarkesto.gwt.client.editor.AFloatEditorModel;
import ilarkesto.gwt.client.editor.AIntegerEditorModel;
import ilarkesto.gwt.client.editor.AOptionEditorModel;
import ilarkesto.gwt.client.editor.ATextEditorModel;
import ilarkesto.gwt.client.editor.ATimeEditorModel;
import ilarkesto.mda.legacy.model.PropertyModel;
import ilarkesto.mda.legacy.model.StringPropertyModel;

public class GwtEntityPropertyEditorClassGeneratorPlugin extends AClassGeneratorPlugin<EntityGenerator> {

	@Override
	public void onClassContent(EntityGenerator generator) {
		for (PropertyModel pm : generator.bean.getProperties()) {
			editor(pm);
		}
	}

	private void editor(PropertyModel property) {
		if (property.isCollection()) return;
		if (property.isReference()) return;
		String modelProperty = property.getName() + "Model";
		String nameUpper = Str.uppercaseFirstLetter(property.getName());
		String baseClassName = null;
		String type = property.getType();
		if (property.isOptionRestricted()) {
			if (type.equals(int.class.getName())) type = Integer.class.getName();
			baseClassName = AOptionEditorModel.class.getName() + "<" + type + ">";
		} else if (type.equals(int.class.getName()) || type.equals(Integer.class.getName())) {
			baseClassName = AIntegerEditorModel.class.getName();
			type = Integer.class.getName();
		} else if (type.equals(float.class.getName()) || type.equals(Float.class.getName())) {
			baseClassName = AFloatEditorModel.class.getName();
			type = Float.class.getName();
		} else if (type.equals(Date.class.getName())) {
			baseClassName = ADateEditorModel.class.getName();
			type = ilarkesto.core.time.Date.class.getName();
		} else if (type.equals(Time.class.getName())) {
			baseClassName = ATimeEditorModel.class.getName();
			type = ilarkesto.core.time.Time.class.getName();
		} else if (type.equals(DateAndTime.class.getName())) {
			baseClassName = ADateAndTimeEditorModel.class.getName();
			type = ilarkesto.core.time.DateAndTime.class.getName();
		} else if (type.equals(String.class.getName())) {
			baseClassName = ATextEditorModel.class.getName();
		} else if (type.equals(boolean.class.getName())) {
			baseClassName = ABooleanEditorModel.class.getName();
			type = Boolean.class.getName();
		} else if (type.equals(Boolean.class.getName())) {
			baseClassName = ABooleanEditorModel.class.getName();
		}
		if (baseClassName == null) return;

		ln();
		ln("    private transient " + nameUpper + "Model " + modelProperty + ";");
		ln();
		ln("    public " + nameUpper + "Model get" + nameUpper + "Model() {");
		ln("        if (" + modelProperty + " == null) " + modelProperty + " = create"
				+ Str.uppercaseFirstLetter(modelProperty) + "();");
		ln("        return " + modelProperty + ";");
		ln("    }");
		ln();
		ln("    protected " + Str.uppercaseFirstLetter(nameUpper) + "Model create" + nameUpper
				+ "Model() { return new " + nameUpper + "Model(); }");
		ln();
		ln("    protected class " + nameUpper + "Model extends " + baseClassName + " {");
		ln();
		ln("        @Override");
		ln("        public String getId() {");
		ln("            return \"" + property.getBean().getName() + "_" + property.getName() + "\";");
		ln("        }");
		ln();
		ln("        @Override");
		ln("        public " + type + " getValue() {");
		if (type.equals(Boolean.class.getName())) {
			ln("            return is" + nameUpper + "();");
		} else {
			ln("            return get" + nameUpper + "();");
		}
		ln("        }");
		ln();
		ln("        @Override");
		ln("        public void setValue(" + type + " value) {");
		ln("            set" + nameUpper + "(value);");
		ln("        }");
		if (baseClassName.equals(AIntegerEditorModel.class.getName())) {
			ln();
			ln("            @Override");
			ln("            public void increment() {");
			ln("                set" + nameUpper + "(get" + nameUpper + "() + 1);");
			ln("            }");
			ln();
			ln("            @Override");
			ln("            public void decrement() {");
			ln("                set" + nameUpper + "(get" + nameUpper + "() - 1);");
			ln("            }");
		}
		if (property.isOptionRestricted()) {
			ln();
			ln("        @Override");
			ln("        public List<" + type + "> getOptions() {");
			ln("            return get" + nameUpper + "Options();");
			ln("        }");
		}
		if (property.isMandatory()) {
			ln();
			ln("        @Override");
			ln("        public boolean isMandatory() { return true; }");
		}
		String editablePredicate = property.getEditablePredicate();
		if (editablePredicate != null) {
			String returnValue = editablePredicate.equals("false") ? "false" : "G" + property.getBean().getName()
					+ ".this.is" + Str.uppercaseFirstLetter(editablePredicate) + "()";
			ln();
			ln("        @Override");
			ln("        public boolean isEditable() { return " + returnValue + "; }");
		}
		if (property instanceof StringPropertyModel) {
			StringPropertyModel sProperty = (StringPropertyModel) property;
			if (sProperty.isRichtext()) {
				ln();
				ln("        @Override");
				ln("        public boolean isRichtext() { return true; }");
			}
			if (sProperty.isMasked()) {
				ln();
				ln("        @Override");
				ln("        public boolean isMasked() { return true; }");
			}
			if (sProperty.isMaxLengthSet()) {
				ln();
				ln("        @Override");
				ln("        public int getMaxLength() { return " + sProperty.getMaxLenght() + "; }");
			}
			if (sProperty.isTemplateAvailable()) {
				ln();
				ln("        @Override");
				ln("        public String getTemplate() { return get" + nameUpper + "Template(); }");
			}
		}
		if (property.getTooltip() != null) {
			ln("        @Override");
			ln("        public String getTooltip() { return \"" + property.getTooltip().replace("\\", "\\\\") + "\"; }");
		}
		ln();
		ln("        @Override");
		ln("        protected void onChangeValue(" + type + " oldValue, " + type + " newValue) {");
		ln("            super.onChangeValue(oldValue, newValue);");
		if (property.isMandatory()) {
			ln("            if (oldValue == null) return;");
		}
		ln("            addUndo(this, oldValue);");
		ln("        }");
		ln();
		ln("    }");
	}

}
