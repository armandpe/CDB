package com.excilys.cdb.dao;

import com.excilys.cdb.validator.InvalidDateException;

@SuppressWarnings("serial")
public class FailedDAOOperationException extends Exception {

	private static String className = InvalidDateException.class.getSimpleName(); 
	
	private String message = null;
	
	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return className + message == null ? "" : " - " + message;
	}

}
