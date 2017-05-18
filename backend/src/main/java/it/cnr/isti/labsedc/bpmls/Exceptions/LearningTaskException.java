package it.cnr.isti.labsedc.bpmls.Exceptions;

import java.util.List;

import it.cnr.isti.labsedc.bpmls.impl.TaskIncompleteErrorMessage;

public class LearningTaskException extends Exception{
	public Integer errCode;
	public List<TaskIncompleteErrorMessage> userInputErrorMsgs;
	public LearningTaskException() {
		super();
		
	}

	
	public LearningTaskException(String message,Integer errCode,List<TaskIncompleteErrorMessage> userInputErrorMsgs) {
		super(message);
		this.errCode=errCode;
		this.userInputErrorMsgs=userInputErrorMsgs;
	}
}
