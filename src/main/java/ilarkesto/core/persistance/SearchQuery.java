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
package ilarkesto.core.persistance;

import java.util.Arrays;

public class SearchQuery extends AEntityQuery {

	private SearchText searchText;

	private Class[] types;

	public SearchQuery(String text) {
		super();
		this.searchText = new SearchText(text);
	}

	public SearchQuery(String text, Class... types) {
		super();
		// TODOK kace
		this.searchText = new SearchText(text);
		this.types = types;
	}

	@Override
	public boolean matches(AEntity entity) {
		return entity.matches(searchText) && acceptType(entity);
	}

	private boolean acceptType(AEntity entity) {
		if (types == null || types.length == 0) { return true; }
		return Arrays.asList(types).contains(entity.getClass());
	}

}
