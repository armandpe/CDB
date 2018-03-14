package com.excilys.cdb.validator;

@SuppressWarnings("serial")
public class InvalidDateException extends InvalidInputException {

	public InvalidDateException(String message) {
		super("" + InvalidDateException.class.getSimpleName() + (message == null ? "" : (" : " + message)));
	}
	
}
