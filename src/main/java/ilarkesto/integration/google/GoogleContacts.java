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
import ilarkesto.integration.google.GoogleClient.GoogleEntity;
import ilarkesto.swing.LoginPanel;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import com.google.gdata.client.authn.oauth.OAuthException;
import com.google.gdata.client.authn.oauth.OAuthHmacSha1Signer;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.util.ServiceException;

public class GoogleContacts {

	public static void main(String[] args) {
		LoginData clientIdLogin = LoginPanel.showDialog(null, "Client ID", new File(
				"runtimedata/google-oauth2.properties"));
		if (clientIdLogin == null) return;

		LoginData refreshTokenLogin = LoginPanel.showDialog(null, "Refresh Token", new File(
				"runtimedata/google-oauth2-refresh.properties"));

		GoogleClient client = new GoogleClient(clientIdLogin.getLogin(), clientIdLogin.getPassword(),
				GoogleClient.REDIRECT_OOB, refreshTokenLogin.getPassword(), GoogleClient.SCOPE_USERINFO_EMAIL,
				GoogleClient.SCOPE_CONTACTS);

		GoogleContacts gc = new GoogleContacts(client, null, null, null, null);
		gc.listContacts();
	}

	private GoogleClient client;
	private String localIdentifierAttribute;
	private String localTimestampAttribute;
	private String localVersionAttribute;
	private String localVersion;

	public GoogleContacts(GoogleClient client, String localIdentifierAttribute, String localTimestampAttribute,
			String localVersionAttribute, String localVersion) {
		super();
		this.client = client;
		this.localIdentifierAttribute = localIdentifierAttribute;
		this.localTimestampAttribute = localTimestampAttribute;
		this.localVersionAttribute = localVersionAttribute;
		this.localVersion = localVersion;
	}

	public ContactFeed listContacts() {
		ContactsService service = new ContactsService("<var>Ilarkesto</var>");
		try {
			service.setOAuthCredentials(client.createOAuthParameters(), new OAuthHmacSha1Signer());
		} catch (OAuthException ex1) {
			throw new RuntimeException(ex1);
		}
		service.useSsl();
		service.setHeader("GData-Version", "3.0");
		URL feedUrl;
		try {
			feedUrl = new URL("https://www.google.com/m8/feeds/contacts/default/full");
		} catch (MalformedURLException ex) {
			throw new RuntimeException(ex);
		}
		ContactFeed resultFeed;
		try {
			resultFeed = service.getFeed(feedUrl, ContactFeed.class);
		} catch (IOException | ServiceException ex) {
			throw new RuntimeException(ex);
		}
		return resultFeed;
	}

	public void updateGoogle(Collection<Contact> localContacts) {
		// TODO
		throw new RuntimeException("not implemented yet");
	}

	public void updateGoogleContact(GoogleEntity googleEntity, Contact localContact) {
		// TODO
		throw new RuntimeException("not implemented yet");
	}

	public static class Contact {

		private String localId;

		private String firstName;
		private String lastName;

	}

}
