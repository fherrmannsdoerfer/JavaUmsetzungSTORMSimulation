package gui;

import java.awt.Component;
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
		frame.getContentPane().setLayout(new CardLayout(0, 0));
		
		JPanel graphPanel = new JPanel();
		graphPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		graphPanel.add((Component) scSwing.getChart().getCanvas());
		frame.getContentPane().add(graphPanel, "name_1427025939649765000");
		graphPanel.setLayout(new GridLayout(1, 0, 0, 0));
		
		JSplitPane splitPane = new JSplitPane();
		frame.getContentPane().add(splitPane, "name_1427025974514597000");
		
		JPanel controlPanel = new JPanel();
		frame.getContentPane().add(controlPanel, "name_1427025964234086000");
		
		JButton btnNewButton = new JButton("New button");
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
