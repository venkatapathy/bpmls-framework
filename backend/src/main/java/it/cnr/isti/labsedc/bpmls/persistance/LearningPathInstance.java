package it.cnr.isti.labsedc.bpmls.persistance;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table
public class LearningPathInstance {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int lpInstId; 
	
	private String lpId;
	
	private String status;
	
	private String result;

	@OneToMany(mappedBy="lpInstance")
	private List<LearningScenarioInstance> learningScenarioInstances;
	
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="learnerId")
	private LearnerDetails ldInstance;
	
	
	public LearningPathInstance(String lpId, List<LearningScenarioInstance> learningScenarioInstances, String status, String result,
			 LearnerDetails ldInstance) {
		super();
		this.lpId = lpId;
		this.status = status;
		this.result = result;
		this.learningScenarioInstances = learningScenarioInstances;
		this.ldInstance = ldInstance;
	}



	
	
	
	
	public LearningPathInstance() {
		super();
		
	}







	public int getLpInstId() {
		return lpInstId;
	}

	public void setLpInstId(int lpInstId) {
		this.lpInstId = lpInstId;
	}

	public String getLpId() {
		return lpId;
	}

	public void setLpId(String lpId) {
		this.lpId = lpId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	
	@Override
    public String toString() {
        return String.format(
                "LearningPathInstance[lpInstId=%d, lpId='%s', status='%s', result='%s']",
                lpInstId, lpId, status,result);
    }

	public List<LearningScenarioInstance> getLearningScenarioInstances() {
		return learningScenarioInstances;
		
	}

	public void setLearningScenarioInstances(List<LearningScenarioInstance> learningScenarioInstaces) {
		this.learningScenarioInstances = learningScenarioInstaces;
	}



	public LearnerDetails getLdInstance() {
		return ldInstance;
	}



	public void setLdInstance(LearnerDetails ldInstance) {
		this.ldInstance = ldInstance;
	}


	



	


	
	
}
