package com.excilys.cdb.web.spring.configuration;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.excilys.cdb.model.Company;
import com.excilys.cdb.model.Computer;

@ComponentScan(basePackages = { "com.excilys.cdb" }) 
@PropertySource(value = "classpath:connection.properties")
@EnableTransactionManagement(proxyTargetClass = true)
public class SpringConfig {

	private Environment environment;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private SpringConfig(Environment environment) {
		this.environment = environment;
	}

	@Bean
    EntityManagerFactory entityManagerFactory() {
		Properties props = new Properties();
		props.put("hibernate.connection.driver_class", environment.getRequiredProperty("driver"));
		props.put("hibernate.connection.url", environment.getRequiredProperty("url"));
		props.put("hibernate.connection.username", environment.getRequiredProperty("login"));
		props.put("hibernate.connection.password", environment.getRequiredProperty("password"));

		PersistenceUnitInfo persistenceUnitInfo = new PersistenceUnitInfo() {

			@Override
			public void addTransformer(ClassTransformer arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public ClassLoader getNewTempClassLoader() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Properties getProperties() {
				return props;
			}

			@Override
			public List<String> getManagedClassNames() {
				return Arrays.asList(Company.class.getName(), Computer.class.getName());
			}

			@Override
			public String getPersistenceUnitName() {
				return "TestUnit";
			}

			@Override
			public String getPersistenceProviderClassName() {
				return HibernatePersistenceProvider.class.getName();
			}

			@Override
			public PersistenceUnitTransactionType getTransactionType() {
				return null;
			}

			@Override
			public DataSource getJtaDataSource() {
				return null;
			}

			@Override
			public DataSource getNonJtaDataSource() {
				return null;
			}

			@Override
			public List<String> getMappingFileNames() {
				return null;
			}

			@Override
			public List<URL> getJarFileUrls() {
				return null;
			}

			@Override
			public URL getPersistenceUnitRootUrl() {
				return null;
			}

			@Override
			public boolean excludeUnlistedClasses() {
				return false;
			}

			@Override
			public SharedCacheMode getSharedCacheMode() {
				return null;
			}

			@Override
			public ValidationMode getValidationMode() {
				return null;
			}

			@Override
			public String getPersistenceXMLSchemaVersion() {
				return null;
			}

			@Override
			public ClassLoader getClassLoader() {
				return null;
			}
		};

		HibernatePersistenceProvider hibernatePersistenceProvider = new HibernatePersistenceProvider();

        return hibernatePersistenceProvider
                .createContainerEntityManagerFactory(persistenceUnitInfo, Collections.EMPTY_MAP);
	}

	@Bean
	public EntityManager entityManager() {
		return entityManagerFactory().createEntityManager();
	}

	@Bean
	public PlatformTransactionManager transactionManager() {
        return new JpaTransactionManager(entityManagerFactory());
	}
}
