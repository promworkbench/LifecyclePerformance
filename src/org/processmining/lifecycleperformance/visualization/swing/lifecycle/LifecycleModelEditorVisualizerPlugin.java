package org.processmining.lifecycleperformance.visualization.swing.lifecycle;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.util.ui.widgets.ProMSplitPane;
import org.processmining.lifecycleperformance.models.lifecycle.LifecycleModel;
import org.processmining.plugins.editor.TransitionSystemEditorPanel;
import org.processmining.plugins.editor.TransitionSystemEditorVisualizerPlugin;

@Plugin(
		name = "@0 Lifecycle model editor",
		returnLabels = { "Edit lifecycle model" },
		returnTypes = { JComponent.class },
		parameterLabels = { "Lifecycle model" },
		userAccessible = true)
@Visualizer
public class LifecycleModelEditorVisualizerPlugin {

	@PluginVariant(
			requiredParameterLabels = { 0 })
	public JComponent visualize(UIPluginContext uiPluginContext, LifecycleModel lifecycleModel) {
		// The top part of the visualizer is the transition system editor
		TransitionSystemEditorPanel graphPanel = new TransitionSystemEditorVisualizerPlugin().visualize(uiPluginContext,
				lifecycleModel);

		// The bottom part of the visualizer is a table with metrics defined on the transition system
		TimedPerformanceMetricsTableModel metricsTableModel = new TimedPerformanceMetricsTableModel(lifecycleModel);
		PerformanceMetricsTable metricsTable = new PerformanceMetricsTable(metricsTableModel);

		ProMSplitPane splitPane = new ProMSplitPane(ProMSplitPane.VERTICAL_SPLIT);
		splitPane.setTopComponent(graphPanel);
		splitPane.setBottomComponent(metricsTable);
		splitPane.setResizeWeight(1.0);
		splitPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

		return splitPane;

	}

}
