package it.cnr.isti.labsedc.bpmls;

import java.util.List;

import org.camunda.bpm.engine.task.Task;

import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningPath;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningPathException;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningPathInstance;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenario;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenarioInstance;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenario.ValuationOracle.ValuationFunction.DataObject;

/**
 * 
 * @author venkat
 *
 */
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
	
	/**gets the current learning path as Learningpath instances
	 * 
	 * @return {@link List} of {@link LearningPathInstance}
	 */
	public List<LearningPathInstance> getRunningLearningPaths();
	
	/**gets the current learning path as Learningpath class
	 * 
	 * @return {@link List} of {@link LearningPath}
	 */
	public List<LearningPath> getDeployedLearningPaths();
	
	/**
	 * Returns a learning path instance given its instance id
	 * @param lpInstId the id of the Learningpath instance as String
	 * @return the {@link LearningPathInstance} if found. null otherwise
	 */
	public LearningPathInstance getRunningLearningPath(String lpInstId);
}
