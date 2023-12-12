package org.processmining.lifecycleperformance.connections;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.connections.impl.AbstractConnection;
import org.processmining.lifecycleperformance.parameters.AssignLifecycleModelLogAttributeParameters;

/**
 * Connection between an input event log and output event log with added
 * lifecycle model log attribute information (using LifecycleExtension).
 * 
 * @author B.F.A. Hompes <b.f.a.hompes@tue.nl>
 *
 */
public class AssignLifecycleModelLogAttributeConnection extends AbstractConnection {

	public static final String EVENTLOGIN = "Event log";
	public static final String EVENTLOGOUT = "Event log with lifecycle model attribute";

	private AssignLifecycleModelLogAttributeParameters parameters;

	public AssignLifecycleModelLogAttributeConnection(XLog eventlogin, XLog eventlogout,
			AssignLifecycleModelLogAttributeParameters parameters) {
		super("Add lifecycle model attribute to event log connection");
		put(EVENTLOGIN, eventlogin);
		put(EVENTLOGOUT, eventlogout);
		this.parameters = parameters;
	}

	public AssignLifecycleModelLogAttributeParameters getParameters() {
		return parameters;
	}
}
