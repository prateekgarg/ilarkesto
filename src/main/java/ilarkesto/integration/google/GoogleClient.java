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
package ilarkesto.integration.google;

import ilarkesto.core.auth.LoginData;
import ilarkesto.core.base.Str;
import ilarkesto.core.logging.Log;
import ilarkesto.integration.oauth.OAuth2;
import ilarkesto.swing.LoginPanel;

import java.io.File;

public class GoogleClient extends OAuth2 {

	public static void main(String[] args) {
		LoginData clientIdLogin = LoginPanel.showDialog(null, "Client ID", new File(
				"runtimedata/google-oauth2.properties"));
		if (clientIdLogin == null) return;

		GoogleClient client = new GoogleClient(clientIdLogin.getLogin(), clientIdLogin.getPassword(), REDIRECT_OOB,
				null, SCOPE_USERINFO_EMAIL, SCOPE_CONTACTS);
		Log.TEST(client.createUrlForAuthenticationRequest());

		LoginData authorizationCodeLogin = LoginPanel.showDialog(null, "Authorization Code", new File(
				"runtimedata/google-oauth2-code.properties"));

		client.exchangeAuthorizationCodeForAccessToken(authorizationCodeLogin.getPassword());

		client.exchangeRefreshTokenForAccessToken();

	}

	public static final String SCOPE_USERINFO_EMAIL = "https://www.googleapis.com/auth/userinfo.email";
	public static final String SCOPE_USERINFO_PROFILE = "https://www.googleapis.com/auth/userinfo.profile";
	public static final String SCOPE_CALENDAR = "https://www.googleapis.com/auth/calendar";
	public static final String SCOPE_CONTACTS = "https://www.google.com/m8/feeds/";

	public GoogleClient(String clientId, String clientSecret, String redirectUri, String refreshToken, String... scopes) {
		super("https://accounts.google.com/o/oauth2/auth", "https://www.googleapis.com/oauth2/v3/token", clientId,
				clientSecret, redirectUri, refreshToken, concatScope(scopes));
	}

	private static String concatScope(String[] scopes) {
		return Str.concat(scopes, "+");
	}

	public static class GoogleEntity {

		private String googleId;

		public final String getGoogleId() {
			return googleId;
		}

		public final void setGoogleId(String googleId) {
			this.googleId = googleId;
		}

	}

}
