package it.cnr.isti.labsedc.bpmls.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gov.adlnet.xapi.model.Verbs;
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
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenario.ValuationOracle.ValuationFunction.DataObject;
import it.cnr.isti.labsedc.bpmls.persistance.LearningPathInstance;
import it.cnr.isti.labsedc.bpmls.persistance.LearningScenarioEvents;
import it.cnr.isti.labsedc.bpmls.persistance.LearningScenarioInstance;
import it.cnr.isti.labsedc.bpmls.persistance.LearningScenarioJpaRepository;

@Component
public class LearningEngineTaskServiceImpl implements LearningEngineTaskService {

	private final Logger logger = LoggerFactory.getLogger(LearningEngineTaskServiceImpl.class);

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

	@Autowired
	private XapiStatementServiceImpl xApiService;

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

	private void completeAndUpdateinDB(LearningScenarioInstance lsInst, String taskId, Map<String, Object> taskInputs) {
		// do typecasts before updating
		// loop through map to typecase boolean values from string
		for (Map.Entry<String, Object> entry : taskInputs.entrySet()) {
			// ifstring
			if (entry.getValue() instanceof String) {

				if (entry.getValue().equals("true") || entry.getValue().equals("false")) {
					entry.setValue(Boolean.parseBoolean((String) entry.getValue()));

				}

				/*
				 * // try parsing it to date DateFormat df = new
				 * SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); Date startDate;
				 * try { startDate = df.parse((String) entry.getValue());
				 * entry.setValue(startDate); if (entry.getValue() instanceof
				 * String) { System.out.println("some problem"); } } catch
				 * (ParseException e) { // e.printStackTrace(); // ignore }
				 */
			}
		}

		// also update in oracle table in case if there are extra userinputs not
		// accounted for in the learnign scenario
		// oracleService.updateOracleValues(lsInst, taskInputs);

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
	 *             1. When the user input does not match to the expected oracle
	 *             values
	 */
	@Transactional
	public void completeCurrentLearningTask(LearningScenarioInstance lsInst, Map<String, Object> taskInputs)
			throws LearningTaskException {
		// if no running scenarios return null this cannot happen
		/*
		 * if (lsInst == null) { throw new
		 * LearningPathException("Not possible to be here!! because it ought to be called internally"
		 * , LearningPathExceptionErrorCodes.LP_NO_RUNNING_LEARNING_SCENARIOS);
		 * }
		 */

		// append empty values to other oracle parameters so that the check can
		// be made full
		Task task = taskServiceCamunda.createTaskQuery().processInstanceId(lsInst.getProcessInstanceId())
				.singleResult();
		List<DataObject> orValues = null;
		try {
			orValues = lpRepositoryService.getCurrentOracleValuesFromRepo(lsInst.getProcessInstanceId(),
					lsInst.getLsId(), task.getTaskDefinitionKey());
		} catch (LearningPathException e1) {
			logger.error("Unexpected Error when trying to get current Oracle values while completing task: "
					+ task.getName() + "for learning" + "path: " + lsInst.getLsId() + "and Learning Inst id: "
					+ lsInst.getLsInstId() + ". Error is:" + e1.getMessage());
		}

		// for all oracle values
		if (orValues != null) {

			for (DataObject dO : orValues) {
				// if not present in input from the user
				if (taskInputs.get(dO.getId()) == null) {
					// init with null
					taskInputs.put(dO.getId(), null);
				}
			}
		}

		List<TaskIncompleteErrorMessage> errMsgList = oracleService.checkOracleValues(lsInst, taskInputs, task); // call
		// the
		// oracle

		// if there is error messages
		if (errMsgList != null) {
			// xAPI Event
			// learningtask start
			// before returning try ans spwan xapi statement, will not affect
			// the flow
			try {
				xApiService.spawnAndTryPublishTaskStatements(lsInst.getLpInstance().getLdInstance().getUsername(),
						Verbs.failed(), task, lsInst);

			} catch (Exception e) {
				// no error should affect the flow so if exception ignore and
				// keep moving ahead
				logger.warn("Cannot emit xAPI events for Learning Path start event! Exception happend with message: "
						+ e.getMessage());
			}

			// xAPI Event- End

			throw new LearningTaskException("Error in User inputs with respect to expected values",
					LearningTaskExceptionErrorCodes.LT_INPUT_ERROR, errMsgList);
		}
		// if the errlist is empty that is all is well
		if (errMsgList == null) {
			// TODO error handling
			/*
			 * if(! (lsInst.getStatus().equals(LearningScenarioEvents.
			 * LS_STATUS_RUNNING))){ throw new
			 * LearningTaskException("Learning Scenario is not running anymore"
			 * ); }
			 */

			this.completeAndUpdateinDB(lsInst, task.getId(), taskInputs);
			logger.info("Completing a Learning Task:" + task.getTaskDefinitionKey() + " for learning instance: "
					+ lsInst.getLsId() + " with Inst ID: " + lsInst.getLsInstId());
			try {
				simulateNonLearningTasks(lsInst);
			} catch (LearningPathException e) {
				logger.error("Unexpected error in completing Learning task: " + task.getTaskDefinitionKey()
						+ " for learning instance: " + lsInst.getLsId() + " with Inst ID: " + lsInst.getLsInstId()
						+ ". Error is: " + e.getMessage());
			}

			// xAPI Event
			// learningtask start
			// before returning try ans spwan xapi statement, will not affect
			// the flow
			try {
				xApiService.spawnAndTryPublishTaskStatements(lsInst.getLpInstance().getLdInstance().getUsername(),
						Verbs.completed(), task, lsInst);

			} catch (Exception e) {
				// no error should affect the flow so if exception ignore and
				// keep moving ahead
				logger.warn("Cannot emit xAPI events for Learning Path start event! Exception happend with message: "
						+ e.getMessage());
			}

			// xAPI Event- End
		}

	}

	@Transactional
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

	public void simulateNonLearningTasks(LearningScenarioInstance lsInst) throws LearningPathException {

		Task task = taskServiceCamunda.createTaskQuery().processInstanceId(lsInst.getProcessInstanceId())
				.singleResult();
		// no tasks just return, ideally the process should have been complete
		// by now
		if (task == null) {
			// wait until either the
			return;
		}
		String nextLT = lsInst.getNextLearningTask();
		while (nextLT != null && task != null && !nextLT.equals(task.getTaskDefinitionKey())) {
			//
			// completeCurrentLearningTask
			logger.info("Simulating: " + task.getTaskDefinitionKey() + " for the LearningScenario with id: "
					+ lsInst.getLsId() + " and Instid: " + lsInst.getLsInstId());
			Map<String, Object> map = oracleService.getOracleValues(lsInst);
			// loop through map to typecase boolean/date values from string
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				// ifstring
				if (entry.getValue() instanceof String) {

					if (entry.getValue().equals("true") || entry.getValue().equals("false")) {
						entry.setValue(Boolean.parseBoolean((String) entry.getValue()));
						return;
					}

					/*
					 * // try parsing it to date DateFormat df = new
					 * SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); Date
					 * startDate; try { startDate = df.parse((String)
					 * entry.getValue()); entry.setValue(startDate); if
					 * (entry.getValue() instanceof String) {
					 * System.out.println("some problem"); } } catch
					 * (ParseException e) { // e.printStackTrace(); // ignore }
					 */
				}
			}
			taskServiceCamunda.complete(task.getId(), map);
			updateNextLearningTaskinLearningScenarioInstance(lsInst);
			nextLT = lsInst.getNextLearningTask();

			task = taskServiceCamunda.createTaskQuery().processInstanceId(lsInst.getProcessInstanceId()).singleResult();
		}

		// when no learning tasks are there, just complete remaining tasks
		if (nextLT == null) {
			task = taskServiceCamunda.createTaskQuery().processInstanceId(lsInst.getProcessInstanceId()).singleResult();
			while (task != null) {
				logger.info("Simulating: " + task.getTaskDefinitionKey() + " for the LearningScenario with id: "
						+ lsInst.getLsId() + " and Instid: " + lsInst.getLsInstId());

				Map<String, Object> map = oracleService.getOracleValues(lsInst);
				// loop through map to typecase boolean/date values from string
				for (Map.Entry<String, Object> entry : map.entrySet()) {
					// ifstring
					if (entry.getValue() instanceof String) {

						if (entry.getValue().equals("true") || entry.getValue().equals("false")) {
							entry.setValue(Boolean.parseBoolean((String) entry.getValue()));
							return;
						}

						/*
						 * // try parsing it to date DateFormat df = new
						 * SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); Date
						 * startDate; try { startDate = df.parse((String)
						 * entry.getValue()); entry.setValue(startDate); if
						 * (entry.getValue() instanceof String) {
						 * System.out.println("some problem"); } } catch
						 * (ParseException e) { // e.printStackTrace(); //
						 * ignore }
						 */
					}
				}
				taskServiceCamunda.complete(task.getId(), map);
				task = taskServiceCamunda.createTaskQuery().processInstanceId(lsInst.getProcessInstanceId())
						.singleResult();
			}
		}

	}
}
