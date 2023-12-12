package org.processmining.lifecycleperformance.connections;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.connections.impl.AbstractConnection;
import org.processmining.lifecycleperformance.parameters.ActivityInstanceXLogPreprocessorParameters;

/**
 * Connection between an input event log and output event log with added
 * activity instance ids.
 * 
 * @author B.F.A. Hompes <b.f.a.hompes@tue.nl>
 *
 */
public class AssignActivityInstanceIdentitiesConnection extends AbstractConnection {

	public static final String EVENTLOGIN = "Event log";
	public static final String EVENTLOGOUT = "Event log with assigned instance identities";

	private ActivityInstanceXLogPreprocessorParameters parameters;

	public AssignActivityInstanceIdentitiesConnection(XLog eventlogin, XLog eventlogout,
			ActivityInstanceXLogPreprocessorParameters parameters) {
		super("Assign activity identities connection");
		put(EVENTLOGIN, eventlogin);
		put(EVENTLOGOUT, eventlogout);
		this.parameters = parameters;
	}

	public ActivityInstanceXLogPreprocessorParameters getParameters() {
		return parameters;
	}

}
