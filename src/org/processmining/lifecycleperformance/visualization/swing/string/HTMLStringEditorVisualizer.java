package org.processmining.lifecycleperformance.visualization.swing.string;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

import com.fluxicon.slickerbox.factory.SlickerFactory;

@Plugin(
		name = "@0 Editor (HTML)",
		returnLabels = { "String" },
		returnTypes = { JComponent.class },
		parameterLabels = { "String" },
		userAccessible = true)
@Visualizer
public class HTMLStringEditorVisualizer {

	@PluginVariant(
			requiredParameterLabels = { 0 })
	public JComponent visualize(UIPluginContext uiPluginContext, String text) {

		JTextPane textPane = new JTextPane();
		textPane.setText(text);

		JCheckBox checkBox = SlickerFactory.instance().createCheckBox("Consider HTML", false);
		checkBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (checkBox.isSelected())
					textPane.setContentType("text/html");
				else
					textPane.setContentType("");
				textPane.setText(text);
			}
		});
		JButton button = SlickerFactory.instance().createButton("Export to workspace as String");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String string = textPane.getText();
				uiPluginContext.getProvidedObjectManager().createProvidedObject("String created in editor", string,
						uiPluginContext);
				uiPluginContext.getGlobalContext().getResourceManager().getResourceForInstance(string)
						.setFavorite(true);
			}
		});
		JPanel top = new JPanel();
		top.add(checkBox);
		top.add(button);

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(top, BorderLayout.NORTH);
		panel.add(textPane, BorderLayout.CENTER);

		JScrollPane scrollPane = new JScrollPane(panel);
		return scrollPane;
	}

}
