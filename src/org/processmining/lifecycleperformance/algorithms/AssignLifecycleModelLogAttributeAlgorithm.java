package org.processmining.lifecycleperformance.algorithms;

import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.lifecycleperformance.parameters.AssignLifecycleModelLogAttributeParameters;

public class AssignLifecycleModelLogAttributeAlgorithm {

	protected XLog apply(PluginContext context, XLog eventlog, AssignLifecycleModelLogAttributeParameters parameters) {
		XLifecycleExtension.instance().assignModel(eventlog, parameters.getModel());
		return eventlog;
	}

}
