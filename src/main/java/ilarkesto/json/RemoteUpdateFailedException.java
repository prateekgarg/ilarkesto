package ilarkesto.json;

public class RemoteUpdateFailedException extends RuntimeException {

	public RemoteUpdateFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public RemoteUpdateFailedException(String message) {
		super(message);
	}

}
