package it.cnr.isti.labsedc.bpmls.Exceptions;

public class LearningPathException extends Exception {
	public Integer errorCode;
	// Parameterless Constructor
	public LearningPathException() {}

	// Constructor that accepts a message
	private LearningPathException(String message)
    {
       super(message);
    }
	
	public LearningPathException(String message,Integer errorCode){
		super(message);
		this.errorCode=errorCode;
	}
}
