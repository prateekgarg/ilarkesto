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
package ilarkesto.integration.jura;

import java.util.ArrayList;
import java.util.List;

public class JuraMultiNormReference {

	private List<JuraNormReference> norms = new ArrayList<JuraNormReference>();

	public void addNorm(JuraNormReference norm) {
		norms.add(norm);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("§§");
		for (JuraNormReference norm : norms) {
			sb.append(" ").append(norm.toString());
		}
		return sb.toString();
	}

}
