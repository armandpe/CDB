package com.excilys.cdb;

import org.apache.log4j.Logger;

import com.excilys.cdb.ihm.CLI;


public class Main {

	final static Logger logger = Logger.getLogger(Main.class);

	public static void main(String[] args) {
		CLI.start();
	}

	public static String getMethodName() {
		final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		return ste[2].getMethodName();
	}
}
