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
package ilarkesto.integration.jfreechart;

import ilarkesto.json.JsonObject;

import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

public class JFreeChartUtil {

	public static PieDataset createPieDataset(Iterable<JsonObject> values, String keyProperty, String valueProperty) {
		DefaultPieDataset dataset = new DefaultPieDataset();
		for (JsonObject jValue : values) {
			String key = jValue.getString(keyProperty);
			if (key == null) continue;
			Number value = jValue.getNumber(valueProperty);
			if (value == null) continue;
			dataset.setValue(key, value);
		}
		return dataset;
	}

}
