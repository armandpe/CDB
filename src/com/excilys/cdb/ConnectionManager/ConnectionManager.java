package com.excilys.cdb.ConnectionManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.excilys.cdb.Main;


public class ConnectionManager {
	
	final Logger logger = Logger.getLogger(this.getClass());

	private static ConnectionManager connectionManager;
	private static String url = "jdbc:mysql://127.0.0.1:3306/computer-database-db?useSSL=false";
	private static String login = "admincdb";
	private static String password = "qwerty1234";
	private Connection connection;

	private ConnectionManager() {}

	public static ConnectionManager getInstance() {
		if(connectionManager == null)
			connectionManager = new ConnectionManager();

		return connectionManager;
	}

	public Connection getConnection() {
		
		try {
			connection = DriverManager.getConnection(url, login, password);
		} catch (SQLException e1) {
			final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
			String methodName = ste[1].getMethodName(); 
			logger.log(Level.ERROR, "Error in method " + methodName + " : " + e.getMessage());
		}
		
		return connection;
	}

	public void closeConnection() {
		try {
			Connection c = connection;
			if (c != null)
				c.close();
		} catch (SQLException e) {
			final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
			String methodName = ste[1].getMethodName(); 
			logger.log(Level.ERROR, "Error in method " + methodName + " : " + e.getMessage());
		}
	}

}
