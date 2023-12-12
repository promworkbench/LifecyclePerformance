package org.processmining.lifecycleperformance.parameters;

import java.util.Objects;

import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.processmining.basicutils.parameters.impl.PluginParametersImpl;

public class AssignLifecycleModelLogAttributeParameters extends PluginParametersImpl {

	public static final boolean DEFAULT_CLONE;
	public static final String DEFAULT_MODEL;

	static {
		DEFAULT_CLONE = true;
		DEFAULT_MODEL = XLifecycleExtension.VALUE_MODEL_STANDARD;
	}

	// FIELDS

	/**
	 * Whether or not we clone the original event log or modify it in place.
	 */
	private boolean clone;

	/*
	 * The name of the lifecycle model to store in the event log attributes.
	 */
	private String model;

	// CONSTRUCTORS

	public AssignLifecycleModelLogAttributeParameters() {
		setClone(DEFAULT_CLONE);
		setModel(DEFAULT_MODEL);
	}

	// GETTERS AND SETTERS

	public boolean isClone() {
		return clone;
	}

	public void setClone(boolean clone) {
		this.clone = clone;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	// METHODS

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AssignLifecycleModelLogAttributeParameters))
			return false;

		AssignLifecycleModelLogAttributeParameters param = (AssignLifecycleModelLogAttributeParameters) obj;

		if (param.clone != clone)
			return false;

		if (!param.model.equals(model))
			return false;

		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), clone, model);
	}

}
