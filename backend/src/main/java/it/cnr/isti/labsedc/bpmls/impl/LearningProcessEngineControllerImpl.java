package it.cnr.isti.labsedc.bpmls.impl;

import static j2html.TagCreator.br;
import static j2html.TagCreator.button;
import static j2html.TagCreator.div;
import static j2html.TagCreator.p;
import static j2html.TagCreator.text;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.security.sasl.AuthenticationException;
import javax.transaction.Transactional;

import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.form.FormField;
import org.camunda.bpm.engine.form.FormFieldValidationConstraint;
import org.camunda.bpm.engine.impl.form.engine.HtmlDocumentBuilder;
import org.camunda.bpm.engine.impl.form.engine.HtmlElementWriter;
import org.camunda.bpm.engine.impl.form.type.BooleanFormType;
import org.camunda.bpm.engine.task.Task;
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

import com.fasterxml.jackson.databind.ObjectMapper;

import it.cnr.isti.labsedc.bpmls.HtmlFormEngine;
import it.cnr.isti.labsedc.bpmls.LearningProcessEngine;
import it.cnr.isti.labsedc.bpmls.LearningProcessEngineController;
import it.cnr.isti.labsedc.bpmls.Exceptions.LearningPathException;
import it.cnr.isti.labsedc.bpmls.Exceptions.LearningPathExceptionErrorCodes;
import it.cnr.isti.labsedc.bpmls.Exceptions.LearningTaskException;
import it.cnr.isti.labsedc.bpmls.Exceptions.UserAuthenticationException;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningPath;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenario;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenario.TargetVertexes.Vertex;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenario.ValuationOracle.ValuationFunction;
import it.cnr.isti.labsedc.bpmls.persistance.LearnerDetails;
import it.cnr.isti.labsedc.bpmls.persistance.LearnerDetailsJpaRepository;
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

	@Autowired
	TaskService taskService;

	@Autowired
	LearnerDetailsJpaRepository userJpaDBRepo;

	private LearnerDetails authenticateUserInternally(String userName) throws UserAuthenticationException {
		LearnerDetails user = userJpaDBRepo.findByUsername(userName);

		if (user == null) {
			throw new UserAuthenticationException("User not authenticated. This incident will be logged!!");
		}
		return user;
	}

	@CrossOrigin(origins = "http://localhost:4200")
	@RequestMapping(value = "/authenticateuser", method = RequestMethod.POST)
	public String authenticateUser(@RequestBody String responseJSON) {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = null;
		String username = null;
		String password = null;
		LearnerDetails user = null;

		try {
			map = mapper.readValue(responseJSON, Map.class);
			username = (String) map.get("username");
			password = (String) map.get("password");

			// check if the user is already present
			user = userJpaDBRepo.findByUsername(username);
			if (user == null) {
				throw new UserAuthenticationException("Username or Password not valid!!");
			}
			logger.info("Loggedin User: " + username);
			JSONObject retMsg = new JSONObject();
			return retMsg.put("status", "success").put("username", username).toString();

		} catch (IOException e1) {

			logger.error("IOException while authenticating user: " + username + ". Exception is: " + e1.getMessage());
			JSONObject retJson = new JSONObject();
			retJson.put("status", "unexpectederror");
			return retJson.put("errMsg", new JSONObject().put("message", e1.getMessage())).toString();
		} catch (UserAuthenticationException e) {

			JSONObject retJson = new JSONObject();
			retJson.put("status", "error");
			return retJson.put("errMsg", e.getMessage()).toString();
		}
	}

	@CrossOrigin(origins = "http://localhost:4200")
	@RequestMapping(value = "/registernewuser", method = RequestMethod.POST)
	@Transactional
	public String registerNewUser(@RequestBody String responseJSON) {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = null;
		String username = null;
		String password = null;
		LearnerDetails user = null;

		try {
			map = mapper.readValue(responseJSON, Map.class);
			username = (String) map.get("username");
			password = (String) map.get("password");

			// check if the user is already present
			user = userJpaDBRepo.findByUsername(username);
			if (user != null) {
				throw new UserAuthenticationException("Username already present. Please choose another username!!");
			}

			LearnerDetails newuser = new LearnerDetails(username, password);

			userJpaDBRepo.save(newuser);

			logger.info("Registered new user: " + username);
			JSONObject retMsg = new JSONObject();
			return retMsg.put("status", "success").toString();

		} catch (IOException e1) {

			logger.error("IOException while registering user: " + username + ". Exception is: " + e1.getMessage());
			JSONObject retJson = new JSONObject();
			retJson.put("status", "unexpectederror");
			return retJson.put("errMsg", new JSONObject().put("message", e1.getMessage())).toString();
		} catch (UserAuthenticationException e) {

			JSONObject retJson = new JSONObject();
			retJson.put("status", "error");
			return retJson.put("errMsg", e.getMessage()).toString();
		}
	}

	@CrossOrigin(origins = "http://localhost:4200")
	@RequestMapping(value = "/getlearningflowdiagram/{lpid}", method = RequestMethod.POST)
	public String getLearningPathFlowDiagram(@PathVariable("lpid") String lpid, @RequestBody String responseJSON) {
		// userauthentication
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = null;
		String username = null;
		LearnerDetails user = null;

		try {
			map = mapper.readValue(responseJSON, Map.class);
			username = (String) map.get("username");
			user = authenticateUserInternally(username);

			if (user == null) {
				throw new UserAuthenticationException("User Authetication failed! Please login again!!");
			}
		} catch (IOException e1) {

			logger.error("IOException while reading responseJSON to getRunningPaths for user: " + username
					+ ". Exception is: " + e1.getMessage());
			JSONObject retJson = new JSONObject();
			retJson.put("status", "unexpectederror");
			return retJson.put("errMsg", new JSONObject().put("message", e1.getMessage())).toString();
		} catch (UserAuthenticationException e) {
			logger.error("User Authentication Failed for user: " + username);
			JSONObject retJson = new JSONObject();
			retJson.put("status", "error");
			return retJson.put("errMsg", e.getMessage()).toString();
		}

		// get the lpinst
		LearningPathInstance lpInst = lpEngine.getLearningEngineRuntimeService().getRunningLearningPathBylpId(lpid,
				user);

		if (lpInst == null) {
			// dont throw yet get the completed one
			lpInst = lpEngine.getLearningEngineRuntimeService().getCompletedLearningPathBylpId(lpid, user);
			if (lpInst == null) {
				// quitely throw error status
				JSONObject retMsg = new JSONObject();
				return retMsg.put("status", "error").toString();
			}
			return lpEngine.getFlowDiagramService().getLearningPathFlowDiagram(lpInst);

		}

		return lpEngine.getFlowDiagramService().getLearningPathFlowDiagram(lpInst);
	}

	@CrossOrigin(origins = "http://localhost:4200")
	@RequestMapping(value = "/getoraclevalues/{lpid}", method = RequestMethod.POST)
	public String gerOracleValues(@PathVariable("lpid") String lpid, @RequestBody String responseJSON) {
		// userauthentication
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = null;
		String username = null;
		LearnerDetails user = null;

		try {
			map = mapper.readValue(responseJSON, Map.class);
			username = (String) map.get("username");
			user = authenticateUserInternally(username);

			if (user == null) {
				throw new UserAuthenticationException("User Authetication failed! Please login again!!");
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			logger.error("IOException while reading responseJSON to getRunningPaths for user: " + username
					+ ". Exception is: " + e1.getMessage());
			JSONObject retJson = new JSONObject();
			retJson.put("status", "unexpectederror");
			return retJson.put("errMsg", new JSONObject().put("message", e1.getMessage())).toString();
		} catch (UserAuthenticationException e) {
			logger.error("User Authentication Failed for user: " + username);
			JSONObject retJson = new JSONObject();
			retJson.put("status", "error");
			return retJson.put("errMsg", e.getMessage()).toString();
		}

		// get the lpinst
		LearningPathInstance lpInst = lpEngine.getLearningEngineRuntimeService().getRunningLearningPathBylpId(lpid,
				user);

		if (lpInst == null) {
			// quitely throw error status
			JSONObject retMsg = new JSONObject();
			return retMsg.put("status", "error").toString();
		}

		LearningScenarioInstance lsInstance = null;
		try {
			lsInstance = lpEngine.getLearningEngineRuntimeService()
					.getRunningLearningScenarioByIpInstId(Integer.toString(lpInst.getLpInstId()));
		} catch (LearningPathException e) {
			// TODO Auto-generated catch block
			JSONObject retMsg = new JSONObject();
			return retMsg.put("status", "error").toString();
		}

		if (lsInstance == null) {
			// quitely throw error status
			JSONObject retMsg = new JSONObject();
			return retMsg.put("status", "error").toString();
		}

		Map<String, Object> oValues = lpEngine.getOracleService().getOracleValues(lsInstance);

		if (oValues == null) {
			// quitely throw error status
			JSONObject retMsg = new JSONObject();
			return retMsg.put("status", "error").toString();
		}

		JSONArray row = new JSONArray();
		for (Map.Entry<String, Object> entry : oValues.entrySet()) {
			row.put(new JSONObject().put("id", entry.getKey()).put("value", entry.getValue()));

		}

		JSONObject retMsg = new JSONObject();
		return retMsg.put("status", "success").put("oracledata", row).toString();
	}

	@CrossOrigin(origins = "http://localhost:4200")
	@RequestMapping(value = "/getprocessdiagramdetails/{lpid}", method = RequestMethod.POST)
	public String getProcessDiagramDetails(@PathVariable("lpid") String lpid, @RequestBody String responseJSON) {

		// userauthentication
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = null;
		String username = null;
		LearnerDetails user = null;

		try {
			map = mapper.readValue(responseJSON, Map.class);
			username = (String) map.get("username");
			user = authenticateUserInternally(username);

			if (user == null) {
				throw new UserAuthenticationException("User Authetication failed! Please login again!!");
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			logger.error("IOException while reading responseJSON to getRunningPaths for user: " + username
					+ ". Exception is: " + e1.getMessage());
			JSONObject retJson = new JSONObject();
			retJson.put("status", "unexpectederror");
			return retJson.put("errMsg", new JSONObject().put("message", e1.getMessage())).toString();
		} catch (UserAuthenticationException e) {
			logger.error("User Authentication Failed for user: " + username);
			JSONObject retJson = new JSONObject();
			retJson.put("status", "error");
			return retJson.put("errMsg", e.getMessage()).toString();
		}

		// get the lpinst
		LearningPathInstance lpInst = lpEngine.getLearningEngineRuntimeService().getRunningLearningPathBylpId(lpid,
				user);

		if (lpInst == null) {
			// quitely throw error status
			JSONObject retMsg = new JSONObject();
			return retMsg.put("status", "error").toString();
		}

		LearningScenarioInstance lsInstance = null;
		try {
			lsInstance = lpEngine.getLearningEngineRuntimeService()
					.getRunningLearningScenarioByIpInstId(Integer.toString(lpInst.getLpInstId()));
		} catch (LearningPathException e) {
			// TODO Auto-generated catch block
			JSONObject retMsg = new JSONObject();
			return retMsg.put("status", "error").toString();
		}

		if (lsInstance == null) {
			// quitely throw error status
			JSONObject retMsg = new JSONObject();
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
	@RequestMapping(value = "/getcurrentlearningpathstatus/{lpid}", method = RequestMethod.POST)
	public String getCurrentLPStatus(@PathVariable("lpid") String lpId, @RequestBody String responseJSON) {
		// userauthentication
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = null;
		String username = null;
		LearnerDetails user = null;

		try {
			map = mapper.readValue(responseJSON, Map.class);
			username = (String) map.get("username");
			user = authenticateUserInternally(username);

			if (user == null) {
				throw new UserAuthenticationException("User Authetication failed! Please login again!!");
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			logger.error("IOException while reading responseJSON to getRunningPaths for user: " + username
					+ ". Exception is: " + e1.getMessage());
			JSONObject retJson = new JSONObject();
			retJson.put("status", "unexpectederror");
			return retJson.put("errMsg", new JSONObject().put("message", e1.getMessage())).toString();
		} catch (UserAuthenticationException e) {
			logger.error("User Authentication Failed for user: " + username);
			JSONObject retJson = new JSONObject();
			retJson.put("status", "error");
			return retJson.put("errMsg", e.getMessage()).toString();
		}

		// check if there any learning path engine for that
		LearningPathInstance lpInst = lpEngine.getLearningEngineRuntimeService().getRunningLearningPathBylpId(lpId,
				user);

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
			retJson.put("status", "unexpectederror");
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

				logger.error("Unexcepted error when trying to get next learning scenario with lpinstid: "
						+ Integer.toString(lpInst.getLpInstId()) + ". Exception message is: " + e.getMessage());
				JSONObject retJson = new JSONObject();
				retJson.put("status", "unexpectederror");
				retJson.put("errortype", "unexpected");
				retJson.put("errMsg", "Unexcepted error when trying to get running learning scenario with lpinstid: "
						+ Integer.toString(lpInst.getLpInstId()) + ". Contact Administrator with this error message!");

				return retJson.toString();
			}

			// no more learning scenario
			if (lsInst == null) {
				// now before congradulating, make that learning path status to
				// completed
				logger.info("Chaning status of a running LP from running to completed with LPID: " + lpInst.getLpId()
						+ " and LPInstID: " + lpInst.getLpInstId() + " for User: " + username);
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
			retJson.put("demostage", "freshSimulator");

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
			// html form should have scenario hint, task hint and then form with
			// do hints
			// get the ls
			StringBuilder htmlRet = new StringBuilder();
			LearningScenario ls = lpEngine.getLearningEngineRepositoryService()
					.getDeployedLearningScenario(lsInst.getLsId());
			// if null quitely sink
			if (ls != null) {
				// <div class="typography-widget" style="color:#dfb81c"
				String hint = ls.getScenariocontexthint();
				if (hint != null) {
					retJson.put("lsname", ls.getName());
					retJson.put("lshint", hint);
					HtmlElementWriter pElement = new HtmlElementWriter("br", true);
					HtmlElementWriter helpToggleButtonElement = new HtmlElementWriter("button")
							.attribute("(click)", "toggleHelp()").attribute("type", "button")
							.attribute("class", "btn btn-with-icon")
							.attribute("style", "color:#FFFFFF; background-color: #008080");

					HtmlElementWriter iIconForHelpButton = new HtmlElementWriter("i")
							.attribute("class", "ion-information").textContent("Toggle Help");

					HtmlDocumentBuilder documentBuilder = new HtmlDocumentBuilder(pElement)
							.startElement(helpToggleButtonElement).startElement(iIconForHelpButton).endElement()
							.endElement()
							.startElement(new HtmlElementWriter("h1").textContent("Task Name:" + task.getName()))
							.endElement().startElement(new HtmlElementWriter("br", true));

					htmlRet.append(documentBuilder.getHtmlString());
				}

				hint = getTaskHint(ls, task.getTaskDefinitionKey());
				if (hint != null) {
					retJson.put("ltname", task.getName());
					retJson.put("lthint", hint);
					HtmlElementWriter pElement = new HtmlElementWriter("br", true);

					HtmlDocumentBuilder documentBuilder = new HtmlDocumentBuilder(pElement);

					htmlRet.append(documentBuilder.startElement(new HtmlElementWriter("br", true))
							.startElement(new HtmlElementWriter("h1").textContent("Task Form")).endElement()
							.getHtmlString());
				}

			}

			// get the valuation function
			ValuationFunction tVfuc = null;
			if (ls.getValuationOracle() != null) {
				for (ValuationFunction vF : ls.getValuationOracle().getValuationFunction()) {
					if (vF.getBpmnActivityid().equals(task.getTaskDefinitionKey())) {
						tVfuc = vF;
						break;
					}
				}
			}

			retJson.put("htmlform",
					htmlRet.append(
							new HtmlFormEngine().renderFormData(formService.getTaskFormData(task.getId()), tVfuc))
							.toString());
			// System.out.println(htmlRet.toString());
			return retJson.toString();

		} catch (LearningPathException e) {
			// program should never flow here logically
			logger.error("Unexcepted error when trying to get current learning task with lpinstid: "
					+ Integer.toString(lpInst.getLpInstId()) + ". Exception message is: " + e.getMessage());
			JSONObject retJson = new JSONObject();
			retJson.put("status", "unexpectederror");
			retJson.put("errortype", "unexpected");
			retJson.put("errMsg", "Unexcepted error when trying to get current learning task with lpinstid: "
					+ Integer.toString(lpInst.getLpInstId()) + ". Contact Administrator with this error message!");
			return retJson.toString();
		}

	}

	private String getTaskHint(LearningScenario ls, String taskId) {
		for (Vertex v : ls.getTargetVertexes().getVertex()) {
			if (v.getBpmnActivityid().equals(taskId)) {
				return v.getActivitycontexthint();
			}
		}
		return null;
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
	@RequestMapping(value = "/getcurrentlearningtaskmodel/{lpid}", method = RequestMethod.POST)
	public String getCurrentLearningTaskModel(@PathVariable("lpid") String lpId, @RequestBody String responseJSON) {
		// userauthentication
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = null;
		String username = null;
		LearnerDetails user = null;

		try {
			map = mapper.readValue(responseJSON, Map.class);
			username = (String) map.get("username");
			user = authenticateUserInternally(username);

			if (user == null) {
				throw new UserAuthenticationException("User Authetication failed! Please login again!!");
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			logger.error("IOException while reading responseJSON to getRunningPaths for user: " + username
					+ ". Exception is: " + e1.getMessage());
			JSONObject retJson = new JSONObject();
			retJson.put("status", "unexpectederror");
			return retJson.put("errMsg", new JSONObject().put("message", e1.getMessage())).toString();
		} catch (UserAuthenticationException e) {
			logger.error("User Authentication Failed for user: " + username);
			JSONObject retJson = new JSONObject();
			retJson.put("status", "error");
			return retJson.put("errMsg", e.getMessage()).toString();
		}

		JSONObject retJson = new JSONObject();

		// check if there any learning path engine for that
		LearningPathInstance lpInst = lpEngine.getLearningEngineRuntimeService().getRunningLearningPathBylpId(lpId,
				user);

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
			retJson.put("status", "unexpectederror");
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
			retJson.put("status", "unexpectederror");
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

		// change of plan get the task variables and send them
		// not working task variables get the oracl

		Map<String, Object> mapT = lpEngine.getOracleService().getOracleValues(lsInst);

		if (mapT != null) {
			
				// if present in task form
				for (FormField formField : formService.getTaskFormData(task.getId()).getFormFields()) {
					if (mapT.get(formField.getId()) != null) {
						// if readonly
						boolean isReadonly=false;
						List<FormFieldValidationConstraint> validationConstraints = formField
								.getValidationConstraints();
						if (validationConstraints != null) {
							for (FormFieldValidationConstraint validationConstraint : validationConstraints) {
								if ("readonly".equals(validationConstraint.getName())) {
									isReadonly= true;
								}
							}
						}
						
						if(isReadonly){
							retJson.put(formField.getId(), mapT.get(formField.getId()));
						}else{
							if(BooleanFormType.TYPE_NAME.equals(formField.getTypeName())){
								if((Boolean)mapT.get(formField.getId())){
									retJson.put(formField.getId(),"false");
								}else{
									retJson.put(formField.getId(),"true");
								}
								
							}else{
								retJson.put(formField.getId(),"");
								}
							
						}
						
					}
				}
				

			
			JSONObject newretJson = new JSONObject();
			newretJson.put("status", "success").put("formmodel", (new JSONObject().put("learningform", retJson)));
			return newretJson.toString();

		} else {
			retJson.put("status", "success");
			retJson.put("formmodel", "");
			return retJson.toString();
		}

		/*
		 * return retJson.put("status", "success") .put("formmodel", new
		 * HtmlFormEngine().getFormModel(formService.getTaskFormData(task.getId(
		 * )))) .toString();
		 */

	}

	@CrossOrigin(origins = "http://localhost:4200")
	@RequestMapping(value = "/getavailablelearningpaths", method = RequestMethod.POST)
	public String getAvailableLearningPaths(@RequestBody String responseJSON) {
		// userauthentication
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = null;
		String username = null;
		LearnerDetails user = null;

		try {
			map = mapper.readValue(responseJSON, Map.class);
			username = (String) map.get("username");
			user = authenticateUserInternally(username);

			if (user == null) {
				throw new UserAuthenticationException("User Authetication failed! Please login again!!");
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			logger.error("IOException while reading responseJSON to getRunningPaths for user: " + username
					+ ". Exception is: " + e1.getMessage());
			JSONObject retJson = new JSONObject();
			retJson.put("status", "unexpectederror");
			return retJson.put("errMsg", new JSONObject().put("message", e1.getMessage())).toString();
		} catch (UserAuthenticationException e) {
			logger.error("User Authentication Failed for user: " + username);
			JSONObject retJson = new JSONObject();
			retJson.put("status", "error");
			return retJson.put("errMsg", e.getMessage()).toString();
		}

		// first get the deployed learning paths
		JSONArray lparray = new JSONArray();

		List<LearningPath> lps = lpEngine.getLearningEngineRepositoryService().getDeployedLearningPaths();

		if (lps != null) {
			for (LearningPath lp : lps) {

				// if not already running
				if (lpEngine.getLearningEngineRuntimeService().getRunningLearningPathBylpId(lp.getId(), user) == null) {
					lparray.put(new JSONObject().put("lpid", lp.getId()).put("lpname", lp.getName()).put("lphint",
							lp.getLearningcontexthint()));

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
	@RequestMapping(value = "/getrunningpaths", method = RequestMethod.POST)
	public String getRunningPaths(@RequestBody String responseJSON) {
		// userauthentication
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = null;
		String username = null;
		LearnerDetails user = null;

		try {
			map = mapper.readValue(responseJSON, Map.class);
			username = (String) map.get("username");
			user = authenticateUserInternally(username);

			if (user == null) {
				throw new UserAuthenticationException("User Authetication failed! Please login again!!");
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			logger.error("IOException while reading responseJSON to getRunningPaths for user: " + username
					+ ". Exception is: " + e1.getMessage());
			JSONObject retJson = new JSONObject();
			retJson.put("status", "unexpectederror");
			return retJson.put("errMsg", new JSONObject().put("message", e1.getMessage())).toString();
		} catch (UserAuthenticationException e) {
			logger.error("User Authentication Failed for user: " + username);
			JSONObject retJson = new JSONObject();
			retJson.put("status", "error");
			return retJson.put("errMsg", e.getMessage()).toString();
		}

		// first get the deployed learning paths
		JSONArray lparray = new JSONArray();

		List<LearningPathInstance> lpInst = lpEngine.getLearningEngineRuntimeService().getRunningLearningPaths(user);

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
					lparray.put(new JSONObject().put("lpid", lp.getLpId()).put("lpname", dlp.getName()).put("lphint",
							dlp.getLearningcontexthint()));

				}
			}
		}

		return new JSONObject().put("rlps", lparray).put("status", "success").toString();

	}

	@CrossOrigin(origins = "http://localhost:4200")
	@RequestMapping(value = "/startalearningpath/{lpid}", method = RequestMethod.POST)
	public String startalearningpath(@PathVariable("lpid") String lpid, @RequestBody String responseJSON) {

		// userauthentication
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = null;
		String username = null;
		LearnerDetails user = null;

		try {
			map = mapper.readValue(responseJSON, Map.class);
			username = (String) map.get("username");
			user = authenticateUserInternally(username);

			if (user == null) {
				throw new UserAuthenticationException("User Authetication failed! Please login again!!");
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			logger.error("IOException while reading responseJSON to startalearningpath for user: " + username
					+ " for with LP ID: " + lpid + ". Exception is: " + e1.getMessage());
			JSONObject retJson = new JSONObject();
			retJson.put("status", "unexpectederror");
			return retJson.put("errMsg", new JSONObject().put("message", e1.getMessage())).toString();
		} catch (UserAuthenticationException e) {
			logger.error("User Authentication Failed for user: " + username + " for with LP ID: " + lpid);
			JSONObject retJson = new JSONObject();
			retJson.put("status", "error");
			return retJson.put("errMsg", e.getMessage()).toString();
		}

		// start the learning path

		try {
			lpEngine.getLearningEngineRuntimeService().startaLearningPathById(lpid, user);

			logger.info("Starting a Learning path with LP ID: " + lpid + " for User: " + username);
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
			return retJson.put("errMsg", e.getMessage()).toString();

			//

		}

	}

	@CrossOrigin(origins = "http://localhost:4200")
	@RequestMapping(value = "/startalearningscenario/{lpid}/{lpinstid}", method = RequestMethod.POST)
	public String startalearningscenario(@PathVariable("lpid") String lpid, @PathVariable("lpinstid") String lpinstid) {

		try {
			lpEngine.getLearningEngineRuntimeService().startNextLearningScenario(lpinstid);
			logger.info("Starting next Learning Scenario with lpid:" + lpid + " and lpinstid: " + lpinstid);
			return new JSONObject().put("status", "success").put("lpid", lpid).toString();
		} catch (LearningPathException e) {
			// TODO Auto-generated catch block
			logger.error("Unexpected error while starting next Learning Scenario with lpid:" + lpid + " and lpinstid: "
					+ lpinstid);
			JSONObject retJson = new JSONObject();
			retJson.put("status", "error");
			return retJson.put("errMsg", e.getMessage()).toString();
		}
	}

	@CrossOrigin(origins = "http://localhost:4200")
	@RequestMapping(value = "/completelearningtask/{lpid}", method = RequestMethod.POST)
	public String completeCurrentLearningTask(@PathVariable("lpid") String lpid, @RequestBody String responseJSON) {
		// userauthentication
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = null;
		String username = null;
		LearnerDetails user = null;

		try {
			map = mapper.readValue(responseJSON, Map.class);
			username = (String) map.get("username");
			user = authenticateUserInternally(username);

			if (user == null) {
				throw new UserAuthenticationException("User Authetication failed! Please login again!!");
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			logger.error("IOException while reading responseJSON to getRunningPaths for user: " + username
					+ ". Exception is: " + e1.getMessage());
			JSONObject retJson = new JSONObject();
			retJson.put("status", "unexpectederror");
			return retJson.put("errMsg", new JSONObject().put("message", e1.getMessage())).toString();
		} catch (UserAuthenticationException e) {
			logger.error("User Authentication Failed for user: " + username);
			JSONObject retJson = new JSONObject();
			retJson.put("status", "error");
			return retJson.put("errMsg", e.getMessage()).toString();
		}

		// get the learning instance
		LearningPathInstance lpInst = lpEngine.getLearningEngineRuntimeService().getRunningLearningPathBylpId(lpid,
				user);

		// if no lpinst with that id
		if (lpInst == null) {
			try {
				throw new LearningPathException("Running Learning Path with instance id: " + lpid + " not found",
						LearningPathExceptionErrorCodes.LP_RUNNING_NOT_FOUND);
			} catch (LearningPathException e) {

				JSONObject retJson = new JSONObject();
				retJson.put("status", "error");
				return retJson.put("errMsg", e.getMessage()).toString();
			}
		}
		LearningScenarioInstance lsInst = null;
		try {
			lsInst = lpEngine.getLearningEngineRuntimeService()
					.getRunningLearningScenarioByIpInstId(Integer.toString(lpInst.getLpInstId()));
		} catch (LearningPathException e1) {
			logger.error(
					"Unexpected error while completing a task. First LPinst found then when trying to gets its LsInst its not found"
							+ "for LPInstId: " + lpInst.getLpInstId());
			JSONObject retJson = new JSONObject();
			retJson.put("status", "unexpectederror");
			return retJson.put("errMsg", new JSONObject().put("message", e1.getMessage())).toString();
		}

		// if no lpinst with that id
		if (lsInst == null) {
			try {
				throw new LearningPathException(
						"Running Learning Scenario for instance with id: " + lpid + " not found",
						LearningPathExceptionErrorCodes.LP_LEARNING_SCENARIO_NOT_FOUND);
			} catch (LearningPathException e) {

				JSONObject retJson = new JSONObject();
				retJson.put("status", "error");
				return retJson.put("errMsg", e.getMessage()).toString();
			}
		}

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
