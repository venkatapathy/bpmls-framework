package it.cnr.isti.labsedc.bpmls;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.spring.boot.starter.event.ProcessApplicationStartedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenario;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenario.ValuationOracle.ValuationFunction.DataObject;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenarioInstance;

@Component
public class LearningProcessEngineImpl implements LearningProcessEngine{
	@Autowired
	JdbcTemplate jdbcTemplate;

	private final Logger logger = LoggerFactory.getLogger(LearningProcessEngineImpl.class);

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private TaskService taskService;

	@Autowired
	private FormService formService;

	@Autowired
	private ProcessEngine processEngine;

	private List<LearningScenario> deployedLearningScenarios;

	private List<LearningScenarioInstance> runningLearningScenarios;

	LearningProcessEngineImpl() {
		logger.info("Empty Constructor of LearningProcessEngine");
	}

	/**
	 * When you start the application as spring boot, deploy the learning
	 * scenarios
	 */
	@EventListener
	public void initLPE(final ProcessApplicationStartedEvent unused) throws Exception{
		
		//init empty Arraylists
		deployedLearningScenarios= new ArrayList<LearningScenario>();
		runningLearningScenarios= new ArrayList<LearningScenarioInstance>();
		
		String lsid = "learningscenario.xml";
		logger.info("Deploying Learning Scenarios after BPMN have been deployed to Camunda");
		logger.info("Hardcoded deployement of LearningScenario ");
		Resource learningScenarioXml;
		learningScenarioXml = new UrlResource("classpath:schema/" + lsid);
		JAXBContext jaxbContext = JAXBContext.newInstance(LearningScenario.class);

		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		LearningScenario learningScenario = (LearningScenario) jaxbUnmarshaller
				.unmarshal(learningScenarioXml.getInputStream());
		
		
		
		deployLearningScenario(learningScenario);
	}
	
	public void deployLearningScenario(LearningScenario learningScenario) {
		//deploying of learning scenario is checking if the corresponding process is deployed in 
		//camunda and add it to the list of deployed learning scenarios
		
		if(processEngine.getRepositoryService().createProcessDefinitionQuery().processDefinitionKey(learningScenario.getBpmnProcessid()).singleResult()!=null){
			logger.info("Process with id, "+learningScenario.getBpmnProcessid()+" found");
			logger.info("Adding "+learningScenario.getName()+" to Deployed Learning Scenario");
			deployedLearningScenarios.add(learningScenario);
			
		}else{
			//ideally should throw an exception
			System.out.println("process not  found");
		}
		
	}

	public List<LearningScenario> getDeployedLearningScenarios() {
		return deployedLearningScenarios;
	}

	public LearningScenarioInstance startLearningScenario(LearningScenario learningScenario) {
		LearningScenarioInstance startedLPInst = null;
		return startedLPInst;
	}

	public LearningScenario getLearningScenario(String learningScenarioId) {
		LearningScenario retrivedLearningSenario = null;
		return retrivedLearningSenario;
	}

	public LearningScenarioInstance getLearningScenarioInstance(String learningScenarioInstanceId) {
		return null;
	}

	public Task getCurrentLearningTask(LearningScenarioInstance learningScenarioInstance) {
		return null;
	}

	public void completeLearningTask(LearningScenarioInstance learningScenarioInstance,
			List<DataObject> userSubmittedDOs) {

	}
}
