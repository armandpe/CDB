package main.java.com.excilys.cdb.connectionmanager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.java.com.excilys.cdb.Main;


public class ConnectionManager {

	private static ConnectionManager connectionManager;
	private static String login;
	private static String password;
	private static String url;
	public static ConnectionManager getInstance() {
		if (connectionManager == null) {
			connectionManager = new ConnectionManager();
		}
		return connectionManager;
	}
	private Connection connection;
	
	final Logger logger = LogManager.getLogger(this.getClass());

	private ConnectionManager() { 
		ResourceBundle bundle = ResourceBundle.getBundle("connection");
        login = bundle.getString("login");
        password = bundle.getString("password");
        url = bundle.getString("url");
        try {
			Class.forName(bundle.getString("driver"));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
