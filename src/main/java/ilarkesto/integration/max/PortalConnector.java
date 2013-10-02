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

import ilarkesto.integration.max.state.MaxCubeState;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * https://www.max-portal.elv.de/dwr/test/MaxRemoteApi
 */
public class PortalConnector {

	private String baseUrl;
	private RequestExecutor requestExecutor;

	private int batchId;
	private String httpSessionId;
	private String scriptSessionId;

	private String user;
	private String password;

	private MaxCubeState maxCubeState;

	public PortalConnector(String baseUrl) {
		super();
		this.baseUrl = baseUrl;

		if (!this.baseUrl.endsWith("/")) this.baseUrl += "/";

	}

	public static PortalConnector createElvInstance() {
		return new PortalConnector("https://www.max-portal.elv.de/");
	}

	public MaxCubeState getMaxCubeState() {
		String result = executeApiMethod("getMaxCubeState");

		DwrParser parser = new DwrParser(result);

		if (!parser.contains("var s0=new MaxCubeState();"))
			throw new MaxPortalProtocolException("Missing 'new MaxCubeState()' in response");

		maxCubeState = (MaxCubeState) parser.parseCallbackObject();
		return maxCubeState;
	}

	void relogin() {
		login(user, password);
	}

	public void login(String user, String password) throws LoginFailedException {
		initialize();

		String result = executeApiMethod("login", "string:" + user, "string:" + password);

		DwrParser parser = new DwrParser(result);
		if (parser.isError()) throw new LoginFailedException(parser.getErrorMessage());

		if (!parser.contains("dwr.engine._remoteHandleCallback("))
			throw new MaxPortalProtocolException("Missing callback in response");

		this.user = user;
		this.password = password;
	}

	String executeApiMethod(String name, String... arguments) {
		batchId++;

		Map<String, String> parameters = new LinkedHashMap<String, String>();
		parameters.put("callCount", "1");
		parameters.put("page", "/dwr/test/MaxRemoteApi");
		parameters.put("httpSessionId", httpSessionId);
		parameters.put("scriptSessionId", scriptSessionId);
		parameters.put("c0-scriptName", "MaxRemoteApi");
		parameters.put("c0-methodName", name);
		parameters.put("c0-id", "0");
		for (int i = 0; i < arguments.length; i++) {
			parameters.put("c0-param" + i, arguments[i]);
		}
		parameters.put("batchId", String.valueOf(batchId));

		String result = requestExecutor.postAndGetContent(baseUrl + "dwr/call/plaincall/MaxRemoteApi.login.dwr",
			parameters);

		if (result.contains("message=\"Subject is not authenticated\"")) {
			if (user == null || password == null) throw new RuntimeException("Login required");
			relogin();
			return executeApiMethod(name, arguments);
		}

		httpSessionId = requestExecutor.getSessionId();

		return result;
	}

	void initialize() {
		requestExecutor = new RequestExecutor();
		batchId = 0;

		String engineScriptUrl = baseUrl + "dwr/engine.js";

		String script = requestExecutor.get(engineScriptUrl);

		httpSessionId = requestExecutor.getSessionId();

		DwrParser parser = new DwrParser(script);
		if (!parser.gotoAfterIf("dwr.engine._origScriptSessionId = \""))
			throw new MaxPortalProtocolException("Missing 'dwr.engine._origScriptSessionId' in " + engineScriptUrl);
		String origScriptSessionId = parser.getUntilIf("\"");
		if (origScriptSessionId == null)
			throw new MaxPortalProtocolException("Missing 'dwr.engine._origScriptSessionId = \"...\"' in "
					+ engineScriptUrl);
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
