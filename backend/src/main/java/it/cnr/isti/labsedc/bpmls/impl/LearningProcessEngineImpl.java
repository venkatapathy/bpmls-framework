package it.cnr.isti.labsedc.bpmls.impl;

import javax.annotation.PostConstruct;

import org.camunda.bpm.engine.ProcessEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import it.cnr.isti.labsedc.bpmls.LearningEngineRepositoryService;
import it.cnr.isti.labsedc.bpmls.LearningEngineRuntimeService;
import it.cnr.isti.labsedc.bpmls.LearningEngineTaskService;
import it.cnr.isti.labsedc.bpmls.LearningProcessEngine;

@Component

public class LearningProcessEngineImpl implements LearningProcessEngine {
	@Autowired
	JdbcTemplate jdbcTemplate;

	private final Logger logger = LoggerFactory.getLogger(LearningProcessEngineImpl.class);

	
	@Autowired
	private ProcessEngine processEngine;

	@Autowired
	private LearningEngineRepositoryService learningEngineRepositoryService;

	@Autowired
	private LearningEngineRuntimeService learningEngineRuntimeService;

	@Autowired
	private LearningEngineTaskService learningEngineTaskService;
	
	
	LearningProcessEngineImpl() {
		logger.info("Empty Constructor of LearningProcessEngine");
		
	}

	
	
	
	public LearningEngineRepositoryService getLearningEngineRepositoryService(){
		return this.learningEngineRepositoryService;
	
	}	

	public LearningEngineRuntimeService getLearningEngineRuntimeService(){
		return this.learningEngineRuntimeService;
	}

	public LearningEngineTaskService getLearningEngineTaskService(){
		return this.learningEngineTaskService;
	}
	
	@PostConstruct
	public void registerMonitor(){
		
	    System.out.println("System deployed");
	    
	}
	
	// public List<LearningPathInstance> getRunningLearningPaths(){
	// return runningLearningPaths;
	// }
	//
	// public List<LearningPath> getDeployedLearningPaths(){
	// return deployedLearningPaths;
	// }
	//
	//
	// /**
	// * Returns a {@link LearningPathInstance} given its lsInstId
	// * @param lpInstId
	// * @return {@link LearningPathInstance}
	// */
	// public LearningPathInstance getRunningLearningPath(String lpInstId){
	// List<LearningPathInstance> rows = new ArrayList<LearningPathInstance>();
	// rows = jdbcTemplate.query("SELECT * from learningpathinstance WHERE
	// lpinstid=?", new Object[] { lpInstId },
	// new
	// BeanPropertyRowMapper<LearningPathInstance>(LearningPathInstance.class));
	// // get the first row, since there should be only one
	//
	// //assumed there is only one LearningPathInstance
	// return rows.iterator().next();
	// }
	//
	// private LearningScenarioInstance
	// getRunningLearningScenario(LearningPathInstance lp){
	// //first get the currentlsinstid, if null then no lsinst
	// if(lp.getCurrentlsInstid()==null){
	// return null;
	// }
	//
	// //else get it from db
	// List<LearningScenarioInstance> rows = new
	// ArrayList<LearningScenarioInstance>();
	// rows = jdbcTemplate.query("SELECT * from learningscenarioinstance WHERE
	// lsinstid=?", new Object[]{lp.getCurrentlsInstid()},
	// new
	// BeanPropertyRowMapper<LearningScenarioInstance>(LearningScenarioInstance.class));
	// // get the first row, since there should be only one
	//
	// return rows.iterator().next();
	// }
	//
	// /**
	// * This will start the current learning task of the current learning
	// scenario
	// *
	// * @param {@link LearningPathInstance} LpIntance for which the task is
	// seeked
	// * @return {@link Task} Human task that is currently waiting for the given
	// LPInstance. null if there is no task
	// *
	// */
	// public Task getCurrentLearningTask(LearningPathInstance lpInstance){
	// //first check if there is any LearningScenario that is running, if none
	// return null
	// if(lpInstance.getCurrentlsInstid()==null){
	// return null;
	// }
	// //get the process instance corresponding to the current learning scenario
	// LearningScenarioInstance lsinst=getRunningLearningScenario(lpInstance);
	//
	// //return the corresponding task
	// Task task =
	// taskService.createTaskQuery().processInstanceId(lsinst.getProcessInstanceId()).singleResult();
	//
	// return task;
	// }
	//
	// public void startNextLearningScenario(LearningPathInstance lpInstance)
	// throws LearningPathException{
	// check if a Learning Scenario is already running. If so throw exception
	// because
	// there can be only one learningscenarioinstance running for a lpinstance
	// at a time
//	if(lpInstance.getCurrentlsInstid()!=null)
//
//	{
//		throw new LearningPathException(
//				"A Learning Scenario already running with instance id: " + lpInstance.getCurrentlsInstid());
//	}
//
//	// check whats the nextlearningscenario in the line
//	lpInstance.getLearningpathid()
//	// start the nextintheline learning scenario
//}
	}
