package org.processmining.lifecycleperformance.dialogs;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.util.ui.wizard.ListWizard;
import org.processmining.framework.util.ui.wizard.ProMWizardDisplay;
import org.processmining.lifecycleperformance.parameters.AddArtifactLifecycleInformationParameters;

public class AddArtifactLifecycleInformationWizard {

	public static AddArtifactLifecycleInformationParameters show(UIPluginContext context, XLog eventlog,
			AddArtifactLifecycleInformationParameters parameters) {

		ListWizard<AddArtifactLifecycleInformationParameters> wizard = new ListWizard<AddArtifactLifecycleInformationParameters>(
				new AddArtifactLifecycleInformationParametersStep("Select artifact lifecycle information", parameters,
						eventlog));

		return ProMWizardDisplay.show(context, wizard, parameters);
	}

}
