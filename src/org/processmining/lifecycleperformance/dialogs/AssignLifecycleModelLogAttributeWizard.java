package org.processmining.lifecycleperformance.dialogs;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.util.ui.wizard.ListWizard;
import org.processmining.framework.util.ui.wizard.ProMWizardDisplay;
import org.processmining.lifecycleperformance.parameters.AssignLifecycleModelLogAttributeParameters;

public class AssignLifecycleModelLogAttributeWizard {

	public static AssignLifecycleModelLogAttributeParameters show(UIPluginContext context, XLog eventlog,
			AssignLifecycleModelLogAttributeParameters parameters) {

		ListWizard<AssignLifecycleModelLogAttributeParameters> wizard = new ListWizard<AssignLifecycleModelLogAttributeParameters>(
				new AssignLifecycleModelLogAttributeParametersStep("Set lifecycle model name", parameters, eventlog));

		return ProMWizardDisplay.show(context, wizard, parameters);
	}

}
