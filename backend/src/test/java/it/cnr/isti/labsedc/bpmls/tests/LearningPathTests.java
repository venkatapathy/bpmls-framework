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
import it.cnr.isti.labsedc.bpmls.persistance.LearningPathInstance;
import it.cnr.isti.labsedc.bpmls.persistance.LearningScenarioInstance;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = BpmlsApp.class)
public class LearningPathTests {

	@Autowired
	private LearningProcessEngine learningProcessEngine;

	@Test
	public void learningPathtest() throws LearningPathException {
		// there should be only one deployed learning process
		List<LearningPath> deployedLps = learningProcessEngine.getLearningEngineRepositoryService()
				.getDeployedLearningPaths();
		Assert.assertEquals(1, deployedLps.size());

		// start a learningpath
		learningProcessEngine.getLearningEngineRuntimeService().startaLearningPathById(deployedLps.get(0).getId());

		// check if only one running learning path is there
		Assert.assertEquals(1,
				learningProcessEngine.getLearningEngineRuntimeService().getRunningLearningPaths().size());

		// check if the id is the same
		Assert.assertEquals(deployedLps.get(0).getId(),
				learningProcessEngine.getLearningEngineRuntimeService().getRunningLearningPaths().get(0).getLpId());

	}

	@Test(expected = LearningPathException.class)
	public void startAnotherLearningProcessInstance() throws LearningPathException{
		// there should be only one deployed learning process
		List<LearningPath> deployedLps = learningProcessEngine.getLearningEngineRepositoryService()
				.getDeployedLearningPaths();

		// start a learningpath- will throw an exception
		learningProcessEngine.getLearningEngineRuntimeService().startaLearningPathById(deployedLps.get(0).getId());
	}

	@Test
	public void learningScenarioTest() throws LearningPathException {
		// get the running learningpath
		List<LearningPath> deployedLps = learningProcessEngine.getLearningEngineRepositoryService()
				.getDeployedLearningPaths();
		LearningPathInstance lpInst = learningProcessEngine.getLearningEngineRuntimeService()
				.getRunningLearningPathBylpId(deployedLps.get(0).getId());

		LearningScenarioInstance nextLsInst= learningProcessEngine.getLearningEngineRuntimeService()
				.getNextLearningScenarioByLpInstId(Integer.toString(lpInst.getLpInstId()));
		
		//Should be the first learningscenario
		Assert.assertEquals("learningscenario1", nextLsInst.getLsId());
		
		//since no learningscenario started should be null
		LearningScenarioInstance currentLsInst= learningProcessEngine.getLearningEngineRuntimeService()
				.getRunningLearningScenario(Integer.toString(lpInst.getLpInstId()));
		Assert.assertNull(currentLsInst);
	}

}
