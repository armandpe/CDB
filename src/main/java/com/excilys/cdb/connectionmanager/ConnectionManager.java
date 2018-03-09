package main.java.com.excilys.cdb.connectionmanager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	final Logger logger = LoggerFactory.getLogger(this.getClass());

	private ConnectionManager() { 
		ResourceBundle bundle = ResourceBundle.getBundle("connection");
        login = bundle.getString("login");
        password = bundle.getString("password");
        url = bundle.getString("url");
        try {
			Class.forName(bundle.getString("driver"));
		} catch (ClassNotFoundException e) {
			logger.error(Main.getErrorMessage(null, e.getMessage()));
		}
	}

	public Connection getConnection() {
		try {
			connection = DriverManager.getConnection(url, login, password);
		} catch (SQLException e) {
			logger.error(Main.getErrorMessage("connection to database failed", e.getMessage()));
		}
		return connection;
	}

}
