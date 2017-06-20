package it.cnr.isti.labsedc.bpmls.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.UUID;

import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.google.gson.JsonSyntaxException;

import gov.adlnet.xapi.client.StatementClient;
import gov.adlnet.xapi.model.Activity;
import gov.adlnet.xapi.model.ActivityDefinition;
import gov.adlnet.xapi.model.Agent;
import gov.adlnet.xapi.model.Context;
import gov.adlnet.xapi.model.Statement;
import gov.adlnet.xapi.model.StatementReference;
import gov.adlnet.xapi.model.Verb;
import it.cnr.isti.labsedc.bpmls.XapiStatementService;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningPath;
import it.cnr.isti.labsedc.bpmls.persistance.LearningPathInstance;
import it.cnr.isti.labsedc.bpmls.persistance.LearningScenarioInstance;
import it.cnr.isti.labsedc.bpmls.persistance.LearningStatementJpaRepository;
import it.cnr.isti.labsedc.bpmls.persistance.LearningStatementRef;

@Service
public class XapiStatementServiceImpl implements XapiStatementService {

	private final Logger logger = LoggerFactory.getLogger(LearningProcessEngineImpl.class);

	@Autowired
	private LearningStatementJpaRepository learningStatementRepo;

	public XapiStatementServiceImpl() {
		// TODO Auto-generated constructor stub
	}

	@Async
	public void spawnAndTryPublishLPStatements(String umailbox, Verb verb, String lpId) {
		try {
			StatementClient client = new StatementClient("http://localhost:8090/v1/xAPI/", "openlrs", "openlrs");

			Activity activity = new Activity(lpId);
			ActivityDefinition acDef = new ActivityDefinition();

			Agent agent = new Agent(null, "mailto:" + umailbox);

			Statement statement = new Statement();

			statement.setId(UUID.randomUUID().toString());
			statement.setActor(agent);
			statement.setVerb(verb);
			statement.setObject(activity);

			// send the statement, if successful store the uuid for future
			// context references
			try {
				String publishedId = client.postStatement(statement);
				System.out.println("Wow published: " + publishedId);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Async
	public void spawnAndTryPublishLPStatements(String umailbox, Verb verb, LearningPathInstance lpId) {
		try {
			StatementClient client=new
			 StatementClient("http://localhost:8090/xAPI/", "openlrs",
			 "openlrs");
			 Activity activity = new Activity(lpId.getLpId());
			 Agent agent = new Agent(null, "mailto:" + umailbox);
			
			 Statement statement = new Statement();
			
			 statement.setId(UUID.randomUUID().toString());
			 statement.setActor(agent);
			 statement.setVerb(verb);
			 statement.setObject(activity);

			// adlnet
//			Statement statement = new Statement(new Agent("test", "mailto:jxapi@example.com"), new Verb("http://adlnet.gov/expapi/verbs/launched"),
//					new Activity("https://www.youtube.com/watch?v=tlBbt5niQto"));
//
//			StatementClient client=new StatementClient("https://lrs.adlnet.gov/xapi/", "xapi-tools",
//					"xapi-tools");

			// send the statement, if successful store the uuid for future
			// context references
			try {
				String publishedId = client.postStatement(statement);
				if (publishedId != null) {
					LearningStatementRef statementRef = new LearningStatementRef(publishedId,
							Integer.toString(lpId.getLpInstId()), LearningStatementRef.LearningTypeEnum.LEARNING_PATH);
					learningStatementRepo.saveAndFlush(statementRef);
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.warn("Xapi Throwed IOexception for LP Publishing. Exception is: " + e.getMessage());
			}

		} catch (JsonSyntaxException e) {
			// ignore due to a bug
			System.out.println("still error comes");
			return;
		} catch (Exception e) {
			// catching any exception because not gonna affect the application
			// for this
			logger.warn("Xapi Throwed exception for LP Publishing. Exception is: " + e.getMessage() + e.getClass());

		}
	}

	@Async
	public void spawnAndTryPublishLSStatements(String umailbox, Verb verb, LearningScenarioInstance lsInstance) {
		try {
			StatementClient client = new StatementClient("http://localhost:8090/v1/xAPI/", "openlrs", "openlrs");

			Activity activity = new Activity(lsInstance.getLsId());
			ActivityDefinition acDef = new ActivityDefinition();

			Agent agent = new Agent(null, "mailto:" + umailbox);

			Statement statement = new Statement();

			statement.setId(UUID.randomUUID().toString());
			statement.setActor(agent);
			statement.setVerb(verb);
			statement.setObject(activity);

			// send the statement, if successful store the uuid for future
			// context references
			try {
				// get the parent
				List<LearningStatementRef> statementRefs = learningStatementRepo.findByInstanceIdAndLearningType(
						Integer.toString(lsInstance.getLpInstance().getLpInstId()),
						LearningStatementRef.LearningTypeEnum.LEARNING_PATH);

				if (!statementRefs.isEmpty()) {

					Context context = new Context();
					StatementReference sf = new StatementReference();
					sf.setId(statementRefs.get(0).getStatementId());
					context.setStatement(sf);
					statement.setContext(context);
				}

				String publishedId = client.postStatement(statement);
				if (publishedId != null) {
					LearningStatementRef statementRef = new LearningStatementRef(publishedId,
							Integer.toString(lsInstance.getLsInstId()),
							LearningStatementRef.LearningTypeEnum.LEARNING_SCENARIO);
					learningStatementRepo.saveAndFlush(statementRef);
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.warn("Xapi Throwed IOexception for LS Publishing. Exception is: " + e.getMessage());
			}

		} catch (JsonSyntaxException e) {
			// ignore due to a bug
			return;
		} catch (Exception e) {
			// catching any exception because not gonna affect the application
			// for this
			logger.warn("Xapi Throwed exception for LS Publishing. Exception is: " + e.getMessage());

		}
	}

	@Async
	public void spawnAndTryPublishTaskStatements(String umailbox, Verb verb, DelegateTask task,
			LearningScenarioInstance lsInstance) {
		try {
			StatementClient client = new StatementClient("http://localhost:8090/v1/xAPI/", "openlrs", "openlrs");

			Activity activity = new Activity(task.getTaskDefinitionKey());
			ActivityDefinition acDef = new ActivityDefinition();

			Agent agent = new Agent(null, "mailto:" + umailbox);

			Statement statement = new Statement();

			statement.setId(UUID.randomUUID().toString());
			statement.setActor(agent);
			statement.setVerb(verb);
			statement.setObject(activity);

			// send the statement, if successful store the uuid for future
			// context references
			try {
				// get the parent
				List<LearningStatementRef> statementRefs = learningStatementRepo.findByInstanceIdAndLearningType(
						Integer.toString(lsInstance.getLsInstId()),
						LearningStatementRef.LearningTypeEnum.LEARNING_SCENARIO);

				if (!statementRefs.isEmpty()) {

					Context context = new Context();
					StatementReference sf = new StatementReference();
					sf.setId(statementRefs.get(0).getStatementId());
					context.setStatement(sf);
					statement.setContext(context);
				}

				String publishedId = client.postStatement(statement);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.warn("Xapi Throwed IOexception for Task Publishing. Exception is: " + e.getMessage());
			}

		} catch (JsonSyntaxException e) {
			// ignore due to a bug
			return;
		} catch (Exception e) {
			// catching any exception because not gonna affect the application
			// for this
			logger.warn("Xapi Throwed exception for Task Publishing. Exception is: " + e.getMessage());

		}
	}

	@Async
	public void spawnAndTryPublishTaskStatements(String umailbox, Verb verb, Task task,
			LearningScenarioInstance lsInstance) {
		try {
			StatementClient client = new StatementClient("http://localhost:8090/v1/xAPI/", "openlrs", "openlrs");

			Activity activity = new Activity(task.getTaskDefinitionKey());
			ActivityDefinition acDef = new ActivityDefinition();

			Agent agent = new Agent(null, "mailto:" + umailbox);

			Statement statement = new Statement();

			statement.setId(UUID.randomUUID().toString());
			statement.setActor(agent);
			statement.setVerb(verb);
			statement.setObject(activity);

			// send the statement, if successful store the uuid for future
			// context references
			try {
				// get the parent
				List<LearningStatementRef> statementRefs = learningStatementRepo.findByInstanceIdAndLearningType(
						Integer.toString(lsInstance.getLsInstId()),
						LearningStatementRef.LearningTypeEnum.LEARNING_SCENARIO);

				if (!statementRefs.isEmpty()) {

					Context context = new Context();
					StatementReference sf = new StatementReference();
					sf.setId(statementRefs.get(0).getStatementId());
					context.setStatement(sf);
					statement.setContext(context);
				}

				String publishedId = client.postStatement(statement);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.warn("Xapi Throwed IOexception for Task Publishing. Exception is: " + e.getMessage());
			}

		} catch (JsonSyntaxException e) {
			// ignore due to a bug
			return;
		} catch (Exception e) {
			// catching any exception because not gonna affect the application
			// for this
			logger.warn("Xapi Throwed exception for Task Publishing. Exception is: " + e.getMessage());

		}
	}
}
