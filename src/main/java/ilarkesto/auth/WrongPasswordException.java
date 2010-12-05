package ilarkesto.auth;


public class WrongPasswordException extends AuthenticationFailedException {

	public WrongPasswordException() {
		super("Wrong password.");
	}

}
