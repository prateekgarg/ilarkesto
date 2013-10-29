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
package ilarkesto.integration.max;

import ilarkesto.core.logging.Log;
import ilarkesto.integration.max.state.MaxCubeState;
import ilarkesto.integration.max.state.MaxRoom;
import ilarkesto.json.Json;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.http.impl.client.DefaultHttpClient;

/**
 * https://www.max-portal.elv.de/dwr/test/MaxRemoteApi
 */
public class MaxConnector {

	private static Log log = Log.get(MaxConnector.class);

	private DefaultHttpClient httpClient;
	private String baseUrl;
	private RequestExecutor requestExecutor;

	private int batchId;
	private String httpSessionId;
	private String scriptSessionId;

	private String user;
	private String password;

	private MaxCubeState maxCubeState;

	public MaxConnector(String baseUrl, DefaultHttpClient httpClient) {
		super();
		this.baseUrl = baseUrl;
		this.httpClient = httpClient;

		if (!this.baseUrl.endsWith("/")) this.baseUrl += "/";

	}

	public static MaxConnector createElvInstance(DefaultHttpClient httpClient) {
		return new MaxConnector("https://www.max-portal.elv.de/", httpClient);
	}

	public void executeSetRoomAutoMode(MaxRoom room) {
		Map<String, String> extra = new LinkedHashMap<String, String>();
		extra.put("c0-e2", "string:" + room.getId());
		extra.put("c0-e1", "Object_MaxSetRoomAutoMode:{roomId:reference:c0-e2}");
		executeApiMethod("setClientCommands", extra, "Array:[reference:c0-e1]");
		log.info("Command transmitted:", "SetRoomAutoMode", room.getName());
	}

	public void executeSetRoomEcoMode(MaxRoom room) {
		executeSetRoomPermanentMode(room, room.getEcoTemperature());
	}

	public void executeSetRoomComfortMode(MaxRoom room) {
		executeSetRoomPermanentMode(room, room.getComfortTemperature());
	}

	public void executeSetRoomPermanentMode(MaxRoom room, float temperature) {
		Map<String, String> extra = new LinkedHashMap<String, String>();
		extra.put("c0-e2", "string:" + room.getId());
		extra.put("c0-e3", "number:" + temperature);
		extra.put("c0-e1", "Object_MaxSetRoomPermanentMode:{roomId:reference:c0-e2, temperature:reference:c0-e3}");
		executeApiMethod("setClientCommands", extra, "Array:[reference:c0-e1]");
		log.info("Command transmitted:", "SetRoomPermanentMode", temperature, room.getName());
	}

	public void executeSetRoomTemporaryMode(MaxRoom room, float temperature, Date until) {
		Map<String, String> extra = new LinkedHashMap<String, String>();
		extra.put("c0-e2", "string:" + room.getId());
		extra.put("c0-e3", "Date:" + until.getTime());
		extra.put("c0-e4", "number:" + temperature);
		extra.put("c0-e1",
			"Object_MaxSetRoomPermanentMode:{roomId:reference:c0-e2, temperature:reference:c0-e3, temperature:reference:c0-e4}");
		executeApiMethod("setClientCommands", extra, "Array:[reference:c0-e1]");
		log.info("Command transmitted:", "SetRoomTemporaryModeMode", temperature, room.getName());
	}

	public MaxCubeState getMaxCubeState() {
		String result = executeApiMethod("getMaxCubeState", null);

		DwrParser parser = new DwrParser(result);

		if (!parser.contains("var s0=new MaxCubeState();"))
			throw new MaxProtocolException("Missing 'new MaxCubeState()' in response");

		maxCubeState = (MaxCubeState) parser.parseCallbackObject();
		maxCubeState.wire();
		log.info("State loaded");
		return maxCubeState;
	}

	void relogin() {
		login(user, password);
	}

	public void login(String user, String password) throws LoginFailedException {
		initialize();

		String result = executeApiMethod("login", null, "string:" + user, "string:" + password);

		DwrParser parser = new DwrParser(result);
		if (parser.isError()) throw new LoginFailedException(baseUrl, user, parser.getErrorMessage());

		if (!parser.contains("dwr.engine._remoteHandleCallback("))
			throw new MaxProtocolException("Missing callback in response");

		this.user = user;
		this.password = password;
	}

	synchronized String executeApiMethod(String name, Map<String, String> extraParams, String... arguments) {
		batchId++;

		Map<String, String> parameters = new LinkedHashMap<String, String>();
		parameters.put("callCount", "1");
		parameters.put("page", "/dwr/test/MaxRemoteApi");
		parameters.put("httpSessionId", httpSessionId);
		parameters.put("scriptSessionId", scriptSessionId);
		parameters.put("c0-scriptName", "MaxRemoteApi");
		parameters.put("c0-methodName", name);
		parameters.put("c0-id", "0");
		if (extraParams != null) parameters.putAll(extraParams);
		for (int i = 0; i < arguments.length; i++) {
			parameters.put("c0-param" + i, arguments[i]);
		}
		parameters.put("batchId", String.valueOf(batchId));

		String result = requestExecutor.postAndGetContent(baseUrl + "dwr/call/plaincall/MaxRemoteApi.login.dwr",
			parameters);

		if (result.contains("message=\"Subject is not authenticated\"")) {
			if (user == null || password == null) throw new RuntimeException("Login required");
			relogin();
			return executeApiMethod(name, extraParams, arguments);
		}

		httpSessionId = requestExecutor.getSessionId();

		if (result.contains("MaxClientException")) {
			String message = "Command execution failed.";
			int messageIdx = result.indexOf("message=\"");
			if (messageIdx > 0) {
				messageIdx += 9;
				message += " -> " + Json.parseString(result.substring(messageIdx, result.indexOf("\"", messageIdx)));
			}
			throw new MaxCommandFailedException(message);
		}

		return result;
	}

	void initialize() {
		requestExecutor = new RequestExecutor(httpClient);
		batchId = 0;

		String engineScriptUrl = baseUrl + "dwr/engine.js";

		String script = requestExecutor.get(engineScriptUrl);

		httpSessionId = requestExecutor.getSessionId();

		DwrParser parser = new DwrParser(script);
		if (!parser.gotoAfterIf("dwr.engine._origScriptSessionId = \""))
			throw new MaxProtocolException("Missing 'dwr.engine._origScriptSessionId' in " + engineScriptUrl);
		String origScriptSessionId = parser.getUntilIf("\"");
		if (origScriptSessionId == null)
			throw new MaxProtocolException("Missing 'dwr.engine._origScriptSessionId = \"...\"' in " + engineScriptUrl);
		scriptSessionId = origScriptSessionId + Math.floor(Math.random() * 1000);
	}

	public MaxCubeState getLastMaxCubeState() {
		return maxCubeState;
	}

	public String getScriptSessionId() {
		return scriptSessionId;
	}

	public String getHttpSessionId() {
		return httpSessionId;
	}

	public int getBatchId() {
		return batchId;
	}

}
