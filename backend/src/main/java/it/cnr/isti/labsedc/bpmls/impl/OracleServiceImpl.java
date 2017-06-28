package it.cnr.isti.labsedc.bpmls.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.form.FormData;
import org.camunda.bpm.engine.form.FormField;
import org.camunda.bpm.engine.impl.form.type.LongFormType;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.cnr.isti.labsedc.bpmls.OracleService;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenario.InitialValuation.DataObject;
import it.cnr.isti.labsedc.bpmls.persistance.LearningScenarioInstance;
import it.cnr.isti.labsedc.bpmls.persistance.OracleValue;
import it.cnr.isti.labsedc.bpmls.persistance.OracleValuesJpaRepository;

@Component
public class OracleServiceImpl implements OracleService {

	@Autowired
	OracleValuesJpaRepository oracleRepo;

	@Autowired
	FormService camundaFormService;
	
	private void saveOracleValue(OracleValue oVa) {
		// System.out.println("saving oracle values for " + oVa.getBpmnCamId());
		oracleRepo.save(oVa);
	}

	/**
	 * Updates oracle value. Need to be called when starting a learning scenario
	 * and before every task is entered
	 */
	@Transactional
	public void updateOracleValues(LearningScenarioInstance lsInst,
			List<it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenario.ValuationOracle.ValuationFunction.DataObject> dos) {
		for (it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenario.ValuationOracle.ValuationFunction.DataObject sinDo : dos) {
			// check if it already exists
			OracleValue oV = oracleRepo.findBylsInstanceAndBpmnCamId(lsInst, sinDo.getBpmnCamundaid());

			// if not present create a new one
			if (oV == null) {
				// if no expected value provided, ignore
				if (sinDo.getExpectedValue() != null && sinDo.getExpectedValue().getValue() != null
						&& !sinDo.getExpectedValue().getValue().equals("")) {
					oV = new OracleValue(lsInst, sinDo.getBpmnCamundaid(), sinDo.getExpectedValue().getValue().trim());
				}
			}
			// else update
			else {
				if (sinDo.getExpectedValue() != null && sinDo.getExpectedValue().getValue() != null
						&& !sinDo.getExpectedValue().getValue().equals("")) {
					oV.setCurrentExpectedValue(sinDo.getExpectedValue().getValue().trim());
				}
			}

			// saveOracleValue
			if (oV != null) {
				saveOracleValue(oV);
			}

		}
	}

	@Transactional
	public void updateOracleValues(LearningScenarioInstance lsInst, Map<String, Object> inputs){
		if(inputs!=null){
			for(String formkey:inputs.keySet()){
				//insert only when it is not already there
				//because if it already there it is inserted before the beginning of the task
				//however there might b some dataobjects not in oracle specifications but in user tasks
				OracleValue oV = oracleRepo.findBylsInstanceAndBpmnCamId(lsInst, formkey);
				// if not present create a new one
				if (oV == null) {
					// if no expected value provided, ignore
					if (inputs.get(formkey) != null
							&& !inputs.get(formkey).equals("")) {
						if(inputs.get(formkey) instanceof String){
							oV = new OracleValue(lsInst, formkey,(String) inputs.get(formkey));
						}else if(inputs.get(formkey) instanceof Boolean){
							oV = new OracleValue(lsInst, formkey,Boolean.toString((Boolean) inputs.get(formkey)));
						}
						saveOracleValue(oV);
					}
				}
			}
		}
	}
	
	@Transactional
	public void updateOracleValuesinit(LearningScenarioInstance lsInst, List<DataObject> dos) {
		for (DataObject sinDo : dos) {
			// check if it already exists
			OracleValue oV = oracleRepo.findBylsInstanceAndBpmnCamId(lsInst, sinDo.getBpmnCamundaid());

			// if not present create a new one
			if (oV == null) {

				oV = new OracleValue(lsInst, sinDo.getBpmnCamundaid(), sinDo.getValue().trim());
			}
			// else update
			else {
				oV.setCurrentExpectedValue(sinDo.getValue().trim());
			}

			// saveOracleValue
			saveOracleValue(oV);

		}
	}

	public List<TaskIncompleteErrorMessage> checkOracleValues(LearningScenarioInstance lsInst,
			Map<String, Object> formMap, Task task) {
		// for each form value check if there is oracle value and if it is the
		// same
		boolean errExists = false;
		List<TaskIncompleteErrorMessage> errMsgs = new ArrayList<TaskIncompleteErrorMessage>();
		
		//first check that for long they havent given non-long values, if so that is also error:
		FormData formData= camundaFormService.getTaskFormData(task.getId());
		
		if(formData!=null && formData.getFormFields()!=null){
			for(FormField formField:formData.getFormFields()){
				Object formVal=formMap.get(formField.getId());
				if(formVal!=null){
					if(LongFormType.TYPE_NAME.equals(formField.getTypeName())){
						try{
							if(formVal instanceof String){
								Long.parseLong((String)formVal);
							}
							
						}catch(NumberFormatException e){
							TaskIncompleteErrorMessage erMsg = new TaskIncompleteErrorMessage(formField.getId(),
									formField.getId(), "0", "0");
							errMsgs.add(erMsg);
							errExists = true;
						}
						
					}
					//try to convert it to long and if exception return the errorMsg
					
				}
			}
		}
		
		//first check if all the expected input is
		for (String formkey : formMap.keySet()) {
			OracleValue oV = oracleRepo.findBylsInstanceAndBpmnCamId(lsInst, formkey);
			if (oV != null) {
				//if formMap is null but Ov is not straight away error exists
				if(formMap.get(formkey)==null){
					errExists = true;
					TaskIncompleteErrorMessage erMsg = new TaskIncompleteErrorMessage(oV.getBpmnCamId(),
							oV.getBpmnCamId(), oV.getCurrentExpectedValue(), formMap.get(formkey));
					errMsgs.add(erMsg);
					
					continue;
				}
				if (formMap.get(formkey) instanceof String) {
					if (!oV.getCurrentExpectedValue().equals(formMap.get(formkey))) {
						errExists = true;
						TaskIncompleteErrorMessage erMsg = new TaskIncompleteErrorMessage(oV.getBpmnCamId(),
								oV.getBpmnCamId(), oV.getCurrentExpectedValue(), formMap.get(formkey));
						errMsgs.add(erMsg);
						
					}
				} else if (formMap.get(formkey) instanceof Boolean) {
					if (!(oV.getCurrentExpectedValue()).equals(Boolean.toString((Boolean) formMap.get(formkey)))) {
						errExists = true;
						TaskIncompleteErrorMessage erMsg = new TaskIncompleteErrorMessage(oV.getBpmnCamId(),
								oV.getBpmnCamId(), oV.getCurrentExpectedValue(), formMap.get(formkey));
						errMsgs.add(erMsg);

					}
				}
			}
		}

		if (errExists) {
			return errMsgs;
		} else {

			return null;
		}

	}

	public Map<String, Object> getOracleValues(LearningScenarioInstance lsInst) {
		List<OracleValue> ovL = oracleRepo.findAllByLsInstance(lsInst);
		Map<String, Object> map = new HashMap<String, Object>();
		for (OracleValue i : ovL) {
			// if boolean return a boolean
			if (i.getCurrentExpectedValue().equals("true") || i.getCurrentExpectedValue().equals("false")) {
				map.put(i.getBpmnCamId(), Boolean.parseBoolean(i.getCurrentExpectedValue()));
			} else {
				map.put(i.getBpmnCamId(), i.getCurrentExpectedValue());
			}

		}

		return map;
	}
}
