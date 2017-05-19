package it.cnr.isti.labsedc.bpmls;

import java.io.File;
import java.util.List;

import com.sun.research.ws.wadl.Link;

import it.cnr.isti.labsedc.bpmls.Exceptions.LearningPathException;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningPath;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenario;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenario.ValuationOracle.ValuationFunction.DataObject;

public interface LearningEngineRepositoryService {

	/**
	 * Can be used deploy a learning path along with its BPMN and Learning Scenario programatically
	 * TODO: 
	 * Currently is left empty and hence cannot be used
	 * @param bpmnFile The BPMN xml File
	 * @param learningPathFile The LearningPath file
	 * @param learningScenariosFiles The learningscenarios file
	 * @throws LearningPathException Exception thrown if the files cannot be deployed
	 */
	public void deployLearningPath(File bpmnFile, File learningPathFile, File[] learningScenariosFiles) throws LearningPathException;
	
	/**gets the current deployed learning path
	 * 
	 * @return {@link List} of {@link LearningPath} Null if none is deployed
	 * @throws {@link LearningPathException}
	 */
	public List<LearningPath> getDeployedLearningPaths();
	
	/**
	 * Gets the deployed learning path given its Learning Path id.
	 * @param lpId the Id of the learning path that we want
	 * @return {@link LearningPath} That is deployed
	 * @throws LearningPathException  if Not found
	 */
	public LearningPath getDeployedLearningPath(String lpId) throws LearningPathException;
	
	/**
	 * Gets the deployed learning scenario given its Learning scenario id.
	 * @param lsId the Id of the learning scenario that we want
	 * @return {@link LearningScenario} That is deployed
	 * @throws LearningPathException if not found
	 */
	public LearningScenario getDeployedLearningScenario(String lsId)throws LearningPathException;
	
	
	public List<DataObject> getCurrentOracleValuesFromRepo(String lsId,String cur_bpmn_activityid)throws LearningPathException;
	
}
