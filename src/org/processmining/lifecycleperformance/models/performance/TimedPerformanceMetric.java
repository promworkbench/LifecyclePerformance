package org.processmining.lifecycleperformance.models.performance;

import java.util.Objects;

public class TimedPerformanceMetric extends PerformanceMetric {

	// FIELDS

	private String measurementTimeExpression;

	// CONSTRUCTOR

	public TimedPerformanceMetric() {
		super();
		setMeasurementTimeExpression("");
	}

	public TimedPerformanceMetric(String label) {
		super(label);
		setMeasurementTimeExpression("");
	}

	public TimedPerformanceMetric(String label, String expression) {
		super(label, expression);
		setMeasurementTimeExpression("");
	}

	public TimedPerformanceMetric(String label, String expression, String measurementTimeExpression) {
		super(label, expression);
		setMeasurementTimeExpression(measurementTimeExpression);
	}

	// GETTERS AND SETTERS

	public String getMeasurementTimeExpression() {
		return measurementTimeExpression;
	}

	public void setMeasurementTimeExpression(String measurementDateExpression) {
		this.measurementTimeExpression = measurementDateExpression;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TimedPerformanceMetric))
			return false;

		TimedPerformanceMetric metric = (TimedPerformanceMetric) obj;

		if (!metric.measurementTimeExpression.equals(measurementTimeExpression))
			return false;

		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), measurementTimeExpression);
	}

	@Override
	public String toString() {
		return label + " (expression: '" + expression + "', measurement time expression: '" + measurementTimeExpression
				+ "')";
	}
}
