package it.cnr.isti.labsedc.bpmls;

import org.springframework.web.bind.annotation.RequestParam;

public interface LearningProcessEngineControllerI {
	public String startLearningScenario(@RequestParam(value = "lsid") String lsid) throws Exception;
}
