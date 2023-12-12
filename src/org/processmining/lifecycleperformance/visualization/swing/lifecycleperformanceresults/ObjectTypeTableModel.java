package org.processmining.lifecycleperformance.visualization.swing.lifecycleperformanceresults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.python.icu.text.UTF16.StringComparator;

public class ObjectTypeTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 7316383546418048376L;

	//FIELDS

	private String name;
	private List<String> types;

	// CONSTRUCTORS

	public ObjectTypeTableModel(String name) {
		super();
		this.name = name;
		types = new ArrayList<String>();
	}

	// METHODS

	@Override
	public int getRowCount() {
		return types.size();
	}

	@Override
	public int getColumnCount() {
		return 1;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	@Override
	public String getColumnName(int index) {
		return name;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return types.get(rowIndex);
	}

	public boolean add(String type) {
		if (types.contains(type))
			return false;
		boolean b = types.add(type);
		if (b) {
			int index = types.indexOf(type);
			fireTableRowsInserted(index, index);
		}
		sort();
		return b;
	}

	public boolean addAll(Collection<String> types) {
		boolean b = false;
		for (String type : types) {
			if (!this.types.contains(type))
				b = b || this.types.add(type);
		}
		if (b)
			fireTableDataChanged();
		sort();
		return b;
	}

	public boolean remove(String type) {
		int index = types.indexOf(type);
		boolean b = types.remove(type);
		if (b)
			fireTableRowsDeleted(index, index);
		return b;
	}

	public void clear() {
		types.clear();
		fireTableDataChanged();
	}

	public String get(int index) {
		return types.get(index);
	}

	public List<String> getAll() {
		return types;
	}

	public int size() {
		return types.size();
	}

	private void sort() {
		Collections.sort(types, new StringComparator());
	}
}
