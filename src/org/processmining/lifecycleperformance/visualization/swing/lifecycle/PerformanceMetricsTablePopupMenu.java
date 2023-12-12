package org.processmining.lifecycleperformance.visualization.swing.lifecycle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.processmining.lifecycleperformance.models.lifecycle.LifecycleModel;
import org.processmining.lifecycleperformance.models.performance.TimedPerformanceMetric;

public class PerformanceMetricsTablePopupMenu extends JPopupMenu {

	private static final long serialVersionUID = 5110752291391710493L;

	// FIELDS

	private JMenuItem add;
	private JMenuItem edit;
	private JMenuItem delete;
	private PerformanceMetricsTable performanceMetricsTable;

	// CONSTRUCTORS

	public PerformanceMetricsTablePopupMenu(String label, PerformanceMetricsTable performanceMetricsTable) {
		super(label);
		setPerformanceMetricsTable(performanceMetricsTable);

		PerformanceMetricsTablePopupMenuActionListener performanceMetricsTablePopupMenuActionListener = new PerformanceMetricsTablePopupMenuActionListener();

		add = new JMenuItem("Add");
		add.setToolTipText("Adds a new performance metric.");
		add.setActionCommand("add");
		add.addActionListener(performanceMetricsTablePopupMenuActionListener);

		edit = new JMenuItem("Edit");
		edit.setToolTipText("Edits the selected metric.");
		edit.setActionCommand("edit");
		edit.addActionListener(performanceMetricsTablePopupMenuActionListener);

		delete = new JMenuItem("Delete");
		delete.setToolTipText("Deletes the selected metrics.");
		delete.setActionCommand("delete");
		delete.addActionListener(performanceMetricsTablePopupMenuActionListener);

		add(add);
		add(edit);
		add(delete);

		PopupMenuListener listener = new PerformanceMetricsTablePopupMenuListener();
		addPopupMenuListener(listener);
	}

	// GETTERS AND SETTERS

	public PerformanceMetricsTable getPerformanceMetricsTable() {
		return performanceMetricsTable;
	}

	public void setPerformanceMetricsTable(PerformanceMetricsTable performanceMetricsTable) {
		this.performanceMetricsTable = performanceMetricsTable;
	}

	// METHODS

	private void setEnabled(boolean isAddEnabled, boolean isEditEnabled, boolean isDeleteEnabled) {
		add.setEnabled(isAddEnabled);
		edit.setEnabled(isEditEnabled);
		delete.setEnabled(isDeleteEnabled);
	}

	private LifecycleModel getLifecycleModel() {
		return ((TimedPerformanceMetricsTableModel) performanceMetricsTable.getTable().getModel()).getLifecycleModel();
	}

	class PerformanceMetricsTablePopupMenuActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			TimedPerformanceMetric metric;
			switch (e.getActionCommand()) {
				case "add" :
					// Create a new metric, add it to the table, and show the editor.
					metric = new TimedPerformanceMetric();
					synchronized (metric) {
						new TimedPerformanceMetricEditor(getLifecycleModel(), metric);
						((TimedPerformanceMetricsTableModel) performanceMetricsTable.getTable().getModel()).add(metric);
					}
					break;
				case "edit" :
					// Show the metric editor for the selected metric.
					metric = ((TimedPerformanceMetricsTableModel) performanceMetricsTable.getTable().getModel())
							.get(performanceMetricsTable.getTable().getSelectedRow());
					synchronized (metric) {
						new TimedPerformanceMetricEditor(getLifecycleModel(), metric);
					}
					break;
				case "delete" :
					// Delete the selected metrics.
					int[] selectedRows = performanceMetricsTable.getTable().getSelectedRows();
					for (int i = 0; i < selectedRows.length; i++) {
						// Don't remove row i, but remove the selected row (which will change every iteration of the loop.
						// After the first row is removed, the index i doesn't match anymore.
						((TimedPerformanceMetricsTableModel) performanceMetricsTable.getTable().getModel())
								.remove(performanceMetricsTable.getTable().getSelectedRow());
					}
					break;
			}
		}
	}

	class PerformanceMetricsTablePopupMenuListener implements PopupMenuListener {

		@Override
		public void popupMenuCanceled(PopupMenuEvent popupMenuEvent) {
		}

		@Override
		public void popupMenuWillBecomeInvisible(PopupMenuEvent popupMenuEvent) {
		}

		@Override
		public void popupMenuWillBecomeVisible(PopupMenuEvent popupMenuEvent) {
			int selectedMetricCount = performanceMetricsTable.getTable().getSelectedRowCount();

			if (selectedMetricCount == 0)
				setEnabled(true, false, false);
			else if (selectedMetricCount == 1)
				setEnabled(true, true, true);
			else
				setEnabled(true, false, true);
		}

	}

}