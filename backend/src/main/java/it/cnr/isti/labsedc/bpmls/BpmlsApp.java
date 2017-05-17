package it.cnr.isti.labsedc.bpmls;

import javax.annotation.PostConstruct;

import org.camunda.bpm.application.PostDeploy;
import org.camunda.bpm.application.ProcessApplication;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.extension.reactor.CamundaReactor;
import org.camunda.bpm.extension.reactor.bus.CamundaEventBus;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication

public class BpmlsApp{
	
	
	
	
	public static void main(String[] args) {
		
		SpringApplication.run(BpmlsApp.class, args);
		
		//
		
	}
	
	
	
	@PostConstruct
    public void init(){
		System.out.println("Entering the Application");
		
		

	}
	
	
}
