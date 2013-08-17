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
package ilarkesto.integration.rintelnde;

import ilarkesto.json.AJsonWrapper;
import ilarkesto.json.JsonObject;

import java.util.LinkedList;
import java.util.List;

public class Branchenbuch extends AJsonWrapper {

	public Branchenbuch(JsonObject json) {
		super(json);
	}

	public Branchenbuch() {}

	public int getLeafCategoriesCount() {
		int count = 0;
		for (Category masterCategory : getCategories()) {
			count += masterCategory.getCategories().size();
		}
		return count;
	}

	public List<Category> getLeafCategories() {
		List<Category> ret = new LinkedList<Branchenbuch.Category>();
		for (Category masterCategory : getCategories()) {
			ret.addAll(masterCategory.getCategories());
		}
		return ret;
	}

	// public int getEntriesCount() {
	// int count = 0;
	// for (Category masterCategory : getCategories()) {
	// for (Category category : masterCategory.getCategories()) {
	// count += category.getEntries().size();
	// }
	// }
	// return count;
	// }

	public List<Category> getCategories() {
		return getWrapperArray("categories", Category.class);
	}

	public void setCategories(List<Category> categories) {
		putArray("categories", categories);
	}

	public static class Category extends AJsonWrapper {

		public Category(JsonObject json) {
			super(json);
		}

		public Category(String label, Integer id) {
			putMandatory("label", label);
			putMandatory("id", id);
		}

		public boolean isFirstInParentCategory() {
			Category parent = getParentCategory();
			List<Category> categories = parent.getCategories();
			if (categories.isEmpty()) return false;
			Category first = categories.get(0);
			return first.getId().equals(getId()) && first.getLabel().equals(getLabel());
		}

		public Category getParentCategory() {
			return getParent(Category.class);
		}

		public String getLabel() {
			return json.getString("label");
		}

		public Integer getId() {
			return json.getInteger("id");
		}

		public List<Category> getCategories() {
			return getWrapperArray("categories", Category.class);
		}

		public void setCategories(List<Category> categories) {
			putArray("categories", categories);
		}

		// public List<Entry> getEntries() {
		// return getWrapperArray("entries", Entry.class);
		// }
		//
		// public void setEntries(List<Entry> entries) {
		// putArray("entries", entries);
		// }

	}

	public static class Entry extends AJsonWrapper implements RintelnDePage {

		public Entry(JsonObject json) {
			super(json);
		}

		public Entry(String label, Integer id) {
			putMandatory("label", label);
			putMandatory("id", id);
		}

		@Override
		public String getPagePath() {
			return RintelnDe.PAGE_BRANCHENBUCH + "/detail/" + getId() + "/";
		}

		public String getLabel() {
			return json.getString("label");
		}

		public Integer getId() {
			return json.getInteger("id");
		}

	}

}
