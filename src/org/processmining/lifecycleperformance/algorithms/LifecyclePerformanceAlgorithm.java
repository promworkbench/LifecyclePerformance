package org.processmining.lifecycleperformance.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.lifecycleperformance.models.comparators.XEventTimeStampComparator;
import org.processmining.lifecycleperformance.models.extensions.XObjectLifecycleExtension;
import org.processmining.lifecycleperformance.models.lifecycle.Aggregation;
import org.processmining.lifecycleperformance.models.lifecycle.LifecycleModel;
import org.processmining.lifecycleperformance.models.lifecycle.LifecycleModelFactory;
import org.processmining.lifecycleperformance.models.performance.PerformanceAnalysisResults;
import org.processmining.lifecycleperformance.models.performance.PerformanceMeasure;
import org.processmining.lifecycleperformance.models.performance.StateVisit;
import org.processmining.lifecycleperformance.models.performance.TimedPerformanceMetric;
import org.processmining.lifecycleperformance.models.performance.TransitionExecution;
import org.processmining.lifecycleperformance.parameters.LifecyclePerformanceParameters;
import org.processmining.models.graphbased.directed.transitionsystem.State;
import org.processmining.models.graphbased.directed.transitionsystem.Transition;

/*
 * This class performs a replay of the events over their lifecycle models.
 * Currently uses DescriptiveStatistics for state measurements, which keeps all
 * values in memory, in order to calculate a median. In case this is too heavy,
 * change to SummaryStatistics since that does not store all values, only
 * aggregates, but cannot compute median (or other percentiles).
 */
public class LifecyclePerformanceAlgorithm {

	// XES EXTENSIONS

	private XTimeExtension te = XTimeExtension.instance();
	private XObjectLifecycleExtension ole = XObjectLifecycleExtension.instance();

	// FIELDS

	private LifecyclePerformanceParameters parameters;
	private PerformanceAnalysisResults results;

	// GETTERS AND SETTERS

	public LifecyclePerformanceParameters getParameters() {
		return parameters;
	}

	public void setParameters(LifecyclePerformanceParameters parameters) {
		this.parameters = parameters;
	}

	public PerformanceAnalysisResults getResults() {
		return results;
	}

	public void setMemory(PerformanceAnalysisResults results) {
		this.results = results;
	}

	// METHODS

	/**
	 * Initializes the memory
	 */
	protected void init() {
		results = new PerformanceAnalysisResults();
	}

	/**
	 * Measures performance measures and calculates performance metrics using
	 * the lifecycle performance approach and a static event log as input.
	 */
	protected void handleEventlog(XLog eventlog) {
		// Transform event log into list of events ('stream')
		List<XEvent> eventList = new ArrayList<XEvent>();

		// Add all events in the log to the list
		for (XTrace trace : eventlog)
			eventList.addAll(trace);

		// Keep only events that affect at least one lifecycle model according to the lifecycle extension
		// Not strictly necessary, but speeds up sorting.
		eventList = eventList.stream().filter(event -> CollectionUtils.isNotEmpty(ole.extractMoves(event)))
				.collect(Collectors.toList());

		// Sort the list by time (assume no events with same timestamp affect the same lifecycle model instance).
		//TODO [low] assumption for lifecycle model instance event timestamps
		Collections.sort(eventList, new XEventTimeStampComparator());

		// First, handle each event
		for (XEvent event : eventList)
			handleEvent(event);

		// Then, at the end, compute the performance metrics only once
		calculateLifecyclePerformanceMetrics();
	}

	/**
	 * Handles the given event and its potential effects on any performance
	 * measure and metric.
	 * 
	 * @param event
	 *            The event to handle
	 */
	@SuppressWarnings("unchecked")
	protected void handleEvent(XEvent event) {
		System.out.println("Handling event");

		// Loop over the lifecycle moves of the event
		Set<List<String>> movesSet = ole.extractMovesListSet(event);
		for (List<String> move : movesSet) {
			String type = move.get(0);
			String instance = move.get(1);
			String model = move.get(2);
			String transition = move.get(3);

			/*
			 * Make sure the prerequisites are in order
			 */

			// Get the affected lifecycle model instance from memory, and if it doesn't exist yet, create a new one.
			if (!results.getTypeResults(type).contains(model, instance))
				results.getTypeResults(type).put(model, instance,
						LifecycleModelFactory.generateLifecycleModel(model, instance, parameters));

			LifecycleModel lcm = results.getResults().get(type).get(model, instance);

			// Make sure that the lifecycle model instance has the aggregations set
			if (!lcm.getAttributeMap().containsKey(LifecycleModel.AGGREGATION))
				lcm.getAttributeMap().put(LifecycleModel.AGGREGATION, new HashMap<String, Object>());

			for (String aggregationAttribute : parameters.getAggregations(model)) {
				if (event.getAttributes().containsKey(aggregationAttribute))
					((Map<String, Object>) lcm.getAttributeMap().get(LifecycleModel.AGGREGATION)).put(
							aggregationAttribute,
							new Aggregation(aggregationAttribute, event.getAttributes().get(aggregationAttribute)));
			}

			/*
			 * Perform the replay
			 */

			// Check whether the transition is possible from the current state and time according to the lifecycle model instance. 
			// Skip if not possible (assume total conformance)
			//TODO [medium] assumption for total conformance
			long eventTimeStamp = te.extractTimestamp(event).getTime();
			if (!lcm.canExecuteTransition(transition, eventTimeStamp)) {
				System.out.printf("Could not execute transition %s for model %s for %s instance %s %n", transition,
						model, type, instance);
				continue;
			}

			// Find the transition (first transition with this label, assume deterministic lifecycle models)
			//TODO [low] assumption for deterministic lifecycle models
			State currentState = lcm.getCurrentState();
			Transition t = lcm.getOutEdges(currentState).stream().filter(trans -> trans.getLabel().equals(transition))
					.findFirst().get();

			// 'Execute' the transition:

			// - Add the currently finishing state visit to the state visits of the current state
			if (!currentState.getAttributeMap().containsKey(PerformanceMeasure.STATE_VISITS_BASE.getLabel()))
				currentState.getAttributeMap().put(PerformanceMeasure.STATE_VISITS_BASE.getLabel(),
						new HashSet<StateVisit>());

			((HashSet<StateVisit>) currentState.getAttributeMap().get(PerformanceMeasure.STATE_VISITS_BASE.getLabel()))
					.add(new StateVisit(lcm.getTimeEnteredCurrentState(), eventTimeStamp));

			if (!currentState.getAttributeMap().containsKey(PerformanceMeasure.STATE_VISIT_DURATIONS_BASE.getLabel()))
				currentState.getAttributeMap().put(PerformanceMeasure.STATE_VISIT_DURATIONS_BASE.getLabel(),
						new DescriptiveStatistics());

			((DescriptiveStatistics) currentState.getAttributeMap()
					.get(PerformanceMeasure.STATE_VISIT_DURATIONS_BASE.getLabel()))
							.addValue(eventTimeStamp - lcm.getTimeEnteredCurrentState());

			// - Add another timestamp to the executions of the transition
			if (!t.getAttributeMap().containsKey(PerformanceMeasure.TRANSITION_EXECUTIONS_BASE.getLabel()))
				t.getAttributeMap().put(PerformanceMeasure.TRANSITION_EXECUTIONS_BASE.getLabel(),
						new HashSet<TransitionExecution>());

			((HashSet<TransitionExecution>) t.getAttributeMap()
					.get(PerformanceMeasure.TRANSITION_EXECUTIONS_BASE.getLabel()))
							.add(new TransitionExecution(eventTimeStamp));

			// - Set the new current state
			currentState = lcm.setCurrentStateIdentifier(t.getTarget().getIdentifier());

			// - Set the time that the new current state was entered
			lcm.setTimeEnteredCurrentState(eventTimeStamp);
		}
	}

	/**
	 * This method takes as input the performance measurements obtained by
	 * replaying the events on the lifecycle model instances, and calculates any
	 * performance metrics defined on them.
	 */
	protected void calculateLifecyclePerformanceMetrics() {
		// Algorithm
		// For every object type, for every instance, calculate the performance metrics
		for (String type : results.getResults().keySet()) {
			for (LifecycleModel instanceModel : results.getResults().get(type).values()) {
				for (TimedPerformanceMetric timedPerformanceMetric : instanceModel.getTimedPerformanceMetrics()) {
					instanceModel.getAttributeMap().put(timedPerformanceMetric.getLabel(),
							CalculatePerformanceMetricAlgorithm.compute(timedPerformanceMetric, instanceModel));
				}
			}
		}
	}

	/**
	 * Sets the current state to the first found initial state (i.e. any state
	 * without input transitions). It is assumed that only one such state
	 * exists, but technically, that needn't be the case.
	 * 
	 * @param model
	 *            The lifecycle model
	 */
	protected void setInitialState(LifecycleModel model) {
		if (model.getCurrentStateIdentifier() != null)
			return;

		for (State state : model.getNodes())
			if (model.getInEdges(state).isEmpty())
				model.setCurrentStateIdentifier(state.getIdentifier());

		model.setTimeEnteredCurrentState(0L);
	}

}
