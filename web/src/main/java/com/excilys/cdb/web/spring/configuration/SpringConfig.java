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

			public void addTransformer(ClassTransformer arg0) {
				// TODO Auto-generated method stub
			}

			public ClassLoader getNewTempClassLoader() {
				// TODO Auto-generated method stub
				return null;
			}

			public Properties getProperties() {
				return props;
			}

			public List<String> getManagedClassNames() {
				return Arrays.asList(Company.class.getName(), Computer.class.getName());
			}

			public String getPersistenceUnitName() {
				return "TestUnit";
			}

			public String getPersistenceProviderClassName() {
				return HibernatePersistenceProvider.class.getName();
			}

			public PersistenceUnitTransactionType getTransactionType() {
				return null;
			}

			public DataSource getJtaDataSource() {
				return null;
			}

			public DataSource getNonJtaDataSource() {
				return null;
			}

			public List<String> getMappingFileNames() {
				return null;
			}

			public List<URL> getJarFileUrls() {
				return null;
			}

			public URL getPersistenceUnitRootUrl() {
				return null;
			}

			public boolean excludeUnlistedClasses() {
				return false;
			}

			public ClassLoader getClassLoader() {
				return null;
			}

			@Override
			public String getPersistenceXMLSchemaVersion() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public SharedCacheMode getSharedCacheMode() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public ValidationMode getValidationMode() {
				// TODO Auto-generated method stub
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
