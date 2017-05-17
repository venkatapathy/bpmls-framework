package it.cnr.isti.labsedc.bpmls;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.application.ProcessApplication;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.extension.reactor.CamundaReactor;
import org.camunda.bpm.extension.reactor.bus.CamundaEventBus;
import org.camunda.bpm.extension.reactor.bus.CamundaSelector;
import org.camunda.bpm.extension.reactor.spring.listener.ReactorTaskListener;
import org.springframework.stereotype.Component;

/**
 * A listener that is registered to listen to all process activity events.
 */

@Component
@CamundaSelector(type = "userTask", event = TaskListener.EVENTNAME_CREATE)
public class GlobalExecutionListener extends ReactorTaskListener {

    public static final String ACTIVITY_EXECUTION_MAP_VAR_NAME = "activityExecutionMap";

    

	@Override
    public void notify(DelegateTask delegateTask) {
    	System.out.println("Task started: "+delegateTask.getTaskDefinitionKey());
        
    }

    


}
