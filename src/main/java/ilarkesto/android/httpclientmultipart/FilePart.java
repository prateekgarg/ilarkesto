package ilarkesto.android.httpclientmultipart;

import ilarkesto.core.base.Str;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.protocol.HTTP;

public final class FilePart extends BasePart {

	private final File file;

	public FilePart(String name, File file, String filename, String contentType) {
		if (file == null) throw new IllegalArgumentException("file == null");
		if (name == null) throw new IllegalArgumentException("name == null");

		this.file = file;
		final String partName = Str.encodeUrlParameter(name);
		final String partFilename = Str.encodeUrlParameter((filename == null) ? file.getName() : filename);
		final String partContentType = (contentType == null) ? HTTP.DEFAULT_CONTENT_TYPE : contentType;

		headersProvider = new IHeadersProvider() {

			@Override
			public String getContentDisposition() {
				return "Content-Disposition: form-data; name=\"" + partName + "\"; filename=\"" + partFilename + '"';
			}

			@Override
			public String getContentType() {
				return "Content-Type: " + partContentType;
			}

			@Override
			public String getContentTransferEncoding() {
				return "Content-Transfer-Encoding: binary"; //$NON-NLS-1$
			}
		};
	}

	@Override
	public long getContentLength(Boundary boundary) {
		return getHeader(boundary).length + file.length() + CRLF.length;
	}

	@Override
	public void writeTo(OutputStream out, Boundary boundary) throws IOException {
		out.write(getHeader(boundary));
		InputStream in = new FileInputStream(file);
		try {
			byte[] tmp = new byte[4096];
			int l;
			while ((l = in.read(tmp)) != -1) {
				out.write(tmp, 0, l);
			}
		} finally {
			in.close();
		}
		out.write(CRLF);
	}
}
