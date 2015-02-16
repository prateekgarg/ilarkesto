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

import ilarkesto.integration.google.GoogleClient.GoogleEntity;

import java.util.Collection;

public class GoogleContacts {

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
