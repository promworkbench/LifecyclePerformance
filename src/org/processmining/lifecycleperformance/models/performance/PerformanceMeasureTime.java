package org.processmining.lifecycleperformance.models.performance;

/**
 * Enum that holds the lifecycle performance measures that are stored for the
 * states and transitions of lifecycle models.
 * 
 * @author B.F.A. Hompes <b.f.a.hompes@tue.nl>
 * 
 */
public enum PerformanceMeasureTime {
	//@formatter:off
	// We add "BASE" to the collection measures so they won't be matched by the regex pattern matcher of aggregate measures.
	// State performance measures
	FIRST_STATE_VISIT_ENTRY("state:visit_first_entry", "first_entry\\(\\s*((\\w+)(?:,\\s*\\w+)*)\\s*\\)"),
	LAST_STATE_VISIT_ENTRY("state:visit_last_entry", "last_entry\\(\\s*((\\w+)(?:,\\s*\\w+)*)\\s*\\)"),
	FIRST_STATE_VISIT_EXIT("state:visit_first_exit", "first_exit\\(\\s*((\\w+)(?:,\\s*\\w+)*)\\s*\\)"),
	LAST_STATE_VISIT_EXIT("state:visit_last_exit", "last_exit\\(\\s*((\\w+)(?:,\\s*\\w+)*)\\s*\\)"),
	// Transition performance measures,
	FIRST_TRANSITION_EXECUTIONS("transition:executions_first","first_execution\\(\\s*((\\w+)(?:,\\s*\\w+)*)\\s*\\)"),
	LAST_TRANSITION_EXECUTIONS("transition:executions_last","last_execution\\(\\s*((\\w+)(?:,\\s*\\w+)*)\\s*\\)");
	
	private String label;
	private String regex;
	
	PerformanceMeasureTime(String label, String regex) {
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