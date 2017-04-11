package it.cnr.isti.labsedc.bpmls;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import it.cnr.isti.labsedc.bpmls.senarios.LearningScenario;

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
	
	private static String LEARNINGSCENARIO="learningscenario1";
	
	/**
	 * Given a learning scenario, it start the scenario runs each task and finishes it
	 */
	private void testLearningScenario(LearningScenario learningScenario){
		//get the initial values
		Map<String, Object> variables = new HashMap<String, Object>();
		
		Iterator<LearningScenario.InitialValuation.DataObject> i=learningScenario.getInitialValuation().getDataObject().iterator();
		while(i.hasNext()){
			LearningScenario.InitialValuation.DataObject dataObject=(LearningScenario.InitialValuation.DataObject)i.next();
			variables.put(dataObject.getCamundaid(), dataObject.getValue());
		}
		
		//start the learning scenario which is the bpmn process
		System.out.println("starting the learning scenario and is associated bpmn process");
		System.out.println("initial variables initated are");
		for (String key : variables.keySet()) {
		    System.out.println(key + " " + variables.get(key));
		}
		
		String processinstanceId=runtimeService.startProcessInstanceByKey(learningScenario.getProcessid(),variables).getId();
		System.out.println("Starting successful");
		
		boolean isProcessRunning=true;
		
		while(isProcessRunning){
			//get the current user task
			Task task = taskService.createTaskQuery().processInstanceId(processinstanceId).singleResult();
			if(task!=null){
				System.out.println("Current task is: "+task.getName());
				System.out.println("completing the current task and moving on");
				taskService.complete(task.getId());
			}else{
				isProcessRunning=false;
				System.out.println("process run successfully");
			}
			
			
		}
		
	}
	
	
	/**
	 * This starts a learning scenario based on the id sent. 
	 * @return json object with status true or false and id which is the processinstance id, if 
	 * the starting is success
	 * @throws Exception
	 */
	@RequestMapping(value="/startlearningscenario")
	public String startLearningScenario() throws Exception{
		File f = new File("./src/main/resources/schema/learningscenario.xml");
		JAXBContext jaxbContext = JAXBContext.newInstance(LearningScenario.class);
		

		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		LearningScenario learningScenario = (LearningScenario) jaxbUnmarshaller.unmarshal(f);
		testLearningScenario(learningScenario);
		return "success";
		//when the learningscenario id comes check if it exists 
		
	}
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
			processInstanceId = runtimeService.startProcessInstanceByKey("ithelpdeskprocess").getProcessInstanceId();
		}
		
		//get the current task
		Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
		System.out.println("./src/main/resources/learningscenarios/"+LEARNINGSCENARIO+"/"+task.getTaskDefinitionKey()+".html");
		
		File f = new File("./src/main/resources/learningscenarios/"+LEARNINGSCENARIO+"/"+task.getTaskDefinitionKey()+".html");
		if(f.exists() && !f.isDirectory()) { 
		    // send the whole html file
			BufferedReader br = new BufferedReader(new FileReader(f));
		    try {
		        StringBuilder sb = new StringBuilder();
		        String line = br.readLine();

		        while (line != null) {
		            sb.append(line);
		            sb.append("\n");
		            line = br.readLine();
		        }
		        return sb.toString();
		    } finally {
		        br.close();
		    }
		}
		
		return "no task: no task";
		//shortcut, check if the file with taskname.scenarioname exist! if so send it back as 
		//check if the current task has a UI in the learningscenario config, else complete the task and move ahead
		
	}
}
