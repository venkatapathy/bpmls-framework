package it.cnr.isti.labsedc.bpmls.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import it.cnr.isti.labsedc.bpmls.LearningProcessEngine;
import it.cnr.isti.labsedc.bpmls.LearningProcessEngineController;
import it.cnr.isti.labsedc.bpmls.Exceptions.LearningPathException;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningPath;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenario;
import it.cnr.isti.labsedc.bpmls.persistance.LearningPathInstance;
import it.cnr.isti.labsedc.bpmls.persistance.LearningScenarioInstance;


@Component
public class LearningProcessEngineControllerImpl implements LearningProcessEngineController{
	
	@Autowired
	LearningProcessEngine lpEngine;
	
	public String getCurrentLearningTask(String lpInstId){
		//check if there any learning path engine for that
		LearningPathInstance lpInst=lpEngine.getLearningEngineRuntimeService().getRunningLearningPathBylpId(lpInstId);
		
		if(lpInst==null){
			StringBuilder retMsg=new StringBuilder("{\"error\": {\"message\":\"No learning path running that you selected\"}}");
			return retMsg.toString();
		}
		
		LearningScenarioInstance lsInst=lpEngine.getLearningEngineRuntimeService().getRunningLearningScenarioByIpInstId(lpInstId);
		
		//if lsinst null then get the next Learning Scenario
		if(lsInst==null){
			lsInst=lpEngine.getLearningEngineRuntimeService().getNextLearningScenarioByLpInstId(lpInstId);
			
			if(lsInst==null){
				
			}
		}
		return "I am Working";
	}
	
	
	
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
	
	public String startalearningpath(String lpid){
		//StringBuilder retMsg=new StringBuilder("{\"error\": \"sample error\"}"); 
		
		
		//start the learning path
		try {
			LearningPathInstance lpInst= lpEngine.getLearningEngineRuntimeService().startaLearningPathById(lpid);
			StringBuilder retMsg=new StringBuilder("{\"success\": {\"lpinstid\":\""+lpInst.getLpInstId()+"\"}}");
			return retMsg.toString();
		} catch (LearningPathException e) {
			// TODO Auto-generated catch block
			StringBuilder retMsg=new StringBuilder("{\"error\": {\"message\":\""+e.getMessage()+"\"}}");
			return retMsg.toString();
			
		}
			
	}
}
