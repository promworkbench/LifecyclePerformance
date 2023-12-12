package org.processmining.lifecycleperformance.parameters;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.processmining.basicutils.parameters.impl.PluginParametersImpl;
import org.processmining.lifecycleperformance.models.lifecycle.LifecycleModel;

public class LifecyclePerformanceParameters extends PluginParametersImpl {

	// FIELDS

	private Map<String, LifecycleModel> lifecycleModels;
	private Map<String, Set<String>> aggregations;

	// CONSTRUCTORS

	public LifecyclePerformanceParameters() {
		lifecycleModels = new HashMap<String, LifecycleModel>();
		aggregations = new HashMap<String, Set<String>>();
	}

	// METHODS

	public Collection<LifecycleModel> getLifecycleModels() {
		return lifecycleModels.values();
	}

	public void addLifecycleModels(LifecycleModel... lifecycleModels) {
		for (LifecycleModel lcm : lifecycleModels) {
			this.lifecycleModels.put(lcm.getLabel(), lcm);
		}
	}

	public Set<String> getAggregations(String model) {
		if (!aggregations.containsKey(model))
			aggregations.put(model, new HashSet<String>());
		return aggregations.get(model);
	}

	public void addAggregation(String model, String aggregation) {
		getAggregations(model).add(aggregation);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof LifecyclePerformanceParameters))
			return false;

		LifecyclePerformanceParameters parameters = (LifecyclePerformanceParameters) obj;

		if (!parameters.lifecycleModels.equals(lifecycleModels))
			return false;
		if (!parameters.aggregations.equals(aggregations))
			return false;

		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), lifecycleModels, aggregations);
	}

	@Override
	public String toString() {
		return "Models: " + lifecycleModels.toString() + ", aggregations: " + aggregations.toString();
	}

}
