package org.processmining.lifecycleperformance.models.lifecycle;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.processmining.lifecycleperformance.models.performance.TimedPerformanceMetric;
import org.processmining.models.graphbased.directed.transitionsystem.State;
import org.processmining.models.graphbased.directed.transitionsystem.Transition;
import org.processmining.models.graphbased.directed.transitionsystem.TransitionSystem;
import org.processmining.models.graphbased.directed.transitionsystem.TransitionSystemImpl;

public class LifecycleModel extends TransitionSystemImpl {

	public static final String AGGREGATION = "aggregation";

	// FIELDS

	/*
	 * The instance of this lifecycle model.
	 */
	private String instance;

	/*
	 * The state the lifecycle model is currently in.
	 */
	private Object currentStateIdentifier;

	/*
	 * The time at which the current state was entered.
	 */
	private long timeEnteredCurrentState;

	/**
	 * A list of performance metrics defined on this lifecycle model.
	 */
	private List<TimedPerformanceMetric> performanceMetrics;

	// CONSTRUCTORS

	public LifecycleModel(String label, String instance, Object currentStateIdentifier, long timeEnteredCurrentState,
			List<TimedPerformanceMetric> performanceMetrics) {
		super(label);
		setInstance(instance);
		setCurrentStateIdentifier(currentStateIdentifier);
		setTimeEnteredCurrentState(timeEnteredCurrentState);
		setTimedPerformanceMetrics(new ArrayList<TimedPerformanceMetric>(performanceMetrics));
	}

	public LifecycleModel(String label) {
		this(label, null, null, 0l, new ArrayList<TimedPerformanceMetric>());
	}

	public LifecycleModel(String label, String instance) {
		this(label, instance, null, 0l, new ArrayList<TimedPerformanceMetric>());
	}

	public LifecycleModel(LifecycleModel model) {
		this(model.getLabel(), null, model.getCurrentStateIdentifier(), model.getTimeEnteredCurrentState(),
				new ArrayList<TimedPerformanceMetric>(model.getTimedPerformanceMetrics()));
		cloneFrom(model);
	}

	public LifecycleModel(LifecycleModel model, String instance) {
		this(model.getLabel(), instance, model.getCurrentStateIdentifier(), model.getTimeEnteredCurrentState(),
				new ArrayList<TimedPerformanceMetric>(model.getTimedPerformanceMetrics()));
		cloneFrom(model);
	}

	public LifecycleModel(TransitionSystem ts) {
		this(ts.getLabel(), null, null, 0l, new ArrayList<TimedPerformanceMetric>());
		cloneFrom(ts);
	}

	public LifecycleModel(TransitionSystem ts, String instance) {
		this(ts.getLabel(), instance, null, 0l, new ArrayList<TimedPerformanceMetric>());
		cloneFrom(ts);
	}

	// GETTERS AND SETTERS

	public String getInstance() {
		return instance;
	}

	public void setInstance(String instance) {
		this.instance = instance;
	}

	public Object getCurrentStateIdentifier() {
		return currentStateIdentifier;
	}

	public State getCurrentState() {
		return getNode(currentStateIdentifier);
	}

	public State setCurrentStateIdentifier(Object identifier) {
		if (getStates().contains(identifier)) {
			currentStateIdentifier = identifier;
			return getCurrentState();
		} else {
			return null;
		}
	}

	public long getTimeEnteredCurrentState() {
		return timeEnteredCurrentState;
	}

	public void setTimeEnteredCurrentState(long timeEnteredCurrentState) {
		this.timeEnteredCurrentState = timeEnteredCurrentState;
	}

	public List<TimedPerformanceMetric> getTimedPerformanceMetrics() {
		return performanceMetrics;
	}

	public void setTimedPerformanceMetrics(List<TimedPerformanceMetric> performanceMetrics) {
		this.performanceMetrics = performanceMetrics;
	}

	// METHODS

	protected void cloneFrom(TransitionSystem ts) {
		for (State state : ts.getNodes()) {
			addState(state.getIdentifier());
			getNode(state.getIdentifier()).setLabel(state.getLabel());
		}
		for (Transition trans : ts.getEdges()) {
			addTransition(trans.getSource().getIdentifier(), trans.getTarget().getIdentifier(), trans.getIdentifier());
			findTransition(trans.getSource().getIdentifier(), trans.getTarget().getIdentifier(), trans.getIdentifier())
					.setLabel(trans.getLabel());
		}
	}

	/**
	 * Checks whether the transition can be executed given the current state and
	 * time.
	 * 
	 * @param identifier
	 * @param dateTime
	 * @return
	 */
	public boolean canExecuteTransition(Object identifier, long dateTime) {
		return stateHasOutGoingTransition(currentStateIdentifier, identifier)
				&& timeIsLaterThanTimeEnteredCurrentState(dateTime);
	}

	/**
	 * Checks whether the state has the outgoing transition.
	 * 
	 * @param stateIdentifier
	 * @param transitionIdentifier
	 * @return
	 */
	public boolean stateHasOutGoingTransition(Object stateIdentifier, Object transitionIdentifier) {
		return getOutEdges(getNode(stateIdentifier)).stream()
				.anyMatch(t -> t.getIdentifier().equals(transitionIdentifier));
	}

	/**
	 * Checks whether the dateTime is (strictly) later than the time of the last
	 * executed transition in this lifecyle model.
	 * 
	 * @param dateTime
	 * @return
	 */
	public boolean timeIsLaterThanTimeEnteredCurrentState(long dateTime) {
		return dateTime > timeEnteredCurrentState;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof LifecycleModel))
			return false;

		LifecycleModel lcm = (LifecycleModel) obj;

		if (lcm.instance != null && instance != null) {
			if (!lcm.instance.equals(instance))
				return false;
		} else {
			if (!(lcm.instance == null && instance == null))
				return false;
		}

		if (lcm.currentStateIdentifier != null && currentStateIdentifier != null) {
			if (!lcm.currentStateIdentifier.equals(currentStateIdentifier))
				return false;
		} else {
			if (!(lcm.currentStateIdentifier == null && currentStateIdentifier == null))
				return false;
		}

		if (lcm.timeEnteredCurrentState != timeEnteredCurrentState)
			return false;

		if (lcm.performanceMetrics.equals(performanceMetrics))
			return false;

		if (!lcm.getLabel().equals(getLabel()))
			return false;

		if (!lcm.getNodes().equals(getNodes()))
			return false;

		if (!lcm.getEdges().equals(getEdges()))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hash(instance, currentStateIdentifier, timeEnteredCurrentState, performanceMetrics, getLabel(),
				getNodes(), getEdges());
	}

	@Override
	public String toString() {
		return "model: " + getLabel() + ", instance: " + instance + ", current state: " + currentStateIdentifier + " @ "
				+ timeEnteredCurrentState + " nr. metrics: " + performanceMetrics.size();
	}

	public int compareTo(LifecycleModel model) {
		return instance.compareTo(model.instance);
	}

}
