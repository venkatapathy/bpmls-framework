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
public class BpmlsApp{
	
	@Autowired
    JdbcTemplate jdbcTemplate;
	
	
	public static void main(String[] args) {
		
		SpringApplication.run(BpmlsApp.class, args);
		
		//
		
	}
	
	
	
	@PostConstruct
    public void init(){
		System.out.println("Creating learningscenarioinstance table");

		//table necessary for learning scenario instance
        jdbcTemplate.execute("DROP TABLE learningscenarioinstance IF EXISTS");
        jdbcTemplate.execute("CREATE TABLE learningscenarioinstance(" +
                "lsinstid SERIAL, learningscenarioid VARCHAR(255), processinstanceid VARCHAR(255))");

       
        //table necessar for learning path instance
        jdbcTemplate.execute("DROP TABLE learningpathinstance IF EXISTS");
        jdbcTemplate.execute("CREATE TABLE learningpathinstance(" +
                "lpinstid SERIAL, learningpathid VARCHAR(255), currentlsinst INTEGER(255))");

	}
}
