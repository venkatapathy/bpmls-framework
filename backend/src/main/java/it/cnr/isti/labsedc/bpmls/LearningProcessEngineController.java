package it.cnr.isti.labsedc.bpmls;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public interface LearningProcessEngineController {
	@CrossOrigin(origins = "http://localhost:4200")
	@RequestMapping(value = "/getcurrentlearningtask", method = RequestMethod.GET)
	public String getCurrentLearningTask(@RequestParam(value = "lpinstid") String lsinstid);
	
	@CrossOrigin(origins = "http://localhost:4200")
	@RequestMapping(value = "/getavailablelearningpaths", method = RequestMethod.GET)
	public String getAvailableLearningPaths();
	
	@CrossOrigin(origins = "http://localhost:4200")
	@RequestMapping(value = "/startalearningpath", method = RequestMethod.POST)
	public String startalearningpath(@RequestParam(value = "lpid") String lpid);
}
