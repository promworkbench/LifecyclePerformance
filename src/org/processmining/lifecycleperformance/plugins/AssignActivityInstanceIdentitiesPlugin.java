package org.processmining.lifecycleperformance.plugins;

import java.util.Collection;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.lifecycleperformance.algorithms.AssignActivityInstanceIdentitiesAlgorithm;
import org.processmining.lifecycleperformance.connections.AssignActivityInstanceIdentitiesConnection;
import org.processmining.lifecycleperformance.constants.AuthorConstants;
import org.processmining.lifecycleperformance.help.AssignActivityInstanceIdentitiesPluginHelp;
import org.processmining.lifecycleperformance.parameters.ActivityInstanceXLogPreprocessorParameters;
import org.processmining.lifecycleperformance.parameters.ActivityInstanceXLogPreprocessorParameters.ActivityInstanceXLogPreprocessorStrategy;

/**
 * Plug-in that assigns activity identities to events.
 * 
 * @author B.F.A. Hompes <b.f.a.hompes@tue.nl>
 *
 */
@Plugin(
		name = "Assign activity instance identities",
		parameterLabels = { "Event log", "Parameters" },
		returnLabels = { "The event log with activity instance identities" },
		returnTypes = { XLog.class },
		help = AssignActivityInstanceIdentitiesPluginHelp.TEXT)
public class AssignActivityInstanceIdentitiesPlugin extends AssignActivityInstanceIdentitiesAlgorithm {

	/**
	 * The plug-in variant that runs in any context and uses the default
	 * parameters.
	 * 
	 * @param context
	 *            The context to run in.
	 * @param eventlogin
	 *            The event log.
	 * @return The event log with event instance ids.
	 */
	@UITopiaVariant(
			author = AuthorConstants.NAME,
			email = AuthorConstants.EMAIL,
			affiliation = AuthorConstants.AFFILIATION)
	@PluginVariant(
			variantLabel = "Assign activity instance identities, default",
			requiredParameterLabels = { 0 })
	public XLog runDefault(PluginContext context, XLog eventlogin) {
		// Get the default parameters.
		ActivityInstanceXLogPreprocessorParameters parameters = new ActivityInstanceXLogPreprocessorParameters();
		//		parameters.setStrategy(ActivityInstanceXLogPreprocessorStrategy.GREEDY);
		parameters.setStrategy(ActivityInstanceXLogPreprocessorStrategy.GREEDYFIFOSHORT);
		// Apply the algorithm depending on whether a connection already exists.
		return runConnections(context, eventlogin, parameters);
	}

	/**
	 * The plug-in variant that runs in any context and requires parameters.
	 * 
	 * @param context
	 *            The context to run in.
	 * @param eventlogin
	 *            The event log.
	 * @param parameters
	 *            The parameters to use.
	 * @return The event log with event instance ids.
	 */
	@UITopiaVariant(
			author = AuthorConstants.NAME,
			email = AuthorConstants.EMAIL,
			affiliation = AuthorConstants.AFFILIATION)
	@PluginVariant(
			variantLabel = "Assign activity instance identities, parameters",
			requiredParameterLabels = { 0, 1 })
	public XLog run(PluginContext context, XLog eventlogin, ActivityInstanceXLogPreprocessorParameters parameters) {
		// Apply the algorithm depending on whether a connection already exists.
		return runConnections(context, eventlogin, parameters);
	}

	//TODO [low] make a ui plugin variant with a dialog where the strategy can be selected

	/**
	 * Apply the algorithm depending on whether a connection already exists.
	 * 
	 * @param context
	 *            The context to run in.
	 * @param eventlogin
	 *            The event log.
	 * @return The event log with event instance ids.
	 */
	private XLog runConnections(PluginContext context, XLog eventlogin,
			ActivityInstanceXLogPreprocessorParameters parameters) {
		if (parameters.isTryConnections()) {
			// Try to found a connection that matches the inputs and the parameters.
			Collection<AssignActivityInstanceIdentitiesConnection> connections;
			try {
				connections = context.getConnectionManager()
						.getConnections(AssignActivityInstanceIdentitiesConnection.class, context, eventlogin);
				for (AssignActivityInstanceIdentitiesConnection connection : connections) {
					if (connection.getObjectWithRole(AssignActivityInstanceIdentitiesConnection.EVENTLOGIN)
							.equals(eventlogin) && connection.getParameters().equals(parameters)) {
						// Found a match. Return the associated output as result of the algorithm.
						return connection.getObjectWithRole(AssignActivityInstanceIdentitiesConnection.EVENTLOGOUT);
					}
				}
			} catch (ConnectionCannotBeObtained e) {
			}
		}
		// No connection found. Apply the algorithm to compute a fresh output result.
		XLog eventlogout = apply(eventlogin, parameters);
		if (parameters.isTryConnections()) {
			// Store a connection containing the inputs, output, and parameters.
			context.getConnectionManager()
					.addConnection(new AssignActivityInstanceIdentitiesConnection(eventlogin, eventlogout, parameters));
		}
		// Return the output.
		return eventlogout;
	}

}
