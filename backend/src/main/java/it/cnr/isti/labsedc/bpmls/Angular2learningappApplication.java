package it.cnr.isti.labsedc.bpmls;

import javax.annotation.PostConstruct;

import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
@EnableProcessApplication
public class Angular2learningappApplication{
	
	@Autowired
    JdbcTemplate jdbcTemplate;
	
	
	public static void main(String[] args) {
		
		SpringApplication.run(Angular2learningappApplication.class, args);
		
		//
		
	}
	
	
	
	@PostConstruct
    public void init(){
		System.out.println("Creating learningscenarioinstance table");

        jdbcTemplate.execute("DROP TABLE learningscenarioinstance IF EXISTS");
        jdbcTemplate.execute("CREATE TABLE learningscenarioinstance(" +
                "lsinstid SERIAL, learningscenarioid VARCHAR(255), processinstaneid VARCHAR(255))");

       
	}
}
