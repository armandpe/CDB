package main.java.com.excilys.cdb;

import org.apache.log4j.Logger;

import main.java.com.excilys.cdb.ihm.CLI;


public class Main {

	static final Logger LOGGER = Logger.getLogger(Main.class);

	public static void main(String[] args) {
		CLI.start();
	}

	public static String getMethodName() {
		final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		return ste[2].getMethodName();
	}
}
