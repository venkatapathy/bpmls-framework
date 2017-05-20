package it.cnr.isti.labsedc.bpmls.impl;

import java.util.ArrayList;
import java.util.HashMap;
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
public class OracleServiceImpl implements OracleService {

	@Autowired
	OracleValuesJpaRepository oracleRepo;

	// TODO logger

	private void saveOracleValue(OracleValue oVa) {
		//System.out.println("saving oracle values for " + oVa.getBpmnCamId());
		oracleRepo.save(oVa);
	}

	/**
	 * Updates oracle value. Need to be called when starting a learning scenario
	 * and before every task is entered
	 */
	@Transactional
	public void updateOracleValues(LearningScenarioInstance lsInst, List<it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenario.ValuationOracle.ValuationFunction.DataObject> dos) {
		for (it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenario.ValuationOracle.ValuationFunction.DataObject sinDo : dos) {
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


	public List<TaskIncompleteErrorMessage> checkOracleValues(LearningScenarioInstance lsInst, Map<String, Object> formMap) {
		// for each form value check if there is oracle value and if it is the
		// same
		boolean errExists = false;
		List<TaskIncompleteErrorMessage> errMsgs=new ArrayList<TaskIncompleteErrorMessage>();
		for (String formkey : formMap.keySet()) {
			OracleValue oV = oracleRepo.findBylsInstanceAndBpmnCamId(lsInst, formkey);
			if (oV != null) {
				if (formMap.get(formkey) instanceof String) {
					if (!oV.getCurrentExpectedValue().equals(formMap.get(formkey))) {
						errExists = true;
						TaskIncompleteErrorMessage erMsg=new TaskIncompleteErrorMessage(oV.getBpmnCamId(), oV.getBpmnCamId(), oV.getCurrentExpectedValue(), formMap.get(formkey));
						errMsgs.add(erMsg);
					}
				} else if (formMap.get(formkey) instanceof Boolean) {
					if (! (oV.getCurrentExpectedValue()).equals(Boolean.toString((Boolean)formMap.get(formkey)))) {
						errExists = true;
						TaskIncompleteErrorMessage erMsg=new TaskIncompleteErrorMessage(oV.getBpmnCamId(), oV.getBpmnCamId(), oV.getCurrentExpectedValue(), formMap.get(formkey));
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
			map.put(i.getBpmnCamId(), i.getCurrentExpectedValue());
		}

		return map;
	}
}
