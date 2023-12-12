package org.processmining.lifecycleperformance.algorithms;

import org.deckfour.xes.model.XLog;
import org.processmining.lifecycleperformance.models.preprocessors.AssignActivityInstanceXLogPreprocessor;
import org.processmining.lifecycleperformance.parameters.ActivityInstanceXLogPreprocessorParameters;

public class AssignActivityInstanceIdentitiesAlgorithm {

	protected XLog apply(XLog eventlogin, ActivityInstanceXLogPreprocessorParameters parameters) {

		XLog eventlogout = parameters.isClone() ? (XLog) eventlogin.clone() : eventlogin;

		AssignActivityInstanceXLogPreprocessor preprocessor = new AssignActivityInstanceXLogPreprocessor();
		preprocessor.setParameters(parameters);

		return preprocessor.preprocess(eventlogout);

	}

}
