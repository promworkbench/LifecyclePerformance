package org.processmining.lifecycleperformance.visualization.swing.lifecycleperformanceresults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.processmining.lifecycleperformance.models.lifecycle.LifecycleModel;

public class LifecycleTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1067313447857601631L;

	// FIELDS

	private String name;
	private List<LifecycleModel> models;

	// CONSTRUCTORS

	public LifecycleTableModel(String name) {
		super();
		this.name = name;
		this.models = new ArrayList<LifecycleModel>();
	}

	// METHODS

	@Override
	public int getRowCount() {
		return models.size();
	}

	@Override
	public int getColumnCount() {
		return 1;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return models.get(rowIndex);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return LifecycleModel.class;
	}

	@Override
	public String getColumnName(int index) {
		return name;
	}

	public boolean add(LifecycleModel model) {
		boolean b = models.add(model);
		int index = models.indexOf(model);
		if (b)
			fireTableRowsInserted(index, index);
		return b;
	}

	public boolean addAll(Collection<LifecycleModel> models) {
		boolean b = this.models.addAll(models);
		if (b)
			fireTableDataChanged();
		return b;
	}

	public boolean remove(LifecycleModel model) {
		int index = models.indexOf(model);
		boolean b = models.remove(model);
		if (b)
			fireTableRowsDeleted(index, index);
		return b;
	}

	public boolean removeAll(Collection<LifecycleModel> models) {
		boolean b = this.models.removeAll(models);
		if (b)
			fireTableDataChanged();
		return b;
	}

	public void clear() {
		models.clear();
		fireTableDataChanged();
	}

	public LifecycleModel get(int index) {
		return models.get(index);
	}

	public List<LifecycleModel> getAll() {
		return models;
	}

	public int size() {
		return models.size();
	}

}
