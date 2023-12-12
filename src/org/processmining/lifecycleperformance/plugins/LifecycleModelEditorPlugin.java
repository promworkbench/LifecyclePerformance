package org.processmining.lifecycleperformance.plugins;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.lifecycleperformance.constants.AuthorConstants;
import org.processmining.lifecycleperformance.help.LifecycleModelEditorHelp;
import org.processmining.lifecycleperformance.models.lifecycle.LifecycleModel;
import org.processmining.models.graphbased.directed.transitionsystem.TransitionSystem;

@Plugin(
		name = "Lifecycle model editor.",
		parameterLabels = { "Lifecycle model", "Transition system" },
		returnLabels = { "Lifecycle model" },
		returnTypes = { LifecycleModel.class },
		help = LifecycleModelEditorHelp.TEXT)
public class LifecycleModelEditorPlugin {

	@UITopiaVariant(
			affiliation = AuthorConstants.AFFILIATION,
			author = AuthorConstants.NAME,
			email = AuthorConstants.EMAIL)
	@PluginVariant(
			variantLabel = "Lifecycle model editor, start from scratch.",
			help = LifecycleModelEditorHelp.TEXT + " Start with an empty lifecycle model.",
			requiredParameterLabels = {})
	public LifecycleModel runDefault(UIPluginContext context) {
		return new LifecycleModel("New lifecycle model");
	}

	@UITopiaVariant(
			affiliation = AuthorConstants.AFFILIATION,
			author = AuthorConstants.NAME,
			email = AuthorConstants.EMAIL)
	@PluginVariant(
			variantLabel = "Lifecycle model editor, start from a lifecycle model (copy).",
			help = LifecycleModelEditorHelp.TEXT + " Start from a lifecycle model (copy).",
			requiredParameterLabels = { 0 })
	public LifecycleModel runDefault(UIPluginContext context, LifecycleModel model) {
		return new LifecycleModel(model);
	}

	@UITopiaVariant(
			affiliation = AuthorConstants.AFFILIATION,
			author = AuthorConstants.NAME,
			email = AuthorConstants.EMAIL)
	@PluginVariant(
			variantLabel = "Lifecycle model editor, start from a transition system (copy).",
			help = LifecycleModelEditorHelp.TEXT + " Start from a transition system (copy).",
			requiredParameterLabels = { 1 })
	public LifecycleModel runDefault(UIPluginContext context, TransitionSystem ts) {
		return new LifecycleModel(ts);
	}

}
