package org.processmining.lifecycleperformance.models.performance;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.processmining.lifecycleperformance.models.lifecycle.LifecycleModel;
import org.processmining.lifecycleperformance.parameters.LifecyclePerformanceParameters;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class PerformanceAnalysisResults {

	private LifecyclePerformanceParameters parameters;

	// Map of <String object type, 
	// Table of <String lifecycle model label, String instance label, LifeCycleModel instance lifecycle model>>
	// e.g. Map<"Activity", Table<"StandardActivityLifecycleModel", "activity123", sam123>>
	private Map<String, Table<String, String, LifecycleModel>> results;

	public PerformanceAnalysisResults() {
		parameters = new LifecyclePerformanceParameters();
		results = new HashMap<String, Table<String, String, LifecycleModel>>(); //HashBasedTable.create();
	}

	public LifecyclePerformanceParameters getParameters() {
		return parameters;
	}

	public void setParameters(LifecyclePerformanceParameters parameters) {
		this.parameters = parameters;
	}

	public Map<String, Table<String, String, LifecycleModel>> getResults() {
		return results;
	}

	public void setResults(Map<String, Table<String, String, LifecycleModel>> results) {
		this.results = results;
	}

	public Table<String, String, LifecycleModel> getTypeResults(String type) {
		if (!results.containsKey(type))
			results.put(type, HashBasedTable.create());
		return results.get(type);
	}

	public Table<String, String, LifecycleModel> setTypeResults(String type,
			Table<String, String, LifecycleModel> typeMemory) {
		return results.put(type, typeMemory);
	}

	/**
	 * Returns all LifecycleModels in the parameters that have instances in the
	 * results for a given object type.
	 * 
	 * @param type
	 *            The object type
	 * @return
	 */
	public Set<LifecycleModel> getLifecycleModelsForObjectType(String type) {
		Set<String> modelLabels = results.get(type).rowKeySet();
		Set<LifecycleModel> models = parameters.getLifecycleModels().stream()
				.filter(m -> modelLabels.contains(m.getLabel())).collect(Collectors.toSet());
		return models;
	}

}