package ilarkesto.core.base;

public class UserInputException extends Exception implements WrapperException {

	public UserInputException(String msg) {
		super(msg);
	}

}
