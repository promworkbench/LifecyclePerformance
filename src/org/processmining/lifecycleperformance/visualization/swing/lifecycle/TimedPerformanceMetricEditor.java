package org.processmining.lifecycleperformance.visualization.swing.lifecycle;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.processmining.framework.util.ui.widgets.ProMTextField;
import org.processmining.lifecycleperformance.models.lifecycle.LifecycleModel;
import org.processmining.lifecycleperformance.models.performance.TimedPerformanceMetric;

import com.fluxicon.slickerbox.factory.SlickerFactory;

public class TimedPerformanceMetricEditor extends JFrame {

	private static final long serialVersionUID = 7695819649169756983L;

	// FIELDS

	private LifecycleModel lifecycleModel;
	private TimedPerformanceMetric timedPerformanceMetric;

	private ProMTextField txtLabel;
	//	private ProMTextField txtFormat;
	private JLabel lblExpression;
	private RSyntaxTextArea txtExpression;
	private JLabel lblMeasurementTimeExpression;
	private RSyntaxTextArea txtMeasurementTimeExpression;

	// CONSTRUCTORS

	public TimedPerformanceMetricEditor(LifecycleModel lifecycleModel, TimedPerformanceMetric timedPerformanceMetric) {
		setTitle("Performance metric editor");
		setLifecycleModel(lifecycleModel);
		setPerformanceMetric(timedPerformanceMetric);

		txtLabel = new ProMTextField(timedPerformanceMetric.getLabel(), "Enter a label for the metric.");

		txtExpression = new RSyntaxTextArea(timedPerformanceMetric.getExpression(), 20, 40);
		txtExpression.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_GROOVY);
		txtExpression.setCodeFoldingEnabled(true);
		RTextScrollPane spTxtExpression = new RTextScrollPane(txtExpression);

		lblExpression = new JLabel("Metric expression");
		lblExpression.setLabelFor(spTxtExpression);

		txtMeasurementTimeExpression = new RSyntaxTextArea(timedPerformanceMetric.getMeasurementTimeExpression(), 20,
				40);
		txtMeasurementTimeExpression.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_GROOVY);
		txtMeasurementTimeExpression.setCodeFoldingEnabled(true);
		RTextScrollPane spTxtMeasurementTimeExpression = new RTextScrollPane(txtMeasurementTimeExpression);

		lblMeasurementTimeExpression = new JLabel("Measurement time expression");
		lblMeasurementTimeExpression.setLabelFor(spTxtExpression);

		JButton btnSave = SlickerFactory.instance().createButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				timedPerformanceMetric.setLabel(txtLabel.getText());
				timedPerformanceMetric.setExpression(txtExpression.getText());
				timedPerformanceMetric.setMeasurementTimeExpression(txtMeasurementTimeExpression.getText());
				//				performanceMetric.setFormat(txtFormat.getText());
			}
		});

		//		txtFormat = new ProMTextField(performanceMetric.getFormat(), "Enter a format for the metric.");

		JPanel mainPanel = SlickerFactory.instance().createRoundedPanel();
		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		mainPanel.add(txtLabel, c);

		//		c.gridy = 1;
		//		c.fill = GridBagConstraints.HORIZONTAL;
		//		mainPanel.add(txtFormat, c);

		c.gridy = 1;
		mainPanel.add(lblExpression, c);

		c.gridy = 2;
		c.weighty = 0.5;
		c.fill = GridBagConstraints.BOTH;
		mainPanel.add(spTxtExpression, c);

		c.gridy = 3;
		c.weighty = 0.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		mainPanel.add(lblMeasurementTimeExpression, c);

		c.gridy = 4;
		c.weighty = 0.5;
		c.fill = GridBagConstraints.BOTH;
		mainPanel.add(spTxtMeasurementTimeExpression, c);

		c.gridy = 5;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.LAST_LINE_END;
		mainPanel.add(btnSave, c);

		setContentPane(mainPanel);

		setSize(600, 400);
		setLocationRelativeTo(null);
		pack();
		setVisible(true);
	}

	// GETTERS AND SETTERS

	public LifecycleModel getLifecycleModel() {
		return lifecycleModel;
	}

	public void setLifecycleModel(LifecycleModel lifecycleModel) {
		this.lifecycleModel = lifecycleModel;
	}

	public TimedPerformanceMetric getPerformanceMetric() {
		return timedPerformanceMetric;
	}

	public void setPerformanceMetric(TimedPerformanceMetric timedPerformanceMetric) {
		this.timedPerformanceMetric = timedPerformanceMetric;
	}

}
