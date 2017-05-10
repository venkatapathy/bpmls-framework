package it.cnr.isti.labsedc.bpmls.impl;

import java.util.Map;

import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.cnr.isti.labsedc.bpmls.LearningEngineRuntimeService;
import it.cnr.isti.labsedc.bpmls.LearningEngineTaskService;
import it.cnr.isti.labsedc.bpmls.Exceptions.LearningPathException;
import it.cnr.isti.labsedc.bpmls.Exceptions.LearningTaskException;
import it.cnr.isti.labsedc.bpmls.persistance.LearningScenarioInstance;

@Component
public class LearningEngineTaskServiceImpl implements LearningEngineTaskService{

	@Autowired
	TaskService taskServiceCamunda;
	
	@Autowired
	LearningEngineRuntimeService learningengineRuntimeService;
	
	public Task getCurrentLearningTask(String lpInstId){
		//get the current LearningScenarioInstance
		LearningScenarioInstance lsInst= learningengineRuntimeService.getRunningLearningScenarioByIpInstId(lpInstId);
		
		//if no running scenarios return null
		if(lsInst==null){
			return null;
		}
		
		
		//get its corresponding process instance and its task
		Task task = taskServiceCamunda.createTaskQuery().processInstanceId(lsInst.getProcessInstanceId()).singleResult();
		
		return task;
	}
	
	public void completeCurrentLearningTask(String lpInstId,Map<String, Object> taskInputs)throws LearningTaskException,LearningPathException{
		//check if the input is per the expected oracle values
		
		//if not collect the wrong inputs with msgs and throw an exception
		
		//if all is fine complete the task
		
		//dont end check for if the next user task is a learning task in this learning scenario context
		
		//complete all the upcoming user tasks that are not learnign tasks
		
		//done
	}
}
