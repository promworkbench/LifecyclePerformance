package org.processmining.lifecycleperformance.visualization.swing.lifecycle;

import javax.swing.table.DefaultTableModel;

import org.processmining.lifecycleperformance.models.lifecycle.LifecycleModel;
import org.processmining.lifecycleperformance.models.performance.TimedPerformanceMetric;

public class TimedPerformanceMetricsTableModel extends DefaultTableModel {

	private static final long serialVersionUID = 7730514525696626160L;

	// FIELDS

	private LifecycleModel lifecycleModel;

	// CONSTRUCTORS

	public TimedPerformanceMetricsTableModel(LifecycleModel lifecycleModel) {
		super();
		setLifecycleModel(lifecycleModel);
	}

	// GETTERS AND SETTERS

	public LifecycleModel getLifecycleModel() {
		return lifecycleModel;
	}

	public void setLifecycleModel(LifecycleModel lifecycleModel) {
		this.lifecycleModel = lifecycleModel;
	}

	// METHODS

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	@Override
	public int getRowCount() {
		return lifecycleModel == null ? 0
				: lifecycleModel.getTimedPerformanceMetrics() == null ? 0
						: lifecycleModel.getTimedPerformanceMetrics().size();
	}

	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	@Override
	public String getColumnName(int column) {
		switch (column) {
			case 0 :
				return "Metric";
			case 1 :
				return "Expression";
			case 2 :
				return "Measurement time expression";
		}
		return null;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		TimedPerformanceMetric timedPerformanceMetric = lifecycleModel.getTimedPerformanceMetrics().get(rowIndex);
		switch (columnIndex) {
			case 0 :
				return timedPerformanceMetric.getLabel();
			case 1 :
				return timedPerformanceMetric.getExpression();
			case 2 :
				return timedPerformanceMetric.getMeasurementTimeExpression();
		}
		return null;
	}

	public void add(TimedPerformanceMetric timedPerformanceMetric) {
		lifecycleModel.getTimedPerformanceMetrics().add(timedPerformanceMetric);
		int row = lifecycleModel.getTimedPerformanceMetrics().indexOf(timedPerformanceMetric);
		fireTableRowsInserted(row, row);
		fireTableDataChanged();
	}

	public TimedPerformanceMetric get(int row) {
		if (lifecycleModel.getTimedPerformanceMetrics().size() > row) {
			return lifecycleModel.getTimedPerformanceMetrics().get(row);
		}
		return null;
	}

	public void remove(TimedPerformanceMetric timedPerformanceMetric) {
		if (lifecycleModel.getTimedPerformanceMetrics().contains(timedPerformanceMetric)) {
			int row = lifecycleModel.getTimedPerformanceMetrics().indexOf(timedPerformanceMetric);
			remove(row);
		}
	}

	public void remove(int row) {
		if (lifecycleModel.getTimedPerformanceMetrics().size() > row) {
			lifecycleModel.getTimedPerformanceMetrics().remove(row);
			fireTableRowsDeleted(row, row);
		}
	}

}
