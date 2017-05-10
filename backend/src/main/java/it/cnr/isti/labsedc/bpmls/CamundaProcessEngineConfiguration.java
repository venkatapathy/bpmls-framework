package it.cnr.isti.labsedc.bpmls;

import javax.sql.DataSource;

import org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration;
import org.h2.server.web.WebServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.transaction.PlatformTransactionManager;

//removed @configuration cause there is soem problem
public class CamundaProcessEngineConfiguration {

	@Bean
	public DataSource dataSource() {
		// Use a JNDI data source or read the properties from
		// env or a properties file.
		// Note: The following shows only a simple data source
		// for In-Memory H2 database.

		SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
		dataSource.setDriverClass(org.h2.Driver.class);
		dataSource.setUrl("jdbc:h2:mem:camunda;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
		dataSource.setUsername("sa");
		dataSource.setPassword("");
		return dataSource;
	}

	/*@Bean
	public ServletRegistrationBean h2servletRegistration() {
	    ServletRegistrationBean registration = new ServletRegistrationBean(new WebServlet());
	    registration.addUrlMappings("/console/");
	    return registration;
	}*/
	
	@Bean
	public PlatformTransactionManager transactionManager() {
		return new DataSourceTransactionManager(dataSource());
	}

	@Bean
	public SpringProcessEngineConfiguration processEngineConfiguration() {
		SpringProcessEngineConfiguration config = new SpringProcessEngineConfiguration();

		config.setAuthorizationEnabled(true);
		config.setDataSource(dataSource());
		config.setTransactionManager(transactionManager());

		config.setDatabaseSchemaUpdate("true");
		config.setHistory("audit");
		config.setJobExecutorActivate(true);

		return config;
	}

	

	// more engine services and additional beans ...

}