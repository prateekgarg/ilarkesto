package ilarkesto.webapp;

public class GwtConversationDoesNotExist extends RuntimeException {

	public GwtConversationDoesNotExist(int conversationNumber) {
		super(String.valueOf(conversationNumber));
	}

}
