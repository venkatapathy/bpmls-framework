package it.cnr.isti.labsedc.bpmls;

import java.util.List;
import java.util.Map;

import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenario.InitialValuation.DataObject;
import it.cnr.isti.labsedc.bpmls.persistance.LearningScenarioInstance;
import it.cnr.isti.labsedc.bpmls.persistance.OracleValue;

public interface OracleService {
	
	public void updateOracleValues(LearningScenarioInstance lsInst,List<DataObject> dos);
	
	public String checkOracleValues(LearningScenarioInstance lsInst,Map<String, Object> formMap);
	
	public Map<String, Object> getOracleValues();
}
