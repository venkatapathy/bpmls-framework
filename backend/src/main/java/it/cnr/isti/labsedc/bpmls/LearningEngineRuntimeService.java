package it.cnr.isti.labsedc.bpmls;

import java.util.List;

import org.camunda.bpm.engine.task.Task;

import it.cnr.isti.labsedc.bpmls.Exceptions.LearningPathException;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningPath;
import it.cnr.isti.labsedc.bpmls.persistance.LearningPathInstance;
import it.cnr.isti.labsedc.bpmls.persistance.LearningScenarioInstance;




public interface LearningEngineRuntimeService {
	/**
	 * This will start a given learning path. Throws a LearningPathException
	 * if the learningpath is already started
	 * @param learningPath
	 */
	public void startaLearningPathById(String learningPathId)throws LearningPathException;
	
	/**gets the current learning path as Learningpath instances
	 * 
	 * @return {@link List} of {@link LearningPathInstance}
	 */
	public List<LearningPathInstance> getRunningLearningPaths();
	
	
	
	public LearningPathInstance getRunningLearningPathBylpId(String lpInstId);
	
	public LearningScenarioInstance getNextLearningScenarioByLpInstId(String lpInstId);
	
	public LearningScenarioInstance getRunningLearningScenarioByIpInstId(String lpInstId);
	
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
