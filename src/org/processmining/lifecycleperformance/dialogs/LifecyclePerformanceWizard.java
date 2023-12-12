package org.processmining.lifecycleperformance.dialogs;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.util.ui.wizard.ListWizard;
import org.processmining.framework.util.ui.wizard.ProMWizardDisplay;
import org.processmining.lifecycleperformance.models.lifecycle.LifecycleModel;
import org.processmining.lifecycleperformance.parameters.LifecyclePerformanceParameters;

public class LifecyclePerformanceWizard {

	public static LifecyclePerformanceParameters show(UIPluginContext context, XLog eventlog,
			LifecyclePerformanceParameters parameters) {

		LifecyclePerformanceParametersStep[] steps = new LifecyclePerformanceParametersStep[parameters
				.getLifecycleModels().size()];

		int i = 0;
		for (LifecycleModel model : parameters.getLifecycleModels()) {
			steps[i] = new LifecyclePerformanceParametersStep(
					"Choose aggregation attributes for model \"" + model.getLabel() + "\"", parameters, eventlog,
					model);
			i++;
		}

		ListWizard<LifecyclePerformanceParameters> wizard = new ListWizard<LifecyclePerformanceParameters>(steps);

		return ProMWizardDisplay.show(context, wizard, parameters);
	}

}
