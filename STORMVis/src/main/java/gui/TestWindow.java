package gui;

import gui.DataTypeDetector.DataType;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import org.javatuples.Pair;
import org.jzy3d.chart.Chart;
import org.jzy3d.plot3d.primitives.Polygon;

public class TestWindow {

	private JFrame frame;
	private final Action action = new SwingAction();
	private JComponent chartComponent;
	private Chart chart;
	private final Action importFileAction = new FileImportAction();
	private ScatterSwing scSwing;
	private Component graphComponent;
	private final JPanel graphPanel = new JPanel(new BorderLayout());
	private final JLabel lblNewLabel = new JLabel("Please import data.");

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
//		scSwing = new ScatterSwing();
		frame = new JFrame();
		frame.setBounds(100, 100, 888, 726);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		graphPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
//		graphComponent = (Component) scSwing.getSwingChart().getCanvas();
//		graphPanel.add(graphComponent);
		
		frame.getContentPane().add(graphPanel);
		
		
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		graphPanel.add(lblNewLabel, BorderLayout.CENTER);
		
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
		GridBagLayout gbl_controlPanel = new GridBagLayout();
		gbl_controlPanel.columnWidths = new int[]{117, 0};
		gbl_controlPanel.rowHeights = new int[] {20, 20, 0};
		gbl_controlPanel.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_controlPanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		controlPanel.setLayout(gbl_controlPanel);
		
		JButton clearButton = new JButton("Clear");
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearView();
			}
		});
		
		final JCheckBox chckbxLight = new JCheckBox("Light");
		chckbxLight.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.out.println("Selected: " + chckbxLight.isSelected());
//				scSwing.lighton = chckbxLight.isSelected();
//				graphComponent = (Component) scSwing.getSwingChart().getCanvas();
//				graphPanel.remove(0);
//				graphPanel.add(graphComponent);
//				graphPanel.revalidate();
			}
		});
		GridBagConstraints gbc_chckbxLight = new GridBagConstraints();
		gbc_chckbxLight.anchor = GridBagConstraints.NORTH;
		gbc_chckbxLight.fill = GridBagConstraints.HORIZONTAL;
		gbc_chckbxLight.insets = new Insets(0, 0, 5, 0);
		gbc_chckbxLight.gridx = 0;
		gbc_chckbxLight.gridy = 0;
		controlPanel.add(chckbxLight, gbc_chckbxLight);
		GridBagConstraints gbc_clearButton = new GridBagConstraints();
		gbc_clearButton.fill = GridBagConstraints.BOTH;
		gbc_clearButton.gridx = 0;
		gbc_clearButton.gridy = 1;
		controlPanel.add(clearButton, gbc_clearButton);
		
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
		if(type.equals(DataType.TRIANGLES)) {
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
	
	private void clearView() {
		graphPanel.removeAll();
		graphPanel.add(lblNewLabel);
		graphPanel.revalidate();
		graphPanel.repaint();
	}

}
