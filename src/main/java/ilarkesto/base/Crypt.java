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
import ilarkesto.io.IO;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Crypt {

	public static void main(String[] args) {
		Crypt crypt = Crypt.createAesInstance();
		System.out.println("AES random key: " + Base64.encodeBytes(crypt.generateKey()));
	}

	private String algorithm;
	private String algorithmSuffix = "";
	private String keyAlgorithm = "PBKDF2WithHmacSHA1";
	private int keyLength = 128; // 192 and 256 bits may not be available
	private byte[] keySalt = Base64.decode("4XmAmqaxiT2GXZVvqXy1dA==");
	private int keyIterationCount = 666;

	public Crypt(String algorithm) {
		this.algorithm = algorithm;
	}

	public Crypt setKeyLength(int keyLength) {
		this.keyLength = keyLength;
		return this;
	}

	public Crypt setKeySalt(byte[] keySeed) {
		this.keySalt = keySeed;
		return this;
	}

	public Crypt setAlgorithmSuffix(String algorithmSuffix) {
		this.algorithmSuffix = algorithmSuffix;
		return this;
	}

	public static final Crypt createAesInstance() {
		return new Crypt("AES");
	}

	public static final Crypt createAesWithCbcPkcAndPaddingInstance() {
		return new Crypt("AES").setAlgorithmSuffix("/CBC/PKCS5Padding");
	}

	public byte[] decrypt(byte[] encryptedData, byte[] key) throws DecryptionFailedException {
		SecretKeySpec skeySpec = new SecretKeySpec(key, algorithm);
		try {
			Cipher cipher = Cipher.getInstance(algorithm + algorithmSuffix);
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			return cipher.doFinal(encryptedData);
		} catch (Exception ex) {
			throw new DecryptionFailedException(ex);
		}
	}

	public String decryptToString(byte[] encryptedData, byte[] key) throws DecryptionFailedException {
		try {
			return new String(decrypt(encryptedData, key), IO.UTF_8);
		} catch (UnsupportedEncodingException ex) {
			throw new RuntimeException(ex);
		}
	}

	public byte[] decryptFile(File file, byte[] key) throws DecryptionFailedException {
		byte[] encrypted = IO.readFileToByteArray(file);
		return decrypt(encrypted, key);
	}

	public String decryptFileToString(File file, byte[] key) throws DecryptionFailedException {
		byte[] encrypted = IO.readFileToByteArray(file);
		return decryptToString(encrypted, key);
	}

	public byte[] encrypt(byte[] data, byte[] key) {
		SecretKeySpec skeySpec = new SecretKeySpec(key, algorithm);
		try {
			Cipher cipher = Cipher.getInstance(algorithm + algorithmSuffix);
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			return cipher.doFinal(data);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public byte[] encrypt(String data, byte[] key) {
		try {
			return encrypt(data.getBytes(IO.UTF_8), key);
		} catch (UnsupportedEncodingException ex) {
			throw new RuntimeException(ex);
		}
	}

	public void encryptToFile(byte[] data, byte[] key, File file) {
		byte[] encrypted = encrypt(data, key);
		IO.write(encrypted, file);
	}

	public void encryptToFile(String data, byte[] key, File file) {
		byte[] encrypted = encrypt(data, key);
		IO.write(encrypted, file);
	}

	public byte[] generateKey() {
		KeyGenerator kgen;
		try {
			kgen = KeyGenerator.getInstance(algorithm);
		} catch (NoSuchAlgorithmException ex) {
			throw new RuntimeException(ex);
		}
		kgen.init(keyLength);
		SecretKey skey = kgen.generateKey();
		return skey.getEncoded();
	}

	public byte[] createKeyFromPassword(String password) {
		SecretKeyFactory factory;
		try {
			factory = SecretKeyFactory.getInstance(keyAlgorithm);
		} catch (NoSuchAlgorithmException ex1) {
			throw new RuntimeException(ex1);
		}
		KeySpec spec = new PBEKeySpec(password.toCharArray(), keySalt, keyIterationCount, keyLength);
		SecretKey tmp;
		try {
			tmp = factory.generateSecret(spec);
		} catch (InvalidKeySpecException ex) {
			throw new RuntimeException(ex);
		}
		SecretKey secret = new SecretKeySpec(tmp.getEncoded(), algorithm);
		return secret.getEncoded();
	}

	public static class DecryptionFailedException extends Exception {

		public DecryptionFailedException(Exception cause) {
			super(cause);
		}
	}

}
