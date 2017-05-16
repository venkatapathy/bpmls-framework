package it.cnr.isti.labsedc.bpmls.persistance;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table
public class OracleValue {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int oId;
	
		
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="lsInstId")
	private LearningScenarioInstance lsInstance;
	
	public OracleValue() {
		
		// TODO Auto-generated constructor stub
	}

	private String bpmnCamId;
	
	private String currentExpectedValue;

	

	public LearningScenarioInstance getLpInstance() {
		return lsInstance;
	}

	public void setLpInstance(LearningScenarioInstance lsInstance) {
		this.lsInstance = lsInstance;
	}

	public OracleValue(LearningScenarioInstance lsInstance, String bpmnCamId,
			String currentExpectedValue) {
		super();
		
		this.lsInstance = lsInstance;
		this.bpmnCamId = bpmnCamId;
		this.currentExpectedValue = currentExpectedValue;
	}

	public String getBpmnCamId() {
		return bpmnCamId;
	}

	public void setBpmnCamId(String bpmnCamId) {
		this.bpmnCamId = bpmnCamId;
	}

	public String getCurrentExpectedValue() {
		return currentExpectedValue;
	}

	public void setCurrentExpectedValue(String currentExpectedValue) {
		this.currentExpectedValue = currentExpectedValue;
	}
	
	
}
