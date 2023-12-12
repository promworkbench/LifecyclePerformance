package org.processmining.lifecycleperformance.models.lifecycle;

import java.util.HashSet;
import java.util.NoSuchElementException;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.processmining.lifecycleperformance.models.performance.PerformanceMeasure;
import org.processmining.lifecycleperformance.models.performance.StateVisit;
import org.processmining.lifecycleperformance.models.performance.TimedPerformanceMeasurement;
import org.processmining.lifecycleperformance.models.performance.TimedPerformanceMetric;
import org.processmining.lifecycleperformance.models.performance.TransitionExecution;
import org.processmining.lifecycleperformance.parameters.LifecyclePerformanceParameters;
import org.processmining.models.graphbased.directed.transitionsystem.State;
import org.processmining.models.graphbased.directed.transitionsystem.Transition;

public class LifecycleModelFactory {

	public static LifecycleModel generateLifecycleModel(String label, String instance,
			LifecyclePerformanceParameters parameters) {
		try {

			// Find the lifecycle model in the parameters, if it exists, else return a new empty model
			LifecycleModel paramModel = parameters.getLifecycleModels().stream().filter(m -> m.getLabel().equals(label))
					.findFirst().get();

			// Create an new instance of this model
			LifecycleModel model = new LifecycleModel(paramModel, instance);
			// Set the model to be in its initial state.
			model.setCurrentStateIdentifier(paramModel.getCurrentStateIdentifier());
			model.setTimeEnteredCurrentState(0l);

			return model;

		} catch (NoSuchElementException ex) {

			// Create a new empty model
			System.err.println("Could not find lifecycle model '" + label + "' for instance '" + instance
					+ "', creating an empty lifecycle model instead.");
			return new LifecycleModel(label, instance);

		}

	}

	public static LifecycleModel generateStandardActivityLifecycleModel() {
		LifecycleModel standard = new LifecycleModel("standard");

		standard.addState("init");
		standard.addState("scheduled");
		standard.addState("assigned");
		standard.addState("started");
		standard.addState("suspended");
		standard.addState("completed successfully");
		standard.addState("completed unsuccessfully");

		standard.addTransition("init", "scheduled", "schedule");
		standard.addTransition("scheduled", "assigned", "assign");
		standard.addTransition("assigned", "assigned", "reassign");
		standard.addTransition("assigned", "started", "start");
		standard.addTransition("started", "suspended", "suspend");
		standard.addTransition("suspended", "started", "resume");

		standard.addTransition("init", "completed successfully", "autoskip");
		standard.addTransition("scheduled", "completed successfully", "manualskip");
		standard.addTransition("assigned", "completed successfully", "manualskip");
		standard.addTransition("started", "completed successfully", "complete");

		standard.addTransition("scheduled", "completed unsuccessfully", "withdraw");
		standard.addTransition("assigned", "completed unsuccessfully", "withdraw");
		standard.addTransition("started", "completed unsuccessfully", "abort_activity");
		standard.addTransition("init", "completed unsuccessfully", "abort_case");
		standard.addTransition("scheduled", "completed unsuccessfully", "abort_case");
		standard.addTransition("assigned", "completed unsuccessfully", "abort_case");
		standard.addTransition("started", "completed unsuccessfully", "abort_case");
		standard.addTransition("suspended", "completed unsuccessfully", "abort_case");

		standard.setCurrentStateIdentifier("init");
		standard.setTimeEnteredCurrentState(0l);

		TimedPerformanceMetric duration = new TimedPerformanceMetric("duration");
		TimedPerformanceMetric waitingTime = new TimedPerformanceMetric("waitingtime");
		TimedPerformanceMetric sojournTime = new TimedPerformanceMetric("sojourntime");
		TimedPerformanceMetric suspensions = new TimedPerformanceMetric("suspensions");
		TimedPerformanceMetric reassignments = new TimedPerformanceMetric("reassignments");

		duration.setExpression("sum(state_visit_durations(started))");
		duration.setMeasurementTimeExpression("last_exit(started))");

		waitingTime.setExpression("sum(state_visit_durations(scheduled))");
		waitingTime.setMeasurementTimeExpression("last(state_visits(scheduled))");

		sojournTime.setExpression("sum(state_visit_durations(scheduled,assigned,started,suspended))");
		sojournTime.setMeasurementTimeExpression("last_exit(scheduled,assigned,started,suspended)");

		suspensions.setExpression("count(transition_executions(suspended))");
		suspensions.setMeasurementTimeExpression("last_execution(suspended)");

		reassignments.setExpression("count(transition_executions(reassign))");
		reassignments.setMeasurementTimeExpression("last_execution(reassign)");

		standard.getTimedPerformanceMetrics().add(duration);
		standard.getTimedPerformanceMetrics().add(waitingTime);
		standard.getTimedPerformanceMetrics().add(sojournTime);
		standard.getTimedPerformanceMetrics().add(suspensions);
		standard.getTimedPerformanceMetrics().add(reassignments);

		return standard;
	}

	public static LifecycleModel generateSmallActivityLifecycleModel() {
		LifecycleModel standard = new LifecycleModel("standard");

		standard.addState("init");
		standard.addState("started");
		standard.addState("completed");

		standard.addTransition("init", "started", "start");
		standard.addTransition("started", "completed", "complete");

		standard.setCurrentStateIdentifier("init");
		standard.setTimeEnteredCurrentState(0l);

		TimedPerformanceMetric duration = new TimedPerformanceMetric("duration");
		duration.setExpression("sum(state_visit_durations(started))");
		duration.setMeasurementTimeExpression("last_exit(started)");
		standard.getTimedPerformanceMetrics().add(duration);

		return standard;
	}

	public static LifecycleModel generateBPAFActivityLifecycleModel() {
		return new LifecycleModel("BPAF");
	}

	@SuppressWarnings("unchecked")
	public static LifecycleModel aggregateModels(LifecycleModel... models) {
		/**
		 * Create a copy from the first model in the collection of models to be
		 * aggregated, as the selected models should have the same structure and
		 * metrics defined on them.
		 */
		LifecycleModel aggregateModel = new LifecycleModel(models[0]);

		// Copy the measures from the instance level to the aggregate level
		for (LifecycleModel instanceModel : models) {
			for (State instanceState : instanceModel.getNodes()) {
				if (instanceState.getAttributeMap().containsKey(PerformanceMeasure.STATE_VISITS_BASE.getLabel())) {
					State modelState = aggregateModel.getNode(instanceState.getIdentifier());
					if (!modelState.getAttributeMap().containsKey(PerformanceMeasure.STATE_VISITS_BASE.getLabel())) {
						modelState.getAttributeMap().put(PerformanceMeasure.STATE_VISITS_BASE.getLabel(),
								new HashSet<StateVisit>());
					}
					((HashSet<StateVisit>) modelState.getAttributeMap()
							.get(PerformanceMeasure.STATE_VISITS_BASE.getLabel()))
									.addAll(((HashSet<StateVisit>) instanceState.getAttributeMap()
											.get(PerformanceMeasure.STATE_VISITS_BASE.getLabel())));
				}
				if (instanceState.getAttributeMap()
						.containsKey(PerformanceMeasure.STATE_VISIT_DURATIONS_BASE.getLabel())) {
					State modelState = aggregateModel.getNode(instanceState.getIdentifier());
					if (!modelState.getAttributeMap()
							.containsKey(PerformanceMeasure.STATE_VISIT_DURATIONS_BASE.getLabel())) {
						modelState.getAttributeMap().put(PerformanceMeasure.STATE_VISIT_DURATIONS_BASE.getLabel(),
								new DescriptiveStatistics());
					}
					for (double value : ((DescriptiveStatistics) instanceState.getAttributeMap()
							.get(PerformanceMeasure.STATE_VISIT_DURATIONS_BASE.getLabel())).getValues()) {
						((DescriptiveStatistics) modelState.getAttributeMap()
								.get(PerformanceMeasure.STATE_VISIT_DURATIONS_BASE.getLabel())).addValue(value);
					}
				}
			}
			for (Transition instanceTransition : instanceModel.getEdges()) {
				if (instanceTransition.getAttributeMap()
						.containsKey(PerformanceMeasure.TRANSITION_EXECUTIONS_BASE.getLabel())) {
					Transition modelTransition = aggregateModel.getEdges(instanceTransition.getIdentifier()).iterator()
							.next();
					if (!modelTransition.getAttributeMap()
							.containsKey(PerformanceMeasure.TRANSITION_EXECUTIONS_BASE.getLabel())) {
						modelTransition.getAttributeMap().put(PerformanceMeasure.TRANSITION_EXECUTIONS_BASE.getLabel(),
								new HashSet<TransitionExecution>());
					}
					((HashSet<TransitionExecution>) modelTransition.getAttributeMap()
							.get(PerformanceMeasure.TRANSITION_EXECUTIONS_BASE.getLabel()))
									.addAll(((HashSet<TransitionExecution>) instanceTransition.getAttributeMap()
											.get(PerformanceMeasure.TRANSITION_EXECUTIONS_BASE.getLabel())));
				}
			}
		}

		for (TimedPerformanceMetric timedPerformanceMetric : aggregateModel.getTimedPerformanceMetrics()) {
			DescriptiveStatistics stats = new DescriptiveStatistics();
			for (LifecycleModel instanceModel : models) {
				stats.addValue(((TimedPerformanceMeasurement<Double>) instanceModel.getAttributeMap()
						.get(timedPerformanceMetric.getLabel())).getResult());
			}
			aggregateModel.getAttributeMap().put(timedPerformanceMetric.getLabel(), stats);
		}

		return aggregateModel;
	}

}
