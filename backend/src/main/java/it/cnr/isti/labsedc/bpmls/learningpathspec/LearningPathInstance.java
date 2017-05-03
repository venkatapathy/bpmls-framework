package it.cnr.isti.labsedc.bpmls.learningpathspec;

import java.util.List;

import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningPath.LearningActivities.LearningActivity;

public class LearningPathInstance {
	private String lpinstid;
	private String learningpathid;
	private String currentlsInstid;
	private List<LearningScenarioInstance> completedLSIntances;
	private LearningScenarioInstance currentLSI;
	private List<LearningActivity> completedLearningActivities;
	private LearningActivity currentLearningActivity;
	
	
	public String getLpinstid() {
		return lpinstid;
	}
	public void setLpinstid(String lpinstid) {
		this.lpinstid = lpinstid;
	}
	public String getLearningpathid() {
		return learningpathid;
	}
	public void setLearningpathid(String learningpathid) {
		this.learningpathid = learningpathid;
	}
	public String getCurrentlsInstid() {
		return currentlsInstid;
	}
	public void setCurrentlsInstid(String currentlsInstid) {
		this.currentlsInstid = currentlsInstid;
	}
	
	
}
