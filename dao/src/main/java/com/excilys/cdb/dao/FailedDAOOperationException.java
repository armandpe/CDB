package com.excilys.cdb.dao;

@SuppressWarnings("serial")
public class FailedDAOOperationException extends Exception {

	private static String className = FailedDAOOperationException.class.getSimpleName(); 
	
	private String message = null;
	
	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return className + ((message == null) ? "" : " - " + message);
	}

}
