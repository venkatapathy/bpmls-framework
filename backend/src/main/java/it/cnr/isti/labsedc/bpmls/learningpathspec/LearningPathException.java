package it.cnr.isti.labsedc.bpmls.learningpathspec;

public class LearningPathException extends Exception {
	// Parameterless Constructor
	public LearningPathException() {}

	// Constructor that accepts a message
	public LearningPathException(String message)
    {
       super(message);
    }
}
