package org.processmining.lifecycleperformance.plugins;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.lifecycleperformance.constants.AuthorConstants;
import org.processmining.lifecycleperformance.help.TransitionSystemStringExportHelp;
import org.processmining.models.graphbased.directed.transitionsystem.State;
import org.processmining.models.graphbased.directed.transitionsystem.Transition;
import org.processmining.models.graphbased.directed.transitionsystem.TransitionSystem;

@Plugin(
		name = "Export transition system to text",
		parameterLabels = { "Lifecycle model", "Transition system" },
		returnLabels = { "String (HTML)" },
		returnTypes = { String.class },
		help = TransitionSystemStringExportHelp.TEXT)
public class TransitionSystemStringExportPlugin {

	//	@UITopiaVariant(
	//			affiliation = AuthorConstants.AFFILIATION,
	//			author = AuthorConstants.NAME,
	//			email = AuthorConstants.EMAIL)
	//	@PluginVariant(
	//			variantLabel = "Export lifecycle model to text",
	//			help = TransitionSystemStringExportHelp.TEXT,
	//			requiredParameterLabels = { 0 })
	//	public String runDefault(UIPluginContext context, LifecycleModel model) {
	//		return runPrivate(context, model);
	//	}

	@UITopiaVariant(
			affiliation = AuthorConstants.AFFILIATION,
			author = AuthorConstants.NAME,
			email = AuthorConstants.EMAIL)
	@PluginVariant(
			variantLabel = "Export transition system to text",
			help = TransitionSystemStringExportHelp.TEXT,
			requiredParameterLabels = { 1 })
	public String runDefault(UIPluginContext context, TransitionSystem ts) {
		return runPrivate(context, ts);
	}

	private String runPrivate(UIPluginContext context, TransitionSystem ts) {
		String output = "";

		//		output += "<html>";

		output += ts.getLabel();
		output += System.lineSeparator();

		for (State state : ts.getNodes()) {
			output += state.getLabel();
			output += System.lineSeparator();
		}

		for (Transition transition : ts.getEdges()) {
			output += "(" + transition.getSource().getLabel() 
					+ "," + transition.getLabel()
					+ "," + transition.getTarget().getLabel()
					+ ")";
			output += System.lineSeparator();
		}

		//		output += "</html>";

		return output;
	}

}
