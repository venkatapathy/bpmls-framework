package it.cnr.isti.labsedc.bpmls.impl;

import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.cnr.isti.labsedc.bpmls.OracleService;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenario.InitialValuation.DataObject;
import it.cnr.isti.labsedc.bpmls.persistance.LearningScenarioInstance;
import it.cnr.isti.labsedc.bpmls.persistance.OracleValue;
import it.cnr.isti.labsedc.bpmls.persistance.OracleValuesJpaRepository;

@Component
public class OracleServiceImpl implements OracleService{
	
	@Autowired
	OracleValuesJpaRepository oracleRepo;
	
	//TODO logger
	
	private void saveOracleValue(OracleValue oVa){
		System.out.println("saving oracle values for "+oVa.getBpmnCamId());
		oracleRepo.save(oVa);
	}
	
	@Transactional
	public void updateOracleValues(LearningScenarioInstance lsInst,List<DataObject> dos){
		for(DataObject sinDo:dos){
			//check if it already exists
			OracleValue oV= oracleRepo.findBylsInstanceAndBpmnCamId(lsInst,sinDo.getBpmnCamundaid());
			
			//if not present create a new one
			if(oV==null){
				oV=new OracleValue(lsInst, sinDo.getBpmnCamundaid(),sinDo.getValue());
			}
			//else update
			else{
				oV.setCurrentExpectedValue(sinDo.getValue());
			}
			
			//saveOracleValue
			saveOracleValue(oV);
				
		}
	}
	
	public String checkOracleValues(LearningScenarioInstance lsInst,Map<String, Object> formMap){
		//for each form value check if there is oracle value and if it is the same
		boolean errExists=false;
		StringBuilder retMsg=new StringBuilder("{\"error\":");
		StringBuilder errMsg=new StringBuilder("{\"message\":\"");
		for(String formkey:formMap.keySet()){
			OracleValue oV= oracleRepo.findBylsInstanceAndBpmnCamId(lsInst,formkey);
			if(oV!=null){
				if(!oV.getCurrentExpectedValue().equals(formMap.get(formkey))){
					errExists=true;
					errMsg.append("Expected:"+oV.getCurrentExpectedValue()+"Provided: "+formMap.get(formkey)+"<br>" );
					
				}
			}
		}
		
		if(errExists){
			errMsg.append("\"}}");
			retMsg.append(errMsg);
			return retMsg.toString();
		}else{
			retMsg=new StringBuilder("{\"success\":\"success\"}");
			return retMsg.toString();
		}
		
	}
}
