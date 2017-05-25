package it.cnr.isti.labsedc.bpmls.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.task.Task;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import com.google.gson.JsonObject;

import it.cnr.isti.labsedc.bpmls.FlowDiagramService;
import it.cnr.isti.labsedc.bpmls.LearningEngineRepositoryService;
import it.cnr.isti.labsedc.bpmls.LearningEngineRuntimeService;
import it.cnr.isti.labsedc.bpmls.LearningEngineTaskService;
import it.cnr.isti.labsedc.bpmls.Exceptions.LearningPathException;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningPath;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenario;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenario.TargetVertexes.Vertex;
import it.cnr.isti.labsedc.bpmls.persistance.LearningPathInstance;
import it.cnr.isti.labsedc.bpmls.persistance.LearningScenarioEvents;
import it.cnr.isti.labsedc.bpmls.persistance.LearningScenarioInstance;

@Component
public class FlowDiagramServiceImpl implements FlowDiagramService {

	private final Logger logger = LoggerFactory.getLogger(FlowDiagramServiceImpl.class);
	
	@Autowired
	LearningEngineRepositoryService lpRepositoryService;

	@Autowired
	LearningEngineRuntimeService lpRuntimeService;

	@Autowired
	LearningEngineTaskService lpTaskService;
	
	
	@Autowired
	RepositoryService camundaRepoService;

	@Autowired
	RuntimeService camundaRuntimeService;

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
		JSONObject retMsg = new JSONObject();
		if (lpInstance == null) {
			return retMsg.put("status", "error").toString();
		}
		// get the Learning Path model
		LearningPath lpModel = null;
		try {
			lpModel = lpRepositoryService.getDeployedLearningPath(lpInstance.getLpId());
		} catch (LearningPathException e) {
			
			logger.error("Unexpected error encountered when trying to get lp model for the lpinstance with id: "+lpInstance.getLpId()+" and lpinstid: "+lpInstance.getLpInstId()+". Error Message is: "+e.getMessage());
			return retMsg.put("status", "error").toString();
		}

		// var and flows
		StringBuilder varDeclarations = new StringBuilder();
		StringBuilder flowData = new StringBuilder();
		String fromNodeId = "start";
		String fromNodeName = "Start";
		String toNodeId;
		String toNodeName;
		// put the startinto variable
		varDeclarations.append(fromNodeId + "=>" + "start" + ": ").append(fromNodeName).append("\n");
		flowData.append(fromNodeId).append("(right)").append("->");
		// get all the learning scenarios
		List<LearningScenarioInstance> lsInsts = lpInstance.getLearningScenarioInstances();

		for (LearningScenarioInstance lsInst : lsInsts) {
			LearningScenario curLs = null;
			try {
				curLs = lpRepositoryService.getDeployedLearningScenario(lsInst.getLsId());
			} catch (LearningPathException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			toNodeId = curLs.getId();
			toNodeName = curLs.getName();
			// for coloring
			if (lsInst.getStatus().equals(LearningScenarioEvents.LS_STATUS_COMPLETED)) {
				toNodeName = toNodeName + "|past";

			} else if (lsInst.getStatus().equals(LearningScenarioEvents.LS_STATUS_RUNNING)) {
				toNodeName = toNodeName + "|current";
			} else {
				toNodeName = toNodeName + "|future";
			}

			

			fromNodeId = toNodeId;
			fromNodeName = toNodeName;

			flowData.append(fromNodeId).append("(right)").append("->");
			varDeclarations.append(fromNodeId + "=>" + "operation" + ": ").append(fromNodeName).append("\n");
		}

		toNodeId = "end";
		toNodeName = "End";

		

		fromNodeId = toNodeId;
		fromNodeName = toNodeName;

		flowData.append(fromNodeId);
		varDeclarations.append(fromNodeId + "=>" + "end" + ": ").append(fromNodeName).append("\n");

		return retMsg.put("status", "success").put("flowdata", varDeclarations.toString() + flowData.toString()).put("lpname", lpModel.getName())
				.toString();

	}

	public String getProcessDiagramDetails(LearningScenarioInstance lsInstance) {
		// camundaRepoService.getProcessModel(processDefinitionId)
		JSONObject retMsg = new JSONObject();
		if (lsInstance == null) {
			return retMsg.put("status", "error").toString();
		}

		LearningScenario curLs = null;
		try {
			curLs = lpRepositoryService.getDeployedLearningScenario(lsInstance.getLsId());
		} catch (LearningPathException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return retMsg.put("status", "error").toString();
		}

		if (curLs == null) {
			return retMsg.put("status", "error").toString();
		}

		String xmlData;
		try {
			// damnit too complex!
			ProcessDefinition processDefinition = camundaRepoService
			        .createProcessDefinitionQuery()
			        .processDefinitionKey(curLs.getBpmnProcessid())
			        .withoutTenantId()
			        .latestVersion()
			        .singleResult();	
			xmlData = new String(
					FileCopyUtils.copyToByteArray(camundaRepoService.getProcessModel(processDefinition.getId())),
					StandardCharsets.UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return retMsg.put("status", "error").toString();
		}

		//get the current learning task
		Task task=null;
		try {
			task=lpTaskService.getCurrentLearningTask(Integer.toString(lsInstance.getLpInstance().getLpInstId()));
		} catch (LearningPathException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return retMsg.put("status", "error").toString();
		}
		
		if(task==null){
			//cannot be null if there is a learning scenario
			//TODO log error;
			return retMsg.put("status", "error").toString();
		}
		
		boolean isTaskRunning = true;
		String currentLearningTask=task.getTaskDefinitionKey();
		
		JSONArray completed = new JSONArray();
		JSONArray available = new JSONArray();
		JSONObject running = new JSONObject();
		JSONArray trace = new JSONArray();
		//for the trace
		for(it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenario.Trace.Vertex tT:curLs.getTrace().getVertex()){
			//if the trace is currrentlearning task
			if(tT.getBpmnActivityid().equals(currentLearningTask)){
				running.put("taskid",tT.getBpmnActivityid());
				isTaskRunning = false;
			}
			
			//if the trace is a learning task
			boolean isLT=false;
			for (Vertex lT : curLs.getTargetVertexes().getVertex()) {
				if(lT.getBpmnActivityid().equals(tT.getBpmnActivityid()) && !(lT.getBpmnActivityid().equals(currentLearningTask))){
					isLT=true;
				}
			}
			
			//if learning task
			if(isLT){
				//if not running then available
				if(!isTaskRunning){
					available.put(new JSONObject().put("taskid", tT.getBpmnActivityid()));
				}else{
					//else is completed
					completed.put(new JSONObject().put("taskid",tT.getBpmnActivityid()));
				}
			}else{
				//else is a trace
				trace.put(new JSONObject().put("taskid", tT.getBpmnActivityid()));
			}
			
		}
		
		
		return retMsg.put("status","success").put("xmldata", xmlData).put("completed", completed).put("running", running).put("available", available).put("trace", trace).toString();

	}

}
