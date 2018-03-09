package main.java.com.excilys.cdb.validator;

@SuppressWarnings("serial")
public class InvalidNameException extends InvalidInputException {
	
	public InvalidNameException(String message) {
		super("" + InvalidNameException.class.getSimpleName() + (message == null ? "" : (" : " + message)));
	}
}
