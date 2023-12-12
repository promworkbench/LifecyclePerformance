package org.processmining.lifecycleperformance.models.extensions;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeList;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XEvent;

/**
 * Extension that adds possibly multiple lifecycle transitions in lifecycle
 * models for object instances to events. Using this extension, events can
 * signal a transition in multiple lifecycle models for multiple object
 * instances. For example, an event can signal a transition 'complete' in the
 * case lifecycle model of case instance 'case1', while at the same time
 * signaling a 'complete' transition in the activity lifecycle model of activity
 * instance 'activityXnr123'.
 * 
 * @author B.F.A. Hompes
 */
// Lifecycle moves are of the form (object instance type, object instance, lifecycle model, action/transition)
public class XObjectLifecycleExtension extends XExtension {

	private static final long serialVersionUID = -4974421470391509121L;

	private XFactory factory;

	/**
	 * Properties of this XES extension. Note that this extension exists only in
	 * code, it does not have a unique URI at this moment.
	 */
	private static final String NAME;
	private static final String PREFIX;
	private static final URI URII;

	/**
	 * Singleton instance of this extension.
	 */
	private static XObjectLifecycleExtension SINGLETON;

	/**
	 * Keys for the attribute.
	 */
	public static final String KEY_MOVES;
	public static final String KEY_MODEL;
	public static final String KEY_TYPE;
	public static final String KEY_INSTANCE;
	public static final String KEY_TRANSITION;

	/**
	 * Attribute prototype.
	 */
	public static XAttributeList ATTR_MOVES;

	static {
		NAME = "Object Lifecycle";
		PREFIX = "objectlifecycle:";
		//		URII = URI.create("http://xes-standard.org/objectlifecycle.xesext");
		URII = URI.create("none");

		KEY_MOVES = "moves";
		KEY_TYPE = "type";
		KEY_INSTANCE = "instance";
		KEY_MODEL = "model";
		KEY_TRANSITION = "transition";

		SINGLETON = new XObjectLifecycleExtension();
	}

	/**
	 * Private constructor
	 */
	private XObjectLifecycleExtension() {
		super(NAME, PREFIX, URII);
		factory = XFactoryRegistry.instance().currentDefault();
		ATTR_MOVES = factory.createAttributeList(PREFIX + KEY_MOVES, this);
		this.eventAttributes.add((XAttributeList) ATTR_MOVES.clone());
	}

	/**
	 * Provides static access to the singleton instance of this extension.
	 * 
	 * @return Singleton instance.
	 */
	public static XObjectLifecycleExtension instance() {
		return SINGLETON;
	}

	private Object readResolve() {
		return SINGLETON;
	}

	public Collection<XAttribute> extractMoves(XEvent event) {
		XAttribute moves = event.getAttributes().get(PREFIX + KEY_MOVES);
		if (moves == null) {
			return new HashSet<XAttribute>();
		} else {
			return ((XAttributeList) moves).getCollection();
		}
	}

	public void add(XEvent event, String type, String instance, String model, String transition) {
		if (extractMoves(event) == null || extractMoves(event).isEmpty())
			event.getAttributes().put(PREFIX + KEY_MOVES, (XAttributeList) ATTR_MOVES.clone());

		XAttributeLiteral attrType = factory.createAttributeLiteral(PREFIX + KEY_TYPE, type, this);
		XAttributeLiteral attrInstance = factory.createAttributeLiteral(PREFIX + KEY_INSTANCE, instance, this);
		XAttributeLiteral attrModel = factory.createAttributeLiteral(PREFIX + KEY_MODEL, model, this);
		XAttributeLiteral attrTransition = factory.createAttributeLiteral(PREFIX + KEY_TRANSITION, transition, this);

		attrType.getAttributes().put(PREFIX + KEY_INSTANCE, attrInstance);
		attrType.getAttributes().put(PREFIX + KEY_MODEL, attrModel);
		attrType.getAttributes().put(PREFIX + KEY_TRANSITION, attrTransition);

		extractMoves(event).add(attrType);
	}

	public Set<List<String>> extractMovesListSet(XEvent event) {
		Set<List<String>> set = new HashSet<List<String>>();

		Collection<XAttribute> collection = extractMoves(event);

		// Loop over all the entries in the moves list (type attributes)
		Iterator<XAttribute> it = collection.iterator();
		while (it.hasNext()) {
			XAttributeLiteral type = (XAttributeLiteral) it.next();
			/*
			 * Only consider type attributes (no other attributes should
			 * exist...) that have meta-attributes for instance, model, and
			 * transition.
			 */
			if (!type.getKey().equals(PREFIX + KEY_TYPE) || !type.hasAttributes())
				continue;

			// Get the instance meta-attribute
			XAttributeLiteral instance = (XAttributeLiteral) type.getAttributes().get(PREFIX + KEY_INSTANCE);
			if (instance == null)
				continue;

			// Get the model meta-attribute
			XAttributeLiteral model = (XAttributeLiteral) type.getAttributes().get(PREFIX + KEY_MODEL);
			if (model == null)
				continue;

			// Get the transition meta-attribute
			XAttributeLiteral transition = (XAttributeLiteral) type.getAttributes().get(PREFIX + KEY_TRANSITION);
			if (transition == null)
				continue;

			// Save the model, instance, transition entry.
			List<String> list = new ArrayList<String>();
			list.add(type.getValue());
			list.add(instance.getValue());
			list.add(model.getValue());
			list.add(transition.getValue());
			set.add(list);
		}

		return set;
	}

	//	public void assignMoves(XEvent event, Collection<XAttribute> collection) {
	//		if (collection != null && collection.size() > 0) {
	//			XAttributeList moves = (XAttributeList) ATTR_MOVES.clone();
	//
	//			for (XAttribute type : collection) {
	//				// Only add the type attribute if it has meta-attributes for instance, model, and transition.
	//				if (type.getAttributes().containsKey(PREFIX + KEY_INSTANCE)
	//						&& type.getAttributes().containsKey(PREFIX + KEY_MODEL)
	//						&& type.getAttributes().containsKey(PREFIX + KEY_TRANSITION))
	//					moves.addToCollection(type);
	//			}
	//
	//			event.getAttributes().put(PREFIX + KEY_MOVES, moves);
	//		}
	//	}

	//	public void assignTable(XEvent event, String objectType, Table<String, String, String> table) {
	//		XAttributeList moves = (XAttributeList) ATTR_MOVES.clone();
	//
	//		for (Cell<String, String, String> cell : table.cellSet()) {
	//			// Add the type attribute as the entry in the moves list
	//			XAttributeLiteral type = factory.createAttributeLiteral(PREFIX + KEY_TYPE, objectType, this);
	//
	//			// Add the model, instance, and transition attributes as meta-attributes to the type attribute
	//			XAttributeLiteral instance = factory.createAttributeLiteral(PREFIX + KEY_INSTANCE, cell.getColumnKey(),
	//					this);
	//			XAttributeLiteral model = factory.createAttributeLiteral(PREFIX + KEY_MODEL, cell.getRowKey(), this);
	//			XAttributeLiteral transition = factory.createAttributeLiteral(PREFIX + KEY_TRANSITION, cell.getValue(),
	//					this);
	//
	//			type.getAttributes().put(PREFIX + KEY_INSTANCE, instance);
	//			type.getAttributes().put(PREFIX + KEY_MODEL, model);
	//			type.getAttributes().put(PREFIX + KEY_TRANSITION, transition);
	//
	//			// Add the type attribute as the entry in the moves list
	//			moves.addToCollection(type);
	//		}
	//
	//		event.getAttributes().put(PREFIX + KEY_MOVES, moves);
	//	}

}
