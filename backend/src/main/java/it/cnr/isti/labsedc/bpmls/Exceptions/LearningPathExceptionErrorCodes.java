package it.cnr.isti.labsedc.bpmls.Exceptions;

/**
 * Exception codes for {@link LearningPathException}
 * @author venkat
 *
 */
public interface LearningPathExceptionErrorCodes {
	public Integer LP_NOT_FOUND=1;
	public Integer LP_NO_LEARNING_SCENARIOS=2;
	public Integer LP_ALREADY_RUNNING=3;
	public Integer LP_RUNNING_NOT_FOUND=4;
	public Integer LP_LEARNING_SCENARIO_NOT_FOUND=5;
	public Integer LP_LEARNING_SCENARIO_ALREADY_RUNNING=6;
	public Integer LP_NO_NEXT_LEARNING_SCENARIO=7;
	public Integer LP_NO_RUNNING_LEARNING_SCENARIOS=2;
}
