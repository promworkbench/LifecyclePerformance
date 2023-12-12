package org.processmining.lifecycleperformance.models.lifecycle;

import java.util.Objects;

public class Aggregation {

	public static final Aggregation NO_AGGREGATION = new Aggregation("No aggregation", "");

	// FIELDS

	private String key;
	private Object value;

	// CONSTRUCTORS

	public Aggregation(String key, Object value) {
		setKey(key);
		setValue(value);
	}

	// GETTERS AND SETTERS

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	// METHODS

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Aggregation))
			return false;
		Aggregation aggregation = (Aggregation) obj;
		if (!aggregation.key.equals(key))
			return false;
		if (!aggregation.value.equals(value))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hash(key, value);
	}

	@Override
	public String toString() {
		return key + " = " + value;
	}

}
