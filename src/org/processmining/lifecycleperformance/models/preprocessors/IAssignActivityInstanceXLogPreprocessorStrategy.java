package org.processmining.lifecycleperformance.models.preprocessors;

import org.deckfour.xes.model.XLog;
import org.processmining.lifecycleperformance.parameters.ActivityInstanceXLogPreprocessorParameters;

public interface IAssignActivityInstanceXLogPreprocessorStrategy {

	XLog preprocess(XLog eventlog, ActivityInstanceXLogPreprocessorParameters parameters);

}
