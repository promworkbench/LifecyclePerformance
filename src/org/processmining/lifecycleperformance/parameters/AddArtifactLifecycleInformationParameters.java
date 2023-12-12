package org.processmining.lifecycleperformance.parameters;

import java.util.Objects;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.processmining.basicutils.parameters.impl.PluginParametersImpl;

public class AddArtifactLifecycleInformationParameters extends PluginParametersImpl {

	public static final boolean DEFAULT_CLONE;
	public static final boolean DEFAULT_USELOGLEVELMODELLABEL;
	public static final String DEFAULT_MODELATTRIBUTE;
	public static final String DEFAULT_INSTANCEATTRIBUTE;
	public static final String DEFAULT_TRANSITIONATTRIBUTE;

	static {
		DEFAULT_CLONE = true;
		DEFAULT_USELOGLEVELMODELLABEL = true;
		DEFAULT_MODELATTRIBUTE = XLifecycleExtension.KEY_MODEL;
		DEFAULT_INSTANCEATTRIBUTE = XConceptExtension.KEY_INSTANCE;
		DEFAULT_TRANSITIONATTRIBUTE = XLifecycleExtension.KEY_TRANSITION;
	}

	// FIELDS

	/**
	 * Whether or not we clone the original event log or modify it in place.
	 */
	private boolean clone;
	private boolean useLogLevelModelLabel;
	private String modelAttribute;
	private String instanceAttribute;
	private String transitionAttribute;

	// CONSTRUCTORS

	public AddArtifactLifecycleInformationParameters() {
		setClone(DEFAULT_CLONE);
		setUseLogLevelModelLabel(DEFAULT_USELOGLEVELMODELLABEL);
		setModelAttribute(DEFAULT_MODELATTRIBUTE);
		setInstanceAttribute(DEFAULT_INSTANCEATTRIBUTE);
		setTransitionAttribute(DEFAULT_TRANSITIONATTRIBUTE);
	}

	// GETTERS AND SETTERS

	public boolean isClone() {
		return clone;
	}

	public void setClone(boolean clone) {
		this.clone = clone;
	}

	public boolean isUseLogLevelModelLabel() {
		return useLogLevelModelLabel;
	}

	public void setUseLogLevelModelLabel(boolean useLogLevelModelLabel) {
		this.useLogLevelModelLabel = useLogLevelModelLabel;
	}

	public String getModelAttribute() {
		return modelAttribute;
	}

	public void setModelAttribute(String modelAttribute) {
		this.modelAttribute = modelAttribute;
	}

	public String getInstanceAttribute() {
		return instanceAttribute;
	}

	public void setInstanceAttribute(String instanceAttribute) {
		this.instanceAttribute = instanceAttribute;
	}

	public String getTransitionAttribute() {
		return transitionAttribute;
	}

	public void setTransitionAttribute(String transitionAttribute) {
		this.transitionAttribute = transitionAttribute;
	}

	// METHODS

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AddArtifactLifecycleInformationParameters))
			return false;

		AddArtifactLifecycleInformationParameters param = (AddArtifactLifecycleInformationParameters) obj;

		if (param.clone != clone)
			return false;
		if (param.useLogLevelModelLabel != useLogLevelModelLabel)
			return false;
		if (!param.modelAttribute.equals(modelAttribute))
			return false;
		if (!param.instanceAttribute.equals(instanceAttribute))
			return false;
		if (!param.transitionAttribute.equals(transitionAttribute))
			return false;

		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), clone, useLogLevelModelLabel, modelAttribute, instanceAttribute,
				transitionAttribute);
	}

}
