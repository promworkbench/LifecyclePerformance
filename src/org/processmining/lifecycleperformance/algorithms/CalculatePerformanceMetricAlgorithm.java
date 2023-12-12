package org.processmining.lifecycleperformance.algorithms;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.processmining.lifecycleperformance.models.lifecycle.LifecycleModel;
import org.processmining.lifecycleperformance.models.performance.PerformanceMeasure;
import org.processmining.lifecycleperformance.models.performance.PerformanceMeasureTime;
import org.processmining.lifecycleperformance.models.performance.StateVisit;
import org.processmining.lifecycleperformance.models.performance.TimedPerformanceMeasurement;
import org.processmining.lifecycleperformance.models.performance.TimedPerformanceMetric;
import org.processmining.lifecycleperformance.models.performance.TransitionExecution;
import org.processmining.models.graphbased.directed.transitionsystem.State;
import org.processmining.models.graphbased.directed.transitionsystem.Transition;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class CalculatePerformanceMetricAlgorithm {

	// FIELDS

	static Map<String, Double> variableValues;
	static Map<String, Long> variableValuesTime;

	// METHODS

	/**
	 * 
	 * Computes the performance metrics by evaluating the expression. The values
	 * for the variables that are set in the expression are obtained from the
	 * lifecycle model for which the metric is being computed.
	 * 
	 * @param metric
	 *            The performance metric
	 * @param model
	 *            The lifecycle model
	 * @return The value
	 */
	public static TimedPerformanceMeasurement<Double> compute(TimedPerformanceMetric metric, LifecycleModel model) {
		/**
		 * Performance metric expression
		 */
		// Keep track of all variables and their values for all measures present in the metric's expression
		variableValues = new HashMap<String, Double>();

		// The expression string is where we will match arguments and inject their values
		String expression = metric.getExpression();

		// Assign all performance measure variables their values.
		findPerformanceMeasureValues(model, expression);

		/**
		 * Note that we cannot use the variable or function capabilities of
		 * ExpressionBuilder since Functions can only have doubles as arguments
		 * (so no state or transition names), and variables cannot have argument
		 * brackets. Alternatively we could remove the brackets from the
		 * performance measure texts, e.g. time_spent_min_statename instead of
		 * time_spent_min(statename), but that is more confusing.
		 */

		// Replace the variables with their values in-line
		for (String variable : variableValues.keySet())
			expression = expression.replace(variable, variableValues.get(variable).toString());

		// Create the exp4j expression
		ExpressionBuilder builder = new ExpressionBuilder(expression);

		// Assign the variables their values in the expression
		Expression exp = builder.build();

		// Evaluate the expression and calculate how long it took
		long calculationTime = -System.currentTimeMillis();
		double result = exp.evaluate();
		calculationTime += System.currentTimeMillis();

		/**
		 * Performance metric measurement time expression, similar evaluation
		 * method as above. First matches arguments and injects their values,
		 * then evaluates the expression.
		 */

		variableValuesTime = new HashMap<String, Long>();
		expression = metric.getMeasurementTimeExpression();
		findPerformanceMeasureTimeValues(model, expression);
		for (String variable : variableValuesTime.keySet())
			expression = expression.replace(variable, variableValuesTime.get(variable).toString());
		builder = new ExpressionBuilder(expression);
		exp = builder.build();
		double measurementTime = exp.evaluate();

		return new TimedPerformanceMeasurement<Double>(result, (long) measurementTime, calculationTime);
	}

	/**
	 * Finds every occurrence of the provided measure in the expression, and
	 * replace it with the matching values stored in the lifecycle model. Note
	 * that measures are defined as functions that can have more than one
	 * argument. In case an argument (state or transition identifier) does not
	 * exist in the model, the measure cannot be retrieved and the metric cannot
	 * be calculated, and an exception is thrown.
	 * 
	 * @param model
	 *            The lifecycle model
	 * @param expression
	 *            The expression
	 * @param measure
	 *            The performance measure
	 * @return A variable->value map
	 */
	@SuppressWarnings("unchecked")
	private static void findPerformanceMeasureValues(LifecycleModel model, String expression) {

		for (PerformanceMeasure measure : PerformanceMeasure.values()) {

			// Create a regular expression matcher for the regex that represents the measure
			Pattern pattern = Pattern.compile(measure.getRegex());
			Matcher matcher = pattern.matcher(expression);

			// For every match of this measure, replace it by the correct values.
			while (matcher.find()) {
				// Find the complete measure regex including it's arguments
				String variable = matcher.group(0);

				// Extract the arguments of the measure
				String measureArguments = matcher.group(1);
				String[] arguments = measureArguments.split(",");

				// Now try to match every argument to a state or a transition in the model
				//TODO [low] use multiple arguments? not sure if necessary since values are stored in a single state or transition
				// Obtain the value stored in the model
				double value = 0d;
				DescriptiveStatistics stats = new DescriptiveStatistics();
				for (String argument : arguments) {
					if (model.getStates().contains(argument)) {
						Set<State> states = model.getNodes();
						State state = states.stream().filter(s -> s.getLabel().equals(argument)).findFirst().get();
						switch (measure) {
							case STATE_VISITS_COUNT :
								if (state.getAttributeMap()
										.containsKey(PerformanceMeasure.STATE_VISITS_BASE.getLabel())) {
									value += ((HashSet<StateVisit>) state.getAttributeMap()
											.get(PerformanceMeasure.STATE_VISITS_BASE.getLabel())).size();
								}
								break;
							case STATE_VISIT_DURATIONS_MIN :
								if (state.getAttributeMap()
										.containsKey(PerformanceMeasure.STATE_VISIT_DURATIONS_BASE.getLabel())) {
									double min = ((DescriptiveStatistics) state.getAttributeMap()
											.get(PerformanceMeasure.STATE_VISIT_DURATIONS_BASE.getLabel())).getMin();
									value = min < value ? min : value;
								}
								break;
							case STATE_VISIT_DURATIONS_MAX :
								if (state.getAttributeMap()
										.containsKey(PerformanceMeasure.STATE_VISIT_DURATIONS_BASE.getLabel())) {
									double max = ((DescriptiveStatistics) state.getAttributeMap()
											.get(PerformanceMeasure.STATE_VISIT_DURATIONS_BASE.getLabel())).getMax();
									value = max > value ? max : value;
								}
								break;
							case STATE_VISIT_DURATIONS_AVG :
								if (state.getAttributeMap()
										.containsKey(PerformanceMeasure.STATE_VISIT_DURATIONS_BASE.getLabel())) {
									//	value = ((DescriptiveStatistics) state.getAttributeMap()
									//	.get(PerformanceMeasure.STATE_VISIT_DURATIONS_BASE.getLabel()))
									//  .getMean();

									for (double stat : ((DescriptiveStatistics) state.getAttributeMap()
											.get(PerformanceMeasure.STATE_VISIT_DURATIONS_BASE.getLabel()))
													.getValues()) {
										stats.addValue(stat);
									}
								}
								break;
							case STATE_VISIT_DURATIONS_MEDIAN :
								if (state.getAttributeMap()
										.containsKey(PerformanceMeasure.STATE_VISIT_DURATIONS_BASE.getLabel())) {
									// value = ((DescriptiveStatistics) state.getAttributeMap()
									//	.get(PerformanceMeasure.STATE_VISIT_DURATIONS_BASE.getLabel()))
									//	.getPercentile(50);

									for (double stat : ((DescriptiveStatistics) state.getAttributeMap()
											.get(PerformanceMeasure.STATE_VISIT_DURATIONS_BASE.getLabel()))
													.getValues()) {
										stats.addValue(stat);
									}
								}
								break;
							case STATE_VISIT_DURATIONS_SUM :
								if (state.getAttributeMap()
										.containsKey(PerformanceMeasure.STATE_VISIT_DURATIONS_BASE.getLabel())) {
									value += ((DescriptiveStatistics) state.getAttributeMap()
											.get(PerformanceMeasure.STATE_VISIT_DURATIONS_BASE.getLabel())).getSum();
								}
								break;
							default :
								// Should never occur
								throw new IllegalArgumentException("Performance measure \"" + measure.getLabel()
										+ "\" undefined for state \"" + state.getLabel() + "\"");
						}
					} else if (model.getTransitions().contains(argument)) {
						Set<Transition> transitions = model.getEdges();
						// Take the first transition with this identifier (as they are assumed to be unique)
						Transition transition = transitions.stream().filter(t -> t.getLabel().equals(argument))
								.findFirst().get();
						switch (measure) {
							case TRANSITION_EXECUTIONS_COUNT :
								if (transition.getAttributeMap()
										.containsKey(PerformanceMeasure.TRANSITION_EXECUTIONS_BASE.getLabel()))
									value += ((HashSet<TransitionExecution>) transition.getAttributeMap()
											.get(PerformanceMeasure.TRANSITION_EXECUTIONS_BASE.getLabel())).size();
								break;
							default :
								throw new IllegalArgumentException("Performance measure \"" + measure.getLabel()
										+ "\" undefined for transition \"" + transition.getLabel() + "\"");
						}

					}

				} // for each argument

				// For the average and median state durations, calculate here.
				if (stats.getN() > 0 && measure.equals(PerformanceMeasure.STATE_VISIT_DURATIONS_AVG))
					value = stats.getMean();
				else if (stats.getN() > 0 && measure.equals(PerformanceMeasure.STATE_VISIT_DURATIONS_MEDIAN))
					value = stats.getPercentile(50.0);

				// Add the measure to the expression as a variable	
				variableValues.put(variable, value);

			} // while matcher finds

		} // for each measure

	}

	@SuppressWarnings("unchecked")
	private static void findPerformanceMeasureTimeValues(LifecycleModel model, String expression) {

		for (PerformanceMeasureTime measure : PerformanceMeasureTime.values()) {

			// Create a regular expression matcher for the regex that represents the measure
			Pattern pattern = Pattern.compile(measure.getRegex()); // e.g. "last_exit\(\s*(\w*)\s*\)"
			Matcher matcher = pattern.matcher(expression);

			// For every match of this measure, replace it by the correct values.
			while (matcher.find()) {
				// Find the complete measure regex including it's arguments
				String variable = matcher.group(0); // e.g. "last_exit(started)" or "last_exit(started,scheduled)"

				// Extract the arguments of the measure
				String measureArguments = matcher.group(1); // e.g. "started" or "started,scheduled"
				String[] arguments = measureArguments.split(","); // e.g. ["started"] or ["started", "scheduled"]

				// Now try to match every argument to a state or a transition in the model
				//TODO [low] use multiple arguments? not sure if necessary since values are stored in a single state or transition
				// Obtain the value stored in the model
				long measurementTime = 0l;
				for (String argument : arguments) {
					if (model.getStates().contains(argument)) {
						Set<State> states = model.getNodes();
						State state = states.stream().filter(s -> s.getLabel().equals(argument)).findFirst().get();
						switch (measure) {
							case FIRST_STATE_VISIT_ENTRY :
								if (state.getAttributeMap()
										.containsKey(PerformanceMeasure.STATE_VISITS_BASE.getLabel())) {
									measurementTime = measurementTime == 0l ? Long.MAX_VALUE : measurementTime;
									for (StateVisit sv : ((HashSet<StateVisit>) state.getAttributeMap()
											.get(PerformanceMeasure.STATE_VISITS_BASE.getLabel()))) {
										measurementTime = sv.getTimestampEntry() < measurementTime
												? sv.getTimestampEntry()
												: measurementTime;
									}
								}
								break;
							case FIRST_STATE_VISIT_EXIT :
								if (state.getAttributeMap()
										.containsKey(PerformanceMeasure.STATE_VISITS_BASE.getLabel())) {
									measurementTime = measurementTime == 0l ? Long.MAX_VALUE : measurementTime;
									for (StateVisit sv : ((HashSet<StateVisit>) state.getAttributeMap()
											.get(PerformanceMeasure.STATE_VISITS_BASE.getLabel()))) {
										measurementTime = sv.getTimestampExit() < measurementTime
												? sv.getTimestampExit()
												: measurementTime;
									}
								}
								break;
							case LAST_STATE_VISIT_ENTRY :
								if (state.getAttributeMap()
										.containsKey(PerformanceMeasure.STATE_VISITS_BASE.getLabel())) {
									measurementTime = measurementTime == 0l ? Long.MIN_VALUE : measurementTime;
									for (StateVisit sv : ((HashSet<StateVisit>) state.getAttributeMap()
											.get(PerformanceMeasure.STATE_VISITS_BASE.getLabel()))) {
										measurementTime = sv.getTimestampEntry() > measurementTime
												? sv.getTimestampEntry()
												: measurementTime;
									}
								}
								break;
							case LAST_STATE_VISIT_EXIT :
								if (state.getAttributeMap()
										.containsKey(PerformanceMeasure.STATE_VISITS_BASE.getLabel())) {
									measurementTime = measurementTime == 0l ? Long.MIN_VALUE : measurementTime;
									for (StateVisit sv : ((HashSet<StateVisit>) state.getAttributeMap()
											.get(PerformanceMeasure.STATE_VISITS_BASE.getLabel()))) {
										measurementTime = sv.getTimestampExit() > measurementTime
												? sv.getTimestampExit()
												: measurementTime;
									}
								}
								break;
							default :
								// Should never occur
								throw new IllegalArgumentException("Performance measure time \"" + measure.getLabel()
										+ "\" undefined for state \"" + state.getLabel() + "\"");
						}
					} else if (model.getTransitions().contains(argument)) {
						Set<Transition> transitions = model.getEdges();
						// Take the first transition with this identifier (as they are assumed to be unique)
						Transition transition = transitions.stream().filter(t -> t.getLabel().equals(argument))
								.findFirst().get();
						if (!transition.getAttributeMap()
								.containsKey(PerformanceMeasure.TRANSITION_EXECUTIONS_BASE.getLabel()))
							throw new IllegalArgumentException("Performance measure time \"" + measure.getLabel()
									+ "\" undefined for transition \"" + transition.getLabel() + "\"");
						switch (measure) {
							case FIRST_TRANSITION_EXECUTIONS :
								measurementTime = measurementTime == 0l ? Long.MAX_VALUE : measurementTime;
								for (TransitionExecution te : ((HashSet<TransitionExecution>) transition
										.getAttributeMap()
										.get(PerformanceMeasure.TRANSITION_EXECUTIONS_BASE.getLabel()))) {
									measurementTime = te.getTimestamp() < measurementTime ? te.getTimestamp()
											: measurementTime;
								}
								break;
							case LAST_TRANSITION_EXECUTIONS :
								measurementTime = measurementTime == 0l ? Long.MIN_VALUE : measurementTime;
								for (TransitionExecution te : ((HashSet<TransitionExecution>) transition
										.getAttributeMap()
										.get(PerformanceMeasure.TRANSITION_EXECUTIONS_BASE.getLabel()))) {
									measurementTime = te.getTimestamp() > measurementTime ? te.getTimestamp()
											: measurementTime;
								}
								break;
							default :
								throw new IllegalArgumentException("Performance measure time \"" + measure.getLabel()
										+ "\" undefined for transition \"" + transition.getLabel() + "\"");
						}

					}

				} // for each argument

				variableValuesTime.put(variable, measurementTime);

			} // while matcher finds

		} // for measure

	}

}
