package it.cnr.isti.labsedc.bpmls.impl;

import java.util.List;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.builder.ManualTaskBuilder;
import org.camunda.bpm.model.bpmn.builder.ProcessBuilder;
import org.camunda.bpm.model.bpmn.builder.StartEventBuilder;
import org.camunda.bpm.model.bpmn.impl.instance.bpmndi.BpmnDiagramImpl;
import org.camunda.bpm.model.bpmn.instance.Definitions;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnDiagram;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnPlane;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;

import it.cnr.isti.labsedc.bpmls.FlowDiagramService;
import it.cnr.isti.labsedc.bpmls.LearningEngineRepositoryService;
import it.cnr.isti.labsedc.bpmls.LearningEngineRuntimeService;
import it.cnr.isti.labsedc.bpmls.Exceptions.LearningPathException;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningPath;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenario;
import it.cnr.isti.labsedc.bpmls.persistance.LearningPathInstance;
import it.cnr.isti.labsedc.bpmls.persistance.LearningScenarioEvents;
import it.cnr.isti.labsedc.bpmls.persistance.LearningScenarioInstance;

@Component
public class FlowDiagramServiceImpl implements FlowDiagramService {

	@Autowired
	LearningEngineRepositoryService lpRepositoryService;
	
	@Autowired
	LearningEngineRuntimeService lpRuntimeService;
	
	public FlowDiagramServiceImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getLearningPathFlowDiagram(LearningPathInstance lpInstance) {
		/*
		 * BpmnModelInstance modelInstance = Bpmn.createProcess() .startEvent()
		 * .userTask() .endEvent() .done();
		 */
		
		// if null return null
		JSONObject retMsg=new JSONObject();
		if(lpInstance==null){
			return retMsg.put("status", "error").toString();
		}
		//get the Learning Path model
		LearningPath lpModel=null;
		try {
			lpModel = lpRepositoryService.getDeployedLearningPath(lpInstance.getLpId());
		} catch (LearningPathException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//create a bpmn model
		StartEventBuilder modelInstance = Bpmn.createProcess().name(lpModel.getName()).startEvent();
		
		
		//get all the learning scenarios
		List<LearningScenarioInstance> lsInsts=lpInstance.getLearningScenarioInstances();
		
		JSONArray completed=new JSONArray();
		JSONArray available=new JSONArray();
		JSONObject running=new JSONObject();
		
		ManualTaskBuilder nextLSTask=null;
		for(LearningScenarioInstance lsInst:lsInsts){
			LearningScenario curLs=null;
			try {
				curLs = lpRepositoryService.getDeployedLearningScenario(lsInst.getLsId());
			} catch (LearningPathException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(nextLSTask==null){
				nextLSTask=modelInstance.manualTask().id(curLs.getId()).name(curLs.getName());
			}else{
				nextLSTask=nextLSTask.manualTask().id(curLs.getId()).name(curLs.getName());
			}
			
			//for coloring
			if(lsInst.getStatus().equals(LearningScenarioEvents.LS_STATUS_COMPLETED)){
				
				completed.put(new JSONObject().put("taskid",curLs.getId()));
				
			}else if(lsInst.getStatus().equals(LearningScenarioEvents.LS_STATUS_RUNNING)){
				running.put("taskid",curLs.getId());
			}else{
				available.put(new JSONObject().put("taskid",curLs.getId()));
			}
		}
		
		//
		//completetask and put it iin json
		BpmnModelInstance completedPathFlow= nextLSTask.endEvent().done();
		
		BpmnDiagram diagram=completedPathFlow.newInstance(BpmnDiagram.class);
		
		
		BpmnPlane plane = completedPathFlow.newInstance(BpmnPlane.class);
		
		org.camunda.bpm.model.bpmn.instance.Process process= completedPathFlow.getDefinitions().getChildElementsByType(Process.class).iterator().next();
		plane.setBpmnElement(process);
		diagram.setBpmnPlane(plane);
		
		//start, all manual tasks
	
		
		completedPathFlow.getDefinitions().addChildElement(diagram);
		// create the process
	    
	    
		
		return retMsg.put("status","success").put("data", Bpmn.convertToString(completedPathFlow)).put("completed", completed).put("running", running).put("available", available).toString();
		
		
		
	}

}
