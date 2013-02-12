package ilarkesto.android.httpclientmultipart;

import java.io.IOException;
import java.io.OutputStream;

public interface Part {

	long getContentLength(Boundary boundary);

	void writeTo(final OutputStream out, Boundary boundary) throws IOException;

}
