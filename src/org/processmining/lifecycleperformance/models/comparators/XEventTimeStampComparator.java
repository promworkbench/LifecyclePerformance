package org.processmining.lifecycleperformance.models.comparators;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XEvent;

/**
 * Event comparator based on timestamp of events. If one of two events does not
 * have a timestamp and the other does, it is considered smaller than the other
 * event. If both have no timestamp they are considered equal.
 * 
 * @author B.F.A. Hompes <b.f.a.hompes@tue.nl>
 *
 */
public class XEventTimeStampComparator implements Comparator<XEvent>, Serializable {

	private static final long serialVersionUID = -1613999918834750787L;

	public static final String DESCRIPTION;

	static {
		DESCRIPTION = "Sort events based on their timestamp.";
	}

	XTimeExtension te = XTimeExtension.instance();

	@Override
	public int compare(XEvent e1, XEvent e2) {
		try {
			return te.extractTimestamp(e1).compareTo(te.extractTimestamp(e2));
		} catch (NullPointerException ex) {
			Date start = te.extractTimestamp(e1);
			Date end = te.extractTimestamp(e2);

			if (start == null && end != null)
				return -1;
			if (end == null && start != null)
				return 1;
			if (start == null && end == null)
				return 0;
		}
		return 0;
	}

	@Override
	public String toString() {
		return DESCRIPTION;
	}
}