package it.cnr.isti.labsedc.bpmls.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.cnr.isti.labsedc.bpmls.LearningProcessEngine;
import it.cnr.isti.labsedc.bpmls.LearningProcessEngineController;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningPath;
import it.cnr.isti.labsedc.bpmls.persistance.LearningPathInstance;


@Component
public class LearningProcessEngineControllerImpl implements LearningProcessEngineController{
	
	@Autowired
	LearningProcessEngine lpEngine;
	
	public String getCurrentLearningTask(String lsinstid){
		//check if there any learning path engine for that
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
}
