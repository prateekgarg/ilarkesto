package ilarkesto.gwt.client;

import ilarkesto.core.base.Str;
import ilarkesto.core.base.Utl;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ErrorWrapper implements Serializable, IsSerializable {

	private String name;
	private String message;

	public ErrorWrapper(String name, String message) {
		super();
		this.name = name;
		this.message = message;
	}

	public ErrorWrapper(Throwable ex) {
		this(ex.getClass().getName(), Str.formatException(ex));
	}

	private ErrorWrapper() {
		this("unknown error", null);
	}

	public String getName() {
		return name;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		if (message == null) return name;
		return name + ": " + message;
	}

	@Override
	public int hashCode() {
		int hash = name.hashCode();
		if (message != null) hash = hash * message.hashCode();
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ErrorWrapper)) return false;
		ErrorWrapper other = (ErrorWrapper) obj;
		return name.equals(other.name) && Utl.equals(message, other.message);
	}

}
