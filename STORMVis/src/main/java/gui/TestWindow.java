package gui;

import gui.DataTypeDetector.DataType;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;

import java.awt.FlowLayout;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import javax.swing.AbstractAction;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import org.javatuples.Pair;
import org.jzy3d.chart.Chart;
import org.jzy3d.plot3d.primitives.Polygon;

import parsing.LineObjectParser;

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

import javax.swing.JToolBar;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JCheckBox;

public class TestWindow {

	private JFrame frame;
	private final Action action = new SwingAction();
	private JComponent chartComponent;
	private Chart chart;
	private final Action importFileAction = new FileImportAction();
	private ScatterSwing scSwing;
	private Component graphComponent;
	private final JPanel graphPanel = new JPanel(new BorderLayout());;

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
		scSwing = new ScatterSwing();
		frame = new JFrame();
		frame.setBounds(100, 100, 888, 726);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		graphPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		graphComponent = (Component) scSwing.getSwingChart().getCanvas();
		graphPanel.add(graphComponent);
		
		frame.getContentPane().add(graphPanel);
		
		JToolBar toolBar = new JToolBar();
		frame.getContentPane().add(toolBar, BorderLayout.NORTH);
		
		JButton btnImportFile = new JButton("Import file");
		btnImportFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnImportFile.setAction(importFileAction);
		toolBar.add(btnImportFile);
		btnImportFile.setText("Import file");
		
		JPanel controlPanel = new JPanel();
		frame.getContentPane().add(controlPanel, BorderLayout.EAST);
		
		final JCheckBox chckbxLight = new JCheckBox("Light");
		chckbxLight.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.out.println("Selected: " + chckbxLight.isSelected());
				scSwing.lighton = chckbxLight.isSelected();
				graphComponent = (Component) scSwing.getSwingChart().getCanvas();
				graphPanel.remove(0);
				graphPanel.add(graphComponent);
				graphPanel.revalidate();
			}
		});
		controlPanel.add(chckbxLight);
		
	}
	
	private void proceedFileImport(File file) {
		System.out.println("Path: " + file.getAbsolutePath());
		DataType type = DataType.UNKNOWN;
		try {
			type = DataTypeDetector.getDataType(file.getAbsolutePath());
			System.out.println(type.toString());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Object data = ParserWrapper.parseFileOfType(file.getAbsolutePath(), type);
		if(type.equals(DataType.NFF)) {
			Pair<List<Polygon>, List<float[][]>> data2 = (Pair<List<Polygon>, List<float[][]>>) data;
			data = data2.getValue0();
		}
		@SuppressWarnings("unused")
		Plotter plotter = new Plotter(data, type);
		graphPanel.removeAll();
		graphComponent = (Component) plotter.createChart().getCanvas();
		graphPanel.add(graphComponent);
		graphPanel.revalidate();
		graphPanel.repaint();
		graphComponent.revalidate();
	}
	
	

	private class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, "SwingAction");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		public void actionPerformed(ActionEvent e) {
		}
	}
	
	private class FileImportAction extends AbstractAction {
		public FileImportAction() {
			putValue(NAME, "fileImportAction");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		public void actionPerformed(ActionEvent e) {
			System.out.println("Choose file");
			JFileChooser chooser = new JFileChooser();
			chooser.setAcceptAllFileFilterUsed(false);
			chooser.setFileFilter(new TriangleLineFilter());
			chooser.setFileSelectionMode(0);
			int returnVal = chooser.showOpenDialog(frame); //replace null with your swing container
			File file;
			if(returnVal == JFileChooser.APPROVE_OPTION) {     
				proceedFileImport(chooser.getSelectedFile());
			}
		}
	}
	
}
