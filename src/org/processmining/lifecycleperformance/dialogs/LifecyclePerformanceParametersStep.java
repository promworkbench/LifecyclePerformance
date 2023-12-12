package org.processmining.lifecycleperformance.dialogs;

import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableModel;

import org.deckfour.xes.info.XAttributeInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.util.ui.widgets.ProMHeaderPanel;
import org.processmining.framework.util.ui.widgets.ProMTable;
import org.processmining.framework.util.ui.wizard.ProMWizardStep;
import org.processmining.lifecycleperformance.models.lifecycle.LifecycleModel;
import org.processmining.lifecycleperformance.parameters.LifecyclePerformanceParameters;

public class LifecyclePerformanceParametersStep extends ProMHeaderPanel
		implements ProMWizardStep<LifecyclePerformanceParameters> {

	private static final long serialVersionUID = 1054786127714893932L;

	// FIELDS

	private String title;
	private LifecycleModel model;
	private JLabel label;
	private DefaultTableModel tableModel;
	private ProMTable table;

	// CONSTRUCTORS

	public LifecyclePerformanceParametersStep(String title, final LifecyclePerformanceParameters parameters,
			final XLog eventlog, final LifecycleModel model) {
		super(title);
		this.title = title;
		this.model = model;

		label = new JLabel("Select the event attributes you want to use as aggregations for lifecycle model \""
				+ model.getLabel() + "\".");

		tableModel = new DefaultTableModel();
		tableModel.addColumn("Event attribute");

		XAttributeInfo info = XLogInfoFactory.createLogInfo(eventlog).getEventAttributeInfo();
		ArrayList<String> attributeList = new ArrayList<String>(info.getAttributeKeys());
		Collections.sort(attributeList);
		for (String attribute : attributeList)
			tableModel.addRow(new Object[] { attribute });

		table = new ProMTable(tableModel);

		this.add(label);
		this.add(table);
	}

	// GETTERS AND SETTERS

	public String getTitle() {
		return title;
	}

	// METHODS

	public boolean canApply(LifecyclePerformanceParameters parameters, JComponent component) {
		return true;
	}

	public LifecyclePerformanceParameters apply(LifecyclePerformanceParameters parameters, JComponent component) {
		int[] rows = table.getTable().getSelectedRows();
		for (int i = 0; i < rows.length; i++) {
			parameters.addAggregation(model.getLabel(), (String) table.getTable().getValueAt(rows[i], 0));
		}
		return parameters;
	}

	public JComponent getComponent(LifecyclePerformanceParameters parameters) {
		// Select the correct values.
		return this;
	}

}
