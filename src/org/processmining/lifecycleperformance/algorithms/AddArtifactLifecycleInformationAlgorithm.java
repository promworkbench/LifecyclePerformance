package org.processmining.lifecycleperformance.algorithms;

import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.basicutils.parameters.PluginParameters;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.events.Logger.MessageLevel;
import org.processmining.lifecycleperformance.models.extensions.XObjectLifecycleExtension;
import org.processmining.lifecycleperformance.parameters.AddArtifactLifecycleInformationParameters;

public class AddArtifactLifecycleInformationAlgorithm {

	// XES Extensions
	private XLifecycleExtension le = XLifecycleExtension.instance();
	private XObjectLifecycleExtension ole = XObjectLifecycleExtension.instance();

	/**
	 * Runs through the event log and for each event, creates an object
	 * lifecycle moves attribute using the object lifecycle XES extension. For
	 * each event, a single entry is created using the activity instance,
	 * lifecycle model (obtained from log level attribute), and transition
	 * information, obtained from the concept and (default) lifecycle
	 * extensions. Note that if no log-level lifecycle model attribute or
	 * event-level model attribute is present, nothing is done. Also, the
	 * lifecycle event attribute does not need to be global, as there may be
	 * events that do not affect a lifecycle.
	 * 
	 * @param context
	 *            The plugin context.
	 * @param eventlog
	 *            The event log.
	 * @return The event log with object-centric lifecycle information.
	 */
	protected XLog apply(PluginContext context, XLog eventlog, AddArtifactLifecycleInformationParameters parameters) {

		String logModel = "";
		if (parameters.isUseLogLevelModelLabel()) {
			// Obtain the lifecycle model used in this event log (typically 'standard' or 'BPAF', as per the XES specification).
			logModel = le.extractModel(eventlog);

			// In case no model is present as a log-level attribute, we don't know which model to use, so quit with an error message.
			if (logModel == null) {
				String error = "Could not find a log-level attribute specifying the lifecycle model used. Returning original event log.";
				parameters.setMessageLevel(PluginParameters.ERROR);
				parameters.displayError(error);
				context.log(error, MessageLevel.ERROR);
				return eventlog;
			}
		}

		// Add the lifecycle model instance transition to each event.
		for (XTrace trace : eventlog) {
			for (XEvent event : trace) {

				if (!event.getAttributes().containsKey(parameters.getInstanceAttribute())
						|| (!parameters.isUseLogLevelModelLabel()
								&& !event.getAttributes().containsKey(parameters.getModelAttribute()))
						|| !event.getAttributes().containsKey(parameters.getTransitionAttribute()))
					continue;

				String instance = event.getAttributes().get(parameters.getInstanceAttribute()).toString();
				String model = parameters.isUseLogLevelModelLabel() ? logModel
						: event.getAttributes().get(parameters.getModelAttribute()).toString();
				String transition = event.getAttributes().get(parameters.getTransitionAttribute()).toString();

				ole.add(event, "Activity", instance, model, transition);
			}
		}

		// Add the extension to the log
		eventlog.getExtensions().add(ole);

		// Note that the event attribute 'moves' is not global, as there may be events that do not signal any lifecycle moves.

		return eventlog;
	}

}
