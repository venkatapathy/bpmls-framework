package it.cnr.isti.labsedc.bpmls.tests;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.cnr.isti.labsedc.bpmls.BpmlsApp;
import it.cnr.isti.labsedc.bpmls.LearningProcessEngine;

@RunWith(SpringJUnit4ClassRunner.class)

public class BpmlsAppTest {


	
	@Test
	public void contextLoads() {
		String str = "Junit is working fine";
		assertThat(str).isEqualTo("Junit is working fine");
	}
	
	
}
