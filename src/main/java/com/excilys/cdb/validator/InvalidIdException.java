package main.java.com.excilys.cdb.validator;

public class InvalidIdException extends Exception {

	public InvalidIdException(String message) {
		super("" + InvalidIdException.class.getSimpleName() + (message == null ? "" : (" : " + message)));
	}
	
	
}