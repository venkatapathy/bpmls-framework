package it.cnr.isti.labsedc.bpmls.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.transaction.Transactional;

import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.cnr.isti.labsedc.bpmls.LearningEngineRepositoryService;
import it.cnr.isti.labsedc.bpmls.LearningEngineRuntimeService;
import it.cnr.isti.labsedc.bpmls.OracleService;
import it.cnr.isti.labsedc.bpmls.Exceptions.LearningPathException;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningPath;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningPath.LearningGoals;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningPath.LearningGoals.LearningGoal;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenario;
import it.cnr.isti.labsedc.bpmls.persistance.LearningPathInstance;
import it.cnr.isti.labsedc.bpmls.persistance.LearningPathJpaRepository;
import it.cnr.isti.labsedc.bpmls.persistance.LearningScenarioInstance;
import it.cnr.isti.labsedc.bpmls.persistance.LearningScenarioJpaRepository;

@Component
public class LearningEngineRuntimeServiceImpl implements LearningEngineRuntimeService {

	private final Logger logger = LoggerFactory.getLogger(LearningProcessEngineImpl.class);

	@Autowired
	LearningPathJpaRepository lpRepository;

	@Autowired
	LearningScenarioJpaRepository lsRepository;

	@Autowired
	LearningEngineRepositoryService lpRepositoryService;

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private OracleService oracleService;

	LearningEngineRuntimeServiceImpl() {
		logger.info("Empty Constructor of LearningEngineRuntimeService");
	}

	private LearningScenarioInstance createaLSInst(LearningScenario ls, int order) {
		return new LearningScenarioInstance(ls.getId(), order, "init", "NA");
	}

	private List<LearningScenarioInstance> createLSInstanceList(LearningGoals lg) throws LearningPathException {
		// First create the LearningScenarioInstances
		List<LearningScenarioInstance> lsInstLists = new ArrayList<LearningScenarioInstance>();

		int order = 1;

		Iterator<LearningGoal> lgIt = lg.getLearningGoal().iterator();
		while (lgIt.hasNext()) {
			// loop through the learning scenarios
			Iterator<it.cnr.isti.labsedc.bpmls.learningpathspec.LearningPath.LearningGoals.LearningGoal.LearningScenarios.LearningScenario> lssIt = lgIt.next().getLearningScenarios().getLearningScenario().iterator();

			while (lssIt.hasNext()) {
				LearningScenario ls = lpRepositoryService.getDeployedLearningScenario(lssIt.next().getLsid());

				lsInstLists.add(createaLSInst(ls, order++));
			}
		}

		// if order hasnt changed. No learning Scenario is there, return null
		if (order == 1) {
			return null;
		} else {
			return lsInstLists;
		}
	}

	private LearningPathInstance createaLPInst(LearningPath lp) throws LearningPathException {
		// first get the LSInstances
		List<LearningScenarioInstance> lsInstLists = createLSInstanceList(lp.getLearningGoals());

		return new LearningPathInstance(lp.getId(), lsInstLists, "running", "NA");
	}

	@Transactional
	private LearningPathInstance startaLearningPath(LearningPath learningPath) throws LearningPathException {
		// when you start a learning path

		// 1. make sure that the learning path is not already started
		LearningPathInstance runnintlpInstance = lpRepository.findOneByLpId(learningPath.getId());

		if (runnintlpInstance != null)
			throw new LearningPathException(
					"Given Learning Path with ID: " + learningPath.getId() + " Instance already Running");

		// 2. save to LPInstance
		LearningPathInstance lpInst = createaLPInst(learningPath);

		lpInst = lpRepository.save(lpInst);

		for (LearningScenarioInstance lsInst : lpInst.getLearningScenarioInstances()) {
			lsInst.setLpInstance(lpInst);
			lsRepository.save(lsInst);
		}
		
		return lpInst;
		//

		// set LpInstance for all lps instance

		// System.out.print("to stop");
		// //3. add to running learningpaths list
		//
		// //if first time init the list
		// if(runningLearningPaths==null) runningLearningPaths= new
		// ArrayList<LearningPathInstance>();
		//
		// runningLearningPaths.add(getRunningLearningPath(holder.getKey().toString()));
		//
		// logger.info("started LearningPath with instance id:");

	}

	public LearningPathInstance startaLearningPathById(String learningPathId) throws LearningPathException {
		return startaLearningPath(lpRepositoryService.getDeployedLearningPath(learningPathId));
	}

	public List<LearningPathInstance> getRunningLearningPaths() {
		for (LearningPathInstance customer : lpRepository.findAll()) {
			logger.info(customer.toString());
		}
		return lpRepository.findByStatus("running");
	}

	public LearningPathInstance getRunningLearningPathBylpId(String lpId) {
		return lpRepository.findOneByLpId(lpId);
	}

	public LearningScenarioInstance getNextLearningScenarioByLpInstId(String lpInstId) {
		LearningPathInstance lpInst = lpRepository.findByLpInstId(Integer.parseInt(lpInstId));
		List<LearningScenarioInstance> nextLS = lsRepository.findBylpInstanceAndStatusOrderByOrderinLP(lpInst, "init");

		// if there is next
		if (nextLS.size() > 0)
			return nextLS.get(0);
		else
			return null;
	}

	public LearningScenarioInstance getRunningLearningScenarioByIpInstId(String lpInstId) {
		List<LearningScenarioInstance> curLs = lsRepository.findBylpInstanceAndStatusOrderByOrderinLP(
				lpRepository.findByLpInstId(Integer.parseInt(lpInstId)), "running");

		// if something is running
		if (curLs.size() > 0)
			return curLs.get(0);
		// else return null
		else
			return null;
	}

	
	
	@Transactional
	public void startNextLearningScenario(String lpInstId) throws LearningPathException {
		LearningScenarioInstance lsInst = getRunningLearningScenarioByIpInstId(lpInstId);

		// if there is a LS already running, throw exception
		if (lsInst != null) {
			throw new LearningPathException(
					"A Learning Scenario for the LS ID: " + lsInst.getLsId() + " is already running");
		}

		lsInst = getNextLearningScenarioByLpInstId(lpInstId);
		// no next LS available throw exception
		if (lsInst == null) {
			LearningPathInstance lp = getRunningLearningPathBylpId(lpInstId);

			throw new LearningPathException(
					"There are no next learningscenarios available for Learning path: " + lp.getLpId());
		}

		// what is starting a learning scenario instance?

		// 1. start the corresponding BPMN Process
		LearningScenario corLS = lpRepositoryService.getDeployedLearningScenario(lsInst.getLsId());
		String processId = corLS.getBpmnProcessid();
		String processInstId = runtimeService.startProcessInstanceByKey(processId).getProcessInstanceId();
		// 2. change the status in LSI
		lsInst.setStatus("running");
		// 3. set the processinstanceid in LSI
		lsInst.setProcessInstanceId(processInstId);

		//set the first learningtask as this instances next learningtask
		LearningScenario ls=lpRepositoryService.getDeployedLearningScenario(lsInst.getLsId());
		String nextLT=corLS.getTargetVertexes().getVertex().iterator().next().getBpmnActivityid();
		lsInst.setNextLearningTask(nextLT);
		
		// set the initial values to the oracle
		oracleService.updateOracleValues(lsInst, corLS.getInitialValuation().getDataObject());
		lsRepository.save(lsInst);

		// save everythin
		
		//TODO
		//dont stop yet
		//simulate the corresponding user tasks that are not learning tasks
		
		

	}
}
