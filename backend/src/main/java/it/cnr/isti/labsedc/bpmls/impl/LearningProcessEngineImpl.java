package it.cnr.isti.labsedc.bpmls.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.camunda.bpm.application.PostDeploy;
import org.camunda.bpm.application.ProcessApplication;
import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.extension.reactor.CamundaReactor;
import org.camunda.bpm.extension.reactor.bus.CamundaEventBus;
import org.camunda.bpm.extension.reactor.spring.EnableCamundaReactor;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.camunda.bpm.spring.boot.starter.event.ProcessApplicationStartedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import it.cnr.isti.labsedc.bpmls.ThirdEyeTaskListener;
import it.cnr.isti.labsedc.bpmls.HtmlFormEngine;
import it.cnr.isti.labsedc.bpmls.LearningEngineRepositoryService;
import it.cnr.isti.labsedc.bpmls.LearningEngineRuntimeService;
import it.cnr.isti.labsedc.bpmls.LearningEngineTaskService;
import it.cnr.isti.labsedc.bpmls.LearningProcessEngine;
import it.cnr.isti.labsedc.bpmls.Exceptions.LearningPathException;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningPath;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningPathInstance;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenario;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenario.ValuationOracle.ValuationFunction.DataObject;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenarioInstance;

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
