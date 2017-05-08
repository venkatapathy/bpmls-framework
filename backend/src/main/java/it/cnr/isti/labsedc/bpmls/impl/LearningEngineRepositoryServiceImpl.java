package it.cnr.isti.labsedc.bpmls.impl;

import java.util.Iterator;
import java.util.List;

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
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningPath;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenario;

@Component
public class LearningEngineRepositoryServiceImpl implements LearningEngineRepositoryService {

	private final Logger logger = LoggerFactory.getLogger(LearningProcessEngineImpl.class);

	private List<LearningScenario> deployedLearningScenarios;

	private List<LearningPath> deployedLearningPaths;

	LearningEngineRepositoryServiceImpl() {
		logger.info("Empty Constructor of LearningEngineRepositoryService");
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

	public List<LearningPath> getDeployedLearningPaths() {
		return deployedLearningPaths;
	}

	public LearningPath getDeployedLearningPath(String lpId) throws LearningPathException {
		Iterator<LearningPath> lpIt = deployedLearningPaths.iterator();

		while (lpIt.hasNext()) {
			LearningPath lpTemp = lpIt.next();
			if (lpTemp.getId().equals(lpId)) {
				return lpTemp;
			}
		}

		throw new LearningPathException("Learning path with id, "+ lpId+" not found");
	}

	public LearningScenario getDeployedLearningScenario(String lsId) throws LearningPathException {
		Iterator<LearningScenario> lpIt = deployedLearningScenarios.iterator();

		while (lpIt.hasNext()) {
			LearningScenario lpTemp = lpIt.next();
			if (lpTemp.getId().equals(lsId)) {
				return lpTemp;
			}
		}

		throw new LearningPathException("Learning scenario id, "+ lsId+" not found");

	}
}
