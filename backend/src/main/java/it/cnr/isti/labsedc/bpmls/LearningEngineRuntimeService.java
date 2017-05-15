package it.cnr.isti.labsedc.bpmls;

import java.util.List;

import org.camunda.bpm.engine.task.Task;

import it.cnr.isti.labsedc.bpmls.Exceptions.LearningPathException;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningPath;
import it.cnr.isti.labsedc.bpmls.persistance.LearningPathInstance;
import it.cnr.isti.labsedc.bpmls.persistance.LearningScenarioInstance;




public interface LearningEngineRuntimeService {
	
	/**
	 * This will start a given learning path.
	 * @param learningPathId ID of the learning path we wanted to start
	 * @throws LearningPathException If ID is not found or if a Learning path already running
	 */
	public LearningPathInstance startaLearningPathById(String learningPathId)throws LearningPathException;
	
	
	/**
	 * gets the current running learning path as Learningpath instances
	 * @return A {@link List} of {@link LearningPathInstance} or null if there is none
	 */
	public List<LearningPathInstance> getRunningLearningPaths();
	
	
	/**
	 * gets the current running learning path as Learningpath instance by id
	 * @param lpInstId the id of the lp instance that we want
	 * @return the corresponding {@link LearningPathInstance} or null if there is none
	 */
	public LearningPathInstance getRunningLearningPathBylpId(String lpInstId);
	
	/**
	 * Gets the next learning scenario that we need to run given by the lpInstId
	 * @param lpInstId the id of the lp instance for which we want the learning scenario
	 * @return {@link LearningScenarioInstance} that needs to be executed next for learning or null if there is none
	 */
	public LearningScenarioInstance getNextLearningScenarioByLpInstId(String lpInstId);
	
	/**
	 * Gets the running learning scenario given by the lpInstId
	 * @param lpInstId the id of the lp instance for which we want the learning scenario
	 * @return {@link LearningScenarioInstance} that is executed now for learning or null if there is none
	 */
	public LearningScenarioInstance getRunningLearningScenarioByIpInstId(String lpInstId);
	
	/**
	 * Start the {@link #getNextLearningScenarioByLpInstId(String)}} 
	 * @param lpInstId the id of the lp instance for which we want to start the learning scenario
	 * @throws LearningPathException Throws an exception if Lp not found, or if 
	 * a Learning Scenario is already running or if there no more Learning Scenario to run
	 */
	public void startNextLearningScenario(String lpInstId) throws LearningPathException;
		
//	/**
//	 * Returns a learning path instance given its instance id
//	 * @param lpInstId the id of the Learningpath instance as String
//	 * @return the {@link LearningPathInstance} if found. null otherwise
//	 */
//	public LearningPathInstance getRunningLearningPathById(String lpInstId)throws LearningPathException;
//	
//	
//	public LearningScenarioInstance getRunningLearningScenarioByLpId(String lpInstId)throws LearningPathException;
//	
//	
//	public void startNextLearningScenario(LearningPathInstance lpInstance) throws LearningPathException;
}
