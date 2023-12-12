package org.processmining.lifecycleperformance.models.performance;

import java.util.Objects;

public class StateVisit {

	// FIELDS

	private long timestampEntry;
	private long timestampExit;
	//	private Map<String, Object> properties;

	// CONSTRUCTORS

	public StateVisit() {
		//		setProperties(new HashMap<String, Object>());
	}

	public StateVisit(long timestampEntry, long timestampExit) {
		this();
		setTimestampEnty(timestampEntry);
		setTimestampExit(timestampExit);
	}

	// GETTERS AND SETTERS

	public long getTimestampEntry() {
		return timestampEntry;
	}

	public void setTimestampEnty(long timestampEntered) {
		this.timestampEntry = timestampEntered;
	}

	public long getTimestampExit() {
		return timestampExit;
	}

	public void setTimestampExit(long timestampExited) {
		this.timestampExit = timestampExited;
	}

	//	public Map<String, Object> getProperties() {
	//		return properties;
	//	}
	//
	//	public void setProperties(Map<String, Object> properties) {
	//		this.properties = properties;
	//	}

	// METHODS

	public long getTimeSpent() {
		return timestampExit - timestampEntry;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof StateVisit))
			return false;

		StateVisit sv = (StateVisit) obj;

		if (sv.timestampEntry != timestampEntry)
			return false;
		if (sv.timestampExit != timestampExit)
			return false;
		//		if (!sv.properties.equals(properties))
		//			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hash(timestampEntry, timestampExit); //, properties);
	}

	@Override
	public String toString() {
		return "in: " + timestampEntry + ", out: " + timestampExit; // + ", prop: " + properties.toString();
	}

}
