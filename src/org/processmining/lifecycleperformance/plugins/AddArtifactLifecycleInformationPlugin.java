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
import org.processmining.lifecycleperformance.algorithms.AddArtifactLifecycleInformationAlgorithm;
import org.processmining.lifecycleperformance.connections.AddArtifactLifecycleInformationConnection;
import org.processmining.lifecycleperformance.constants.AuthorConstants;
import org.processmining.lifecycleperformance.dialogs.AddArtifactLifecycleInformationWizard;
import org.processmining.lifecycleperformance.help.AddArtifactLifecycleInformationHelp;
import org.processmining.lifecycleperformance.parameters.AddArtifactLifecycleInformationParameters;

@Plugin(
		name = "Add artifact lifecycle information to events",
		parameterLabels = { "Event log", "Parameters" },
		returnLabels = { "Event log (with artifact lifecycle information)" },
		returnTypes = { XLog.class },
		help = AddArtifactLifecycleInformationHelp.TEXT)

public class AddArtifactLifecycleInformationPlugin extends AddArtifactLifecycleInformationAlgorithm {

	@UITopiaVariant(
			affiliation = AuthorConstants.AFFILIATION,
			author = AuthorConstants.NAME,
			email = AuthorConstants.EMAIL)
	@PluginVariant(
			variantLabel = "Add artifact lifecycle information to events, default",
			requiredParameterLabels = { 0 })
	public XLog runDefault(PluginContext context, XLog eventlogin) {
		return runConnections(context, eventlogin, new AddArtifactLifecycleInformationParameters());
	}

	@UITopiaVariant(
			affiliation = AuthorConstants.AFFILIATION,
			author = AuthorConstants.NAME,
			email = AuthorConstants.EMAIL)
	@PluginVariant(
			variantLabel = "Add artifact lifecycle information to events, dialog",
			requiredParameterLabels = { 0 })
	public XLog runParameters(UIPluginContext context, XLog eventlogin) {
		AddArtifactLifecycleInformationParameters parameters = new AddArtifactLifecycleInformationParameters();
		parameters = AddArtifactLifecycleInformationWizard.show(context, eventlogin, parameters);
		if (parameters == null) {
			// The dialog was cancelled
			context.getFutureResult(0).cancel(true);
			return null;
		}
		return runConnections(context, eventlogin, parameters);
	}

	@UITopiaVariant(
			affiliation = AuthorConstants.AFFILIATION,
			author = AuthorConstants.NAME,
			email = AuthorConstants.EMAIL)
	@PluginVariant(
			variantLabel = "Add artifact lifecycle information to events, parameters",
			requiredParameterLabels = { 0, 1 })
	public XLog runParameters(PluginContext context, XLog eventlogin,
			AddArtifactLifecycleInformationParameters parameters) {
		return runConnections(context, eventlogin, parameters);
	}

	private XLog runConnections(PluginContext context, XLog eventlogin,
			AddArtifactLifecycleInformationParameters parameters) {

		if (parameters.isTryConnections()) {
			Collection<AddArtifactLifecycleInformationConnection> connections;
			try {
				connections = context.getConnectionManager()
						.getConnections(AddArtifactLifecycleInformationConnection.class, context, eventlogin);
				for (AddArtifactLifecycleInformationConnection connection : connections) {
					if (connection.getObjectWithRole(AddArtifactLifecycleInformationConnection.EVENTLOGIN)
							.equals(eventlogin) && connection.getParameters().equals(parameters)) {
						parameters.displayMessage("Connection found, returning the previously calculated output.");
						return connection.getObjectWithRole(AddArtifactLifecycleInformationConnection.EVENTLOGOUT);
					}
				}
			} catch (ConnectionCannotBeObtained e) {
				parameters.displayMessage("No connection found, have to calculate now.");
			}
		}

		XLog eventlogout = runPrivate(context, eventlogin, parameters);

		if (parameters.isTryConnections()) {
			context.getConnectionManager()
					.addConnection(new AddArtifactLifecycleInformationConnection(eventlogin, eventlogout, parameters));
		}

		return eventlogout;
	}

	public XLog runPrivate(PluginContext context, XLog eventlogin,
			AddArtifactLifecycleInformationParameters parameters) {
		long time = -System.currentTimeMillis();
		parameters.displayMessage("[Algorithm] Start");
		parameters.displayMessage("[Algorithm] Parameters: " + parameters.toString());

		XLog eventlogout = apply(context, parameters.isClone() ? (XLog) eventlogin.clone() : eventlogin, parameters);

		time += System.currentTimeMillis();
		parameters.displayMessage("[Algorithm] End (took " + DurationFormatUtils.formatDurationHMS(time) + ").");
		return eventlogout;
	}
}