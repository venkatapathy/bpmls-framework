package it.cnr.isti.labsedc.bpmls.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.spring.boot.starter.event.ProcessApplicationStartedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import it.cnr.isti.labsedc.bpmls.LearningProcessEngine;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningPath;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningPathException;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningPathInstance;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenario;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenario.ValuationOracle.ValuationFunction.DataObject;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenarioInstance;

@Component
public class LearningProcessEngineImpl implements LearningProcessEngine {
	@Autowired
	JdbcTemplate jdbcTemplate;

	private final Logger logger = LoggerFactory.getLogger(LearningProcessEngineImpl.class);

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private TaskService taskService;

	@Autowired
	private FormService formService;

	@Autowired
	private ProcessEngine processEngine;

	private List<LearningScenario> deployedLearningScenarios;

	private List<LearningPath> deployedLearningPaths;

	private List<LearningScenarioInstance> runningLearningScenarios;

	private List<LearningPathInstance> runningLearningPaths;

	LearningProcessEngineImpl() {
		logger.info("Empty Constructor of LearningProcessEngine");
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

	
	
	public void startaLearningPath(LearningPath learningPath) throws LearningPathException{
		//when you start a learning path
		
		//1. make sure that the learning path is not already started
		//PreparedStatementCreator
		Integer rowCount= jdbcTemplate.query("SELECT count(*) from learningpathinstance WHERE learningpathid=?",new Object[] { learningPath.getId() },new ResultSetExtractor<Integer>(){  
		    @Override  
		     public Integer extractData(ResultSet rs) throws SQLException,  
		            DataAccessException {  
		       
		        while(rs.next()){  
		        
		        	return rs.getInt(1);  
		        
		        }     
		        return 0;
		    }
		          
		    } );
		
		if(rowCount.intValue()!=0) throw new LearningPathException("Learning path has already been started! Cannot start again!!");
		
		
		//2. create an entry in the learning path instance table
		GeneratedKeyHolder holder = new GeneratedKeyHolder();
		int rowsaffected = jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement statement = con.prepareStatement(
						"INSERT INTO learningpathinstance(learningpathid, currentlsinst) VALUES (?, ?) ",
						Statement.RETURN_GENERATED_KEYS);
				statement.setString(1, learningPath.getId());
				statement.setInt(2, -1);
				return statement;
			}
		}, holder);

		
		//3. add to running learningpaths list
		
		//if first time init the list
		if(runningLearningPaths==null) runningLearningPaths= new ArrayList<LearningPathInstance>();
		
		runningLearningPaths.add(getRunningLearningPath(holder.getKey().toString()));
		
		logger.info("started LearningPath with instance id:");
		
	}
	
	
	
	public List<LearningPathInstance> getRunningLearningPaths(){
		return runningLearningPaths;
	}
	
	public List<LearningPath> getDeployedLearningPaths(){
		return deployedLearningPaths;
	}
	
	
	/**
	 * Returns a {@link LearningPathInstance} given its lsInstId
	 * @param lpInstId
	 * @return {@link LearningPathInstance}
	 */
	public LearningPathInstance getRunningLearningPath(String lpInstId){
		List<LearningPathInstance> rows = new ArrayList<LearningPathInstance>();
		rows = jdbcTemplate.query("SELECT * from learningpathinstance WHERE lpinstid=?", new Object[] { lpInstId },
					new BeanPropertyRowMapper<LearningPathInstance>(LearningPathInstance.class));
		// get the first row, since there should be only one

		//assumed there is only one LearningPathInstance
		return rows.iterator().next();
	}
}
