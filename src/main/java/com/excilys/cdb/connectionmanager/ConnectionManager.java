package main.java.com.excilys.cdb.connectionmanager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariProxyConnection;

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
	
	private HikariProxyConnection connection;
	private HikariConfig config = new HikariConfig();
	private HikariDataSource dsConnectionPool;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private ConnectionManager() { 
		ResourceBundle bundle = ResourceBundle.getBundle("connection");
        login = bundle.getString("login");
        password = bundle.getString("password");
        url = bundle.getString("url");
        
        config.setJdbcUrl(url);
        config.setPassword(password);
        config.setUsername(login);
        config.setMaximumPoolSize(100);
        
        try {
			Class.forName(bundle.getString("driver"));
		} catch (ClassNotFoundException e) {
			logger.error(Main.getErrorMessage("Driver loading failed", e.getMessage()));
		}
        
        dsConnectionPool = new HikariDataSource(config);
	}

	public Connection getConnection() {
		try {
			connection = (HikariProxyConnection) dsConnectionPool.getConnection();
		} catch (SQLException e) {
			logger.error(Main.getErrorMessage("connection to database failed", e.getMessage()));
		}
		return connection;
	}

}
