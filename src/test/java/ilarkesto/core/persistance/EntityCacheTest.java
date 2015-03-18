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

import ilarkesto.base.Utl;
import ilarkesto.testng.ATest;

import java.util.Set;

import org.testng.annotations.Test;

public class EntityCacheTest extends ATest {

	@Test
	public void basic() {
		EntityCache cache = new EntityCache();
		cache.add(new Car().setName("a"));
		cache.add(new Car().setName("b"));
		cache.add(new Car().setName("c"));

		assertSize(cache.getAll(), 3);
	}

	// @Test
	public void concurrency() {
		final EntityCache cache = new EntityCache();
		cache.add(new Car().setName("c1"));
		cache.add(new Car().setName("c2"));
		cache.add(new Mercedes().setName("m1"));

		new Thread() {

			@Override
			public void run() {
				Utl.sleep(100);
				cache.add(new Car().setName("new"));
			};
		}.start();

		Set<AEntity> all = cache.list(new AEntityQuery<Car>() {

			@Override
			public boolean test(Car entity) {
				Utl.sleep(100);
				return true;
			}

		});
		assertSize(all, 3);
	}

	@Test
	public void query() {
		EntityCache cache = new EntityCache();
		cache.add(new Car().setName("c1"));
		cache.add(new Car().setName("c2"));
		cache.add(new Mercedes().setName("m1"));

		Set<AEntity> mercedeses = cache.list(new AEntityQuery<Mercedes>() {

			@Override
			public boolean test(Mercedes entity) {
				return true;
			}

			@Override
			public Class<Mercedes> getType() {
				return Mercedes.class;
			}
		});
		assertSize(mercedeses, 1);

		Set<AEntity> cars = cache.list(new AEntityQuery<Car>() {

			@Override
			public boolean test(Car entity) {
				return true;
			}

			@Override
			public Class<Car> getType() {
				return Car.class;
			}
		});
		assertSize(cars, 3);
	}

	@Test
	public void isInstanceOf() {
		EntityCache cache = new EntityCache();
		assertTrue(cache.isInstanceOf(Car.class, Car.class));
		assertTrue(cache.isInstanceOf(Mercedes.class, Car.class));
		assertTrue(cache.isInstanceOf(Mercedes.class, AEntity.class));
		assertFalse(cache.isInstanceOf(Car.class, Mercedes.class));
	}

	public static class Mercedes extends Car {}

	public static class Car extends AEntity {

		private String name;

		public Car setName(String name) {
			this.name = name;
			return this;
		}

		@Override
		protected String asString() {
			return name;
		}
	}
}
