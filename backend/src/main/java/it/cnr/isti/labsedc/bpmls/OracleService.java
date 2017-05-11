package it.cnr.isti.labsedc.bpmls;

import java.util.List;

import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenario.InitialValuation.DataObject;
import it.cnr.isti.labsedc.bpmls.persistance.LearningScenarioInstance;

public interface OracleService {
	public void updateOracleValues(LearningScenarioInstance lsInst,List<DataObject> dos);
	
	
}
