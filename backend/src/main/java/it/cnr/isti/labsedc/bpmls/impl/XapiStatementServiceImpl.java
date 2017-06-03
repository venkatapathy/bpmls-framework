package it.cnr.isti.labsedc.bpmls.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.UUID;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import gov.adlnet.xapi.client.StatementClient;
import gov.adlnet.xapi.model.Activity;
import gov.adlnet.xapi.model.Agent;
import gov.adlnet.xapi.model.Statement;
import gov.adlnet.xapi.model.Verb;
import it.cnr.isti.labsedc.bpmls.XapiStatementService;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningPath;

@Service
public class XapiStatementServiceImpl implements XapiStatementService {

	public XapiStatementServiceImpl() {
		// TODO Auto-generated constructor stub
	}

	@Async
	public void spawnAndTryPublishLPStatements(String umailbox,Verb verb,String lpId){
		try {
			StatementClient client=new StatementClient("http://localhost:8090/v1/xAPI/", "openlrs", "openlrs");
			
			Activity activity = new Activity(lpId);
			
			Agent agent = new Agent(null, "mailto:"+umailbox);
			
			Statement statement = new Statement();
			
			statement.setId(UUID.randomUUID().toString());
			statement.setActor(agent);
			statement.setVerb(verb);
			statement.setObject(activity);
			
			//send the statement, if successful store the uuid for future context references
			try {
				String publishedId= client.postStatement(statement);
				System.out.println("Wow published: "+publishedId);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
