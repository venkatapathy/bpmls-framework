package it.cnr.isti.labsedc.bpmls.impl;

import java.util.Iterator;
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
import it.cnr.isti.labsedc.bpmls.Exceptions.LearningTaskException;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningPath.LearningGoals.LearningGoal.LearningScenarios.LearningScenario;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenario.TargetVertexes.Vertex;
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

	public Task getCurrentLearningTask(String lpInstId) {
		// get the current LearningScenarioInstance
		LearningScenarioInstance lsInst = learningengineRuntimeService.getRunningLearningScenarioByIpInstId(lpInstId);

		// if no running scenarios return null
		if (lsInst == null) {
			return null;
		}

		// get its corresponding process instance and its task
		Task task = taskServiceCamunda.createTaskQuery().processInstanceId(lsInst.getProcessInstanceId())
				.singleResult();

		return task;
	}

	public void completeCurrentLearningTask(String lpInstId, Map<String, Object> taskInputs)
			throws LearningTaskException, LearningPathException {
		// check if the input is per the expected oracle values

		// if not collect the wrong inputs with msgs and throw an exception

		// if all is fine complete the task
		// Task task=
		// getCurrentLearningTask(learningengineRuntimeService.getRunningLearningScenarioByIpInstId(lpInstId)
		// dont end check for if the next user task is a learning task in this
		// learning scenario context

		// complete all the upcoming user tasks that are not learnign tasks

		// done
		// String retMsg= oracleService.checkOracleValues(lsInst, taskInputs);

	}

	public String completeCurrentLearningTask(LearningScenarioInstance lsInst, Map<String, Object> taskInputs) {
		String retMsg = oracleService.checkOracleValues(lsInst, taskInputs);

		if (!retMsg.contains("error")) {
			Task task = getCurrentLearningTask(Integer.toString(lsInst.getLpInstance().getLpInstId()));
			taskServiceCamunda.complete(task.getId(),taskInputs);
			updateNextLearningTaskinLearningScenarioInstance(lsInst);
			simulateNonLearningTasks(lsInst);
		}

		return retMsg;
	}

	@Transactional
	private void updateNextLearningTaskinLearningScenarioInstance(LearningScenarioInstance lsInst) {
		boolean changed = false;

		it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenario ls = null;
		try {
			ls = lpRepositoryService.getDeployedLearningScenario(lsInst.getLsId());
		} catch (LearningPathException e) {
			// TODO Auto-generated catch block
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

		lsInst.setNextLearningTask(null);
		lsRepository.save(lsInst);
		return;

	}

	protected void simulateNonLearningTasks(LearningScenarioInstance lsInst) {
		Task task = getCurrentLearningTask(Integer.toString(lsInst.getLpInstance().getLpInstId()));

		if (task == null) {
			return;
		}
		String nextLT = lsInst.getNextLearningTask();
		while (nextLT!=null && task != null && !nextLT.equals(task.getTaskDefinitionKey())) {
			//
			// completeCurrentLearningTask
			taskServiceCamunda.complete(task.getId());
			task = getCurrentLearningTask(Integer.toString(lsInst.getLpInstance().getLpInstId()));
		}
		
		//when no learning tasks are there, just complete remaining tasks
		if(nextLT==null){
			task = getCurrentLearningTask(Integer.toString(lsInst.getLpInstance().getLpInstId()));
			while(task!=null){
				taskServiceCamunda.complete(task.getId(),oracleService.getOracleValues());
				task = getCurrentLearningTask(Integer.toString(lsInst.getLpInstance().getLpInstId()));
			}
		}

	}
}
