package it.cnr.isti.labsedc.bpmls.tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.camunda.bpm.engine.RuntimeService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.cnr.isti.labsedc.bpmls.BpmlsApp;
import it.cnr.isti.labsedc.bpmls.LearningProcessEngine;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningPath;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningPathException;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningPathInstance;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = BpmlsApp.class)
public class LearningPathTests {

	@Autowired
	private LearningProcessEngine learningProcessEngine;

	@Test
	public void testStartingLearningPath() throws LearningPathException{
		//there should be only one deployed learning process
		List<LearningPath> deployedLps=learningProcessEngine.getDeployedLearningPaths();
		Assert.assertEquals(1, deployedLps.size());
		
		//start a learningpath
		learningProcessEngine.startaLearningPath(deployedLps.get(0));
		
		List<LearningPathInstance> lpInsts=learningProcessEngine.getRunningLearningPaths();
		Assert.assertEquals(1, lpInsts.size());
		
		LearningPathInstance retLPInst=learningProcessEngine.getRunningLearningPath(lpInsts.get(0).getLpinstid());
		Assert.assertTrue(lpInsts.get(0).getLpinstid().equals(retLPInst.getLpinstid()));
	}
	
	@Test(expected=LearningPathException.class)
	public void testStartingLpTwice() throws LearningPathException{
		//there should be only one deployed learning process
				List<LearningPath> deployedLps=learningProcessEngine.getDeployedLearningPaths();
				
				
				//start a learningpath
				learningProcessEngine.startaLearningPath(deployedLps.get(0));
				
				learningProcessEngine.startaLearningPath(deployedLps.get(0));
	}

}
