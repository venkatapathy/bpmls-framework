package it.cnr.isti.labsedc.bpmls.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import it.cnr.isti.labsedc.bpmls.BpmlsApp;
import it.cnr.isti.labsedc.bpmls.LearningProcessEngine;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes= BpmlsApp.class)
public class BpmlsAppTest {


	@Autowired
	private LearningProcessEngine learningProcessEngine;
	
	@Test
	public void contextLoads() {
		String str = "Junit is working fine";
		assertThat(str).isEqualTo("Junit is working fine");
	}
	
	
}
