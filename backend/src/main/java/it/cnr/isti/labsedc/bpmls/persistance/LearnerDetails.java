package it.cnr.isti.labsedc.bpmls.persistance;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table
public class LearnerDetails {

	

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int learnerId;
	
	
	private String username;
	
	private String password;

	@OneToMany(mappedBy="ldInstance")
	private List<LearningPathInstance> learningPathInstances;
	
	public LearnerDetails() {
		
	}

	public LearnerDetails(String username2, String password2) {
		this.username=username2;
		this.password=password2;
	}
	
	public int getLearnerId() {
		return learnerId;
	}

	public void setLearnerId(int learnerId) {
		this.learnerId = learnerId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
