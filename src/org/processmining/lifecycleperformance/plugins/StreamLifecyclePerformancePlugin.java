package org.processmining.lifecycleperformance.plugins;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.eventstream.core.interfaces.XSEventStream;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.lifecycleperformance.algorithms.StreamLifecyclePerformanceAlgorithm;
import org.processmining.lifecycleperformance.constants.AuthorConstants;
import org.processmining.lifecycleperformance.help.LifecyclePerformanceHelp;
import org.processmining.lifecycleperformance.models.lifecycle.LifecycleModel;
import org.processmining.lifecycleperformance.parameters.LifecyclePerformanceParameters;

@Plugin(
		name = "Calculate performance using lifecycle models on an event stream",
		parameterLabels = { "Event stream", "Parameters", "Lifecycle models" },
		returnLabels = { "Lifecycle-based Process Performance Analysis Results" },
		returnTypes = { StreamLifecyclePerformanceAlgorithm.class },
		help = LifecyclePerformanceHelp.TEXT)

public class StreamLifecyclePerformancePlugin {

	@UITopiaVariant(
			affiliation = AuthorConstants.AFFILIATION,
			author = AuthorConstants.NAME,
			email = AuthorConstants.EMAIL)
	@PluginVariant(
			variantLabel = "Calculate performance using lifecycle models on an event stream, default",
			requiredParameterLabels = { 0 })
	public StreamLifecyclePerformanceAlgorithm run(UIPluginContext context, XSEventStream stream) {
		return runStream(context, stream, new LifecyclePerformanceParameters());
	}

	@UITopiaVariant(
			affiliation = AuthorConstants.AFFILIATION,
			author = AuthorConstants.NAME,
			email = AuthorConstants.EMAIL)
	@PluginVariant(
			variantLabel = "Calculate performance using lifecycle models on an event stream, lifecycle models",
			help = LifecyclePerformanceHelp.TEXT + " Using lifecycle models.",
			requiredParameterLabels = { 0, 2 })
	public StreamLifecyclePerformanceAlgorithm runModels(UIPluginContext context, XSEventStream stream,
			LifecycleModel... lifecycleModels) {
		LifecyclePerformanceParameters parameters = new LifecyclePerformanceParameters();
		parameters.addLifecycleModels(lifecycleModels);
		return runStream(context, stream, parameters);
	}

	@UITopiaVariant(
			affiliation = AuthorConstants.AFFILIATION,
			author = AuthorConstants.NAME,
			email = AuthorConstants.EMAIL)
	@PluginVariant(
			variantLabel = "Calculate performance using lifecycle models on an event stream, parameters",
			requiredParameterLabels = { 0, 1 })
	public StreamLifecyclePerformanceAlgorithm runParameters(UIPluginContext context, XSEventStream stream,
			LifecyclePerformanceParameters parameters) {
		return runStream(context, stream, parameters);
	}

	public StreamLifecyclePerformanceAlgorithm runStream(UIPluginContext context, XSEventStream stream,
			LifecyclePerformanceParameters parameters) {

		StreamLifecyclePerformanceAlgorithm reader = new StreamLifecyclePerformanceAlgorithm(context, parameters);
		reader.start();
		stream.connect(reader);
		return reader;

	}

}