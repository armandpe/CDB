package com.excilys.cdb.web.validator;

@SuppressWarnings("serial")
public class InvalidNameException extends InvalidInputException {
	
	public InvalidNameException(String message) {
		super("" + InvalidNameException.class.getSimpleName() + (message == null ? "" : (" : " + message)));
	}
}
