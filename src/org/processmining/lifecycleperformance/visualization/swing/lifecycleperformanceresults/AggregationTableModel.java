package org.processmining.lifecycleperformance.visualization.swing.lifecycleperformanceresults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.processmining.lifecycleperformance.models.lifecycle.Aggregation;

public class AggregationTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 7316383546418048376L;

	//FIELDS

	private String name;
	private List<Aggregation> aggregations;

	// CONSTRUCTORS

	public AggregationTableModel(String name) {
		super();
		this.name = name;
		aggregations = new ArrayList<Aggregation>();
	}

	// METHODS

	@Override
	public int getRowCount() {
		return aggregations.size();
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
		return aggregations.get(rowIndex);
	}

	public boolean add(Aggregation aggregation) {
		if (aggregations.contains(aggregation))
			return false;
		boolean b = aggregations.add(aggregation);
		if (b) {
			int index = aggregations.indexOf(aggregation);
			fireTableRowsInserted(index, index);
		}
		sort();
		return b;
	}

	public boolean addAll(Collection<Aggregation> aggregations) {
		boolean b = false;
		for (Aggregation aggregation : aggregations) {
			if (!this.aggregations.contains(aggregation))
				b = b || this.aggregations.add(aggregation);
		}
		if (b)
			fireTableDataChanged();
		sort();
		return b;
	}

	public boolean remove(Aggregation aggregation) {
		int index = aggregations.indexOf(aggregation);
		boolean b = aggregations.remove(aggregation);
		if (b)
			fireTableRowsDeleted(index, index);
		return b;
	}

	public void clear() {
		aggregations.clear();
		fireTableDataChanged();
	}

	public Aggregation get(int index) {
		return aggregations.get(index);
	}

	public List<Aggregation> getAll() {
		return aggregations;
	}

	public int size() {
		return aggregations.size();
	}

	private void sort() {
		Collections.sort(aggregations, new Comparator<Aggregation>() {
			public int compare(Aggregation a1, Aggregation a2) {
				return a1.getKey().compareTo(a2.getKey());
			}
		});
	}
}
