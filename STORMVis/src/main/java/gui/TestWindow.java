package gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;

import java.awt.FlowLayout;

import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import javax.swing.AbstractAction;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import org.jzy3d.chart.Chart;

import javax.swing.JPanel;

import java.awt.BorderLayout;

import javax.swing.border.LineBorder;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JTextPane;
import javax.swing.Box;
import javax.swing.JButton;

import java.awt.CardLayout;

import javax.swing.JSplitPane;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

public class TestWindow {

	private JFrame frame;
	private final Action action = new SwingAction();
	private JComponent chartComponent;
	private Chart chart;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TestWindow window = new TestWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public TestWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		ScatterSwing scSwing = new ScatterSwing();
		frame = new JFrame();
		frame.setBounds(100, 100, 860, 633);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("2px"),
				ColumnSpec.decode("521px"),
				ColumnSpec.decode("1px"),},
			new RowSpec[] {
				RowSpec.decode("611px"),}));
		
		JPanel graphPanel = new JPanel();
		graphPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		graphPanel.add((Component) scSwing.getChart().getCanvas());
		frame.getContentPane().add(graphPanel, "1, 1, left, fill");
		graphPanel.setLayout(new GridLayout(1, 0, 0, 0));
		graphPanel.setPreferredSize(new Dimension(600, 600));
		
		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(null);
		frame.getContentPane().add(controlPanel, "3, 1, left, fill");
		
		JButton btnNewButton = new JButton("New button");
		btnNewButton.setBounds(5, 5, 117, 29);
		controlPanel.add(btnNewButton);
//		chartComponent = (javax.swing.JComponent) scSwing.getChart().getCanvas();
	}

	private class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, "SwingAction");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		public void actionPerformed(ActionEvent e) {
		}
	}
}
