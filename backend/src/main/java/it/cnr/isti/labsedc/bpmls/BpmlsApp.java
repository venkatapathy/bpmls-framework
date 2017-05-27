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

import it.cnr.isti.labsedc.bpmls.persistance.LearnerDetails;
import it.cnr.isti.labsedc.bpmls.persistance.LearnerDetailsJpaRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication

public class BpmlsApp{
	
	@Autowired
	LearnerDetailsJpaRepository lRepo;
	
	
	public static void main(String[] args) {
		
		SpringApplication.run(BpmlsApp.class, args);
		
		//
		
	}
	
	
	
	@PostConstruct
    public void init(){
		System.out.println("Entering the Application");
		LearnerDetails newuser = new LearnerDetails("venkat.s.iyer@gmail.com", "demo");

		lRepo.save(newuser);

		System.out.println("Registered new user: venkat.s.iyer@gmail.com");
		

	}
	
	
}
