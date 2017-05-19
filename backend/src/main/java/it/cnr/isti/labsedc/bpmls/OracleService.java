package it.cnr.isti.labsedc.bpmls;

import java.util.List;
import java.util.Map;

import it.cnr.isti.labsedc.bpmls.impl.TaskIncompleteErrorMessage;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenario.InitialValuation.DataObject;
import it.cnr.isti.labsedc.bpmls.persistance.LearningScenarioInstance;
import it.cnr.isti.labsedc.bpmls.persistance.OracleValue;

public interface OracleService {
	
	public void updateOracleValuesinit(LearningScenarioInstance lsInst,List<DataObject> dos);
	
	public void updateOracleValues(LearningScenarioInstance lsInst,List<it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenario.ValuationOracle.ValuationFunction.DataObject> dos);
	
	public List<TaskIncompleteErrorMessage> checkOracleValues(LearningScenarioInstance lsInst,Map<String, Object> formMap);
	
	public Map<String, Object> getOracleValues(LearningScenarioInstance lsInst);
}
