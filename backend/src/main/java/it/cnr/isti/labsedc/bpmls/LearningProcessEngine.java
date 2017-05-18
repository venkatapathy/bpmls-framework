package it.cnr.isti.labsedc.bpmls;

import java.util.List;

import org.camunda.bpm.application.ProcessApplication;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.extension.reactor.spring.EnableCamundaReactor;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;

import it.cnr.isti.labsedc.bpmls.Exceptions.LearningPathException;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningPath;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningPathInstance;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenario;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenarioInstance;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenario.ValuationOracle.ValuationFunction.DataObject;

/**
 * Main interfact of the Learning Process Engine which contains within itself the Camunda Process Engine
 * @author venkat
 *
 */
@ProcessApplication
@EnableProcessApplication
@EnableCamundaReactor
public interface LearningProcessEngine {
	/*public List<LearningScenario> getDeployedLearningScenarios();

	public LearningScenarioInstance startLearningScenario(LearningScenario learningScenario);

	public LearningScenario getLearningScenario(String learningScenarioId);

	public LearningScenarioInstance getLearningScenarioInstance(String learningScenarioInstanceId);

	public Task getCurrentLearningTask(LearningScenarioInstance learningScenarioInstance);

	public void completeLearningTask(LearningScenarioInstance learningScenarioInstance,
			List<DataObject> userSubmittedDOs);
			
	*/
	
	public LearningEngineRepositoryService getLearningEngineRepositoryService();
	
	public LearningEngineRuntimeService getLearningEngineRuntimeService();
	
	
	public LearningEngineTaskService getLearningEngineTaskService();
	
	
//	/**
//	 * This will get the current learning task of the current learning scenario
//	 * 
//	 * @param {@link LearningPathInstance} LpIntance for which the task is seeked
//	 * @return {@link Task} Human task that is currently waiting for the given LPInstance. null if there are no task 
//	 * 
//	 */
//	public Task getCurrentLearningTask(LearningPathInstance lpInstance);
//	
//	
//	
	//public Task getCurrentLearningTask(String lpInstId);
	
}
