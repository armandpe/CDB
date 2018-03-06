package main.java.com.excilys.cdb.validator;

public class InvalidIdException extends InvalidInputException {

	public InvalidIdException(String message) {
		super("" + InvalidIdException.class.getSimpleName() + (message == null ? "" : (" : " + message)));
	}
	
	
}
