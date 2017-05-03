package it.cnr.isti.labsedc.bpmls;

import java.util.List;

import org.camunda.bpm.engine.task.Task;

import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningPath;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningPathException;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningPathInstance;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenario;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenarioInstance;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenario.ValuationOracle.ValuationFunction.DataObject;

public interface LearningProcessEngine {
	/*public List<LearningScenario> getDeployedLearningScenarios();

	public LearningScenarioInstance startLearningScenario(LearningScenario learningScenario);

	public LearningScenario getLearningScenario(String learningScenarioId);

	public LearningScenarioInstance getLearningScenarioInstance(String learningScenarioInstanceId);

	public Task getCurrentLearningTask(LearningScenarioInstance learningScenarioInstance);

	public void completeLearningTask(LearningScenarioInstance learningScenarioInstance,
			List<DataObject> userSubmittedDOs);
			
	*/
	
	/**
	 * This will start a given learning path. Throws a LearningPathException
	 * if the learningpath is already started
	 * @param learningPath
	 */
	public void startaLearningPath(LearningPath learningPath) throws LearningPathException;
	
	public List<LearningPathInstance> getRunningLearningPaths();
	
	public List<LearningPath> getDeployedLearningPaths();
	
	public LearningPathInstance getRunningLearningPath(String lpInstId);
}
