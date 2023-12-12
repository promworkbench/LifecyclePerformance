package org.processmining.lifecycleperformance.dialogs;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.util.ui.widgets.ProMHeaderPanel;
import org.processmining.framework.util.ui.widgets.ProMTextField;
import org.processmining.framework.util.ui.wizard.ProMWizardStep;
import org.processmining.lifecycleperformance.parameters.AssignLifecycleModelLogAttributeParameters;

public class AssignLifecycleModelLogAttributeParametersStep extends ProMHeaderPanel
		implements ProMWizardStep<AssignLifecycleModelLogAttributeParameters> {

	private static final long serialVersionUID = 1054786127714893932L;

	private String title;
	JLabel label;
	ProMTextField textField;

	public AssignLifecycleModelLogAttributeParametersStep(String title,
			final AssignLifecycleModelLogAttributeParameters parameters, final XLog eventlog) {
		super(title);
		setTitle(title);

		// Information text label
		label = new JLabel(
				"Enter the name of the lifecycle model for which the events in the event log represent transitions.");

		// Model name text field
		textField = new ProMTextField();

		this.add(label);
		this.add(textField);
	}

	public boolean canApply(AssignLifecycleModelLogAttributeParameters parameters, JComponent component) {
		return true;
	}

	public AssignLifecycleModelLogAttributeParameters apply(AssignLifecycleModelLogAttributeParameters parameters,
			JComponent component) {
		parameters.setModel(textField.getText());
		return parameters;
	}

	public JComponent getComponent(AssignLifecycleModelLogAttributeParameters parameters) {
		textField.setText(parameters.getModel());
		return this;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
