package org.processmining.lifecycleperformance.models.preprocessors;

public interface IPreprocessor<I, O> {

	O preprocess(I input);

}
