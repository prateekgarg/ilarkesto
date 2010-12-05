package ilarkesto.auth;

import ilarkesto.base.PermissionDeniedException;

public class AuthenticationFailedException extends PermissionDeniedException {

	public AuthenticationFailedException(String message) {
		super(message);
	}

	public AuthenticationFailedException() {
		super("Authentication failed.");
	}

}
