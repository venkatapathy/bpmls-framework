package it.cnr.isti.labsedc.bpmls.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.cnr.isti.labsedc.bpmls.LearningProcessEngine;
import it.cnr.isti.labsedc.bpmls.LearningProcessEngineController;


@Component
public class LearningProcessEngineControllerImpl implements LearningProcessEngineController{
	
	@Autowired
	LearningProcessEngine lpEngine;
	
	public String getCurrentLearningTask(String lsinstid){
		//check if there any learning path engine for that
		return "I am Working";
	}
}
