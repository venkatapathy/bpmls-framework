package it.cnr.isti.labsedc.bpmls;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


public interface LearningProcessEngineController {
	
	/**
	 * TODO: per user Returns a JSON. {status:error,errortype: ,htmlform:dynamic
	 * content} 1. Status is error and errortype is 'lpnonexistant',if the given
	 * Learning Path if not running, in which case the client app needs to
	 * redirect the page <br>
	 * 2. No Learning scenario in progress but next in line present, status is
	 * success with a form to start the next Learning Scenario <br>
	 * 3. No Learning scenario in progress and no next in line present, status
	 * is error and errortype is 'nonextls' because this state shouldnt be
	 * reached <br>
	 * 4. Learning Scenario present and no task present, status is error and
	 * errortype is 'notask' because this state shouldnt be reached <br>
	 * 5. Learning Scenario task and task present, status is success and the
	 * htmlform is the task form
	 */
	public String getCurrentLPStatus(String lpid);
	
	
	public String getAvailableLearningPaths();
	
	public String getRunningPaths();
	
	public String startalearningpath( String lpid);
	
	public String startalearningscenario(String lpid,String lpinstid);
	
	public String completeCurrentLearningTask(String lpid, String responseJSON)throws Exception;
	
	public String getLearningPathFlowDiagram(String lpid);
	
	public String getProcessDiagramDetails(String lpid);
}
