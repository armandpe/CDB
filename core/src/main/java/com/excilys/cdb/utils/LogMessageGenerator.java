package com.excilys.cdb.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogMessageGenerator {
	static final Logger LOGGER = LoggerFactory.getLogger(LogMessageGenerator.class);

	private static String getMethodName() {
		final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		return ste[3].getMethodName();
	}

	public static String getErrorMessage(String description, String errorMessage) {
		StringBuilder builder = new StringBuilder("Error in ");
		builder.append(getCallerCallerClassName());
		builder.append("::");
		builder.append(getMethodName());

		if (description != null || errorMessage != null) {
			builder.append(" : ");
		}
		
		if (description != null) { 
			builder.append(description);
			if (errorMessage != null) {
				builder.append(" - ");
			}
		}
		
		if (errorMessage != null) {
			builder.append(errorMessage);
		}

		return builder.toString();
	}

	public static void main(String[] args) {
	}
	
	private static String getCallerCallerClassName() { 
		StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
		String callerClassName = null;
		for (int i = 1; i < stElements.length; i++) {
			StackTraceElement ste = stElements[i];
			if (!ste.getClassName().equals(LogMessageGenerator.class.getName()) && ste.getClassName().indexOf("java.lang.Thread") != 0) {
				if (callerClassName == null) {
					callerClassName = ste.getClassName();
				} else if (!callerClassName.equals(ste.getClassName())) {
					return ste.getClassName();
				}
			}
		}
		return null;
	}
}
