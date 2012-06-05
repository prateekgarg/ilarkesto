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
package ilarkesto.base;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hasher {

	public static void main(String[] args) {
		System.out.println(createSha1Instance().hash("geheim!23"));
	}

	private String algorithm;

	public Hasher(String algorithm) {
		this.algorithm = algorithm;
	}

	public static final Hasher createSha1Instance() {
		return new Hasher("SHA-1");
	}

	public String hash(String value) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException ex) {
			throw new RuntimeException("Unsupported algorithm: " + algorithm, ex);
		}
		md.reset();
		md.update(value.getBytes());
		byte[] hash = md.digest();

		// return String.format("%1$02x", hash);
		return new BigInteger(1, hash).toString(16);
	}

}
