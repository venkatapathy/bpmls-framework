package it.cnr.isti.labsedc.bpmls.impl;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.camunda.bpm.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.cnr.isti.labsedc.bpmls.LearningEngineRepositoryService;
import it.cnr.isti.labsedc.bpmls.LearningEngineRuntimeService;
import it.cnr.isti.labsedc.bpmls.LearningEngineTaskService;
import it.cnr.isti.labsedc.bpmls.OracleService;
import it.cnr.isti.labsedc.bpmls.Exceptions.LearningPathException;
import it.cnr.isti.labsedc.bpmls.Exceptions.LearningPathExceptionErrorCodes;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningPath;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningPath.LearningGoals;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningPath.LearningGoals.LearningGoal;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenario;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenario.InitialValuation.DataObject;
import it.cnr.isti.labsedc.bpmls.persistance.LearnerDetails;
import it.cnr.isti.labsedc.bpmls.persistance.LearningPathEvents;
import it.cnr.isti.labsedc.bpmls.persistance.LearningPathInstance;
import it.cnr.isti.labsedc.bpmls.persistance.LearningPathJpaRepository;
import it.cnr.isti.labsedc.bpmls.persistance.LearningScenarioEvents;
import it.cnr.isti.labsedc.bpmls.persistance.LearningScenarioInstance;
import it.cnr.isti.labsedc.bpmls.persistance.LearningScenarioJpaRepository;

/**
 * A service class linked with a LearningEngine that provides runtime services
 * such as starting a learning path/learning scenario, getting current/next
 * learning scenarios, current learning tasks and complete a learning task
 * 
 * @author venkat
 *
 */
@Component
public class LearningEngineRuntimeServiceImpl implements LearningEngineRuntimeService {

	

	@Autowired
	LearningPathJpaRepository lpRepository;

	@Autowired
	LearningScenarioJpaRepository lsRepository;

	@Autowired
	LearningEngineRepositoryService lpRepositoryService;

	@Autowired
	private RuntimeService camundaRuntimeService;

	@Autowired
	private OracleService oracleService;

	@Autowired
	private LearningEngineTaskService lpTaskService;

	LearningEngineRuntimeServiceImpl() {
		
	}

	/**
	 * Simply creates a new LearningScenarioInstance with the given
	 * learningscenario id, order in which it needs to be executed, status as
	 * init,
	 * 
	 * @param ls
	 * @param order
	 * @return
	 */
	private LearningScenarioInstance createaLSInst(LearningScenario ls, int order) {
		return new LearningScenarioInstance(ls.getId(), order, LearningScenarioEvents.LS_STATUS_INIT,
				LearningScenarioEvents.LS_RESULT_NA);
	}

	private List<LearningScenarioInstance> createLSInstanceList(LearningGoals lg) throws LearningPathException {
		// First create the LearningScenarioInstances
		List<LearningScenarioInstance> lsInstLists = new ArrayList<LearningScenarioInstance>();

		int order = 1;

		Iterator<LearningGoal> lgIt = lg.getLearningGoal().iterator();
		while (lgIt.hasNext()) {
			// loop through the learning scenarios
			Iterator<it.cnr.isti.labsedc.bpmls.learningpathspec.LearningPath.LearningGoals.LearningGoal.LearningScenarios.LearningScenario> lssIt = lgIt
					.next().getLearningScenarios().getLearningScenario().iterator();

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

	private LearningPathInstance createaLPInst(LearningPath lp, LearnerDetails user) throws LearningPathException {
		// first get the LSInstances
		List<LearningScenarioInstance> lsInstLists = createLSInstanceList(lp.getLearningGoals());

		// iflpInst is null, not a good sign throw exception
		if (lsInstLists == null) {
			throw new LearningPathException(
					"Couldn't create Learning Scenario Instances for LP: " + lp.getId()
							+ ". Make sure the deployment is proper?",
					LearningPathExceptionErrorCodes.LP_NO_LEARNING_SCENARIOS);
		}

		return new LearningPathInstance(lp.getId(), lsInstLists, LearningPathEvents.LP_STATUS_RUNNING,
				LearningPathEvents.LP_RESULT_NA, user);
	}

	/**
	 * This will start a learning path for a given user. Note Assumption is the user is validated already and called here without null.
	 * @param learningPath
	 * @param user
	 * @return
	 * @throws LearningPathException
	 */
	@Transactional
	private LearningPathInstance startaLearningPath(LearningPath learningPath, LearnerDetails user) throws LearningPathException {
		// when you start a learning path

		// 1. make sure that the learning path is not already started
		LearningPathInstance runnintlpInstance = lpRepository.findOneByLpIdAndLdInstanceAndStatus(learningPath.getId(),user,LearningPathEvents.LP_STATUS_RUNNING);

		if (runnintlpInstance != null)
			throw new LearningPathException(
					"Given Learning Path with ID: " + learningPath.getId() + " Instance already Running",
					LearningPathExceptionErrorCodes.LP_ALREADY_RUNNING);

		// 2. save to LPInstance
		LearningPathInstance lpInst = createaLPInst(learningPath,user);

		lpInst = lpRepository.save(lpInst);

		for (LearningScenarioInstance lsInst : lpInst.getLearningScenarioInstances()) {
			lsInst.setLpInstance(lpInst);
			lsRepository.save(lsInst);
		}

		return lpInst;

	}

	/**
	 * Starts a Learning path given its learningPath id. TODO: Make run check
	 * for running learning path per user
	 * 
	 * @param {@link
	 * 			String} The ID of the Learning Path
	 * @return {@link LearningPathInstance} The intansance created for that
	 *         particular Learning Path TODO: to link also the user
	 * @throws {@link
	 *             LearningPathException} An exception is thrown if: 1. A
	 *             LearningPath with given id is not available 2. No learning
	 *             scenario is present that is corresponding to the given
	 *             Learning path 3. If one instance of Learning path is already
	 *             running
	 */
	public LearningPathInstance startaLearningPathById(String learningPathId,LearnerDetails user) throws LearningPathException {
		return startaLearningPath(lpRepositoryService.getDeployedLearningPath(learningPathId),user);
	}

	public void completeaLearningPath(LearningPathInstance lpInstance){
		lpInstance.setStatus(LearningPathEvents.LP_STATUS_COMPLETED);
		lpRepository.saveAndFlush(lpInstance);
		
	}
	
	/**
	 * Get the current running Learning Paths. (Only one per deployed
	 * LearningPaths(later per user)). TODO: find it per user
	 * 
	 * @return {@link List} of {@link LearningPathInstance}. Null if none is
	 *         present
	 */
	public List<LearningPathInstance> getRunningLearningPaths(LearnerDetails owner) {
		return lpRepository.findByldInstanceAndStatus(owner,LearningPathEvents.LP_STATUS_RUNNING);
	}
	
	

	/**
	 * Get the current running Learning Path. (Only one per deployed
	 * LearningPaths(later per user)), given the learning path id. TODO: find it
	 * per user
	 * 
	 * @param The
	 *            learning path id
	 * @return {@link LearningPathInstance}. Null if none is present
	 */
	public LearningPathInstance getRunningLearningPathBylpId(String lpId, LearnerDetails user) {
		return lpRepository.findOneByLpIdAndLdInstanceAndStatus(lpId,user,LearningPathEvents.LP_STATUS_RUNNING);
	}

	public LearningPathInstance getCompletedLearningPathBylpId(String lpId, LearnerDetails user) {
		List<LearningPathInstance> temp=lpRepository.findByLpIdAndLdInstanceAndStatus(lpId,user,LearningPathEvents.LP_STATUS_COMPLETED);
		
		if(temp==null){
			return null;
		}
		
		return lpRepository.findByLpIdAndLdInstanceAndStatus(lpId,user,LearningPathEvents.LP_STATUS_COMPLETED).get(0);
	}
	
	private LearningPathInstance getRunningLearningPathBylpInstId(String lpInstId){
		return lpRepository.findByLpInstId(Integer.parseInt(lpInstId));
	}
	/**
	 * Gets the next learning scenario that we need to run given by the lpInstId
	 * TODO: find it per user
	 * 
	 * @param lpInstId
	 *            the id of the lp instance for which we want the learning
	 *            scenario
	 * @return {@link LearningScenarioInstance} that needs to be executed next
	 *         for learning or null if there is none
	 * @throws LearningPathException
	 *             with LearningPathExceptionErrorCodes.LP_RUNNING_NOT_FOUND if
	 *             the LearningScenarioInstance with the given id is not found
	 */

	public LearningScenarioInstance getNextLearningScenarioByLpInstId(String lpInstId) throws LearningPathException {
		
		LearningPathInstance lpInst = lpRepository.findByLpInstId(Integer.parseInt(lpInstId));
		if (lpInst == null) {
			throw new LearningPathException("Running Learning Path with instance id: " + lpInstId + " not found",
					LearningPathExceptionErrorCodes.LP_RUNNING_NOT_FOUND);
		}
		List<LearningScenarioInstance> nextLS = lsRepository.findBylpInstanceAndStatusOrderByOrderinLP(lpInst,
				LearningScenarioEvents.LS_STATUS_INIT);

		// if there is next
		if (nextLS.size() > 0)
			return nextLS.get(0);
		else
			return null;
	}

	/**
	 * TODO: Per user
	 * 
	 * @return a {@link LearningScenarioInstance} given a lpinstid, null if no
	 *         Learning Scenario is currently running
	 * @throws LearningPathException
	 *             with LearningPathExceptionErrorCodes.LP_RUNNING_NOT_FOUND, if
	 *             LearningPathInstance not found with the id
	 * 
	 */
	public LearningScenarioInstance getRunningLearningScenarioByIpInstId(String lpInstId) throws LearningPathException {

		LearningPathInstance lpInst = lpRepository.findByLpInstId(Integer.parseInt(lpInstId));
		if (lpInst == null) {
			throw new LearningPathException("Running Learning Path with instance id: " + lpInstId + " not found",
					LearningPathExceptionErrorCodes.LP_RUNNING_NOT_FOUND);
		}

		List<LearningScenarioInstance> curLs = lsRepository.findBylpInstanceAndStatusOrderByOrderinLP(
				lpRepository.findByLpInstId(Integer.parseInt(lpInstId)), LearningScenarioEvents.LS_STATUS_RUNNING);

		// if something is running
		if (curLs.size() > 0)
			return curLs.get(0);
		// else return null
		else
			return null;
	}

	
	@Transactional
	private void startNextLearningScenario1(String lpInstId) throws LearningPathException {
		LearningScenarioInstance lsInst = getRunningLearningScenarioByIpInstId(lpInstId);

		// if there is a LS already running, throw exception
		if (lsInst != null) {
			throw new LearningPathException(
					"A Learning Scenario for the LS ID: " + lsInst.getLsId() + " is already running",
					LearningPathExceptionErrorCodes.LP_LEARNING_SCENARIO_ALREADY_RUNNING);
		}

		lsInst = getNextLearningScenarioByLpInstId(lpInstId);
		// no next LS available throw exception
		if (lsInst == null) {
			LearningPathInstance lp = getRunningLearningPathBylpInstId(lpInstId);

			throw new LearningPathException(
					"There are no next learningscenarios available for Learning path: " + lp.getLpId(),
					LearningPathExceptionErrorCodes.LP_NO_NEXT_LEARNING_SCENARIO);
		}

		// what is starting a learning scenario instance?

		// 1. start the corresponding BPMN Process
		LearningScenario corLS = lpRepositoryService.getDeployedLearningScenario(lsInst.getLsId());
		String processId = corLS.getBpmnProcessid();
		
		// conver init oracle values into map
		List<DataObject> dos = corLS.getInitialValuation().getDataObject();
		Map<String, Object> map = new HashMap<String, Object>();
		for (DataObject sinDo : dos) {
			map.put(sinDo.getBpmnCamundaid(), sinDo.getValue());
		}
		
		//loop through map to typecase boolean values from string
		for(Map.Entry<String, Object> entry:map.entrySet()){
			
			//ifstring
			if(entry.getValue() instanceof String){
				
			
			if(entry.getValue().equals("true") || entry.getValue().equals("false")){
				entry.setValue(Boolean.parseBoolean((String)entry.getValue()));
				return;
			}
			
			/*// try parsing it to date
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			Date startDate;
			try {
				startDate = df.parse((String) entry.getValue());
				entry.setValue(startDate);
				if (entry.getValue() instanceof String) {
					System.out.println("some problem");
				}
			} catch (ParseException e) {
				// e.printStackTrace();
				// ignore
			}*/
			}
		}
		
		String processInstId = camundaRuntimeService.startProcessInstanceByKey(processId, map).getProcessInstanceId();
		
		// 2. change the status in LSI
		lsInst.setStatus("running");
		// 3. set the processinstanceid in LSI
		lsInst.setProcessInstanceId(processInstId);

		// set the first learningtask as this instances next learningtask
		LearningScenario ls = lpRepositoryService.getDeployedLearningScenario(lsInst.getLsId());
		String nextLT = corLS.getTargetVertexes().getVertex().iterator().next().getBpmnActivityid();
		lsInst.setNextLearningTask(nextLT);

		// set the initial values to the oracle
		oracleService.updateOracleValuesinit(lsInst, corLS.getInitialValuation().getDataObject());
		
		//do it also for the first task because task event called along with start event and wont give time to update oracle values
		if(corLS.getValuationOracle()!=null && corLS.getValuationOracle().getValuationFunction()!=null && 
				!corLS.getValuationOracle().getValuationFunction().isEmpty()){
			oracleService.updateOracleValues(lsInst, corLS.getValuationOracle().getValuationFunction().get(0).getDataObject());
		}
		
		lsRepository.save(lsInst);

		// save everythin

		//simulate non-learning user tasks
		lpTaskService.simulateNonLearningTasks(lsInst);
		
		
		

	}
	
	/**
	 * Starts the next LearningScenario in the line given a LpInstID. TODO: per
	 * user
	 * 
	 * @param The
	 *            LearningScenarioInstanceID
	 * @throws LearningPathException
	 *             1. LearningPathExceptionErrorCodes.LP_RUNNING_NOT_FOUND When running learningpath with the ID is not found
	 *             2. LearningPathExceptionErrorCodes.LP_LEARNING_SCENARIO_ALREADY_RUNNING when a Learning Scenario already running
	 *             3. LearningPathExceptionErrorCodes.LP_NO_NEXT_LEARNING_SCENARIO when no next learning scenario is found
	 */
	public void startNextLearningScenario(String lpInstId) throws LearningPathException {
		//first create the process and everything
		startNextLearningScenario1(lpInstId);
		
		
		
	}
}
