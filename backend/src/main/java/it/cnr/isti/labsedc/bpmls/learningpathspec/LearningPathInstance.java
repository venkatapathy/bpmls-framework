package it.cnr.isti.labsedc.bpmls.learningpathspec;

import java.util.List;

import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningPath.LearningActivities.LearningActivity;

public class LearningPathInstance {
	private long lpinstid;
	private String learningpathid;
	private List<LearningScenarioInstance> completedLSIntances;
	private LearningScenarioInstance currentLSI;
	private List<LearningActivity> completedLearningActivities;
	private LearningActivity currentLearningActivity;
}
