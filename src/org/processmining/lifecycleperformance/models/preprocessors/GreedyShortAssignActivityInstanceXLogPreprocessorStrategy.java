package org.processmining.lifecycleperformance.models.preprocessors;

import java.util.ArrayList;
import java.util.Map.Entry;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension.StandardModel;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.lifecycleperformance.parameters.ActivityInstanceXLogPreprocessorParameters;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * Basic activity matching. Uses only event log. Walks through the log and each
 * trace only once. Matches events that share the conceptname extension and
 * differ in lifecycle transition. Please note that this strategy only handles
 * START and COMPLETE lifecycle transitions. Other transitions are given unique
 * instance IDs.
 * 
 * [A1sA2sA1cA2c] => [A1sA1c] + [A2sA2c]
 * 
 * @param eventlog
 *            The original event log. Events are assumed to be ordered based on
 *            timestamps.
 * @return The event log with an activity instance id attribute added to the
 *         events.
 */
public class GreedyShortAssignActivityInstanceXLogPreprocessorStrategy
		extends AssignActivityInstanceXLogPreprocessorStrategy {

	@Override
	public XLog preprocess(XLog eventlog, ActivityInstanceXLogPreprocessorParameters parameters) {

		XConceptExtension ce = XConceptExtension.instance();
		XLifecycleExtension le = XLifecycleExtension.instance();
		//		XIDFactory xidf = XIDFactory.instance();

		for (XTrace trace : eventlog) {
			BiMap<XEvent, XEvent> matchedEvents = HashBiMap.create();
			ArrayList<XEvent> eventsToMatch = new ArrayList<XEvent>();

			event: for (XEvent currentEvent : trace) {
				String currentEventConceptName = ce.extractName(currentEvent);
				StandardModel currentEventTransition = le.extractStandardTransition(currentEvent);

				unmatchedEventFromTrace: for (XEvent eventToMatch : eventsToMatch) {
					String eventToMatchConceptName = ce.extractName(eventToMatch);

					if (!currentEventConceptName.equals(eventToMatchConceptName)) {
						// The events have different concept names, no match
						continue unmatchedEventFromTrace;
					}

					StandardModel eventToMatchTransition = le.extractStandardTransition(eventToMatch);

					if ((eventToMatchTransition.equals(StandardModel.START)
							&& currentEventTransition.equals(StandardModel.COMPLETE))
							|| (eventToMatchTransition.equals(StandardModel.COMPLETE)
									&& currentEventTransition.equals(StandardModel.START))) {

						// Same concept name and matching start-complete. Match!
						matchedEvents.put(currentEvent, eventToMatch);
						eventsToMatch.remove(eventToMatch);
						continue event;

					}
				}

				// No match
				eventsToMatch.add(currentEvent);
			}

			ID id = ID.getInstance();

			// Create attribute for the matched events
			for (Entry<XEvent, XEvent> match : matchedEvents.entrySet()) {
				String i = id.createId();
				ce.assignInstance(match.getKey(), i);
				ce.assignInstance(match.getValue(), i);
			}
			// Create attribute for the unmatched events
			for (XEvent event : eventsToMatch) {
				String i = id.createId();
				ce.assignInstance(event, i);
			}

		}

		return eventlog;
	}

	private static class ID {
		private static ID id;
		private static int counter;

		public ID() {
			counter = 0;
		}

		public static ID getInstance() {
			if (id == null) {
				id = new ID();
			}
			return id;
		}

		public String createId() {
			counter++;
			return "" + counter;
		}
	}

}
