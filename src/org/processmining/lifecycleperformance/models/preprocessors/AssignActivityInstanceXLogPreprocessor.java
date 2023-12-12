package org.processmining.lifecycleperformance.models.preprocessors;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XLog;
import org.processmining.lifecycleperformance.parameters.ActivityInstanceXLogPreprocessorParameters;

/**
 * Pre-processes an event log by adding activity instance id (XID) attribute to
 * events.
 * 
 * @author B.F.A. Hompes <b.f.a.hompes@tue.nl>
 *
 */
public class AssignActivityInstanceXLogPreprocessor extends XLogPreprocessor {

	private static final String NAME;

	static {
		NAME = "Activity instance XLog preprocessor";
	}

	private ActivityInstanceXLogPreprocessorParameters parameters;

	public AssignActivityInstanceXLogPreprocessor() {
		super(NAME);
		setParameters(new ActivityInstanceXLogPreprocessorParameters());
	}

	public ActivityInstanceXLogPreprocessorParameters getParameters() {
		return parameters;
	}

	public void setParameters(ActivityInstanceXLogPreprocessorParameters parameters) {
		this.parameters = parameters;
	}

	@Override
	public XLog preprocess(XLog eventlog) {

		if (eventlog.getGlobalEventAttributes().contains(XConceptExtension.ATTR_INSTANCE))
			return eventlog;

		AssignActivityInstanceXLogPreprocessorStrategy strategy;

		switch (parameters.getStrategy()) {
			case GREEDYFIFO :
				strategy = new GreedyAssignActivityInstanceXLogPreprocessorStrategy();
				break;
			case GREEDYFIFOSHORT :
				strategy = new GreedyShortAssignActivityInstanceXLogPreprocessorStrategy();
				break;
			default :
				throw new UnsupportedOperationException("Invalid activity instance preprocessor strategy parameter!");
		}

		eventlog = strategy.preprocess(eventlog, parameters);
		eventlog.getGlobalEventAttributes().add(XConceptExtension.ATTR_INSTANCE);

		return eventlog;

	}

}
