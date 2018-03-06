package main.java.com.excilys.cdb.validator;

public class InvalidDateException extends Exception {

	public InvalidDateException(String message) {
		super("" + InvalidDateException.class.getSimpleName() + (message == null ? "" : (" : " + message)));
	}
	
}