package com.excilys.cdb.dao.connectionmanager;

import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.excilys.cdb.Main;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariProxyConnection;

@Component
public class ConnectionManager {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private HikariDataSource dsConnectionPool; 
	
	public Connection getConnection() {
		
		Connection connection = null;
		try {
			connection = (HikariProxyConnection) dsConnectionPool.getConnection();
		} catch (SQLException e) {
			logger.error(Main.getErrorMessage("connection to database failed", e.getMessage()));
		}
		return connection;
	}

}
