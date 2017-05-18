package it.cnr.isti.labsedc.bpmls.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.cnr.isti.labsedc.bpmls.LearningEngineRepositoryService;
import it.cnr.isti.labsedc.bpmls.LearningEngineRuntimeService;
import it.cnr.isti.labsedc.bpmls.LearningEngineTaskService;
import it.cnr.isti.labsedc.bpmls.OracleService;
import it.cnr.isti.labsedc.bpmls.Exceptions.LearningPathException;
import it.cnr.isti.labsedc.bpmls.Exceptions.LearningPathExceptionErrorCodes;
import it.cnr.isti.labsedc.bpmls.Exceptions.LearningTaskException;
import it.cnr.isti.labsedc.bpmls.Exceptions.LearningTaskExceptionErrorCodes;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningPath.LearningGoals.LearningGoal.LearningScenarios.LearningScenario;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenario.TargetVertexes.Vertex;
import it.cnr.isti.labsedc.bpmls.persistance.LearningPathInstance;
import it.cnr.isti.labsedc.bpmls.persistance.LearningScenarioInstance;
import it.cnr.isti.labsedc.bpmls.persistance.LearningScenarioJpaRepository;

@Component
public class LearningEngineTaskServiceImpl implements LearningEngineTaskService {

	@Autowired
	TaskService taskServiceCamunda;

	@Autowired
	LearningScenarioJpaRepository lsRepository;

	@Autowired
	LearningEngineRuntimeService learningengineRuntimeService;

	@Autowired
	LearningEngineRepositoryService lpRepositoryService;

	@Autowired
	OracleService oracleService;

	/**
	 * Gets the current Learning task given the learning path instance id. TODO:
	 * per user
	 * 
	 * @param learning
	 *            path instance id
	 * @return {@link Task} If there is a task running for that path instance
	 *         and a learning scenario
	 * @throws LearningPathException
	 *             1. Learning when running Learning path cannot be found 2.
	 *             when no learningscenario is running
	 */
	public Task getCurrentLearningTask(String lpInstId) throws LearningPathException {
		// get the current LearningScenarioInstance
		LearningScenarioInstance lsInst = learningengineRuntimeService.getRunningLearningScenarioByIpInstId(lpInstId);

		// if no running scenarios return null
		if (lsInst == null) {
			throw new LearningPathException("No Learning Scenario running for LP instance with id: ",
					LearningPathExceptionErrorCodes.LP_NO_RUNNING_LEARNING_SCENARIOS);
		}

		// get its corresponding process instance and its task
		Task task = taskServiceCamunda.createTaskQuery().processInstanceId(lsInst.getProcessInstanceId())
				.singleResult();

		// throw exception

		return task;
	}

	
	private Task getCurrentLearningTask(LearningScenarioInstance lsInst) {

		// get its corresponding process instance and its task
		Task task = taskServiceCamunda.createTaskQuery().processInstanceId(lsInst.getProcessInstanceId())
				.singleResult();

		// throw exception

		return task;
	}

	/*
	 * public void completeCurrentLearningTask(String lpInstId, Map<String,
	 * Object> taskInputs) throws LearningTaskException, LearningPathException {
	 * // check if the input is per the expected oracle values
	 * 
	 * // if not collect the wrong inputs with msgs and throw an exception
	 * 
	 * // if all is fine complete the task // Task task= //
	 * getCurrentLearningTask(learningengineRuntimeService.
	 * getRunningLearningScenarioByIpInstId(lpInstId) // dont end check for if
	 * the next user task is a learning task in this // learning scenario
	 * context
	 * 
	 * // complete all the upcoming user tasks that are not learnign tasks
	 * 
	 * // done // String retMsg= oracleService.checkOracleValues(lsInst,
	 * taskInputs);
	 * 
	 * }
	 */

	private void completeAndUpdateinDB(LearningScenarioInstance lsInst, String taskId, Map<String, Object> taskInputs) {
		taskServiceCamunda.complete(taskId, taskInputs);
		updateNextLearningTaskinLearningScenarioInstance(lsInst);
	}

	/**
	 * Complete a learning task.
	 * 
	 * @param lpInstId
	 *            The Lp instance for which the current learning task is
	 *            submitted
	 * @param taskInputs
	 *            Input from the user for the task
	 * @throws LearningTaskException
	 *             1. When the user input does not match to the expected oracle values
	 */
	@Transactional
	public void completeCurrentLearningTask(LearningScenarioInstance lsInst,
			Map<String, Object> taskInputs) throws LearningTaskException {
		// if no running scenarios return null this cannot happen
		/*if (lsInst == null) {
			throw new LearningPathException("Not possible to be here!! because it ought to be called internally",
					LearningPathExceptionErrorCodes.LP_NO_RUNNING_LEARNING_SCENARIOS);
		}*/

		List<TaskIncompleteErrorMessage> errMsgList =oracleService.checkOracleValues(lsInst, taskInputs); // call the oracle

		 

		//if there is error messages
		if(errMsgList!=null){
			throw new LearningTaskException("Error in User inputs with respect to expected values", LearningTaskExceptionErrorCodes.LT_INPUT_ERROR, errMsgList);
		}
		// if the errlist is empty that is all is well
		if (errMsgList == null) {
			Task task = getCurrentLearningTask(lsInst);
			
			completeAndUpdateinDB(lsInst, task.getId(), taskInputs);
			//simulateNonLearningTasks(lsInst);
		}

		
	}

	private void updateNextLearningTaskinLearningScenarioInstance(LearningScenarioInstance lsInst) {
		boolean changed = false;

		it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenario ls = null;
		try {
			ls = lpRepositoryService.getDeployedLearningScenario(lsInst.getLsId());
		} catch (LearningPathException e) {
			// this cannot happen-GOD
			e.printStackTrace();

		}

		Iterator<Vertex> lsIT = ls.getTargetVertexes().getVertex().iterator();

		while (lsIT.hasNext()) {
			String tmp = lsIT.next().getBpmnActivityid();
			if (tmp.equals(lsInst.getNextLearningTask())) {
				if (lsIT.hasNext()) {
					tmp = lsIT.next().getBpmnActivityid();
					lsInst.setNextLearningTask(tmp);
					lsRepository.save(lsInst);
					return;
				} else {
					lsInst.setNextLearningTask(null);
					lsRepository.save(lsInst);
					return;
				}
			}
		}

		

	}

//	protected void simulateNonLearningTasks(LearningScenarioInstance lsInst) {
//		// try {
//		// Thread.sleep(3000);
//		// } catch (InterruptedException e) {
//		// // TODO Auto-generated catch block
//		// e.printStackTrace();
//		// }
//		// List<Task> tls=;
//		// System.out.println("2. when next ls at: " +
//		// lsInst.getNextLearningTask() + "task size is:" +
//		// taskServiceCamunda.createTaskQuery().processInstanceId(lsInst.getProcessInstanceId()).count());
//
//		Task task = taskServiceCamunda.createTaskQuery().processInstanceId(lsInst.getProcessInstanceId())
//				.singleResult();
//
//		if (task == null) {
//			// wait until either the
//			return;
//		}
//		String nextLT = lsInst.getNextLearningTask();
//		while (nextLT != null && task != null && !nextLT.equals(task.getTaskDefinitionKey())) {
//			//
//			// completeCurrentLearningTask
//			taskServiceCamunda.complete(task.getId());
//			task = getCurrentLearningTask(Integer.toString(lsInst.getLpInstance().getLpInstId()));
//		}
//
//		// when no learning tasks are there, just complete remaining tasks
//		if (nextLT == null) {
//			task = getCurrentLearningTask(Integer.toString(lsInst.getLpInstance().getLpInstId()));
//			while (task != null) {
//				taskServiceCamunda.complete(task.getId(), oracleService.getOracleValues());
//				task = getCurrentLearningTask(Integer.toString(lsInst.getLpInstance().getLpInstId()));
//			}
//		}
//
//	}
}
