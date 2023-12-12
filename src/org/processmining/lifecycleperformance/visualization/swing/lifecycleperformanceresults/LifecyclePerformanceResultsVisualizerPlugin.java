package org.processmining.lifecycleperformance.visualization.swing.lifecycleperformanceresults;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.util.ui.widgets.ProMSplitPane;
import org.processmining.framework.util.ui.widgets.ProMTable;
import org.processmining.lifecycleperformance.models.lifecycle.Aggregation;
import org.processmining.lifecycleperformance.models.lifecycle.LifecycleModel;
import org.processmining.lifecycleperformance.models.lifecycle.LifecycleModelFactory;
import org.processmining.lifecycleperformance.models.performance.PerformanceAnalysisResults;
import org.processmining.lifecycleperformance.models.performance.PerformanceMeasure;
import org.processmining.lifecycleperformance.models.performance.TimedPerformanceMetric;
import org.processmining.lifecycleperformance.models.performance.TransitionExecution;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.transitionsystem.State;
import org.processmining.models.graphbased.directed.transitionsystem.Transition;
import org.processmining.models.jgraph.ProMJGraphVisualizer;
import org.processmining.models.jgraph.visualization.ProMJGraphPanel;

@Plugin(
		name = "Lifecycle-based performance analysis",
		returnLabels = { "Visualized performance analysis results." },
		returnTypes = { JComponent.class },
		parameterLabels = { "Results" },
		userAccessible = true)
@Visualizer
public class LifecyclePerformanceResultsVisualizerPlugin {

	PerformanceAnalysisResults results = null;
	ProMSplitPane spPanel = null;
	ProMSplitPane spDetails = null;
	JPanel modelsPanel = null;
	ProMJGraphPanel graphPanel = null;
	JPanel metricsPanel = null;

	@PluginVariant(
			requiredParameterLabels = { 0 })
	public JComponent visualize(UIPluginContext uiPluginContext, PerformanceAnalysisResults results) {
		this.results = results;

		spPanel = new ProMSplitPane(ProMSplitPane.HORIZONTAL_SPLIT);
		spDetails = new ProMSplitPane(ProMSplitPane.VERTICAL_SPLIT);

		createModelsPanel();
		createDetails();

		spDetails.setTopComponent(graphPanel);
		spDetails.setBottomComponent(metricsPanel);
		spDetails.setResizeWeight(1.0);
		spPanel.setLeftComponent(modelsPanel);
		spPanel.setDividerLocation(350);
		spPanel.setRightComponent(spDetails);
		//		spLeftRight.setResizeWeight(1.0);

		return spPanel;
	}

	public void createModelsPanel() {
		modelsPanel = new JPanel();

		ObjectTypeTableModel tblmObjectType = new ObjectTypeTableModel("Object type");
		ProMTable tblObjectType = new ProMTable(tblmObjectType);
		tblObjectType.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// Add all object types for which we have results
		tblmObjectType.addAll(results.getResults().keySet());

		LifecycleTableModel tblmLifecycleModel = new LifecycleTableModel("Lifecycle model");
		LifecycleModelTable tblLifecycleModel = new LifecycleModelTable(tblmLifecycleModel);
		tblLifecycleModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		AggregationTableModel tblmAggregation = new AggregationTableModel("Aggregation");
		ProMTable tblAggregation = new ProMTable(tblmAggregation);

		LifecycleTableModel tblmInstances = new LifecycleTableModel("Instances");
		LifecycleModelTable tblInstances = new LifecycleModelTable(tblmInstances);

		// Types
		tblObjectType.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					tblmLifecycleModel.clear();
					tblmAggregation.clear();
					tblmInstances.clear();
					if (tblObjectType.getTable().getSelectedRowCount() > 0) {
						String type = tblmObjectType.get(tblObjectType.getSelectedRow());
						tblmLifecycleModel.addAll(results.getLifecycleModelsForObjectType(type));
					}
					createDetails(tblInstances.getSelectedModels());
				}
			}
		});
		tblObjectType.getTable().setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
			private static final long serialVersionUID = -3211383190507363388L;

			@Override
			protected void setValue(Object value) {
				String type = (String) value;
				setText((value == null) ? "" : type);
			}
		});

		// Models
		tblLifecycleModel.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					tblmAggregation.clear();
					tblmInstances.clear();
					if (tblLifecycleModel.getTable().getSelectedRowCount() > 0) {
						// Find the selected type and model
						String type = tblmObjectType.get(tblObjectType.getSelectedRow());
						LifecycleModel model = tblmLifecycleModel.get(tblLifecycleModel.getSelectedRow());
						// Find all instances of the selected type and model
						tblmInstances.addAll(results.getResults().get(type).row(model.getLabel()).values());
						for (LifecycleModel instanceModel : tblmInstances.getAll()) {
							// If there is any aggregations in the instance model, add them to the aggregation tablemodel
							if (instanceModel.getAttributeMap().containsKey(LifecycleModel.AGGREGATION)) {
								@SuppressWarnings("unchecked")
								Collection<Aggregation> aggregations = ((Map<String, Aggregation>) instanceModel
										.getAttributeMap().get(LifecycleModel.AGGREGATION)).values();
								tblmAggregation.addAll(aggregations);
							}
						}
						// Select all instances in order to show aggregate in details panel
						tblInstances.getTable().selectAll();
					}
					createDetails(tblInstances.getSelectedModels());
				}
			}
		});
		tblLifecycleModel.getTable().setDefaultRenderer(LifecycleModel.class, new DefaultTableCellRenderer() {
			private static final long serialVersionUID = -3211383190507363388L;

			@Override
			protected void setValue(Object value) {
				LifecycleModel model = (LifecycleModel) value;
				setText((value == null) ? "" : model.getLabel());
			}
		});

		// Aggregations
		tblAggregation.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					int[] selectedRows = tblAggregation.getTable().getSelectedRows();
					Aggregation[] selectedAggregations = new Aggregation[selectedRows.length];
					for (int i = 0; i < selectedRows.length; i++)
						selectedAggregations[i] = ((AggregationTableModel) tblAggregation.getTable().getModel())
								.get(selectedRows[i]);

					tblmInstances.clear();
					String type = tblmObjectType.get(tblObjectType.getSelectedRow());
					if (tblLifecycleModel.getTable().getSelectedRowCount() > 0) {
						LifecycleModel model = tblmLifecycleModel.get(tblLifecycleModel.getSelectedRow());
						if (selectedAggregations.length > 0) {
							instance: for (LifecycleModel instanceModel : results.getResults().get(type)
									.row(model.getLabel()).values()) {

								if (instanceModel.getAttributeMap().containsKey(LifecycleModel.AGGREGATION)) {
									@SuppressWarnings("unchecked")
									Map<String, Aggregation> instanceAggregations = (Map<String, Aggregation>) instanceModel
											.getAttributeMap().get(LifecycleModel.AGGREGATION);

									for (Aggregation selectedAggregation : selectedAggregations) {
										// Check that this object instance lifecycle model also contains the aggregation key.
										// If not, continue with the next instance
										if (!instanceAggregations.containsKey(selectedAggregation.getKey()))
											continue instance;
										// Check that the instance's aggregation key's value is the same as the selected aggregation's value.
										// If not, continue with the next instance
										if (!selectedAggregation.getValue().equals(
												instanceAggregations.get(selectedAggregation.getKey()).getValue()))
											continue instance;
									}
									// Only if for all aggregations, the object instance's values match, will it be included.
									tblmInstances.add(instanceModel);
								}
							}
						} else {
							tblmInstances.addAll(results.getResults().get(type).row(model.getLabel()).values());
						}
						tblInstances.getTable().selectAll();
					}
					createDetails(tblInstances.getSelectedModels());
				}
			}
		});
		tblAggregation.getTable().setDefaultRenderer(Object[].class, new TableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				Object[] array = (Object[]) value;
				JLabel label = new JLabel(array[0] + " = " + array[1]);
				if (isSelected)
					label.setForeground(Color.YELLOW);
				else
					label.setForeground(Color.LIGHT_GRAY);
				return label;
			}
		});

		// Instances
		tblInstances.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					createDetails(tblInstances.getSelectedModels());
				}
			}
		});
		tblInstances.getTable().setDefaultRenderer(LifecycleModel.class, new DefaultTableCellRenderer() {
			private static final long serialVersionUID = -3211383190507363388L;

			@Override
			protected void setValue(Object value) {
				LifecycleModel model = (LifecycleModel) value;
				setText((value == null) ? model.getLabel() : model.getInstance());
			}
		});

		// Layout
		modelsPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.BOTH;
		modelsPanel.add(tblObjectType, c);
		c.gridy = 1;
		modelsPanel.add(tblLifecycleModel, c);
		c.gridy = 2;
		modelsPanel.add(tblAggregation, c);
		c.gridy = 3;
		modelsPanel.add(tblInstances, c);
	}

	public void createDetails(LifecycleModel... models) {
		LifecycleModel model;

		if (models == null || models.length == 0)
			model = LifecycleModelFactory.aggregateModels(new LifecycleModel("Empty lifecycle model"));
		else
			model = LifecycleModelFactory.aggregateModels(models);

		graphPanel = createGraphPanel(model);
		metricsPanel = createMetricsPanel(model);

		spDetails.setTopComponent(graphPanel);
		spDetails.setBottomComponent(metricsPanel);
	}

	@SuppressWarnings("unchecked")
	private ProMJGraphPanel createGraphPanel(LifecycleModel model) {

		int maxTransitionExecutions = 0;

		for (Transition transition : model.getEdges()) {
			if (transition.getAttributeMap().containsKey(PerformanceMeasure.TRANSITION_EXECUTIONS_BASE.getLabel())) {
				if (((HashSet<TransitionExecution>) transition.getAttributeMap()
						.get(PerformanceMeasure.TRANSITION_EXECUTIONS_BASE.getLabel()))
								.size() > maxTransitionExecutions) {
					maxTransitionExecutions = ((HashSet<TransitionExecution>) transition.getAttributeMap()
							.get(PerformanceMeasure.TRANSITION_EXECUTIONS_BASE.getLabel())).size();
				}
			}
		}
		for (Transition transition : model.getEdges()) {
			transition.getAttributeMap().put(AttributeMap.EDGEEND, AttributeMap.ArrowType.ARROWTYPE_CLASSIC);
			if (transition.getAttributeMap().containsKey(PerformanceMeasure.TRANSITION_EXECUTIONS_BASE.getLabel())) {
				double percentage = (((HashSet<TransitionExecution>) transition.getAttributeMap()
						.get(PerformanceMeasure.TRANSITION_EXECUTIONS_BASE.getLabel())).size())
						/ (double) maxTransitionExecutions;
				float width = (float) Math.max(1, 3 * percentage);
				transition.getAttributeMap().put(AttributeMap.LINEWIDTH, width);
			}
		}

		long maxVisits = 0l;
		double maxTimeSpent = 0d;

		for (State state : model.getNodes()) {
			if (!state.getIdentifier().equals("init")
					&& state.getAttributeMap().containsKey(PerformanceMeasure.STATE_VISIT_DURATIONS_BASE.getLabel())) {
				DescriptiveStatistics stats = (DescriptiveStatistics) state.getAttributeMap()
						.get(PerformanceMeasure.STATE_VISIT_DURATIONS_BASE.getLabel());
				if (stats.getSum() > maxTimeSpent)
					maxTimeSpent = stats.getSum();
				if (stats.getN() > maxVisits)
					maxVisits = stats.getN();
			}
		}
		for (State state : model.getNodes()) {

			state.getAttributeMap().put(AttributeMap.LABEL, "<html><br>" + state.getLabel() + "</html>");
			state.getAttributeMap().put(AttributeMap.FILLCOLOR, new Color(230, 230, 230));
			state.getAttributeMap().put(AttributeMap.SHOWLABEL, true);
			state.getAttributeMap().put(AttributeMap.SIZE, new Dimension(75, 40));

			if (state.getIdentifier().equals("init"))
				state.getAttributeMap().put(AttributeMap.DASHPATTERN, new float[] { 1f });
			else {
				if (state.getAttributeMap().containsKey(PerformanceMeasure.STATE_VISIT_DURATIONS_BASE.getLabel())) {
					DescriptiveStatistics stats = (DescriptiveStatistics) state.getAttributeMap()
							.get(PerformanceMeasure.STATE_VISIT_DURATIONS_BASE.getLabel());
					double percentage;

					percentage = 100d / maxVisits * stats.getN();
					percentage = Math.max(1, Math.log(Math.E) * Math.log(percentage));
					state.getAttributeMap().put(AttributeMap.LINEWIDTH, (float) percentage);

					percentage = 1d - Math.min(1, Math.max(0.2, 1 / maxTimeSpent * stats.getSum()));
					state.getAttributeMap().put(AttributeMap.STROKECOLOR,
							new Color((int) (255 * percentage), (int) (255 * percentage), (int) (255 * percentage)));
				}
			}
		}

		ProMJGraphPanel panel = ProMJGraphVisualizer.instance().visualizeGraphWithoutRememberingLayout(model);
		return panel;
	}

	private JPanel createMetricsPanel(LifecycleModel model) {
		JPanel metricsPanel = new JPanel();

		DefaultTableModel tblmMetrics = new DefaultTableModel();
		tblmMetrics.addColumn("Metric");
		tblmMetrics.addColumn("Min");
		tblmMetrics.addColumn("Max");
		tblmMetrics.addColumn("Avg");
		tblmMetrics.addColumn("Med");
		tblmMetrics.addColumn("Sum");
		tblmMetrics.addColumn("N");
		ProMTable tblMetrics = new ProMTable(tblmMetrics);
		tblMetrics.getTable().setDefaultEditor(String.class, null);

		for (TimedPerformanceMetric metric : model.getTimedPerformanceMetrics()) {
			DescriptiveStatistics stats = (DescriptiveStatistics) model.getAttributeMap().get(metric.getLabel());
			//@formatter:off
			tblmMetrics.addRow(
					new Object[] {
							metric.getLabel(), 
							format(metric, stats.getMin()), 
							format(metric, stats.getMax()), 
							format(metric, stats.getMean()),
							format(metric, stats.getPercentile(50)), 
							format(metric, stats.getSum()), 
							stats.getN()
					});
			//@formatter:on
		}

		metricsPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.BOTH;
		metricsPanel.add(tblMetrics, c);

		return metricsPanel;
	}

	private String format(TimedPerformanceMetric metric, Double value) {
		return DurationFormatUtils.formatDurationWords(value.longValue(), false, false);
	}

}
