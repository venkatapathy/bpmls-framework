package it.cnr.isti.labsedc.bpmls;

import it.cnr.isti.labsedc.bpmls.persistance.LearningPathInstance;
import it.cnr.isti.labsedc.bpmls.persistance.LearningScenarioInstance;

public interface FlowDiagramService {

	public String getLearningPathFlowDiagram(LearningPathInstance lpInst);
	
	public String getProcessDiagramDetails(LearningScenarioInstance lsInstance);
}
