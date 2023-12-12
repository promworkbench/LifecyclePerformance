package org.processmining.lifecycleperformance.models.performance;

/**
 * Enum that holds the lifecycle performance measures that are stored for the
 * states and transitions of lifecycle models.
 * 
 * @author B.F.A. Hompes <b.f.a.hompes@tue.nl>
 * 
 */
public enum PerformanceMeasure {
	//@formatter:off
	// We add "BASE" to the collection measures so they won't be matched by the regex pattern matcher of aggregate measures.
	// State performance measures
	STATE_VISITS_BASE("state:visits_base", "state_visits_base\\(\\s*((\\w+)(?:,\\s*\\w+)*)\\s*\\)"),
	STATE_VISITS_COUNT("state:visits_count", "count\\(state_visits\\(\\s*((\\w+)(?:,\\s*\\w+)*)\\s*\\)\\)"),
	STATE_VISIT_DURATIONS_BASE("state:visit_durations_base", "state_visit_durations_base\\(\\s*((\\w+)(?:,\\s*\\w+)*)\\s*\\)"),
	STATE_VISIT_DURATIONS_MIN("state:visit_durations_min", "min\\(state_visit_durations\\(\\s*((\\w+)(?:,\\s*\\w+)*)\\s*\\)\\)"),
	STATE_VISIT_DURATIONS_MAX("state:visit_durations_max", "max\\(state_visit_durations\\(\\s*((\\w+)(?:,\\s*\\w+)*)\\s*\\)\\)"),
	STATE_VISIT_DURATIONS_AVG("state:visit_durations_avg", "avg\\(state_visit_durations\\(\\s*((\\w+)(?:,\\s*\\w+)*)\\s*\\)\\)"),
	STATE_VISIT_DURATIONS_MEDIAN("state:visit_durations_median", "med\\(state_visit_durations\\(\\s*((\\w+)(?:,\\s*\\w+)*)\\s*\\)\\)"),
	STATE_VISIT_DURATIONS_SUM("state:visit_durations_sum", "sum\\(state_visit_durations\\(\\s*((\\w+)(?:,\\s*\\w+)*)\\s*\\)\\)"),
	// Transition performance measures,
	TRANSITION_EXECUTIONS_BASE("transition:executions_base","transition_executions_base\\(\\s*((\\w+)(?:,\\s*\\w+)*)\\s*\\)"),
	TRANSITION_EXECUTIONS_COUNT("transition:executions_count","count\\(transition_executions\\(\\s*((\\w+)(?:,\\s*\\w+)*)\\s*\\)\\)");
	
	private String label;
	private String regex;
	
	PerformanceMeasure(String label, String regex) {
		setLabel(label);
		setRegex(regex);
	}
	
	public void setLabel(String label) {
		this.label=label;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setRegex(String regex) {
		this.regex = regex;
	}
	
	public String getRegex() {
		return regex;
	}
	//@formatter:on
}