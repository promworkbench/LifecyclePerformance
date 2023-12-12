package org.processmining.lifecycleperformance.plugins;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.lifecycleperformance.constants.AuthorConstants;
import org.processmining.lifecycleperformance.help.LifecycleModelStringExportHelp;
import org.processmining.lifecycleperformance.models.lifecycle.LifecycleModel;
import org.processmining.lifecycleperformance.models.performance.TimedPerformanceMetric;
import org.processmining.models.graphbased.directed.transitionsystem.State;
import org.processmining.models.graphbased.directed.transitionsystem.Transition;

@Plugin(
		name = "Export lifecycle model to text",
		parameterLabels = { "Lifecycle model" },
		returnLabels = { "String (HTML)" },
		returnTypes = { String.class },
		help = LifecycleModelStringExportHelp.TEXT)
public class LifecycleModelStringExportPlugin {

	@UITopiaVariant(
			affiliation = AuthorConstants.AFFILIATION,
			author = AuthorConstants.NAME,
			email = AuthorConstants.EMAIL)
	@PluginVariant(
			variantLabel = "Export lifecycle model to text",
			help = LifecycleModelStringExportHelp.TEXT,
			requiredParameterLabels = { 0 })
	public String runDefault(UIPluginContext context, LifecycleModel model) {
		return runPrivate(context, model);
	}

	private String runPrivate(UIPluginContext context, LifecycleModel model) {
		String output = "";

		//		output += "<html>";

		output += model.getLabel();
		output += System.lineSeparator();

		for (State state : model.getNodes()) {
			output += state.getLabel();
			output += System.lineSeparator();
		}

		for (Transition transition : model.getEdges()) {
			output += "(" + transition.getSource().getLabel() 
					+ "," + transition.getLabel()
					+ "," + transition.getTarget().getLabel()
					+ ")";
			output += System.lineSeparator();
		}
		
		for(TimedPerformanceMetric metric: model.getTimedPerformanceMetrics()) {
			output += metric.getLabel()
					+ "="
					+ metric.getExpression()
					+ "||"
					+ metric.getMeasurementTimeExpression();
			output += System.lineSeparator();
		}

		//		output += "</html>";

		return output;
	}

}
