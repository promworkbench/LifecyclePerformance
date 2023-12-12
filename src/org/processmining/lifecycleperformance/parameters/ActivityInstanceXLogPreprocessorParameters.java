package org.processmining.lifecycleperformance.parameters;

import java.util.Objects;

import org.processmining.basicutils.parameters.impl.PluginParametersImpl;

public class ActivityInstanceXLogPreprocessorParameters extends PluginParametersImpl {

	public static final String DESCRIPTION;
	public static final ActivityInstanceXLogPreprocessorStrategy DEFAULT_STRATEGY;
	public static final boolean DEFAULT_CLONE;

	static {
		DESCRIPTION = "AssignActivityInstanceIdentities parameters";
		DEFAULT_STRATEGY = ActivityInstanceXLogPreprocessorStrategy.GREEDYFIFOSHORT;
		DEFAULT_CLONE = false;
	}

	private ActivityInstanceXLogPreprocessorStrategy strategy;
	private boolean clone;

	public ActivityInstanceXLogPreprocessorParameters() {
		super();
		setStrategy(DEFAULT_STRATEGY);
		setClone(DEFAULT_CLONE);
	}

	public ActivityInstanceXLogPreprocessorStrategy getStrategy() {
		return strategy;
	}

	public void setStrategy(ActivityInstanceXLogPreprocessorStrategy strategy) {
		this.strategy = strategy;
	}

	public boolean isClone() {
		return clone;
	}

	public void setClone(boolean clone) {
		this.clone = clone;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ActivityInstanceXLogPreprocessorParameters))
			return false;

		ActivityInstanceXLogPreprocessorParameters parameters = (ActivityInstanceXLogPreprocessorParameters) obj;

		if (!getStrategy().equals(parameters.getStrategy()))
			return false;

		if (isClone() != parameters.isClone())
			return false;

		return super.equals(parameters);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), strategy, clone);
	}

	@Override
	public String toString() {
		return DESCRIPTION + strategy.getDescription() + " (clone:" + clone + ")";
	}

	public enum ActivityInstanceXLogPreprocessorStrategy {
		//@formatter:off
		GREEDYFIFO("Basic, greedy FIFO activity matching. Only start/complete lifecycle transitions."), 
		GREEDYFIFOSHORT("Basic, greedy FIFO activity matching. Only start/complete lifecycle transitions. Rather than using UUIDs uses 'instance[i]' as identifier where [i] is a growing integer.");
		//@formatter:on
		//TODO [low] Add smarter activity matching strategies that support more lifecycle transitions.

		private String description;

		ActivityInstanceXLogPreprocessorStrategy(String description) {
			setDescription(description);
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		@Override
		public String toString() {
			return description;
		}
	}
}
