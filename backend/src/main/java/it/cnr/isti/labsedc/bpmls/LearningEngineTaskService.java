package it.cnr.isti.labsedc.bpmls;

import java.util.Map;

import org.camunda.bpm.engine.task.Task;

import it.cnr.isti.labsedc.bpmls.Exceptions.LearningTaskException;
import it.cnr.isti.labsedc.bpmls.Exceptions.LearningPathException;;

public interface LearningEngineTaskService {
	/**
	 * Gets the current task for the current Learning Path instance, if available
	 * @param lpInstId
	 * @return {@link @Task} The current Camunda task, null if not available
	 */
	public Task getCurrentLearningTask(String lpInstId);
	
	/**
	 * Complete a learning task.
	 * @param lpInstId The Lp instance for which the current learning task is submitted
	 * @param taskInputs Input from the user for the task
	 * @throws LearningTaskException If the input from the user violates oracle values
	 * @throws it.cnr.isti.labsedc.bpmls.Exceptions.LearningPathException If the given learning path is non existant
	 */
	public void completeCurrentLearningTask(String lpInstId,Map<String, Object> taskInputs)throws LearningTaskException,it.cnr.isti.labsedc.bpmls.Exceptions.LearningPathException;
}
