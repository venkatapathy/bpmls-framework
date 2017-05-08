package it.cnr.isti.labsedc.bpmls;

import java.util.List;

import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningPath;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningPathException;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenario;

public interface LearningEngineRepositoryService {

	/**gets the current learning path as Learningpath class
	 * 
	 * @return {@link List} of {@link LearningPath}
	 */
	public List<LearningPath> getDeployedLearningPaths();
	
	public LearningPath getDeployedLearningPath(String lpId) throws LearningPathException;
	
	public LearningScenario getDeployedLearningScenario(String lsId)throws LearningPathException;
	
}
