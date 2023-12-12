package org.processmining.lifecycleperformance.visualization.swing.lifecycleperformanceresults;

import javax.swing.table.TableColumnModel;

import org.processmining.framework.util.ui.widgets.ProMTable;
import org.processmining.lifecycleperformance.models.lifecycle.LifecycleModel;

public class LifecycleModelTable extends ProMTable {

	private static final long serialVersionUID = 8585343711726434828L;

	// CONSTRUCTORS

	public LifecycleModelTable(LifecycleTableModel model) {
		this(model, null);
	}

	public LifecycleModelTable(LifecycleTableModel model, TableColumnModel columnModel) {
		super(model, columnModel);
		getTable().setFillsViewportHeight(true);
	}

	// METHODS

	public LifecycleModel[] getSelectedModels() {
		int[] selectedRows = getTable().getSelectedRows();
		LifecycleModel[] selectedModels = new LifecycleModel[selectedRows.length];

		for (int i = 0; i < selectedRows.length; i++)
			selectedModels[i] = ((LifecycleTableModel) getTable().getModel()).get(selectedRows[i]);

		return selectedModels;
	}

}
