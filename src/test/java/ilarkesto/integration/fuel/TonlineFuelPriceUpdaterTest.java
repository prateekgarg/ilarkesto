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
package ilarkesto.integration.fuel;

import ilarkesto.core.logging.Log;
import ilarkesto.integration.fuel.FuelStation.Price;
import ilarkesto.testng.ATest;

import org.testng.annotations.Test;

public class TonlineFuelPriceUpdaterTest extends ATest {

	@Test
	public void update() {
		TonlineFuelPriceUpdater updater = new TonlineFuelPriceUpdater();
		for (FuelStation station : Fuel.createRintelnStations()) {
			update(station, updater);
		}
	}

	private void update(FuelStation station, TonlineFuelPriceUpdater updater) {
		updater.updatePrices(station);
		Price diesel = station.getLatestPriceByFuel(Fuel.DIESEL);
		assertNotNull(diesel);
		Price e5 = station.getLatestPriceByFuel(Fuel.E5);
		assertNotNull(e5);
		Price e10 = station.getLatestPriceByFuel(Fuel.E10);
		assertNotNull(e10);
		Price plus = station.getLatestPriceByFuel(Fuel.PLUS);
		assertNotNull(plus);
		Price autogas = station.getLatestPriceByFuel(Fuel.AUTOGAS);
		assertNotNull(plus);
		Log.TEST(diesel, e5, e10, plus, autogas);
	}

}
