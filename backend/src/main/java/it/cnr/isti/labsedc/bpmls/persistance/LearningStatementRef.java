package it.cnr.isti.labsedc.bpmls.persistance;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class LearningStatementRef {

	public static interface LearningTypeEnum{
		static String LEARNING_PATH="learningpath";
		static String LEARNING_SCENARIO="learningscenario";
		static String LEARNING_ACTIVITY="learningactivity";
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int refId;
	
	
	private String statementId;
	
	private String instanceId;
	
	private String learningType;
	
	public LearningStatementRef(String statementId, String instanceId, String learningType) {
		super();
		this.refId = refId;
		this.statementId = statementId;
		this.instanceId = instanceId;
		this.learningType = learningType;
	}

	public LearningStatementRef() {
		// Auto-generated constructor stub
	}

	public int getRefId() {
		return refId;
	}

	public void setRefId(int refId) {
		this.refId = refId;
	}

	public String getStatementId() {
		return statementId;
	}

	public void setStatementId(String statementId) {
		this.statementId = statementId;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public String getLearningType() {
		return learningType;
	}

	public void setLearningType(String learningType) {
		this.learningType = learningType;
	}

}
