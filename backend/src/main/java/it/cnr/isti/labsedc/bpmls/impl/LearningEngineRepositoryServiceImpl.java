package it.cnr.isti.labsedc.bpmls.impl;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.spring.boot.starter.event.ProcessApplicationStartedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import it.cnr.isti.labsedc.bpmls.LearningEngineRepositoryService;
import it.cnr.isti.labsedc.bpmls.Exceptions.LearningPathException;
import it.cnr.isti.labsedc.bpmls.Exceptions.LearningPathExceptionErrorCodes;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningPath;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenario;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenario.ValuationOracle.ValuationFunction;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenario.ValuationOracle.ValuationFunction.DataObject;

@Component
public class LearningEngineRepositoryServiceImpl implements LearningEngineRepositoryService {

	@Autowired
	HistoryService historyService;

	private final Logger logger = LoggerFactory.getLogger(LearningProcessEngineImpl.class);

	private List<LearningScenario> deployedLearningScenarios;

	private List<LearningPath> deployedLearningPaths;

	LearningEngineRepositoryServiceImpl() {
		logger.info("Empty Constructor of LearningEngineRepositoryService");
	}

	public void deployLearningPath(File bpmnFile, File learningPathFile, File[] learningScenariosFiles)
			throws LearningPathException {
		// TODO Empty deployment method
	}

	/**
	 * When you start the application as spring boot, deploy the learning
	 * scenarios
	 */
	@EventListener
	public void initLPE(final ProcessApplicationStartedEvent unused) throws Exception {
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

		// Ant-style path matching
		Resource[] resources = resolver.getResources("/schema/**");

		// Deploy LearningPath, Learning Scenarios independently using
		// DeploymentManager
		DeploymentManager learningDM = new DeploymentManager();

		// deploy learning paths
		deployedLearningPaths = learningDM.deployLearningPaths(resources);

		if (deployedLearningPaths == null) {
			logger.info("No Learning Paths to deploy");
		} else {
			logger.info("Deployed following Learning Paths from the resources classpath:");

			for (LearningPath lp : deployedLearningPaths) {
				logger.info("Deployed Learning Path: " + lp.getName());
			}
		}

		// deploy learning scenarios
		deployedLearningScenarios = learningDM.deployLearningScenarios(resources);

		if (deployedLearningScenarios == null) {
			logger.info("No Learning Sceanrios to deploy");
		} else {
			logger.info("Deployed following Learning Scenarios from the resources classpath:");

			for (LearningScenario ls : deployedLearningScenarios) {
				logger.info("Deployed Learning Scenarios: " + ls.getName());
			}
		}

	}

	/**
	 * Returns a {@link List} of {@link LearningPath} or null if none is
	 * deployed (hope not null for this will break hell with the current
	 * application. you need atleast one learning path else what good is a
	 * process-driven learning engine?
	 */
	public List<LearningPath> getDeployedLearningPaths() {
		return deployedLearningPaths;
	}

	/**
	 * Returns a {@link LearningPath}, given the lpid. Throws
	 * {@link LearningPathException} if not found with
	 * LearningPathExceptionErrorCodes.LP_NOT_FOUND
	 */

	public LearningPath getDeployedLearningPath(String lpId) throws LearningPathException {
		Iterator<LearningPath> lpIt = deployedLearningPaths.iterator();

		while (lpIt.hasNext()) {
			LearningPath lpTemp = lpIt.next();
			if (lpTemp.getId().equals(lpId)) {
				return lpTemp;
			}
		}

		throw new LearningPathException("Learning path with id, " + lpId + " not found",
				LearningPathExceptionErrorCodes.LP_NOT_FOUND);
	}

	/**
	 * Returns {@link LearningScenario}, given the lpid. Throws
	 * {@link LearningPathException} if not found with
	 * LearningPathExceptionErrorCodes.LP_LEARNING_SCENARIO_NOT_FOUND
	 */
	public LearningScenario getDeployedLearningScenario(String lsId) throws LearningPathException {
		Iterator<LearningScenario> lpIt = deployedLearningScenarios.iterator();

		while (lpIt.hasNext()) {
			LearningScenario lpTemp = lpIt.next();
			if (lpTemp.getId().equals(lsId)) {
				return lpTemp;
			}
		}

		throw new LearningPathException("Learning scenario id, " + lsId + " not found",
				LearningPathExceptionErrorCodes.LP_LEARNING_SCENARIO_NOT_FOUND);

	}

	public List<DataObject> getCurrentOracleValuesFromRepo(String processInstance, String lsId,
			String cur_bpmn_activityid) throws LearningPathException {

		// find out how many same tasks executed for the given process instance
		// remember running is also historic instance
		long prevOcc = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstance)
				.taskDefinitionKey(cur_bpmn_activityid).finished().count();

		long curOcc = 0;
		// System.out.println("Prev Occ");
		// System.out.println(prevOcc);
		// get the LS
		LearningScenario curLS = getDeployedLearningScenario(lsId);

		// get the valuation functions
		if (curLS.getValuationOracle() == null) {
			return null;
		}
		List<ValuationFunction> vFuncs = curLS.getValuationOracle().getValuationFunction();
		if (vFuncs == null) {
			return null;
		}
		// get the valueation function for the current activity
		for (ValuationFunction vFunc : vFuncs) {
			// ignore prev dataobjects based on historic prev occ
			if (curOcc == prevOcc) {
				if (vFunc.getBpmnActivityid().equals(cur_bpmn_activityid)) {

					return vFunc.getDataObject();
				}

			} else {
				curOcc += 1;
			}

		}

		return null;
	}
}
