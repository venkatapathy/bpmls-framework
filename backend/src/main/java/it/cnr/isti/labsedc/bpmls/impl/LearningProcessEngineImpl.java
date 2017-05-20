package it.cnr.isti.labsedc.bpmls.impl;

import javax.annotation.PostConstruct;

import org.camunda.bpm.engine.ProcessEngine;
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
	
	@Autowired
	private LearningEngineRepositoryService learningEngineRepositoryService;

	@Autowired
	private LearningEngineRuntimeService learningEngineRuntimeService;

	@Autowired
	private LearningEngineTaskService learningEngineTaskService;
	
	
	LearningProcessEngineImpl() {
		
		
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
	
	
	
	
	}
