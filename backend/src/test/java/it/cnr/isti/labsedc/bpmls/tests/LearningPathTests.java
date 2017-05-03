package it.cnr.isti.labsedc.bpmls.tests;

import static org.assertj.core.api.Assertions.assertThat;

import org.camunda.bpm.engine.RuntimeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.cnr.isti.labsedc.bpmls.BpmlsApp;
import it.cnr.isti.labsedc.bpmls.LearningProcessEngine;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes= BpmlsApp.class)
public class LearningPathTests {
	
	  @Autowired
	  private RuntimeService runtimeService;
	
	 
	
	@Test
	public void testStartingLearningPath(){
		String str = "Junit is working fine";
		assertThat(str).isEqualTo("Junit is working fine");
		System.out.println("test working");
	}
	
	
}
