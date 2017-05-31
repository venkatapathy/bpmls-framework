package it.cnr.isti.labsedc.bpmls;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import it.cnr.isti.labsedc.bpmls.persistance.LearnerDetails;
import it.cnr.isti.labsedc.bpmls.persistance.LearnerDetailsJpaRepository;

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
