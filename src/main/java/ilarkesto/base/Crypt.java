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
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package ilarkesto.base;

import ilarkesto.io.Base64;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Crypt {

	public static void main(String[] args) throws Exception {
		Crypt crypt = Crypt.createAesInstance();

		byte[] key = crypt.generateKey();

		System.out.println("key: " + Base64.encodeBytes(key));

		byte[] encrypted = crypt.encrypt("hello world".getBytes(), key);
		System.out.println("encrypted string: " + Base64.encodeBytes(encrypted));

		byte[] original = crypt.decrypt(encrypted, key);
		String originalString = new String(original);
		System.out.println("Original string: " + originalString);
	}

	private String algorithm;

	public Crypt(String algorithm) {
		this.algorithm = algorithm;
	}

	public static final Crypt createAesInstance() {
		return new Crypt("AES");
	}

	public byte[] decrypt(byte[] encryptedData, byte[] key) {
		SecretKeySpec skeySpec = new SecretKeySpec(key, algorithm);
		try {
			Cipher cipher = Cipher.getInstance(algorithm);
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			return cipher.doFinal(encryptedData);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public String decryptString(byte[] encryptedData, byte[] key) {
		return new String(decrypt(encryptedData, key));
	}

	public byte[] encrypt(byte[] data, byte[] key) {
		SecretKeySpec skeySpec = new SecretKeySpec(key, algorithm);
		try {
			Cipher cipher = Cipher.getInstance(algorithm);
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			return cipher.doFinal(data);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public byte[] encrypt(String string, byte[] key) {
		return encrypt(string.getBytes(), key);
	}

	public byte[] generateKey() {
		KeyGenerator kgen;
		try {
			kgen = KeyGenerator.getInstance(algorithm);
		} catch (NoSuchAlgorithmException ex) {
			throw new RuntimeException(ex);
		}
		kgen.init(128); // 192 and 256 bits may not be available
		SecretKey skey = kgen.generateKey();
		return skey.getEncoded();
	}

}
