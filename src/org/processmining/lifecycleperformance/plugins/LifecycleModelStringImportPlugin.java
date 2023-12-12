package org.processmining.lifecycleperformance.plugins;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.lifecycleperformance.constants.AuthorConstants;
import org.processmining.lifecycleperformance.help.LifecycleModelStringImportHelp;
import org.processmining.lifecycleperformance.models.lifecycle.LifecycleModel;
import org.processmining.lifecycleperformance.models.performance.TimedPerformanceMetric;

@Plugin(
		name = "Import lifecycle model from text",
		parameterLabels = { "Text" },
		returnLabels = { "Lifecycle model" },
		returnTypes = { LifecycleModel.class },
		help = LifecycleModelStringImportHelp.TEXT)
public class LifecycleModelStringImportPlugin {

	@UITopiaVariant(
			affiliation = AuthorConstants.AFFILIATION,
			author = AuthorConstants.NAME,
			email = AuthorConstants.EMAIL)
	@PluginVariant(
			variantLabel = "Import lifecycle model from text",
			help = LifecycleModelStringImportHelp.TEXT,
			requiredParameterLabels = { 0 })
	public LifecycleModel runDefault(UIPluginContext context, String text) {
		LifecycleModel model = null;

		if (text == null || text.isEmpty())
			return model;

		BufferedReader reader = new BufferedReader(new StringReader(text));

		String transitionRegex = "^\\(\\s*(.*)\\s*,\\s*(.*)\\s*,\\s*(.*)\\s*\\)$";
		String performanceMetricRegex = "^\\s*(.*)\\s*=\\s*(.*)\\s*\\|\\|\\s*(.*)\\s*$";
		Pattern pt = Pattern.compile(transitionRegex);
		Pattern pp = Pattern.compile(performanceMetricRegex);
		Matcher m = null;
		String line;
		try {
			// The first line is seen as the label of the system
			model = new LifecycleModel(reader.readLine());
			while ((line = reader.readLine()) != null) {
				// Every line that looks like (state1,transition,state2) is considered to be a transition
				if (line.matches(transitionRegex)) {
					m = pt.matcher(line);
					if (m.find()) {
						model.addTransition(m.group(1), m.group(3), m.group(2));
					}
				}
				// Every line that looks like metric = expression (format) is considered to be a performance metric
				else if (line.matches(performanceMetricRegex)) {
					m = pp.matcher(line);
					if (m.find()) {
						model.getTimedPerformanceMetrics()
								.add(new TimedPerformanceMetric(m.group(1), m.group(2), m.group(3)));
					}
				} else {
					model.addState(line);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

		return model;
	}

}
