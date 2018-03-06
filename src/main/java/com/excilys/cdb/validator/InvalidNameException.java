package main.java.com.excilys.cdb.validator;

public class InvalidNameException extends Exception {
	
	public InvalidNameException(String message) {
		super("" + InvalidNameException.class.getSimpleName() + (message == null ? "" : (" : " + message)));
	}
}