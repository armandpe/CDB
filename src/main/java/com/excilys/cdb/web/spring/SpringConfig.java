package com.excilys.cdb.web.spring;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.excilys.cdb.model.Company;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.pagemanager.PageManagerComplete;
import com.excilys.cdb.service.CompanyService;
import com.excilys.cdb.service.ComputerService;

@ComponentScan("com.excilys.cdb") 
@PropertySource(value = "classpath:connection.properties")
public class SpringConfig {

	@Autowired
	private Environment environment;

	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(environment.getRequiredProperty("driver"));
		dataSource.setUrl(environment.getRequiredProperty("url"));
		dataSource.setUsername(environment.getRequiredProperty("login"));
		dataSource.setPassword(environment.getRequiredProperty("password"));
		return dataSource;
	}

	@Bean(name = "companyPageManager")
	public PageManagerComplete<Company> getCompanyPageManager(CompanyService service) {
		return new PageManagerComplete<>(service);
	}

	@Bean(name = "computerPageManager")
	public PageManagerComplete<Computer> getComputerPageManager(ComputerService service) {
		return new PageManagerComplete<>(service);
	}

	@Bean
	public JdbcTemplate jdbcTemplate(DataSource dataSource) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.setResultsMapCaseInsensitive(true);
		return jdbcTemplate;
	}

}
