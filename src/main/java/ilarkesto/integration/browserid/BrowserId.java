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
package ilarkesto.integration.browserid;

import ilarkesto.core.logging.Log;
import ilarkesto.core.time.DateAndTime;
import ilarkesto.io.IO;
import ilarkesto.json.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class BrowserId {

	private static Log log = Log.get(BrowserId.class);

	public static DateAndTime getExpiresAsDateAndTime(JsonObject verification) {
		return new DateAndTime(getExpires(verification));
	}

	public static long getExpires(JsonObject verification) {
		return verification.getLong("expires");
	}

	public static String getVerifiedEmail(String audience, String assertion) {
		JsonObject verification = getVerificationAsJson(audience, assertion);
		return getVerifiedEmail(verification);
	}

	public static String getVerifiedEmail(JsonObject verification) {
		if (!isStatusOkay(verification)) return null;
		return verification.getString("email");
	}

	public static boolean isStatusOkay(JsonObject verification) {
		return verification.containsString("status", "okay");
	}

	public static JsonObject getVerificationAsJson(String audience, String assertion) {
		return new JsonObject(getVerificationAsString(audience, assertion));
	}

	public static String getVerificationAsString(String audience, String assertion) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("audience", audience);
		params.put("assertion", assertion);
		String verification = IO.postAndGetResult("https://browserid.org/verify", params, null, null, null);
		log.info(verification);
		return verification;
	}
}
