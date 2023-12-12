package org.processmining.lifecycleperformance.models.performance;

import java.util.Objects;

public class TransitionExecution {

	// FIELDS

	private long timestamp;
	//	private Map<String, Object> properties;

	// CONSTRUCTORS

	public TransitionExecution() {
		//		setProperties(new HashMap<String, Object>());
	}

	public TransitionExecution(long t) {
		this();
		setTimestamp(t);
	}

	// GETTERS AND SETTERS

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	//	public Map<String, Object> getProperties() {
	//		return properties;
	//	}
	//
	//	public void setProperties(Map<String, Object> properties) {
	//		this.properties = properties;
	//	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TransitionExecution))
			return false;

		TransitionExecution te = (TransitionExecution) obj;

		if (te.timestamp != timestamp)
			return false;

		//		if (!te.properties.equals(properties))
		//			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hash(timestamp); //, properties);
	}

	@Override
	public String toString() {
		return "t: " + timestamp; // + ", prop: " + properties.toString();
	}

}
