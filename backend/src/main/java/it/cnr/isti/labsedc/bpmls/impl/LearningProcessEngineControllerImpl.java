package it.cnr.isti.labsedc.bpmls.impl;

import static j2html.TagCreator.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.task.Task;
import org.dom4j.Branch;
import org.h2.util.New;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.cnr.isti.labsedc.bpmls.HtmlFormEngine;
import it.cnr.isti.labsedc.bpmls.LearningProcessEngine;
import it.cnr.isti.labsedc.bpmls.LearningProcessEngineController;
import it.cnr.isti.labsedc.bpmls.Exceptions.LearningPathException;
import it.cnr.isti.labsedc.bpmls.Exceptions.LearningPathExceptionErrorCodes;
import it.cnr.isti.labsedc.bpmls.Exceptions.LearningTaskException;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningPath;
import it.cnr.isti.labsedc.bpmls.persistance.LearningPathEvents;
import it.cnr.isti.labsedc.bpmls.persistance.LearningPathInstance;
import it.cnr.isti.labsedc.bpmls.persistance.LearningScenarioInstance;
import j2html.tags.ContainerTag;

@Component
@RestController
public class LearningProcessEngineControllerImpl implements LearningProcessEngineController {
	private final Logger logger = LoggerFactory.getLogger(LearningProcessEngineImpl.class);
	@Autowired
	LearningProcessEngine lpEngine;

	@Autowired
	FormService formService;

	@CrossOrigin(origins = "http://localhost:4200")
	@RequestMapping(value = "/getlearningflowdiagram/{lpid}", method = RequestMethod.GET)
	public String getLearningPathFlowDiagram(@PathVariable("lpid") String lpid){
		//get the lpinst
		LearningPathInstance lpInst=lpEngine.getLearningEngineRuntimeService().getRunningLearningPathBylpId(lpid);
		
		if(lpInst==null){
			//quitely throw error status
			JSONObject retMsg=new JSONObject();
			return retMsg.put("status", "error").toString();
		}
		
		return lpEngine.getFlowDiagramService().getLearningPathFlowDiagram(lpInst);
	}
	
	@CrossOrigin(origins = "http://localhost:4200")
	@RequestMapping(value = "/getprocessdiagramdetails/{lpid}", method = RequestMethod.GET)
	public String getProcessDiagramDetails(@PathVariable("lpid") String lpid){
		//get the lpinst
		LearningPathInstance lpInst=lpEngine.getLearningEngineRuntimeService().getRunningLearningPathBylpId(lpid);
		
		if(lpInst==null){
			//quitely throw error status
			JSONObject retMsg=new JSONObject();
			return retMsg.put("status", "error").toString();
		}
		
		LearningScenarioInstance lsInstance=null;
		try {
			lsInstance=lpEngine.getLearningEngineRuntimeService().getRunningLearningScenarioByIpInstId(Integer.toString(lpInst.getLpInstId()));
		} catch (LearningPathException e) {
			// TODO Auto-generated catch block
			JSONObject retMsg=new JSONObject();
			return retMsg.put("status", "error").toString();
		}
		
		if(lsInstance==null){
			//quitely throw error status
			JSONObject retMsg=new JSONObject();
			return retMsg.put("status", "error").toString();
		}
		
		return lpEngine.getFlowDiagramService().getProcessDiagramDetails(lsInstance);
	}
	
	/**
	 * TODO: per user Returns a JSON. {status:error,errortype: ,htmlform:dynamic
	 * content} 1. Status is error and errortype is 'lpnonexistant',if the given
	 * Learning Path if not running, in which case the client app needs to
	 * redirect the page <br>
	 * 2. No Learning scenario in progress but next in line present, status is
	 * success with a form to start the next Learning Scenario <br>
	 * 3. No Learning scenario in progress and no next in line present, status
	 * is error and errortype is 'nonextls' because this state shouldnt be
	 * reached <br>
	 * 4. Learning Scenario present and no task present, status is error and
	 * errortype is 'notask' because this state shouldnt be reached <br>
	 * 5. Learning Scenario task and task present, status is success and the
	 * htmlform is the task form
	 */
	@CrossOrigin(origins = "http://localhost:4200")
	@RequestMapping(value = "/getcurrentlearningpathstatus/{lpid}", method = RequestMethod.GET)
	public String getCurrentLPStatus(@PathVariable("lpid") String lpId) {
		// check if there any learning path engine for that
		LearningPathInstance lpInst = lpEngine.getLearningEngineRuntimeService().getRunningLearningPathBylpId(lpId);

		// {status:error,errortype: lpnonexistant,htmlform:null}
		if (lpInst == null) {
			JSONObject retJson = new JSONObject();
			retJson.put("status", "error");
			retJson.put("errortype", "lpnonexistant");
			retJson.put("errMsg", "No learning path, go back and select a proper learning path");

			return retJson.toString();
		}

		LearningScenarioInstance lsInst = null;
		try {
			lsInst = lpEngine.getLearningEngineRuntimeService()
					.getRunningLearningScenarioByIpInstId(Integer.toString(lpInst.getLpInstId()));
		} catch (LearningPathException e) {
			logger.error("Unexcepted error when trying to get running learning scenario with lpinstid: "
					+ Integer.toString(lpInst.getLpInstId()) + ". Exception message is: " + e.getMessage());
			// you shouldnt be coming here mate!!
			JSONObject retJson = new JSONObject();
			retJson.put("status", "error");
			retJson.put("errortype", "unexpected");
			retJson.put("errMsg", "Unexcepted error when trying to get running learning scenario with lpinstid: "
					+ Integer.toString(lpInst.getLpInstId()) + ". Contact Administrator with this error message!");

			return retJson.toString();
		}

		// if lsinst null then get the next Learning Scenario
		if (lsInst == null) {
			try {
				lsInst = lpEngine.getLearningEngineRuntimeService()
						.getNextLearningScenarioByLpInstId(Integer.toString(lpInst.getLpInstId()));
			} catch (LearningPathException e) {
				// TODO Auto-generated catch block
				logger.error("Unexcepted error when trying to get next learning scenario with lpinstid: "
						+ Integer.toString(lpInst.getLpInstId()) + ". Exception message is: " + e.getMessage());
				JSONObject retJson = new JSONObject();
				retJson.put("status", "error");
				retJson.put("errortype", "unexpected");
				retJson.put("errMsg", "Unexcepted error when trying to get running learning scenario with lpinstid: "
						+ Integer.toString(lpInst.getLpInstId()) + ". Contact Administrator with this error message!");

				return retJson.toString();
			}

			// no more learning scenario
			if (lsInst == null) {
				//now before congradulating, make that learning path status to completed
				logger.info("Chaning status of a running LP from running to completed with LPID: "+lpInst.getLpId()+" and LPInstID: "+lpInst.getLpInstId());
				lpEngine.getLearningEngineRuntimeService().completeaLearningPath(lpInst);
				
				
				ContainerTag resMsg = div()
						.with(text("No more learning Scenario. Congrats you completed this learning path"));
				JSONObject retJson = new JSONObject();
				retJson.put("status", "success");
				retJson.put("errortype", "nomorels");
				retJson.put("htmlform", resMsg.toString());

				return retJson.toString();

			}

			// display the details of the next learning scenario
			ContainerTag lsButton = button("Start Learing Scenario")
					.attr("(click)", "startLs(" + Integer.toString(lpInst.getLpInstId()) + ")")
					.attr("class", "btn btn-primary");

			ContainerTag lsText = div().withText("Currently you dont have learning scenarion. Start one!").with(br())
					.with(br()).with(p()).with(lsButton);

			JSONObject retJson = new JSONObject();
			retJson.put("status", "success");
			retJson.put("errortype", "nols");
			retJson.put("htmlform", lsText.render());

			return retJson.toString();

		}
		Task task = null;
		try {
			task = lpEngine.getLearningEngineTaskService()
					.getCurrentLearningTask(Integer.toString(lpInst.getLpInstId()));

			// this shouldnt happen too
			if (task == null) {
				ContainerTag resMsg = div().with(text("Congrats you completed this learnign scenario."));
				JSONObject retJson = new JSONObject();
				retJson.put("status", "success");
				retJson.put("errortype", "notask");
				retJson.put("htmlform", resMsg.toString());

				return retJson.toString();
			}

			JSONObject retJson = new JSONObject();
			retJson.put("status", "success");
			retJson.put("errortype", "");
			retJson.put("htmlform", new HtmlFormEngine().renderFormData(formService.getTaskFormData(task.getId())));
			return retJson.toString();

		} catch (LearningPathException e) {
			// program should never flow here logically
			logger.error("Unexcepted error when trying to get current learning task with lpinstid: "
					+ Integer.toString(lpInst.getLpInstId()) + ". Exception message is: " + e.getMessage());
			JSONObject retJson = new JSONObject();
			retJson.put("status", "error");
			retJson.put("errortype", "unexpected");
			retJson.put("errMsg", "Unexcepted error when trying to get current learning task with lpinstid: "
					+ Integer.toString(lpInst.getLpInstId()) + ". Contact Administrator with this error message!");
			return retJson.toString();
		}

	}

	/**
	 * TODO: per user Returns a JSON. {status:error/success, formmodel:{dynamic}
	 * content} 1. Status is error and errortype is 'lpnonexistant',if the given
	 * Learning Path if not running, in which case the client app needs to
	 * redirect the page <br>
	 * 2. No Learning scenario in progress , status is success with a empty
	 * object for formmodel <br>
	 * 3. No Learning scenario in progress and no next in line present, status
	 * is error and errortype is 'nonextls' because this state shouldnt be
	 * reached <br>
	 * 4. Learning Scenario present and no task present, status is error and
	 * errortype is 'notask' because this state shouldnt be reached <br>
	 * 5. Learning Scenario task and task present, status is success and the
	 * htmlform is the task form
	 */
	@CrossOrigin(origins = "http://localhost:4200")
	@RequestMapping(value = "/getcurrentlearningtaskmodel/{lpid}", method = RequestMethod.GET)
	public String getCurrentLearningTaskModel(@PathVariable("lpid") String lpId) {
		JSONObject retJson = new JSONObject();

		// check if there any learning path engine for that
		LearningPathInstance lpInst = lpEngine.getLearningEngineRuntimeService().getRunningLearningPathBylpId(lpId);

		if (lpInst == null) {
			retJson.put("status", "error");
			retJson.put("errorMsg", "No LearningPath found");
			return retJson.toString();
		}

		LearningScenarioInstance lsInst;
		try {
			lsInst = lpEngine.getLearningEngineRuntimeService()
					.getRunningLearningScenarioByIpInstId(Integer.toString(lpInst.getLpInstId()));
		} catch (LearningPathException e) {
			// TODO Auto-generated catch block
			logger.error("Unexcepted error when trying to get getRunningLearningScenarioByIpInstId with lpinstid: "
					+ Integer.toString(lpInst.getLpInstId()) + ". Exception message is: " + e.getMessage());
			retJson.put("status", "error");
			retJson.put("errorMsg", "Unexpected error. Contact Administrator");
			return retJson.toString();
		}

		// if lsinst null then null
		if (lsInst == null) {
			
			retJson.put("status", "success");
			retJson.put("formmodel", "");
			return retJson.toString();

		}

		Task task = null;
		try {
			task = lpEngine.getLearningEngineTaskService()
					.getCurrentLearningTask(Integer.toString(lpInst.getLpInstId()));
		} catch (LearningPathException e) {
			// TODO Auto-generated catch block
			logger.error("Unexcepted error when trying to get getRunningLearningScenarioByIpInstId with lpinstid: "
					+ Integer.toString(lpInst.getLpInstId()) + ". Exception message is: " + e.getMessage());
			retJson.put("status", "error");
			retJson.put("errorMsg", "Unexpected error. Contact Administrator");
			return retJson.toString();
		}
		if (task == null) {

			retJson.put("status", "success");
			retJson.put("formmodel", "");
			return retJson.toString();
		}

		// return tthe model
		retJson = new JSONObject();
		
		
		return retJson.put("status", "success").put("formmodel", new HtmlFormEngine().getFormModel(formService.getTaskFormData(task.getId()))).toString();

	}

	@CrossOrigin(origins = "http://localhost:4200")
	@RequestMapping(value = "/getavailablelearningpaths", method = RequestMethod.GET)
	public String getAvailableLearningPaths() {
		// first get the deployed learning paths
		JSONArray lparray = new JSONArray();

		StringBuilder availLPs = new StringBuilder("{\"alps\": [");
		List<LearningPath> lps = lpEngine.getLearningEngineRepositoryService().getDeployedLearningPaths();

		List<LearningPathInstance> lpInst = lpEngine.getLearningEngineRuntimeService().getRunningLearningPaths();

		if (lps != null) {
			for (LearningPath lp : lps) {

				// if not already running
				if (lpEngine.getLearningEngineRuntimeService().getRunningLearningPathBylpId(lp.getId()) == null) {
					lparray.put(new JSONObject().put("lpid", lp.getId()).put("lpname", lp.getName()));

					/*
					 * availLPs.append("{"); availLPs.append("\"lpid\":\"");
					 * availLPs.append(lp.getId() + "\",");
					 * 
					 * availLPs.append("\"lpname\":\"");
					 * availLPs.append(lp.getName() + "\"");
					 * availLPs.append("},");
					 */
				}
			}
			return new JSONObject().put("alps", lparray).put("status", "success").toString();
		} else {
			return new JSONObject().put("alps", lparray).put("status", "error")
					.put("errMsg",
							"All deployed Learning Paths are selected by you for learning! Good Going. Select 'Running Learning Path' page")
					.toString();
		}

	}

	@CrossOrigin(origins = "http://localhost:4200")
	@RequestMapping(value = "/getrunningpaths", method = RequestMethod.GET)
	public String getRunningPaths() {
		// first get the deployed learning paths
		JSONArray lparray = new JSONArray();

		List<LearningPathInstance> lpInst = lpEngine.getLearningEngineRuntimeService().getRunningLearningPaths();

		if (lpInst != null) {
			for (LearningPathInstance lp : lpInst) {
				LearningPath dlp;
				try {
					dlp = lpEngine.getLearningEngineRepositoryService().getDeployedLearningPath(lp.getLpId());
				} catch (LearningPathException e) {
					// TODO Auto-generated catch block
					logger.error("Error while looking up for deployed learning path with learning path instance id: "
							+ lp.getLpInstId() + " and lp id: " + lp.getLpId());
					return new JSONObject().put("rlps", lparray).put("status", "error")
							.put("errMsg", "Error while looking up for running learning path. Contact Administrator")
							.toString();
				}
				// if not already running
				if (dlp != null) {
					lparray.put(new JSONObject().put("lpid", lp.getLpId()).put("lpname", dlp.getName()));

				}
			}
		}

		return new JSONObject().put("rlps", lparray).put("status", "success").toString();

	}

	@CrossOrigin(origins = "http://localhost:4200")
	@RequestMapping(value = "/startalearningpath/{lpid}", method = RequestMethod.POST)
	public String startalearningpath(@PathVariable("lpid") String lpid) {
		// start the learning path
		try {
			lpEngine.getLearningEngineRuntimeService().startaLearningPathById(lpid);

			logger.info("Starting a Learning path with LP ID: " + lpid);
			JSONObject retJson = new JSONObject();
			retJson.put("status", "success").put("lpid", lpid);
			return retJson.toString();
		} catch (LearningPathException e) {
			/*
			 * 1. LearningPathExceptionErrorCodes.LP_RUNNING_NOT_FOUND When
			 * running learningpath with the ID is not found 2.
			 * LearningPathExceptionErrorCodes.
			 * LP_LEARNING_SCENARIO_ALREADY_RUNNING when a Learning Scenario
			 * already running 3.
			 * LearningPathExceptionErrorCodes.LP_NO_NEXT_LEARNING_SCENARIO when
			 * no next learning scenario is found
			 */
			logger.error("Error starting a Learning path with LP ID: " + lpid + "Error is: " + e.getMessage());
			JSONObject retJson = new JSONObject();
			retJson.put("status", "error");
			return retJson.put("errMsg", new JSONObject().put("message", e.getMessage())).toString();

			//

		}

	}

	@CrossOrigin(origins = "http://localhost:4200")
	@RequestMapping(value = "/startalearningscenario/{lpid}/{lpinstid}", method = RequestMethod.POST)
	public String startalearningscenario(@PathVariable("lpid") String lpid, @PathVariable("lpinstid") String lpinstid) {

		try {
			lpEngine.getLearningEngineRuntimeService().startNextLearningScenario(lpinstid);
			logger.info("Starting next Learning Scenario with lpid:"+lpid+" and lpinstid: "+lpinstid);
			return new JSONObject().put("status", "success").put("lpid", lpid).toString();
		} catch (LearningPathException e) {
			// TODO Auto-generated catch block
			logger.error("Unexpected error while starting next Learning Scenario with lpid:"+lpid+" and lpinstid: "+lpinstid);
			JSONObject retJson = new JSONObject();
			retJson.put("status", "error");
			return retJson.put("errMsg", new JSONObject().put("message", e.getMessage())).toString();
		}
	}

	@CrossOrigin(origins = "http://localhost:4200")
	@RequestMapping(value = "/completelearningtask/{lpid}", method = RequestMethod.POST)
	public String completeCurrentLearningTask(@PathVariable("lpid") String lpid, @RequestBody String responseJSON)
			throws LearningPathException, JsonParseException, JsonMappingException, IOException {
		// get the learning instance
		LearningPathInstance lpInst = lpEngine.getLearningEngineRuntimeService().getRunningLearningPathBylpId(lpid);

		// if no lpinst with that id
		if (lpInst == null) {
			throw new LearningPathException("Running Learning Path with instance id: " + lpid + " not found",
					LearningPathExceptionErrorCodes.LP_RUNNING_NOT_FOUND);
		}
		LearningScenarioInstance lsInst = lpEngine.getLearningEngineRuntimeService()
				.getRunningLearningScenarioByIpInstId(Integer.toString(lpInst.getLpInstId()));
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = mapper.readValue(responseJSON, Map.class);

		Map<String, Object> formMap = (Map) map.get("learningform");
		try {
			lpEngine.getLearningEngineTaskService().completeCurrentLearningTask(lsInst, formMap);
			// if all is well return
		} catch (LearningTaskException e) {
			JSONArray errArr = new JSONArray();
			for (TaskIncompleteErrorMessage errTask : e.userInputErrorMsgs) {
				errArr.put(new JSONObject().put("Id", errTask.label).put("expected", errTask.expectedValue)
						.put("provided", errTask.providedValue));

			}
			JSONObject retJson = new JSONObject();
			retJson.put("status", "error").put("errMsg", errArr);
			return retJson.toString();
		}
		JSONObject retJson = new JSONObject();
		retJson.put("status", "success");
		return retJson.toString();
	}
}
