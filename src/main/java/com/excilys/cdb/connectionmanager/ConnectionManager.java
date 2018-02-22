package main.java.com.excilys.cdb.connectionmanager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import main.java.com.excilys.cdb.Main;


public class ConnectionManager {

	final Logger logger = Logger.getLogger(this.getClass());
	private static ConnectionManager connectionManager;
	private static String url;
	private static String login;
	private static String password;
	private Connection connection;
	
	private ConnectionManager() { 
		ResourceBundle bundle = ResourceBundle.getBundle("connection");
        login = bundle.getString("login");
        password = bundle.getString("password");
        url = bundle.getString("url");
	}

	public static ConnectionManager getInstance() {
		if (connectionManager == null) {
			connectionManager = new ConnectionManager();
		}
		return connectionManager;
	}

	public Connection getConnection() {
		try {
			connection = DriverManager.getConnection(url, login, password);
		} catch (SQLException e) {
			logger.log(Level.ERROR, "Error in method " + Main.getMethodName() + " : " + e.getMessage());
		}
		return connection;
	}

}
