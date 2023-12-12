package org.processmining.lifecycleperformance.models.performance;

import java.io.Serializable;
import java.util.Objects;

public class TimedPerformanceMeasurement<T> implements Serializable {

	private static final long serialVersionUID = 8083835720978702924L;

	private T result;
	private long measurementDate;
	private long calculationTime;

	public TimedPerformanceMeasurement() {

	}

	public TimedPerformanceMeasurement(T result, long measurementDate, long calculationTime) {
		setResult(result);
		setMeasurementDate(measurementDate);
		setCalculationTime(calculationTime);
	}

	public T getResult() {
		return result;
	}

	public void setResult(T result) {
		this.result = result;
	}

	public long getCalculationTime() {
		return calculationTime;
	}

	public void setCalculationTime(long calculationTime) {
		this.calculationTime = calculationTime;
	}

	public long getMeasurementDate() {
		return measurementDate;
	}

	public void setMeasurementDate(long measurementDate) {
		this.measurementDate = measurementDate;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TimedPerformanceMeasurement))
			return false;

		TimedPerformanceMeasurement<?> performanceResult = (TimedPerformanceMeasurement<?>) obj;

		if (!performanceResult.getResult().equals(getResult()))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode());
	}

	@Override
	public String toString() {
		return getResult().toString() + " (@ " + measurementDate + ", took " + calculationTime + "ms)";
	}

}
