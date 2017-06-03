package it.cnr.isti.labsedc.bpmls;

import java.util.List;

import org.camunda.bpm.application.ProcessApplication;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.extension.reactor.bus.CamundaSelector;
import org.camunda.bpm.extension.reactor.spring.EnableCamundaReactor;
import org.camunda.bpm.extension.reactor.spring.listener.ReactorExecutionListener;
import org.camunda.bpm.extension.reactor.spring.listener.ReactorTaskListener;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.cnr.isti.labsedc.bpmls.Exceptions.LearningPathException;
import it.cnr.isti.labsedc.bpmls.impl.LearningProcessEngineImpl;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenario.ValuationOracle.ValuationFunction.DataObject;
import it.cnr.isti.labsedc.bpmls.persistance.LearningScenarioEvents;
import it.cnr.isti.labsedc.bpmls.persistance.LearningScenarioInstance;
import it.cnr.isti.labsedc.bpmls.persistance.LearningScenarioJpaRepository;

/**
 * Main interfact of the Learning Process Engine which contains within itself the Camunda Process Engine
 * @author venkat
 *
 */
@ProcessApplication
@EnableCamundaReactor
@EnableProcessApplication
public interface LearningProcessEngine {
	/*public List<LearningScenario> getDeployedLearningScenarios();

	public LearningScenarioInstance startLearningScenario(LearningScenario learningScenario);

	public LearningScenario getLearningScenario(String learningScenarioId);

	public LearningScenarioInstance getLearningScenarioInstance(String learningScenarioInstanceId);

	public Task getCurrentLearningTask(LearningScenarioInstance learningScenarioInstance);

	public void completeLearningTask(LearningScenarioInstance learningScenarioInstance,
			List<DataObject> userSubmittedDOs);
			
	*/
	public FlowDiagramService getFlowDiagramService();
	
	public LearningEngineRepositoryService getLearningEngineRepositoryService();
	
	public LearningEngineRuntimeService getLearningEngineRuntimeService();
	
	
	public LearningEngineTaskService getLearningEngineTaskService();
	
	public OracleService getOracleService();
	
	
	public XapiStatementService getxAPIStatementService();
	
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
	
	/**
	 * A listener that is registered to listen to all process user task events.
	 */

	@Component
	@CamundaSelector(type = "userTask", event = TaskListener.EVENTNAME_CREATE)
	public class ThirdEyeTaskListener extends ReactorTaskListener {
		private final Logger logger = LoggerFactory.getLogger(LearningProcessEngineImpl.class);

		@Autowired
		LearningEngineRepositoryService lpRepoService;
		
		@Autowired
		OracleService oracleService;
		
		@Autowired
		LearningScenarioJpaRepository lsJpaRepo;
		
		@Override
		public void notify(DelegateTask delegateTask) {
			//need to flush the jpa repo because this might be called even before lsinst set
			
			//when a task is created update the oracle values to its corresponding task
			LearningScenarioInstance lsInst=lsJpaRepo.findOneByProcessInstanceId(delegateTask.getProcessInstanceId());
			if(lsInst==null){
				return;
			}
			try {
				List<DataObject> curOracleDOs= lpRepoService.getCurrentOracleValuesFromRepo(lsInst.getProcessInstanceId(), lsInst.getLsId(), delegateTask.getTaskDefinitionKey());
				if(curOracleDOs!=null){
					logger.info("Updating oracle values for the Learning Scenario: "+lsInst.getLsId()+" with InstId: "+lsInst.getLsInstId()+". For the task: "+delegateTask.getTaskDefinitionKey()	);
					oracleService.updateOracleValues(lsInst, curOracleDOs);
				}
			} catch (LearningPathException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			;
			
			
			

		}
		
		

	}
	
	
	/**
	 * A listener that is registered to listen to all process activity events.
	 */

	@Component
	@CamundaSelector(type="endEvent",event = ExecutionListener.EVENTNAME_END)
	public class ThirdEyeExecutionListener extends ReactorExecutionListener {
		private final Logger logger = LoggerFactory.getLogger(LearningProcessEngineImpl.class);

		@Autowired
		LearningEngineRepositoryService lpRepoService;
		
		@Autowired
		OracleService oracleService;
		
		@Autowired
		LearningScenarioJpaRepository lsJpaRepo;
		
		@Override
		public void notify(DelegateExecution delegateExecution) {
			
			//when a task is created update the oracle values to its corresponding task
			LearningScenarioInstance lsInst=lsJpaRepo.findOneByProcessInstanceId(delegateExecution.getProcessInstanceId());
			if(lsInst==null){
				return;
			}
			
			logger.info("Process completed for the LearningScenario for: "+lsInst.getLsId()+". With instId: "+lsInst.getLsInstId());
			logger.info("Changing status from: "+LearningScenarioEvents.LS_STATUS_RUNNING+" to "+LearningScenarioEvents.LS_STATUS_COMPLETED);
			
			lsInst.setStatus(LearningScenarioEvents.LS_STATUS_COMPLETED);
			lsJpaRepo.saveAndFlush(lsInst);
			
			
			

		}
		
		

	}
}
