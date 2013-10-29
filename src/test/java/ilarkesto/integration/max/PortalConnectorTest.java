package ilarkesto.integration.max;

import ilarkesto.integration.max.state.MaxCubeState;
import ilarkesto.integration.max.state.MaxHouse;
import ilarkesto.integration.max.state.MaxRoom;
import ilarkesto.testng.ATest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.http.impl.client.DefaultHttpClient;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

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

public class PortalConnectorTest extends ATest {

	private String username;
	private String password;

	@BeforeClass
	public void loadCredentials() throws FileNotFoundException, IOException {
		File file = new File("runtimedata/max.properties");
		if (!file.exists()) return;
		Properties properties = new Properties();
		properties.load(new BufferedReader(new FileReader(file)));
		username = properties.getProperty("username");
		password = properties.getProperty("password");
	}

	// @Test
	public void initialize() {
		MaxConnector pc = MaxConnector.createElvInstance(new DefaultHttpClient());
		pc.initialize();
		String scriptSessionId = pc.getScriptSessionId();
		System.out.println(scriptSessionId);
		assertNotNull(scriptSessionId);
	}

	// @Test
	public void login() {
		MaxConnector pc = MaxConnector.createElvInstance(new DefaultHttpClient());
		pc.login(username, password);
	}

	@Test
	public void getMaxCubeState() {
		if (username == null) return;
		MaxConnector pc = MaxConnector.createElvInstance(new DefaultHttpClient());
		pc.login(username, password);
		MaxCubeState state = pc.getMaxCubeState();
		System.out.println(state.toString());
		MaxHouse house = state.getHouse();
		assertNotNull(house);
		List<MaxRoom> rooms = state.getRooms();
		assertSize(rooms, 8);
		for (MaxRoom room : rooms) {
			System.out.println(room.getName() + ": " + room.getControlMode());
		}
	}

}
