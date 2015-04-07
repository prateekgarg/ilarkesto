package ilarkesto.net.httpclientmultipart;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.entity.AbstractHttpEntity;

public class MultipartEntity extends AbstractHttpEntity implements Cloneable {

	static final String CRLF = "\r\n";

	private List<Part> parts = new ArrayList<Part>();

	private Boundary boundary;

	public MultipartEntity(String boundaryStr) {
		super();
		boundary = new Boundary(boundaryStr);
		setContentType("multipart/form-data; boundary=\"" + boundary.getBoundary() + '"');
	}

	public MultipartEntity() {
		this(null);
	}

	public void addPart(Part part) {
		parts.add(part);
	}

	@Override
	public boolean isRepeatable() {
		return true;
	}

	@Override
	public long getContentLength() {
		long result = 0;
		for (Part part : parts) {
			result += part.getContentLength(boundary);
		}
		result += boundary.getClosingBoundary().length;
		return result;
	}

	@Override
	public InputStream getContent() throws IOException {
		return null;
	}

	@Override
	public void writeTo(final OutputStream out) throws IOException {
		if (out == null) { throw new IllegalArgumentException("Output stream may not be null"); }
		for (Part part : parts) {
			part.writeTo(out, boundary);
		}
		out.write(boundary.getClosingBoundary());
		out.flush();
	}

	@Override
	public boolean isStreaming() {
		return false;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("MultipartEntity does not support cloning");
	}
}
