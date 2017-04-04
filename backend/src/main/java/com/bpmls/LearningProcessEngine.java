package com.bpmls;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Main Class of BPMLS framework. It executes learning path, its corresponding learning scenarios,
 * initiates monitoring etc. Is a REST controller too
 * @author venkat
 *
 */
@RestController
public class LearningProcessEngine {
	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private TaskService taskService;
	
	private String processInstanceId=null;
	/**
	 * This gets the current task of a given learning scenarios. Currently hardcoded to get the current
	 * task of learningscenario1 for a given process instance with a particular key
	 * TODO: Need to make the function more dynamic
	 * @return
	 * @throws Exception
	 * TODO:
	 * include ways to get the currenttask based on a input parameter called
	 * learning scenario
	 */
	@RequestMapping(value = "/getcurrenttask", method = RequestMethod.GET)
	public String getCurrentLearningTask() throws Exception {
		if(processInstanceId==null){
			//TODO: 
			//Here the process instance should start with inital values from
			//the learningscenario.config
			processInstanceId = runtimeService.startProcessInstanceByKey("Process_1").getProcessInstanceId();
		}
		
		//get the current task
		Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
		System.out.println(task.getName());
		
		//shortcut, check if the file with taskname.scenarioname exist! if so send it back as 
		//check if the current task has a UI in the learningscenario config, else complete the task and move ahead
		
	}
}
