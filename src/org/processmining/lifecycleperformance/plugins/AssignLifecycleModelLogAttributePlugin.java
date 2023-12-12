package org.processmining.lifecycleperformance.plugins;

import java.util.Collection;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.lifecycleperformance.algorithms.AssignLifecycleModelLogAttributeAlgorithm;
import org.processmining.lifecycleperformance.connections.AssignLifecycleModelLogAttributeConnection;
import org.processmining.lifecycleperformance.constants.AuthorConstants;
import org.processmining.lifecycleperformance.dialogs.AssignLifecycleModelLogAttributeWizard;
import org.processmining.lifecycleperformance.help.AssignLifecycleModelLogAttributeHelp;
import org.processmining.lifecycleperformance.parameters.AssignLifecycleModelLogAttributeParameters;

@Plugin(
		name = "Add lifecycle model attribute to event log",
		parameterLabels = { "Event log", "Parameters" },
		returnLabels = { "Event log" },
		returnTypes = { XLog.class },
		help = AssignLifecycleModelLogAttributeHelp.TEXT)

public class AssignLifecycleModelLogAttributePlugin extends AssignLifecycleModelLogAttributeAlgorithm {

	@UITopiaVariant(
			affiliation = AuthorConstants.AFFILIATION,
			author = AuthorConstants.NAME,
			email = AuthorConstants.EMAIL)
	@PluginVariant(
			variantLabel = "Add lifecycle model attribute to event log, default",
			help = AssignLifecycleModelLogAttributeHelp.TEXT
					+ " Sets the value to \"standard\" without custom parameters.",
			requiredParameterLabels = { 0 })
	public XLog runDefault(PluginContext context, XLog eventlog) {
		return runConnections(context, eventlog, new AssignLifecycleModelLogAttributeParameters());
	}

	@UITopiaVariant(
			affiliation = AuthorConstants.AFFILIATION,
			author = AuthorConstants.NAME,
			email = AuthorConstants.EMAIL)
	@PluginVariant(
			variantLabel = "Add lifecycle model attribute to event log, parameters",
			requiredParameterLabels = { 0, 1 })
	public XLog runParameters(PluginContext context, XLog eventlog,
			AssignLifecycleModelLogAttributeParameters parameters) {
		return runConnections(context, eventlog, parameters);
	}

	@UITopiaVariant(
			affiliation = AuthorConstants.AFFILIATION,
			author = AuthorConstants.NAME,
			email = AuthorConstants.EMAIL)
	@PluginVariant(
			variantLabel = "Add lifecycle model attribute to event log, dialog",
			requiredParameterLabels = { 0 })
	public XLog runDialog(UIPluginContext context, XLog eventlog) {
		AssignLifecycleModelLogAttributeParameters parameters = new AssignLifecycleModelLogAttributeParameters();
		parameters = AssignLifecycleModelLogAttributeWizard.show(context, eventlog, parameters);
		if (parameters == null) {
			// The dialog was cancelled
			context.getFutureResult(0).cancel(true);
			return null;
		}
		return runConnections(context, eventlog, parameters);
	}

	private XLog runConnections(PluginContext context, XLog eventlogin,
			AssignLifecycleModelLogAttributeParameters parameters) {

		if (parameters.isTryConnections()) {
			Collection<AssignLifecycleModelLogAttributeConnection> connections;
			try {
				connections = context.getConnectionManager()
						.getConnections(AssignLifecycleModelLogAttributeConnection.class, context, eventlogin);
				for (AssignLifecycleModelLogAttributeConnection connection : connections) {
					if (connection.getObjectWithRole(AssignLifecycleModelLogAttributeConnection.EVENTLOGIN)
							.equals(eventlogin) && connection.getParameters().equals(parameters)) {
						parameters.displayMessage("Connection found, returning the previously calculated output.");
						return connection.getObjectWithRole(AssignLifecycleModelLogAttributeConnection.EVENTLOGOUT);
					}
				}
			} catch (ConnectionCannotBeObtained e) {
				parameters.displayMessage("No connection found, have to calculate now.");
			}
		}

		XLog eventlogout = runPrivate(context, eventlogin, parameters);

		if (parameters.isTryConnections()) {
			context.getConnectionManager()
					.addConnection(new AssignLifecycleModelLogAttributeConnection(eventlogin, eventlogout, parameters));
		}

		return eventlogout;
	}

	public XLog runPrivate(PluginContext context, XLog eventlogin,
			AssignLifecycleModelLogAttributeParameters parameters) {
		long time = -System.currentTimeMillis();
		parameters.displayMessage("[Algorithm] Start");
		parameters.displayMessage("[Algorithm] Parameters: " + parameters.toString());

		XLog eventlogout = apply(context, parameters.isClone() ? (XLog) eventlogin.clone() : eventlogin, parameters);

		time += System.currentTimeMillis();
		parameters.displayMessage("[Algorithm] End (took " + DurationFormatUtils.formatDurationHMS(time) + ").");
		return eventlogout;
	}

}
