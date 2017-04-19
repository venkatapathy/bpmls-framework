package it.cnr.isti.labsedc.bpmls;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.form.FormField;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
    JdbcTemplate jdbcTemplate;
	
	
	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private TaskService taskService;

	@Autowired
	private FormService formService;

	private String processInstanceId=null;

	private static String LEARNINGSCENARIO="learningscenario1";


	private Resource learningScenarioXml;
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

				System.out.println("Generated form is");

				List<FormField> taskformsfields=formService.getTaskFormData(task.getId()).getFormFields();

				for(int temp=0; temp < taskformsfields.size();temp++){
					System.out.println(taskformsfields.get(temp).getLabel());
				}


				//expected 
				taskService.complete(task.getId());
			}else{
				isProcessRunning=false;
				System.out.println("process run successfully");
			}


		}

	}


	/**
	 * This starts a learning scenario based on the LearningScenario id sent as parameter.
	 * @param lsid-ID of the LearningScenario 
	 * @return json object with status true or false and id which is the learningscenarioinstance id, if 
	 * the starting is success. Null if its a failure
	 * @throws Exception
	 */
	@RequestMapping(value="/startlearningscenario", method = RequestMethod.GET)
	public String startLearningScenario(@RequestParam(value = "lsid") String lsid) throws Exception{
		//temp setting
		lsid="learningscenario.xml";
		
		//get the learning scenario id sent as GET parameter
		learningScenarioXml=new UrlResource("classpath:schema/"+lsid);
		
		//open the xml and convert it to JAVA object
		//File f = new File("./src/main/resources/schema/learningscenario.xml");
		JAXBContext jaxbContext = JAXBContext.newInstance(LearningScenario.class);


		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		LearningScenario learningScenario = (LearningScenario) jaxbUnmarshaller.unmarshal(learningScenarioXml.getInputStream());
		
		//start a process instance given by the process id 
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
				
				//done to be used within inner classLocal variable lsid defined in an enclosing scope must be final or effectively final
				final String lsid_final=lsid;
				
				//insert the created pid and its corresponding learning scenario into the table and
				//get its unique id
				GeneratedKeyHolder holder = new GeneratedKeyHolder();
				jdbcTemplate.update(new PreparedStatementCreator() {
				    @Override
				    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				        PreparedStatement statement = con.prepareStatement("INSERT INTO learningscenarioinstance(learningscenarioid, processinstaneid) VALUES (?, ?) ", Statement.RETURN_GENERATED_KEYS);
				        statement.setString(1,lsid_final );
				        statement.setString(2, processinstanceId);
				        return statement;
				    }
				}, holder);

				long primaryKey = holder.getKey().longValue();
				String jsonreturn="{"
						+ "status:success,"
						+ "lsinstid:"+holder.getKey().toString()+","
						+ "lsid:"+lsid_final+","
						+ "pinstid:"+processinstanceId+","
						+ "pid:"+learningScenario.getProcessid()
						+ "}";
		//testLearningScenario(learningScenario);
		return jsonreturn;
		//when the learningscenario id comes check if it exists 

	}
	/**
	 * This gets the current task of a given learning scenarios instance id. 
	 * 
	 * TODO: Need to make the function more dynamic
	 * @return
	 * @throws Exception
	 * TODO:
	 * include ways to get the currenttask based on a input parameter called
	 * learning scenario
	 */
	@CrossOrigin(origins = "http://localhost:4200")
	@RequestMapping(value = "/getcurrentlearningtask", method = RequestMethod.GET)
	public String getCurrentLearningTask(@RequestParam(value = "pinstid") String pinstid) throws Exception {
		

		//get the current task
		Task task = taskService.createTaskQuery().processInstanceId(pinstid).singleResult();
		
		
		
		return new HtmlFormEngine().renderFormData(formService.getTaskFormData(task.getId()));
		//shortcut, check if the file with taskname.scenarioname exist! if so send it back as 
		//check if the current task has a UI in the learningscenario config, else complete the task and move ahead

	}
	
	/**
	 * Completes a task given the learning scenario instance id. It must also provide any form that needs to be completed
	 * @param lsid
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/completelearningtask", method = RequestMethod.GET)
	public String completeLearningTask(@RequestParam(value = "lsid") String lsid) throws Exception{
		Task task = taskService.createTaskQuery().processInstanceId(lsid).singleResult();
		taskService.complete(task.getId());
		return "all is well";
	}
}
