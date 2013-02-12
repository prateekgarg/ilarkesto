package ilarkesto.android.httpclientmultipart;

import java.util.Random;

import org.apache.http.util.EncodingUtils;

import android.text.TextUtils;

class Boundary {

	private final static char[] MULTIPART_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray(); //$NON-NLS-1$

	private final String boundary;
	private final byte[] startingBoundary;
	private final byte[] closingBoundary;

	Boundary(String boundary) {
		if (TextUtils.isEmpty(boundary)) {
			boundary = generateBoundary();
		}
		this.boundary = boundary;

		final String starting = "--" + boundary + MultipartEntity.CRLF; //$NON-NLS-1$
		final String closing = "--" + boundary + "--" + MultipartEntity.CRLF; //$NON-NLS-1$

		startingBoundary = EncodingUtils.getAsciiBytes(starting);
		closingBoundary = EncodingUtils.getAsciiBytes(closing);
	}

	String getBoundary() {
		return boundary;
	}

	byte[] getStartingBoundary() {
		return startingBoundary;
	}

	byte[] getClosingBoundary() {
		return closingBoundary;
	}

	private static String generateBoundary() {
		Random rand = new Random();
		final int count = rand.nextInt(11) + 30;
		StringBuilder buffer = new StringBuilder(count);
		for (int i = 0; i < count; i++) {
			buffer.append(MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)]);
		}
		return buffer.toString();
	}
}