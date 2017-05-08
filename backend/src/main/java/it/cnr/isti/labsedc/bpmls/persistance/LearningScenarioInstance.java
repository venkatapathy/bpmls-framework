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
public class LearningScenarioInstance {
	public LearningScenarioInstance(){
		
	}
	
	public LearningScenarioInstance(String lsid,int orderinLP, String status,String result) {
		// TODO Auto-generated constructor stub
		this.lsId=lsid;
		this.orderinLP=orderinLP;
		this.status=status;
		this.result=result;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int lsInstId;
	
	private String lsId;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="lpInstId")
	private LearningPathInstance lpInstance;
	
	private int orderinLP;
	
	private String status;
	
	private String result;

	public int getLsInstId() {
		return lsInstId;
	}

	public void setLsInstId(int lsInstId) {
		this.lsInstId = lsInstId;
	}

	public String getLsId() {
		return lsId;
	}

	public void setLsId(String lsId) {
		this.lsId = lsId;
	}

	public LearningPathInstance getLpInstance() {
		return lpInstance;
	}

	public void setLpInstance(LearningPathInstance lpInstance) {
		this.lpInstance = lpInstance;
	}

	public int getOrderinLP() {
		return orderinLP;
	}

	public void setOrderinLP(int orderinLP) {
		this.orderinLP = orderinLP;
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
	
	
	

}
