package org.processmining.lifecycleperformance.models.preprocessors;

import org.deckfour.xes.model.XLog;

public abstract class XLogPreprocessor implements IPreprocessor<XLog, XLog> {

	private String name;

	public XLogPreprocessor(String name) {
		setName(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
