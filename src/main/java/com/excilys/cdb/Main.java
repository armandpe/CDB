package com.excilys.cdb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.excilys.cdb.ihm.CLI;
import com.excilys.cdb.web.spring.SpringConfiguration;


public class Main {

	static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

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
		CLI cli;
		try (AnnotationConfigApplicationContext vApplicationContext = new AnnotationConfigApplicationContext(SpringConfiguration.class)) {
			cli = vApplicationContext.getBean(CLI.class);
		}
		cli.start();	
	}
	
	private static String getCallerCallerClassName() { 
		StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
		String callerClassName = null;
		for (int i = 1; i < stElements.length; i++) {
			StackTraceElement ste = stElements[i];
			if (!ste.getClassName().equals(Main.class.getName()) && ste.getClassName().indexOf("java.lang.Thread") != 0) {
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
