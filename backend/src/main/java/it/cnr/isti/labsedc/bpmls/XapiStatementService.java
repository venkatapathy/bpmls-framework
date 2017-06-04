package it.cnr.isti.labsedc.bpmls;

import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.task.Task;
import org.springframework.scheduling.annotation.Async;

import gov.adlnet.xapi.model.Verb;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningPath;
import it.cnr.isti.labsedc.bpmls.persistance.LearningPathInstance;
import it.cnr.isti.labsedc.bpmls.persistance.LearningScenarioInstance;

public interface XapiStatementService {

	@Async
	public void spawnAndTryPublishLPStatements(String umailbox,Verb verb,LearningPathInstance lpInst);
	
	@Async
	public void spawnAndTryPublishLSStatements(String umailbox,Verb verb,LearningScenarioInstance lsInst);
	
	@Async
	public void spawnAndTryPublishTaskStatements(String umailbox,Verb verb,DelegateTask task,LearningScenarioInstance lsInstance);
	
	@Async
	public void spawnAndTryPublishTaskStatements(String umailbox,Verb verb,Task task,LearningScenarioInstance lsInstance);
}