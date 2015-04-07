package ilarkesto.net.httpclientmultipart;

import ilarkesto.core.base.Str;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.http.protocol.HTTP;

public final class StringPart extends BasePart {

	private final byte[] valueBytes;

	public StringPart(String name, String value, String charset) {
		if (name == null) throw new IllegalArgumentException("name == null");
		if (value == null) throw new IllegalArgumentException("value == null");

		final String partName = Str.encodeUrlParameter(name);

		if (charset == null) {
			charset = HTTP.DEFAULT_CONTENT_CHARSET;
		}
		final String partCharset = charset;

		try {
			this.valueBytes = value.getBytes(partCharset);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

		headersProvider = new IHeadersProvider() {

			@Override
			public String getContentDisposition() {
				return "Content-Disposition: form-data; name=\"" + partName + '"';
			}

			@Override
			public String getContentType() {
				return "Content-Type: " + HTTP.PLAIN_TEXT_TYPE + HTTP.CHARSET_PARAM + partCharset;
			}

			@Override
			public String getContentTransferEncoding() {
				return "Content-Transfer-Encoding: 8bit";
			}
		};
	}

	public StringPart(String name, String value) {
		this(name, value, null);
	}

	@Override
	public long getContentLength(Boundary boundary) {
		return getHeader(boundary).length + valueBytes.length + CRLF.length;
	}

	@Override
	public void writeTo(final OutputStream out, Boundary boundary) throws IOException {
		out.write(getHeader(boundary));
		out.write(valueBytes);
		out.write(CRLF);
	}
}