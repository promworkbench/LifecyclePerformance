package org.processmining.lifecycleperformance.algorithms;

import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XEvent;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.eventstream.core.interfaces.XSEvent;
import org.processmining.lifecycleperformance.models.performance.PerformanceAnalysisResults;
import org.processmining.lifecycleperformance.parameters.LifecyclePerformanceParameters;
import org.processmining.lifecycleperformance.visualization.swing.lifecycleperformanceresults.PerformanceAnalysisResultsXSReaderResultVisualizer;
import org.processmining.stream.core.abstracts.AbstractXSReader;
import org.processmining.stream.reader.views.XSReaderSequenceOfResultsView;

public class StreamLifecyclePerformanceAlgorithm
		extends AbstractXSReader<XSEvent, PerformanceAnalysisResults, PerformanceAnalysisResults> {

	// FIELDS
	private LifecyclePerformanceAlgorithm algorithm;

	// CONSTRUCTOR
	public StreamLifecyclePerformanceAlgorithm(UIPluginContext context, LifecyclePerformanceParameters parameters) {
		super("", new XSReaderSequenceOfResultsView<PerformanceAnalysisResults>("Visualizer of: " + "", null, -1,
				PerformanceAnalysisResultsXSReaderResultVisualizer.instance(context)));

		((XSReaderSequenceOfResultsView<PerformanceAnalysisResults>) (this.getVisualization())).setReader(this);
		algorithm = new LifecyclePerformanceAlgorithm();
		algorithm.init();
		algorithm.setParameters(parameters);
	}

	// METHODS
	public Class<XSEvent> getTopic() {
		return XSEvent.class;
	}

	protected PerformanceAnalysisResults computeCurrentResult() {
		algorithm.calculateLifecyclePerformanceMetrics();
		return algorithm.getResults();
	}

	protected void handleNextPacket(XSEvent packet) {
		// Convert the XSEvent back to a normal XEvent
		XEvent event = XFactoryRegistry.instance().currentDefault().createEvent();

		/*
		 * By default, XSEvent have only some attributes copied. There is an
		 * option in which they receive the additional attributes as payload. In
		 * this case, the attribute key has a prefix that we need to remove
		 * first.
		 */
		for (String attribute : packet.keySet()) {
			event.getAttributes()
					.put(attribute.startsWith("xsevent:data:")
							? attribute.substring(attribute.indexOf("xsevent:data:") + 13)
							: attribute, packet.get(attribute));
		}

		// Handle the event
		algorithm.handleEvent(event);
	}

}
