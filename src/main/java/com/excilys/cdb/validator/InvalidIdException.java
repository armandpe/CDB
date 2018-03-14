package com.excilys.cdb.validator;

@SuppressWarnings("serial")
public class InvalidIdException extends InvalidInputException {

	public InvalidIdException(String message) {
		super("" + InvalidIdException.class.getSimpleName() + (message == null ? "" : (" : " + message)));
	}
	
	
}
