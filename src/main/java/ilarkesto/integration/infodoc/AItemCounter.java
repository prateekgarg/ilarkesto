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
package ilarkesto.integration.infodoc;

import ilarkesto.core.base.LazyMap;

public abstract class AItemCounter {

	public abstract String getNumber(int i);

	private static LazyMap<Integer, AItemCounter> countersByDepth = new LazyMap<Integer, AItemCounter>() {

		@Override
		protected AItemCounter create(Integer depth) {
			switch (depth) {
				case 0:
					return new ItemCounter0();
				case 1:
					return new ItemCounter1();
				case 2:
					return new ItemCounter2();
				case 3:
					return new ItemCounter3();
				case 4:
					return new ItemCounter4();
			}
			return new ItemCounter5();
		}
	};

	public static AItemCounter get(int depth) {
		return countersByDepth.get(depth);
	}

	static class ItemCounter0 extends AItemCounter {

		@Override
		public String getNumber(int i) {
			int code = 'A';
			code += i;
			return ((char) code) + ")";
		}

	}

	static class ItemCounter1 extends AItemCounter {

		@Override
		public String getNumber(int i) {
			i++;
			switch (i) {
				case 1:
					return "I.";
				case 2:
					return "II.";
				case 3:
					return "III.";
				case 4:
					return "IV.";
				case 5:
					return "V.";
				case 6:
					return "VI.";
				case 7:
					return "VII.";
				case 8:
					return "VIII.";
				case 9:
					return "IX.";
				case 10:
					return "X.";
			}
			if (i > 10) return "X" + getNumber(i - 11);
			return "(" + i + ")";
		}

	}

	static class ItemCounter2 extends AItemCounter {

		@Override
		public String getNumber(int i) {
			i++;
			return String.valueOf(i) + ".";
		}

	}

	static class ItemCounter3 extends AItemCounter {

		private int count;

		public ItemCounter3() {
			super();
			this.count = 1;
		}

		@Override
		public String getNumber(int n) {
			int code = 'a';
			code += n;

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < count; i++) {
				sb.append(((char) code));
			}
			for (int i = 0; i < count; i++) {
				sb.append(')');
			}

			return sb.toString();
		}

	}

	static class ItemCounter4 extends AItemCounter {

		private int count;

		public ItemCounter4() {
			this.count = 2;
		}

		@Override
		public String getNumber(int n) {
			int code = 'a';
			code += n;

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < count; i++) {
				sb.append(((char) code));
			}
			sb.append(')');

			return sb.toString();
		}

	}

	static class ItemCounter5 extends AItemCounter {

		@Override
		public String getNumber(int i) {
			i++;
			return "(" + String.valueOf(i) + ")";
		}

	}

}
