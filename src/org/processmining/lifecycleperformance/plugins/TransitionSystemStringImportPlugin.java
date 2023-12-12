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
import org.processmining.lifecycleperformance.help.TransitionSystemStringImportHelp;
import org.processmining.models.graphbased.directed.transitionsystem.TransitionSystem;
import org.processmining.models.graphbased.directed.transitionsystem.TransitionSystemImpl;

@Plugin(
		name = "Import transition system from text",
		parameterLabels = { "Text" },
		returnLabels = { "Transition system" },
		returnTypes = { TransitionSystem.class },
		help = TransitionSystemStringImportHelp.TEXT)
public class TransitionSystemStringImportPlugin {

	@UITopiaVariant(
			affiliation = AuthorConstants.AFFILIATION,
			author = AuthorConstants.NAME,
			email = AuthorConstants.EMAIL)
	@PluginVariant(
			variantLabel = "Import transition system from text",
			help = TransitionSystemStringImportHelp.TEXT,
			requiredParameterLabels = { 0 })
	public TransitionSystem runDefault(UIPluginContext context, String text) {
		TransitionSystem ts = null;

		if (text == null || text.isEmpty())
			return ts;

		BufferedReader reader = new BufferedReader(new StringReader(text));

		String transitionRegex = "^\\(\\s*(.*)\\s*,\\s*(.*)\\s*,\\s*(.*)\\s*\\)$";
		Pattern p = Pattern.compile(transitionRegex);
		Matcher m = null;
		String line;
		try {
			// The first line is seen as the label of the system
			ts = new TransitionSystemImpl(reader.readLine());
			while ((line = reader.readLine()) != null) {
				// Every line that looks like (state1,transition,state2) is considered to be a transition
				if (line.matches(transitionRegex)) {
					m = p.matcher(line);
					if (m.find()) {
						ts.addTransition(m.group(1), m.group(3), m.group(2));
					}
				} else {
					ts.addState(line);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

		return ts;
	}

}
