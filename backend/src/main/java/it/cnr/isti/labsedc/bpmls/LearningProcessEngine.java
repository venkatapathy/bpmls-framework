package it.cnr.isti.labsedc.bpmls;

import java.util.List;

import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import it.cnr.isti.labsedc.bpmls.senarios.LearningScenario;
import it.cnr.isti.labsedc.bpmls.senarios.LearningScenarioInstance;

public class LearningProcessEngine {
	@Autowired
	JdbcTemplate jdbcTemplate;

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

	public void deployLearningScenario(LearningScenario learningScenario, Bpmn deployedBPMNRef){
		
	}
	
	public List<LearningScenario> getDeployedLearningScenarios(){
		return deployedLearningScenarios;
	}
	
	public LearningScenarioInstance startLearningScenario(LearningScenario learningScenario){
		LearningScenarioInstance startedLPInst=null;
		return startedLPInst;
	}

	public LearningScenario getLearningScenario(String learningScenarioId){
		LearningScenario retrivedLearningSenario=null;
		return retrivedLearningSenario;	
	}
	
	public Task getCurrentLearningTask(LearningScenarioInstance learningScenarioInstance){
		return null;
	}
}
