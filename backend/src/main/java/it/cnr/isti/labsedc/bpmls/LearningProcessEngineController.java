package it.cnr.isti.labsedc.bpmls;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


public interface LearningProcessEngineController {
	
	public String getCurrentLearningTask(String lpid);
	
	
	public String getAvailableLearningPaths();
	
	
	public String startalearningpath( String lpid);
	
	public String startalearningscenario(String lpid,String lpinstid);
	
	public String completeCurrentLearningTask(String lpid, String responseJSON)throws Exception;
}
