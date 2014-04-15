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
package ilarkesto.integration.kunagi;

import ilarkesto.testng.ATest;

import org.testng.annotations.Test;

public class KunagiConnectorTest extends ATest {

	private static final String PROJECT_ID = "56d5a565-e1b3-4409-aabc-534e8178e2ab-1";

	@Test
	public void submitIssue() {
		KunagiConnector kunagi = new KunagiConnector(PROJECT_ID);
		kunagi.postIssue(observer, "Heisenberg", "wi@koczewski.de", "test issue", "test text", false, false);
	}

}
