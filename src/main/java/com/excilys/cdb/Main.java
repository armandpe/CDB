package main.java.com.excilys.cdb;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.java.com.excilys.cdb.ihm.CLI;


public class Main {

	static final Logger LOGGER = LogManager.getLogger(Main.class);

	public static String getMethodName() {
		final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		return ste[2].getMethodName();
	}

	public static void main(String[] args) {
		CLI.start();
	}
}
