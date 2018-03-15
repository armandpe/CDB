package com.excilys.cdb.spring;

import java.util.ResourceBundle;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@ComponentScan("com.excilys.cdb") 
public class SpringConfiguration {

	@Bean
	public HikariDataSource dataSource() {
		ResourceBundle bundle = ResourceBundle.getBundle("connection");
		String url = bundle.getString("url");
		String login = bundle.getString("login");
		String password = bundle.getString("password");
		String driver = bundle.getString("driver");

		HikariConfig config = new HikariConfig();

        config.setJdbcUrl(url);
        config.setPassword(password);
        config.setUsername(login);
        config.setDriverClassName(driver);
		
		HikariDataSource dsConnectionPool = new HikariDataSource(config);

		return dsConnectionPool;
	}

}
