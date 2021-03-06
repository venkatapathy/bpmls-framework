package it.cnr.isti.labsedc.bpmls;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.camunda.bpm.engine.form.FormData;
import org.camunda.bpm.engine.form.FormField;
import org.camunda.bpm.engine.form.FormFieldValidationConstraint;
import org.camunda.bpm.engine.form.StartFormData;
import org.camunda.bpm.engine.form.TaskFormData;
import org.camunda.bpm.engine.impl.form.engine.FormEngine;
import org.camunda.bpm.engine.impl.form.engine.HtmlDocumentBuilder;
import org.camunda.bpm.engine.impl.form.engine.HtmlElementWriter;
import org.camunda.bpm.engine.impl.form.type.BooleanFormType;
import org.camunda.bpm.engine.impl.form.type.DateFormType;
import org.camunda.bpm.engine.impl.form.type.EnumFormType;
import org.camunda.bpm.engine.impl.form.type.StringFormType;

import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenario.ValuationOracle.ValuationFunction;
import it.cnr.isti.labsedc.bpmls.learningpathspec.LearningScenario.ValuationOracle.ValuationFunction.DataObject;

/**
 * <p>
 * A simple {@link FormEngine} implementaiton which renders forms as HTML such
 * that they can be used as embedded forms inside camunda Tasklist.
 * </p>
 *
 * @author Daniel Meyer
 *
 */
public class HtmlFormEngine {

	/* elements */
	protected static final String FORM_ELEMENT = "form";
	protected static final String DIV_ELEMENT = "div";
	protected static final String SPAN_ELEMENT = "span";
	protected static final String LABEL_ELEMENT = "label";
	protected static final String INPUT_ELEMENT = "input";
	protected static final String BUTTON_ELEMENT = "button";
	protected static final String SELECT_ELEMENT = "select";
	protected static final String OPTION_ELEMENT = "option";
	protected static final String I_ELEMENT = "i";
	// protected static final String SCRIPT_ELEMENT = "script";

	/* attributes */
	protected static final String NAME_ATTRIBUTE = "name";
	protected static final String CLASS_ATTRIBUTE = "class";
	// protected static final String ROLE_ATTRIBUTE = "role";
	protected static final String FOR_ATTRIBUTE = "for";
	protected static final String VALUE_ATTRIBUTE = "value";
	protected static final String TYPE_ATTRIBUTE = "type";
	protected static final String SELECTED_ATTRIBUTE = "selected";

	/* datepicker attributes */
	protected static final String IS_OPEN_ATTRIBUTE = "is-open";
	protected static final String DATEPICKER_POPUP_ATTRIBUTE = "datepicker-popup";

	/* camunda attributes */
	// protected static final String CAM_VARIABLE_TYPE_ATTRIBUTE =
	// "cam-variable-type";
	// protected static final String CAM_VARIABLE_NAME_ATTRIBUTE =
	// "cam-variable-name";
	// protected static final String CAM_SCRIPT_ATTRIBUTE = "cam-script";
	// protected static final String CAM_BUSINESS_KEY_ATTRIBUTE =
	// "cam-business-key";

	/* angular attributes */
	protected static final String NG_CLICK_ATTRIBUTE = "(click)";
	protected static final String NG_IF_ATTRIBUTE = "*ngIf";
	// protected static final String NG_SHOW_ATTRIBUTE = "(hidden)";

	/* classes */
	protected static final String FORM_GROUP_CLASS = "form-group";
	protected static final String FORM_CONTROL_CLASS = "form-control";
	// protected static final String INPUT_GROUP_CLASS = "input-group";
	// protected static final String INPUT_GROUP_BTN_CLASS = "input-group-btn";
	protected static final String BUTTON_DEFAULT_CLASS = "btn btn-default";
	// protected static final String HAS_ERROR_CLASS = "has-error";
	// protected static final String HELP_BLOCK_CLASS = "help-block";

	/* input[type] */
	protected static final String TEXT_INPUT_TYPE = "text";
	protected static final String CHECKBOX_INPUT_TYPE = "checkbox";

	/* button[type] */
	protected static final String BUTTON_BUTTON_TYPE = "button";

	/* script[type] */
	// protected static final String TEXT_FORM_SCRIPT_TYPE = "text/form-script";

	/* glyphicons */
	protected static final String CALENDAR_GLYPHICON = "glyphicon glyphicon-calendar";

	// form learning simuator
	protected static final String FORM_ID_ATTRIBUTE = "learningForm";
	protected static final String FORM_ID_ATTRIBUTE_VALUE = "ngForm";
	/* generated form name */
	// protected static final String GENERATED_FORM_NAME = "generatedForm";
	// protected static final String FORM_ROLE = "form";

	/* error types */
	// protected static final String REQUIRED_ERROR_TYPE = "required";
	// protected static final String DATE_ERROR_TYPE = "date";

	/* form element selector */
	// protected static final String FORM_ELEMENT_SELECTOR = "this." +
	// GENERATED_FORM_NAME + ".%s";
	//
	// /* expressions */
	// protected static final String INVALID_EXPRESSION = FORM_ELEMENT_SELECTOR
	// + ".$invalid";
	// protected static final String DIRTY_EXPRESSION = FORM_ELEMENT_SELECTOR +
	// ".$dirty";
	// protected static final String ERROR_EXPRESSION = FORM_ELEMENT_SELECTOR +
	// ".$error";
	// protected static final String DATE_ERROR_EXPRESSION = ERROR_EXPRESSION +
	// ".date";
	// protected static final String REQUIRED_ERROR_EXPRESSION =
	// ERROR_EXPRESSION + ".required";
	// protected static final String TYPE_ERROR_EXPRESSION = ERROR_EXPRESSION +
	// ".camVariableType";
	//
	// /* JavaScript snippets */
	// protected static final String DATE_FIELD_OPENED_ATTRIBUTE =
	// "dateFieldOpened%s";
	// protected static final String OPEN_DATEPICKER_SNIPPET = "$scope.open%s =
	// function ($event) { $event.preventDefault(); $event.stopPropagation();
	// $scope.dateFieldOpened%s = true; };";
	// protected static final String OPEN_DATEPICKER_FUNCTION_SNIPPET =
	// "open%s($event)";
	//
	// /* date format */
	// protected static final String DATE_FORMAT = "dd/MM/yyyy";
	//
	// /* messages */
	// protected static final String REQUIRED_FIELD_MESSAGE = "Required field";
	// protected static final String TYPE_FIELD_MESSAGE = "Only a %s value is
	// allowed";
	// protected static final String INVALID_DATE_FIELD_MESSAGE = "Invalid date
	// format: the date should have the pattern '" + DATE_FORMAT + "'";

	public String getName() {
		return "html";
	}

	public Object renderStartForm(StartFormData startForm) {
		return renderFormData(startForm, null);
	}

	public Object renderTaskForm(TaskFormData taskForm, List<DataObject> doLs) {
		return renderFormData(taskForm, doLs);
	}

	public String getFormModel(FormData formData) {
		StringBuilder retMsg = new StringBuilder("{\"learningform\":");
		if (formData.getFormFields().size() > 0) {
			retMsg.append("{");
		}

		for (FormField formField : formData.getFormFields()) {

			Object defaultValue = formField.getValue().getValue();
			if (defaultValue != null && isReadOnly(formField)) {
				if (isDate(formField)) {
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); 
					retMsg.append("\"" + formField.getId() + "\":\"" + df.format(defaultValue) + "\",");
					continue;
				}
				retMsg.append("\"" + formField.getId() + "\":\"" + defaultValue.toString() + "\",");

			} else {
				if (isDate(formField)) {
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); 
					retMsg.append("\"" + formField.getId() + "\":\"" + df.format(defaultValue) + "\",");
					continue;
				}
				retMsg.append("\"" + formField.getId() + "\":\"\",");
			}
		}
		if (formData.getFormFields().size() > 0) {
			retMsg.deleteCharAt(retMsg.length() - 1);
			retMsg.append("}}");

		} else {
			retMsg.append("{}}");
		}

		return retMsg.toString();
	}

	private void getFormFieldHint(HtmlDocumentBuilder documentBuilder, List<DataObject> doLs, FormField formField) {
		if (doLs == null) {
			return;
		}

		String defaultValue = null;
		String hint = null;
		for (DataObject dov : doLs) {
			if (dov.getBpmnCamundaid().equals(formField.getId())) {
				
				if(dov.getExpectedValue()!=null){
					defaultValue = dov.getExpectedValue().getValue();
				}
				
				if (dov.getDoHint() != null) {
					hint = dov.getDoHint().getValue();
				}

			}
		}
		if (defaultValue != null && !isReadOnly(formField)) {
			HtmlElementWriter hintSpan = new HtmlElementWriter(SPAN_ELEMENT)
					.attribute("class", "help-block regular-text")
					.attribute("style", "color:white; font-size:120%;").attribute("[hidden]", "helpHidden");
			hintSpan.textContent("Expected Value: &nbsp; &nbsp;" + defaultValue);
			documentBuilder.startElement(hintSpan).endElement();

		}

		if (hint != null) {
			HtmlElementWriter hintSpan = new HtmlElementWriter(DIV_ELEMENT)
					.attribute("class", "help-block regular-text")
					.attribute("style", "color:white; font-size:120%;").attribute("[hidden]", "helpHidden");
			hintSpan.textContent("Hint: &nbsp; &nbsp;" + hint);
			documentBuilder.startElement(new HtmlElementWriter("br", true)).startElement(hintSpan).endElement()
					.startElement(new HtmlElementWriter("br", true));

		}
	}

	public String renderFormData(FormData formData, List<DataObject> doLs) {

		if (formData == null || (formData.getFormFields() == null || formData.getFormFields().isEmpty())
				&& (formData.getFormProperties() == null || formData.getFormProperties().isEmpty())) {
			// when no fields are there just return forms with a complete
			// learning button
			HtmlElementWriter formElement = new HtmlElementWriter(FORM_ELEMENT).attribute(FORM_ID_ATTRIBUTE,
					"#" + FORM_ID_ATTRIBUTE_VALUE);
			HtmlElementWriter divElement = new HtmlElementWriter("button").attribute("type", "button")
					.textContent("Complete Learning");
			// end document element
			HtmlDocumentBuilder documentBuilder = new HtmlDocumentBuilder(formElement);
			documentBuilder.startElement(divElement).endElement();
			documentBuilder.endElement();
			return documentBuilder.getHtmlString();

		} else {
			HtmlElementWriter formElement = new HtmlElementWriter(FORM_ELEMENT).attribute("#f", "ngForm")
					.attribute("novalidate", null);

			HtmlDocumentBuilder documentBuilder = new HtmlDocumentBuilder(formElement);

			// render fields
			for (FormField formField : formData.getFormFields()) {
				renderFormField(formField, documentBuilder, doLs);
			}

			// render deprecated form properties
			// for (FormProperty formProperty : formData.getFormProperties()) {
			// renderFormField(new FormPropertyAdapter(formProperty),
			// documentBuilder);
			// }
			// submitbutton
			HtmlElementWriter divElement = new HtmlElementWriter("button").attribute("type", "submit")
					.attribute("(click)", "completeLearning(f)").attribute("class", "btn btn-primary")
					.textContent("Complete Task");
			// end document element

			documentBuilder.startElement(divElement).endElement();
			documentBuilder.endElement();

			return documentBuilder.getHtmlString();

		}
	}

	protected void renderFormField(FormField formField, HtmlDocumentBuilder documentBuilder, List<DataObject> doLs) {
		// start group
		HtmlElementWriter divElement = new HtmlElementWriter(DIV_ELEMENT).attribute(CLASS_ATTRIBUTE, FORM_GROUP_CLASS);

		documentBuilder.startElement(divElement);

		String formFieldId = formField.getId();
		String formFieldLabel = formField.getLabel();

		// write label
		if (formFieldLabel != null && !formFieldLabel.isEmpty()) {
			HtmlElementWriter labelElement;

			// if check box different label
			if (isBoolean(formField)) {
				labelElement = new HtmlElementWriter(LABEL_ELEMENT).attribute("class",
						"checkbox-inline custom-checkbox nowrap").attribute("style", "color:#285eb8");
				documentBuilder.startElement(labelElement);
				renderInputField(formField, documentBuilder);
				HtmlElementWriter spanElement = new HtmlElementWriter(SPAN_ELEMENT).textContent(formFieldLabel);
				documentBuilder.startElement(spanElement).endElement().endElement().endElement();
				getFormFieldHint(documentBuilder, doLs, formField);

				return;
			} else if (isDate(formField)) {
				labelElement = new HtmlElementWriter(LABEL_ELEMENT).attribute(FOR_ATTRIBUTE, formFieldId)
						.textContent(formFieldLabel);
				documentBuilder.startElement(labelElement).endElement();
				HtmlElementWriter dateElement = new HtmlElementWriter("material-datepicker").textContent(formFieldLabel)
						.attribute("[(date)]", FORM_ID_ATTRIBUTE + "." + formFieldId).attribute("dateFormat", "YYYY-MM-DD");
				if (isReadOnly(formField)) {
					dateElement.attribute("disabled", null);
				}
				documentBuilder.startElement(new HtmlElementWriter("br", true)).startElement(dateElement).endElement()
						.startElement(new HtmlElementWriter("br", true));
				getFormFieldHint(documentBuilder, doLs, formField);
				documentBuilder.endElement();
				return;

			} else {
				
				if(formField.getTypeName().equals("long")){
					//System.out.println("numers only");
					labelElement = new HtmlElementWriter(LABEL_ELEMENT).attribute(FOR_ATTRIBUTE, formFieldId).attribute("style", "color:#285eb8")
							.textContent(formFieldLabel+"(Numbers Only)");
				}else{
					labelElement = new HtmlElementWriter(LABEL_ELEMENT).attribute(FOR_ATTRIBUTE, formFieldId).attribute("style", "color:#285eb8")
							.textContent(formFieldLabel);
				}
			}

			// <label for="...">...</label>
			documentBuilder.startElement(labelElement).endElement();
		}

		// render form control
		if (isEnum(formField)) {
			// <select ...>
			renderSelectBox(formField, documentBuilder);

		} else if (!isBoolean(formField)) {
			// <input ...>
			renderInputField(formField, documentBuilder);

		}

		// add a hint
		// format: <span class="help-block regular-text"
		// style="color:#dfb81c">Expected Value:
		// Xys</span><br/>
		getFormFieldHint(documentBuilder, doLs, formField);

		// renderInvalidMessageElement(formField, documentBuilder);

		// end group
		documentBuilder.endElement();
	}

	protected HtmlElementWriter createInputField(FormField formField) {
		HtmlElementWriter inputField = new HtmlElementWriter(INPUT_ELEMENT, true);

		addCommonFormFieldAttributes(formField, inputField);

		inputField.attribute(TYPE_ATTRIBUTE, TEXT_INPUT_TYPE);

		return inputField;
	}

	// protected void renderDatePicker(FormField formField, HtmlDocumentBuilder
	// documentBuilder) {
	// boolean isReadOnly = isReadOnly(formField);
	//
	// // start input-group
	// HtmlElementWriter inputGroupDivElement = new
	// HtmlElementWriter(DIV_ELEMENT)
	// .attribute(CLASS_ATTRIBUTE, INPUT_GROUP_CLASS);
	//
	// String formFieldId = formField.getId();
	//
	// // <div>
	// documentBuilder.startElement(inputGroupDivElement);
	//
	// // input field
	// HtmlElementWriter inputField = createInputField(formField);
	//
	// if(!isReadOnly) {
	// inputField
	// .attribute(DATEPICKER_POPUP_ATTRIBUTE, DATE_FORMAT)
	// .attribute(IS_OPEN_ATTRIBUTE, String.format(DATE_FIELD_OPENED_ATTRIBUTE,
	// formFieldId));
	// }
	//
	// // <input ... />
	// documentBuilder
	// .startElement(inputField)
	// .endElement();
	//
	//
	// // if form field is read only, do not render date picker open button
	// if(!isReadOnly) {
	//
	// // input addon
	// HtmlElementWriter addonElement = new HtmlElementWriter(DIV_ELEMENT)
	// .attribute(CLASS_ATTRIBUTE, INPUT_GROUP_BTN_CLASS);
	//
	// // <div>
	// documentBuilder.startElement(addonElement);
	//
	// // button to open date picker
	// HtmlElementWriter buttonElement = new HtmlElementWriter(BUTTON_ELEMENT)
	// .attribute(TYPE_ATTRIBUTE, BUTTON_BUTTON_TYPE)
	// .attribute(CLASS_ATTRIBUTE, BUTTON_DEFAULT_CLASS)
	// .attribute(NG_CLICK_ATTRIBUTE,
	// String.format(OPEN_DATEPICKER_FUNCTION_SNIPPET, formFieldId));
	//
	// // <button>
	// documentBuilder.startElement(buttonElement);
	//
	// HtmlElementWriter iconElement = new HtmlElementWriter(I_ELEMENT)
	// .attribute(CLASS_ATTRIBUTE, CALENDAR_GLYPHICON);
	//
	// // <i ...></i>
	// documentBuilder
	// .startElement(iconElement)
	// .endElement();
	//
	// // </button>
	// documentBuilder.endElement();
	//
	// // </div>
	// documentBuilder.endElement();
	//
	//
	// HtmlElementWriter scriptElement = new HtmlElementWriter(SCRIPT_ELEMENT)
	// .attribute(CAM_SCRIPT_ATTRIBUTE, null)
	// .attribute(TYPE_ATTRIBUTE, TEXT_FORM_SCRIPT_TYPE)
	// .textContent(String.format(OPEN_DATEPICKER_SNIPPET, formFieldId,
	// formFieldId));
	//
	// // <script ...> </script>
	// documentBuilder
	// .startElement(scriptElement)
	// .endElement();
	//
	// }
	//
	// // </div>
	// documentBuilder.endElement();
	//
	// }
	//
	protected void renderInputField(FormField formField, HtmlDocumentBuilder documentBuilder) {
		HtmlElementWriter inputField = new HtmlElementWriter(INPUT_ELEMENT, true);

		String inputType = !isBoolean(formField) ? TEXT_INPUT_TYPE : CHECKBOX_INPUT_TYPE;

		inputField.attribute(TYPE_ATTRIBUTE, inputType);

		// diff for check box and text

		addCommonFormFieldAttributes(formField, inputField);

		// add current value for the text box

		// if (inputType == TEXT_INPUT_TYPE) {
		// Object defaultValue = formField.getValue().getValue();
		// if (defaultValue != null) {
		// inputField.attribute("placeholder",
		// defaultValue.toString()).attribute("value",
		// defaultValue.toString());
		// }
		// } else {
		// // TODO: checked if the value has true or false
		// Boolean defaultValue = (Boolean) formField.getValue().getValue();
		// if (defaultValue != null && defaultValue) {
		// inputField.attribute("checked", null);
		// }
		// }

		if (isReadOnly(formField)) {
			inputField.attribute("readonly", "readonly");
		}
		// <input ... />
		
		if(formField.getTypeName().equals("long")){
			inputField.attribute("pattern", "[0-9]");
		}

		documentBuilder.startElement(inputField).endElement();
	}

	protected void renderSelectBox(FormField formField, HtmlDocumentBuilder documentBuilder) {
		HtmlElementWriter selectBox = new HtmlElementWriter(SELECT_ELEMENT, false);

		addCommonFormFieldAttributes(formField, selectBox);

		if (isReadOnly(formField)) {
			selectBox.attribute("[disabled]", "true");
		}
		
		// <select ...>
		documentBuilder.startElement(selectBox);

		// <option ...>
		renderSelectOptions(formField, documentBuilder);

		// </select>
		documentBuilder.endElement();

	}

	protected void renderSelectOptions(FormField formField, HtmlDocumentBuilder documentBuilder) {
		EnumFormType enumFormType = (EnumFormType) formField.getType();
		Map<String, String> values = enumFormType.getValues();

		for (Entry<String, String> value : values.entrySet()) {
			// <option>
			HtmlElementWriter option = new HtmlElementWriter(OPTION_ELEMENT, false)
					.attribute(VALUE_ATTRIBUTE, value.getKey()).textContent(value.getValue());

			// if selected
			Object defaultValue = formField.getValue().getValue();
			if (defaultValue != null) {
				if (value.getKey().equals(defaultValue)) {
					option.attribute("selected", null);
				}
			}

			documentBuilder.startElement(option).endElement();

		}
	}

	// protected void renderInvalidMessageElement(FormField formField,
	// HtmlDocumentBuilder documentBuilder) {
	// HtmlElementWriter divElement = new HtmlElementWriter(DIV_ELEMENT);
	//
	// String formFieldId = formField.getId();
	// String ifExpression = String.format(INVALID_EXPRESSION + " && " +
	// DIRTY_EXPRESSION, formFieldId, formFieldId);
	//
	// divElement.attribute(NG_IF_ATTRIBUTE,
	// ifExpression).attribute(CLASS_ATTRIBUTE, HAS_ERROR_CLASS);
	//
	// // <div ng-if="....$invalid && ....$dirty"...>
	// documentBuilder.startElement(divElement);
	//
	// if (!isDate(formField)) {
	// renderInvalidValueMessage(formField, documentBuilder);
	// renderInvalidTypeMessage(formField, documentBuilder);
	//
	// } else {
	// renderInvalidDateMessage(formField, documentBuilder);
	// }
	//
	// documentBuilder.endElement();
	// }
	//
	// protected void renderInvalidValueMessage(FormField formField,
	// HtmlDocumentBuilder documentBuilder) {
	// HtmlElementWriter divElement = new HtmlElementWriter(DIV_ELEMENT);
	//
	// String formFieldId = formField.getId();
	//
	// String expression = String.format(REQUIRED_ERROR_EXPRESSION,
	// formFieldId);
	//
	// divElement.attribute(NG_SHOW_ATTRIBUTE,
	// expression).attribute(CLASS_ATTRIBUTE, HELP_BLOCK_CLASS)
	// .textContent(REQUIRED_FIELD_MESSAGE);
	//
	// documentBuilder.startElement(divElement).endElement();
	// }
	//
	// protected void renderInvalidTypeMessage(FormField formField,
	// HtmlDocumentBuilder documentBuilder) {
	// HtmlElementWriter divElement = new HtmlElementWriter(DIV_ELEMENT);
	//
	// String formFieldId = formField.getId();
	//
	// String expression = String.format(TYPE_ERROR_EXPRESSION, formFieldId);
	//
	// String typeName = formField.getTypeName();
	//
	// if (isEnum(formField)) {
	// typeName = StringFormType.TYPE_NAME;
	// }
	//
	// divElement.attribute(NG_SHOW_ATTRIBUTE,
	// expression).attribute(CLASS_ATTRIBUTE, HELP_BLOCK_CLASS)
	// .textContent(String.format(TYPE_FIELD_MESSAGE, typeName));
	//
	// documentBuilder.startElement(divElement).endElement();
	// }
	//
	// protected void renderInvalidDateMessage(FormField formField,
	// HtmlDocumentBuilder documentBuilder) {
	// String formFieldId = formField.getId();
	//
	// HtmlElementWriter firstDivElement = new HtmlElementWriter(DIV_ELEMENT);
	//
	// String firstExpression = String.format(REQUIRED_ERROR_EXPRESSION + " &&
	// !" + DATE_ERROR_EXPRESSION, formFieldId,
	// formFieldId);
	//
	// firstDivElement.attribute(NG_SHOW_ATTRIBUTE,
	// firstExpression).attribute(CLASS_ATTRIBUTE, HELP_BLOCK_CLASS)
	// .textContent(REQUIRED_FIELD_MESSAGE);
	//
	// documentBuilder.startElement(firstDivElement).endElement();
	//
	// HtmlElementWriter secondDivElement = new HtmlElementWriter(DIV_ELEMENT);
	//
	// String secondExpression = String.format(DATE_ERROR_EXPRESSION,
	// formFieldId);
	//
	// secondDivElement.attribute(NG_SHOW_ATTRIBUTE,
	// secondExpression).attribute(CLASS_ATTRIBUTE, HELP_BLOCK_CLASS)
	// .textContent(INVALID_DATE_FIELD_MESSAGE);
	//
	// documentBuilder.startElement(secondDivElement).endElement();
	// }

	protected void addCommonFormFieldAttributes(FormField formField, HtmlElementWriter formControl) {

		String typeName = formField.getTypeName();

		if (isEnum(formField) || isDate(formField)) {
			typeName = StringFormType.TYPE_NAME;
		}

		typeName = typeName.substring(0, 1).toUpperCase() + typeName.substring(1);

		String formFieldId = formField.getId();

		formControl.attribute(CLASS_ATTRIBUTE, FORM_CONTROL_CLASS)
				.attribute("[(ngModel)]", FORM_ID_ATTRIBUTE + "." + formFieldId).attribute("ngControl", formFieldId)
				.attribute("name", formFieldId);

		// add validation constraints
		for (FormFieldValidationConstraint constraint : formField.getValidationConstraints()) {
			String constraintName = constraint.getName();
			String configuration = (String) constraint.getConfiguration();
			formControl.attribute(constraintName, configuration);
		}
	}

	// helper
	// /////////////////////////////////////////////////////////////////////////////////////

	protected boolean isEnum(FormField formField) {
		return EnumFormType.TYPE_NAME.equals(formField.getTypeName());
	}

	protected boolean isDate(FormField formField) {
		return DateFormType.TYPE_NAME.equals(formField.getTypeName());
	}

	protected boolean isBoolean(FormField formField) {
		return BooleanFormType.TYPE_NAME.equals(formField.getTypeName());
	}

	protected boolean isReadOnly(FormField formField) {
		List<FormFieldValidationConstraint> validationConstraints = formField.getValidationConstraints();
		if (validationConstraints != null) {
			for (FormFieldValidationConstraint validationConstraint : validationConstraints) {
				if ("readonly".equals(validationConstraint.getName())) {
					return true;
				}
			}
		}
		return false;
	}

}
