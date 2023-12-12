package org.processmining.lifecycleperformance.visualization.swing.lifecycle;

import javax.swing.ListSelectionModel;
import javax.swing.table.TableModel;

import org.processmining.framework.util.ui.widgets.ProMTable;

public class PerformanceMetricsTable extends ProMTable {

	private static final long serialVersionUID = -9212059633244221165L;

	public PerformanceMetricsTable(TableModel model) {
		super(model);
		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		// Make the table fill the entire viewport, so that the popup menu is available in the space below rows as well.
		// Otherwise, no popup menu will be visible when the table is empty.
		getTable().setFillsViewportHeight(true);
		getTable().setComponentPopupMenu(new PerformanceMetricsTablePopupMenu("Menu", this));
	}

}
