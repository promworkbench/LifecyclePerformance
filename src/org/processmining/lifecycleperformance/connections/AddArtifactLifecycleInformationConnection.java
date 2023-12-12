package org.processmining.lifecycleperformance.connections;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.connections.impl.AbstractConnection;
import org.processmining.lifecycleperformance.parameters.AddArtifactLifecycleInformationParameters;

/**
 * Connection between an input event log and output event log with added
 * artifact lifecycle information (using the XArtifactLifecycleExtension).
 * 
 * @author B.F.A. Hompes <b.f.a.hompes@tue.nl>
 *
 */
public class AddArtifactLifecycleInformationConnection extends AbstractConnection {

	public static final String EVENTLOGIN = "Event log";
	public static final String EVENTLOGOUT = "Event log with artifact lifecycle information";

	private AddArtifactLifecycleInformationParameters parameters;

	public AddArtifactLifecycleInformationConnection(XLog eventlogin, XLog eventlogout,
			AddArtifactLifecycleInformationParameters parameters) {
		super("Add artifact lifecycle information to events connection");
		put(EVENTLOGIN, eventlogin);
		put(EVENTLOGOUT, eventlogout);
		this.parameters = parameters;
	}

	public AddArtifactLifecycleInformationParameters getParameters() {
		return parameters;
	}
}
