package main.java.com.excilys.cdb.validator;

public class InvalidInputException extends Exception {

	public InvalidInputException(String message) {
		super("" + InvalidInputException.class.getSimpleName() + (message == null ? "" : (" : " + message)));
	}
	
}
