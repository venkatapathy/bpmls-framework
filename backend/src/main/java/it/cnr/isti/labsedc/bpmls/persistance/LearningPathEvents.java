package it.cnr.isti.labsedc.bpmls.persistance;

public interface LearningPathEvents {
	public final String LP_STATUS_INIT = "init";
	public final String LP_STATUS_RUNNING = "running";
	public final String LP_STATUS_COMPLETED = "completed";
	public final String LP_STATUS_ABORTED = "aborted";
	public final String LP_RESULT_NA = "na";
	public final String LP_RESULT_SUCCESS = "success";
	public final String LP_RESULT_FAILURE = "failure";
}
