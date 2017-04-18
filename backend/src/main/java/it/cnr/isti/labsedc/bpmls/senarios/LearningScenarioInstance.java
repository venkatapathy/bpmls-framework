package it.cnr.isti.labsedc.bpmls.senarios;

public class LearningScenarioInstance {
	private long learningScenarioInstanceid;
    private String learningScenarioId, processInstanceId;

    public LearningScenarioInstance(long learningScenarioInstanceid, String learningScenarioId, String processInstanceId) {
        this.learningScenarioInstanceid = learningScenarioInstanceid;
        this.learningScenarioId = learningScenarioId;
        this.processInstanceId = processInstanceId;
    }

    @Override
    public String toString() {
        return String.format(
                "LearningScenarioInstance[learningScenarioInstanceid=%d, learningScenarioId='%s', processInstanceId='%s']",
                learningScenarioInstanceid, learningScenarioId, processInstanceId);
    }
    
    public void setLearningScenarioInstanceid(long learningScenarioInstanceid){
    	this.learningScenarioInstanceid=learningScenarioInstanceid;
    }
    
    public long getLearningScenarioInstanceid(){
    	return this.learningScenarioInstanceid;
    }
    	
    public void setLearningScenarioId(String learningScenarioId){
    	this.learningScenarioId=learningScenarioId;
    }
    
    public String getLearningScenarioId(){
    	return this.learningScenarioId;
    }
    
    public void setProcessInstanceId(String processInstanceId){
    	this.processInstanceId=processInstanceId;
    }
    
    public String getProcessInstanceId(){
    	return this.processInstanceId;
    }
}
