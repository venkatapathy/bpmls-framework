package it.cnr.isti.labsedc.bpmls.impl;

public class TaskIncompleteErrorMessage {
	String id;
	String label;
	String expectedValue;
	Object providedValue;

	public TaskIncompleteErrorMessage(String id, String label, String expectedValue, Object providedValue) {
		super();
		this.id = id;
		this.label = label;
		this.expectedValue = expectedValue;
		this.providedValue = providedValue;
	}

}
