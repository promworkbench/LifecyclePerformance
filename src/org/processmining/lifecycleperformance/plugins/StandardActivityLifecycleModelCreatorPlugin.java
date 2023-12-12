package org.processmining.lifecycleperformance.plugins;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.lifecycleperformance.constants.AuthorConstants;
import org.processmining.lifecycleperformance.help.TransitionSystemStringExportHelp;
import org.processmining.lifecycleperformance.models.lifecycle.LifecycleModel;
import org.processmining.lifecycleperformance.models.lifecycle.LifecycleModelFactory;

@Plugin(
		name = "Create lifecycle model.",
		parameterLabels = {},
		returnLabels = { "Lifecycle model" },
		returnTypes = { LifecycleModel.class },
		help = "Creates the \"standard\" or \"small\" activity lifecycle model and some performance metrics.")
public class StandardActivityLifecycleModelCreatorPlugin {

	@UITopiaVariant(
			affiliation = AuthorConstants.AFFILIATION,
			author = AuthorConstants.NAME,
			email = AuthorConstants.EMAIL)
	@PluginVariant(
			variantLabel = "Create Standard activity lifecycle model (full).",
			help = TransitionSystemStringExportHelp.TEXT,
			requiredParameterLabels = {})
	public LifecycleModel runDefaultStandard(UIPluginContext context) {
		return runPrivateStandard(context);
	}

	@UITopiaVariant(
			affiliation = AuthorConstants.AFFILIATION,
			author = AuthorConstants.NAME,
			email = AuthorConstants.EMAIL)
	@PluginVariant(
			variantLabel = "Create Standard activity lifecycle model (small).",
			help = TransitionSystemStringExportHelp.TEXT,
			requiredParameterLabels = {})
	public LifecycleModel runDefaultSmall(UIPluginContext context) {
		return runPrivateSmall(context);
	}

	private LifecycleModel runPrivateStandard(UIPluginContext context) {
		return LifecycleModelFactory.generateStandardActivityLifecycleModel();
	}

	private LifecycleModel runPrivateSmall(UIPluginContext context) {
		return LifecycleModelFactory.generateSmallActivityLifecycleModel();
	}

}
