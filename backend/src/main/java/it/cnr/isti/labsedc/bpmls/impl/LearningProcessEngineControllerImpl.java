package it.cnr.isti.labsedc.bpmls.impl;

import java.util.List;

import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.cnr.isti.labsedc.bpmls.HtmlFormEngine;
import it.cnr.isti.labsedc.bpmls.LearningProcessEngine;
import it.cnr.isti.labsedc.bpmls.LearningProcessEngineController;
import it.cnr.isti.labsedc.bpmls.Exceptions.LearningPathException;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningPath;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenario;
import it.cnr.isti.labsedc.bpmls.persistance.LearningPathInstance;
import it.cnr.isti.labsedc.bpmls.persistance.LearningScenarioInstance;
import j2html.tags.ContainerTag;

import static j2html.TagCreator.*;

@Component
@RestController
public class LearningProcessEngineControllerImpl implements LearningProcessEngineController{
	private final Logger logger = LoggerFactory.getLogger(LearningProcessEngineImpl.class);
	@Autowired
	LearningProcessEngine lpEngine;
	
	@Autowired
	FormService formService;
	
	@CrossOrigin(origins = "http://localhost:4200")
	@RequestMapping(value = "/getcurrentlearningtask/{lpid}", method = RequestMethod.GET)
	public String getCurrentLearningTask(@PathVariable("lpid") String lpId){
		//check if there any learning path engine for that
		LearningPathInstance lpInst=lpEngine.getLearningEngineRuntimeService().getRunningLearningPathBylpId(lpId);
		
		if(lpInst==null){
			ContainerTag resMsg=div().with(text("Not a Learning Path. Please goto running learning path and select one from there"));
			return resMsg.toString();
		}
		
		LearningScenarioInstance lsInst=lpEngine.getLearningEngineRuntimeService().getRunningLearningScenarioByIpInstId(Integer.toString(lpInst.getLpInstId()));
		
		//if lsinst null then get the next Learning Scenario
		if(lsInst==null){
			lsInst=lpEngine.getLearningEngineRuntimeService().getNextLearningScenarioByLpInstId(Integer.toString(lpInst.getLpInstId()));
			
			//no more learning scenario
			if(lsInst==null){
				ContainerTag resMsg=div().with(text("No more learning Scenario. COngrats you completed this learnign path"));
				return resMsg.toString();
			}
			
			//display the details of the next learning scenario
			ContainerTag lsButton=button("Start Learing Scenario").attr("(click)", "startLs("+Integer.toString(lpInst.getLpInstId())+")").attr("class", "btn btn-primary");
			
			ContainerTag lsText=div().withText("Currently you dont have learning scenarion. Start one!").with(lsButton);
			System.out.println(lsText.render());
			return lsText.render();
			
		}
		Task task= lpEngine.getLearningEngineTaskService().getCurrentLearningTask(Integer.toString(lpInst.getLpInstId()));
		return new HtmlFormEngine().renderFormData(formService.getTaskFormData(task.getId()));
	}
	
	
	@CrossOrigin(origins = "http://localhost:4200")
	@RequestMapping(value = "/getavailablelearningpaths", method = RequestMethod.GET)
	public String getAvailableLearningPaths(){
		//first get the deployed learning paths
		StringBuilder availLPs=new StringBuilder("{\"alps\": [");
		List<LearningPath> lps= lpEngine.getLearningEngineRepositoryService().getDeployedLearningPaths();
		
		List<LearningPathInstance> lpInst=lpEngine.getLearningEngineRuntimeService().getRunningLearningPaths();
		
		if(lpInst!=null){
		for(LearningPath lp:lps){
			//if not already running
			if(lpEngine.getLearningEngineRuntimeService().getRunningLearningPathBylpId(lp.getId())==null){
				availLPs.append("{");
				availLPs.append("\"lpid\":\"");
				availLPs.append(lp.getId()+"\",");
				
				availLPs.append("\"lpname\":\"");
				availLPs.append(lp.getName()+"\"");
				availLPs.append("}");
			}
		}
		}
		availLPs.append(" ]}");
		
		return availLPs.toString();
		
	}
	
	
	@CrossOrigin(origins = "http://localhost:4200")
	@RequestMapping(value = "/startalearningpath/{lpid}", method = RequestMethod.POST)
	public String startalearningpath(@PathVariable("lpid") String lpid){
		//StringBuilder retMsg=new StringBuilder("{\"error\": \"sample error\"}"); 
		
		
		//start the learning path
		try {
			LearningPathInstance lpInst= lpEngine.getLearningEngineRuntimeService().startaLearningPathById(lpid);
			
			logger.info("Starting a Learning path: {\"success\": {\"lpid\":\""+lpInst.getLpInstId()+"\"}}");
			
			
			StringBuilder retMsg=new StringBuilder("{\"success\": {\"lpid\":\""+lpInst.getLpId()+"\"}}");
			return retMsg.toString();
		} catch (LearningPathException e) {
			logger.error("{\"error\": {\"message\":\""+e.getMessage()+"\"}}");
			// TODO Auto-generated catch block
			StringBuilder retMsg=new StringBuilder("{\"error\": {\"message\":\""+e.getMessage()+"\"}}");
			return retMsg.toString();
			
		}
			
	}
	
	@CrossOrigin(origins = "http://localhost:4200")
	@RequestMapping(value = "/startalearningscenario/{lpid}/{lpinstid}", method = RequestMethod.POST)
	public String startalearningscenario(@PathVariable("lpid") String lpid,@PathVariable("lpinstid") String lpinstid){
		
		
		try {
			lpEngine.getLearningEngineRuntimeService().startNextLearningScenario(lpinstid);
			logger.info("Starting next Learning Scenario");
			StringBuilder retMsg=new StringBuilder("{\"success\": {\"lpid\":\""+lpid+"\"}}");
			return retMsg.toString();
		} catch (LearningPathException e) {
			// TODO Auto-generated catch block
			logger.error("Error in starting next LS");
			StringBuilder retMsg=new StringBuilder("{\"error\": {\"message\":\""+e.getMessage()+"\"}}");
			return retMsg.toString();
		}
	}
}
