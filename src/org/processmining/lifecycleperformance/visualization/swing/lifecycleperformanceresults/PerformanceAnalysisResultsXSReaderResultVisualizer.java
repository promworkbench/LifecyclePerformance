package org.processmining.lifecycleperformance.visualization.swing.lifecycleperformanceresults;

import javax.swing.JComponent;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.lifecycleperformance.models.performance.PerformanceAnalysisResults;
import org.processmining.stream.core.interfaces.XSReaderResultVisualizer;

public class PerformanceAnalysisResultsXSReaderResultVisualizer
		implements XSReaderResultVisualizer<PerformanceAnalysisResults> {

	private static PerformanceAnalysisResultsXSReaderResultVisualizer singleton = null;

	private UIPluginContext context = null;

	private PerformanceAnalysisResultsXSReaderResultVisualizer(UIPluginContext context) {
		this.context = context;
	}

	public static PerformanceAnalysisResultsXSReaderResultVisualizer instance(UIPluginContext context) {
		if (context == null) {
			throw new NullPointerException("The context can not be null");
		} else {
			if (singleton == null) {
				singleton = new PerformanceAnalysisResultsXSReaderResultVisualizer(context);
			} else if (!context.equals(singleton.context)) {
				return new PerformanceAnalysisResultsXSReaderResultVisualizer(context);
			}
			return singleton;
		}
	}

	@Override
	public JComponent visualize(PerformanceAnalysisResults streamBasedObject) {
		return (new LifecyclePerformanceResultsVisualizerPlugin()).visualize(context, streamBasedObject);
	}

}
