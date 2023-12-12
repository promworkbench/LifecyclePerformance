package org.processmining.lifecycleperformance.plugins;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.lifecycleperformance.constants.AuthorConstants;
import org.processmining.lifecycleperformance.help.StringCreatorHelp;
import org.processmining.lifecycleperformance.help.TransitionSystemStringExportHelp;

@Plugin(
		name = "Create String object",
		parameterLabels = {},
		returnLabels = { "String" },
		returnTypes = { String.class },
		help = StringCreatorHelp.TEXT)
public class StringCreatorPlugin {

	@UITopiaVariant(
			affiliation = AuthorConstants.AFFILIATION,
			author = AuthorConstants.NAME,
			email = AuthorConstants.EMAIL)
	@PluginVariant(
			variantLabel = "Create String object",
			help = TransitionSystemStringExportHelp.TEXT,
			requiredParameterLabels = {})
	public String runDefault(UIPluginContext context) {
		return runPrivate(context);
	}

	private String runPrivate(UIPluginContext context) {
		return "";
	}

}
