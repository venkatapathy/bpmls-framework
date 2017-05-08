package it.cnr.isti.labsedc.bpmls.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import it.cnr.isti.labsedc.bpmls.LearningEngineRepositoryService;
import it.cnr.isti.labsedc.bpmls.LearningEngineRuntimeService;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningPath;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningPath.LearningGoals;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningPath.LearningGoals.LearningGoal;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningPathException;
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
			LearningScenario ls = lpRepositoryService
					.getDeployedLearningScenario(lgIt.next().getLearningScenario().getLsid());

			lsInstLists.add(createaLSInst(ls, order++));
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
	private void startaLearningPath(LearningPath learningPath) throws LearningPathException {
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
		//

		// set LpInstance for all lps instance

		System.out.print("to stop");
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

	public void startaLearningPathById(String learningPathId) throws LearningPathException {
		startaLearningPath(lpRepositoryService.getDeployedLearningPath(learningPathId));
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
		
		//if there is next
		if (nextLS.size() > 0)
			return nextLS.get(0);
		else
			return null;
	}

	public LearningScenarioInstance getRunningLearningScenario(String lpInstId) {
		List<LearningScenarioInstance> curLs = lsRepository.findBylpInstanceAndStatusOrderByOrderinLP(
				lpRepository.findByLpInstId(Integer.parseInt(lpInstId)), "running");

		// if something is running
		if (curLs.size() > 0)
			return curLs.get(0);
		// else return null
		else
			return null;
	}
}
