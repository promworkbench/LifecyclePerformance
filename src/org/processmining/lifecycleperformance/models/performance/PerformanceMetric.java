package org.processmining.lifecycleperformance.models.performance;

import java.util.Objects;

public class PerformanceMetric {

	// FIELDS

	protected String label;
	protected String expression;

	// CONSTRUCTOR

	public PerformanceMetric() {
		setLabel("");
		setExpression("");
	}

	public PerformanceMetric(String label) {
		setLabel(label);
		setExpression("");
	}

	public PerformanceMetric(String label, String expression) {
		setLabel(label);
		setExpression(expression);
	}

	// GETTERS AND SETTERS

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof PerformanceMetric))
			return false;

		PerformanceMetric metric = (PerformanceMetric) obj;

		if (!metric.label.equals(label))
			return false;

		if (!metric.expression.equals(expression))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hash(label, expression);
	}

	@Override
	public String toString() {
		return label + " (expression: '" + expression + "')";
	}

}
