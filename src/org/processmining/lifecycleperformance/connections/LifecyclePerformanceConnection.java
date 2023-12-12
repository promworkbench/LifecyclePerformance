package org.processmining.lifecycleperformance.connections;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.connections.impl.AbstractConnection;
import org.processmining.lifecycleperformance.models.performance.PerformanceAnalysisResults;
import org.processmining.lifecycleperformance.parameters.LifecyclePerformanceParameters;

/**
 * Connection between an input event log and lifecycle-based performance results
 * (using the XArtifactLifecycleExtension).
 * 
 * @author B.F.A. Hompes <b.f.a.hompes@tue.nl>
 *
 */
public class LifecyclePerformanceConnection extends AbstractConnection {

	public static final String EVENTLOG = "Event log";
	public static final String RESULTS = "Lifecycle performance analysis results";

	private LifecyclePerformanceParameters parameters;

	public LifecyclePerformanceConnection(XLog eventlogin, PerformanceAnalysisResults results,
			LifecyclePerformanceParameters parameters) {
		super("Lifecycle performance connection");
		put(EVENTLOG, eventlogin);
		put(RESULTS, results);
		this.parameters = parameters;
	}

	public LifecyclePerformanceParameters getParameters() {
		return parameters;
	}
}
