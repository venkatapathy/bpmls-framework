package it.cnr.isti.labsedc.bpmls;

import org.springframework.scheduling.annotation.Async;

import gov.adlnet.xapi.model.Verb;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningPath;

public interface XapiStatementService {

	@Async
	public void spawnAndTryPublishLPStatements(String umailbox,Verb verb,String lpid);
}