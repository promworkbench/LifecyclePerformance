package org.processmining.lifecycleperformance.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SpringLayout;

import org.deckfour.xes.info.XAttributeInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.util.ui.widgets.ProMComboBox;
import org.processmining.framework.util.ui.widgets.ProMHeaderPanel;
import org.processmining.framework.util.ui.wizard.ProMWizardStep;
import org.processmining.lifecycleperformance.parameters.AddArtifactLifecycleInformationParameters;
import org.processmining.lifecycleperformance.utils.swing.SpringUtilities;

import com.fluxicon.slickerbox.factory.SlickerFactory;

public class AddArtifactLifecycleInformationParametersStep extends ProMHeaderPanel
		implements ProMWizardStep<AddArtifactLifecycleInformationParameters> {

	private static final long serialVersionUID = 1054786127714893932L;

	private String title;
	JCheckBox ckModel;
	ProMComboBox<String> cmbModelAttribute;
	ProMComboBox<String> cmbInstanceAttribute;
	ProMComboBox<String> cmbTransitionAttribute;

	public AddArtifactLifecycleInformationParametersStep(String title,
			final AddArtifactLifecycleInformationParameters parameters, final XLog eventlog) {
		super(title);
		setTitle(title);

		XAttributeInfo info = XLogInfoFactory.createLogInfo(eventlog).getEventAttributeInfo();
		String[] attributes = info.getAttributeKeys().toArray(new String[info.getAttributeKeys().size()]);

		// Model name text field
		cmbModelAttribute = new ProMComboBox<String>(attributes);
		cmbModelAttribute.setVisible(false);
		JLabel lblModel = SlickerFactory.instance().createLabel("Model");
		lblModel.setLabelFor(cmbModelAttribute);
		lblModel.setVisible(false);

		ckModel = SlickerFactory.instance().createCheckBox("", true);
		JLabel lblCkModel = SlickerFactory.instance().createLabel("Use log-level lifecycle model attribute");
		lblCkModel.setLabelFor(ckModel);
		ckModel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lblModel.setVisible(!lblModel.isVisible());
				cmbModelAttribute.setVisible(!cmbModelAttribute.isVisible());
			}
		});

		cmbInstanceAttribute = new ProMComboBox<String>(info.getAttributeKeys());
		JLabel lblInstance = SlickerFactory.instance().createLabel("Instance");
		lblInstance.setLabelFor(cmbInstanceAttribute);

		cmbTransitionAttribute = new ProMComboBox<String>(info.getAttributeKeys());
		JLabel lblTransition = SlickerFactory.instance().createLabel("Transition");
		lblTransition.setLabelFor(cmbTransitionAttribute);

		this.setLayout(new SpringLayout());
		this.add(ckModel);
		this.add(lblCkModel);
		this.add(lblModel);
		this.add(cmbModelAttribute);
		this.add(lblInstance);
		this.add(cmbInstanceAttribute);
		this.add(lblTransition);
		this.add(cmbTransitionAttribute);

		SpringUtilities.makeCompactGrid(this, 5, 2, //rows, cols
				6, 6, //initX, initY
				6, 6); //xPad, yPad
	}

	public boolean canApply(AddArtifactLifecycleInformationParameters parameters, JComponent component) {
		return true;
	}

	public AddArtifactLifecycleInformationParameters apply(AddArtifactLifecycleInformationParameters parameters,
			JComponent component) {

		if (ckModel.isSelected()) {
			parameters.setUseLogLevelModelLabel(true);
		} else {
			parameters.setUseLogLevelModelLabel(false);
			parameters.setModelAttribute(cmbModelAttribute.getSelectedItem().toString());
		}
		parameters.setInstanceAttribute(cmbInstanceAttribute.getSelectedItem().toString());
		parameters.setTransitionAttribute(cmbTransitionAttribute.getSelectedItem().toString());

		return parameters;
	}

	public JComponent getComponent(AddArtifactLifecycleInformationParameters parameters) {
		return this;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
