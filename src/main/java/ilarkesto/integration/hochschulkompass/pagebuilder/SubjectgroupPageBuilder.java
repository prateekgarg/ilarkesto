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
package ilarkesto.integration.hochschulkompass.pagebuilder;

import ilarkesto.integration.hochschulkompass.Subjectgroup;
import ilarkesto.ui.web.jqm.Content;
import ilarkesto.ui.web.jqm.Listview;

public class SubjectgroupPageBuilder extends AJqmPageBuilder {

	private Subjectgroup subjectgroup;

	public SubjectgroupPageBuilder(Context context, String subjectgroupKey) {
		super(context);
		this.subjectgroup = context.getValues().getSubjectGroupByKey(subjectgroupKey);
	}

	@Override
	protected void fillContent(Content content) {
		content.addHtmlRenderer().H1(subjectgroup.getValue());
		Listview list = content.addListview();
		// List<Subjectgroup> subjectgroups = context.getValuesCache().getPayload().getSubjectgroups();
		// for (Subjectgroup subjectgroup : subjectgroups) {
		// ListItem item = list.addItem(subjectgroup.getValue());
		// item.setHref(context.href(subjectgroup));
		// if (subjectgroup.isTop()) item.setDataRoleToListDivider();
		// }
	}

	@Override
	protected String getTitle() {
		return subjectgroup.getValue();
	}

}
