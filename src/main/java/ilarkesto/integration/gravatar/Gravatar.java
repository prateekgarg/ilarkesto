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
package ilarkesto.integration.gravatar;

import ilarkesto.core.base.Str;
import ilarkesto.core.logging.Log;
import ilarkesto.io.IO;
import ilarkesto.json.JsonObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Gravatar {

	private static Log log = Log.get(Gravatar.class);

	public static void main(String[] args) {
		System.out.println(createAvatarUrl("wi@koczewski.de"));
		System.out.println(loadProfile("wi@koczewski.de"));
	}

	public static final Profile loadProfile(String email) {
		String url = createJsonProfileUrl(email);
		if (Str.isBlank(url)) return null;
		log.debug("Loading Gravatar profile for", email, "->", email);
		String json = IO.downloadUrlToString(url);
		if (Str.isBlank(json)) return null;
		return new Profile(new JsonObject(json));
	}

	public static final String createJsonProfileUrl(String email) {
		String url = createProfileUrl(email);
		if (Str.isBlank(url)) return null;
		return url + ".json";
	}

	public static final String createProfileUrl(String email) {
		if (Str.isBlank(email)) return null;
		return "https://secure.gravatar.com/" + createHash(email);
	}

	public static final String createAvatarUrl(String email) {
		if (Str.isBlank(email)) return null;
		return "https://secure.gravatar.com/avatar/" + createHash(email);
	}

	public static final String createHash(String email) {
		if (Str.isBlank(email)) return null;
		return MD5Util.md5Hex(email.trim().toLowerCase());
	}

	static class MD5Util {

		public static String hex(byte[] array) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
			}
			return sb.toString();
		}

		public static String md5Hex(String message) {
			try {
				MessageDigest md = MessageDigest.getInstance("MD5");
				return hex(md.digest(message.getBytes("CP1252")));
			} catch (NoSuchAlgorithmException ex) {
				throw new RuntimeException(ex);
			} catch (UnsupportedEncodingException ex) {
				throw new RuntimeException(ex);
			}
		}
	}

}
