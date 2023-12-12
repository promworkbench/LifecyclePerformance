package org.processmining.lifecycleperformance.plugins;

import java.util.Collection;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.lifecycleperformance.algorithms.LifecyclePerformanceAlgorithm;
import org.processmining.lifecycleperformance.connections.LifecyclePerformanceConnection;
import org.processmining.lifecycleperformance.constants.AuthorConstants;
import org.processmining.lifecycleperformance.dialogs.LifecyclePerformanceWizard;
import org.processmining.lifecycleperformance.help.LifecyclePerformanceHelp;
import org.processmining.lifecycleperformance.models.lifecycle.LifecycleModel;
import org.processmining.lifecycleperformance.models.performance.PerformanceAnalysisResults;
import org.processmining.lifecycleperformance.parameters.LifecyclePerformanceParameters;

@Plugin(
		name = "Calculate performance using lifecycle models",
		parameterLabels = { "Event log", "Parameters", "Lifecycle models" },
		returnLabels = { "Lifecycle-based Process Performance Analysis Results" },
		returnTypes = { PerformanceAnalysisResults.class },
		help = LifecyclePerformanceHelp.TEXT)

public class LifecyclePerformancePlugin extends LifecyclePerformanceAlgorithm {

	// Made just for testing
	//	@UITopiaVariant(
	//			affiliation = AuthorConstants.AFFILIATION,
	//			author = AuthorConstants.NAME,
	//			email = AuthorConstants.EMAIL)
	//	@PluginVariant(
	//			variantLabel = "Calculate performance using lifecycle models, default",
	//			help = LifecyclePerformanceHelp.TEXT,
	//			requiredParameterLabels = { 0 })
	//	public PerformanceAnalysisResults run(UIPluginContext context, XLog eventlog) {
	//		LifecyclePerformanceParameters parameters = new LifecyclePerformanceParameters();
	//		LifecycleModel standard = LifecycleModelFactory.generateSmallActivityLifecycleModel();
	//		parameters.addLifecycleModels(standard);
	//		parameters.addAggregation("standard", "concept:name");
	//		parameters.addAggregation("standard", "org:resource");
	//		parameters.addAggregation("standard", "defectFixed");
	//		parameters.addAggregation("standard", "numberRepairs");
	//		parameters.addAggregation("standard", "phoneType");
	//		return runConnections(context, eventlog, parameters);
	//	}

	@UITopiaVariant(
			affiliation = AuthorConstants.AFFILIATION,
			author = AuthorConstants.NAME,
			email = AuthorConstants.EMAIL)
	@PluginVariant(
			variantLabel = "Calculate performance using lifecycle models, dialog",
			help = LifecyclePerformanceHelp.TEXT + " Using dialog.",
			requiredParameterLabels = { 0, 2 })
	public PerformanceAnalysisResults runDialog(UIPluginContext context, XLog eventlog,
			LifecycleModel... lifecycleModels) {
		LifecyclePerformanceParameters parameters = new LifecyclePerformanceParameters();
		parameters.addLifecycleModels(lifecycleModels);
		parameters = LifecyclePerformanceWizard.show(context, eventlog, parameters);
		if (parameters == null) {
			// The dialog was cancelled
			context.getFutureResult(0).cancel(true);
			return null;
		}
		return runConnections(context, eventlog, parameters);
	}

	@UITopiaVariant(
			affiliation = AuthorConstants.AFFILIATION,
			author = AuthorConstants.NAME,
			email = AuthorConstants.EMAIL)
	@PluginVariant(
			variantLabel = "Calculate performance using lifecycle models, parameters",
			help = LifecyclePerformanceHelp.TEXT + " Using parameters object.",
			requiredParameterLabels = { 0, 1 })
	public PerformanceAnalysisResults runParameters(UIPluginContext context, XLog eventlog,
			LifecyclePerformanceParameters parameters) {
		return runConnections(context, eventlog, parameters);
	}

	@UITopiaVariant(
			affiliation = AuthorConstants.AFFILIATION,
			author = AuthorConstants.NAME,
			email = AuthorConstants.EMAIL)
	@PluginVariant(
			variantLabel = "Calculate performance using lifecycle models, lifecycle models",
			help = LifecyclePerformanceHelp.TEXT + " Using lifecycle models.",
			requiredParameterLabels = { 0, 2 })
	public PerformanceAnalysisResults runParameters(UIPluginContext context, XLog eventlog,
			LifecycleModel... lifecycleModels) {
		LifecyclePerformanceParameters parameters = new LifecyclePerformanceParameters();
		parameters.addLifecycleModels(lifecycleModels);
		return runConnections(context, eventlog, parameters);
	}

	private PerformanceAnalysisResults runConnections(UIPluginContext context, XLog eventlogin,
			LifecyclePerformanceParameters parameters) {

		if (parameters.isTryConnections()) {
			Collection<LifecyclePerformanceConnection> connections;
			try {
				connections = context.getConnectionManager().getConnections(LifecyclePerformanceConnection.class,
						context, eventlogin);
				for (LifecyclePerformanceConnection connection : connections) {
					if (connection.getObjectWithRole(LifecyclePerformanceConnection.EVENTLOG).equals(eventlogin)
							&& connection.getParameters().equals(parameters)) {
						parameters.displayMessage("Connection found, returning the previously calculated output.");
						return connection.getObjectWithRole(LifecyclePerformanceConnection.RESULTS);
					}
				}
			} catch (ConnectionCannotBeObtained e) {
				parameters.displayMessage("No connection found, have to calculate now.");
			}
		}

		PerformanceAnalysisResults results = runPrivate(context, eventlogin, parameters);

		if (parameters.isTryConnections()) {
			context.getConnectionManager()
					.addConnection(new LifecyclePerformanceConnection(eventlogin, results, parameters));
		}

		return results;
	}

	public PerformanceAnalysisResults runPrivate(UIPluginContext context, XLog eventlogin,
			LifecyclePerformanceParameters parameters) {

		long time = -System.currentTimeMillis();
		parameters.displayMessage("[Algorithm] Start");
		parameters.displayMessage("[Algorithm] Parameters: " + parameters.toString());

		init();
		setParameters(parameters);
		for (LifecycleModel model : parameters.getLifecycleModels()) {
			setInitialState(model);
		}
		handleEventlog(eventlogin);
		PerformanceAnalysisResults results = getResults();
		results.setParameters(parameters);

		time += System.currentTimeMillis();
		parameters.displayMessage("[Algorithm] End (took " + DurationFormatUtils.formatDurationHMS(time) + ").");

		return results;
	}

}