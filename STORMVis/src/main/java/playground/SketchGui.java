package playground;

import inout.FileManager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import model.DataSet;
import model.ParameterSet;
import model.Project;

import org.javatuples.Pair;
import org.jzy3d.plot3d.primitives.Polygon;

import editor.DataSetTableModel;
import editor.ProjectFileFilter;
import gui.DataTypeDetector;
import gui.DataTypeDetector.DataType;
import gui.ParserWrapper;
import gui.Plotter;
import gui.TriangleLineFilter;


/**
 * @brief Sketch of GUI 
 * 
 * This is the main program that implements both plotter and calculator for STORM simulation.
 * It can load new data from the editor or parse files on the file system.
 * 
 * 
 */

public class SketchGui extends JFrame implements TableModelListener {

	private JPanel contentPane;
	private JTextField radiusOfFilamentsField;
	private JTextField labelingEfficiencyField;
	private JTextField meanAngleField;
	private JTextField backgroundLabelField;
	private JTextField labelLengthField;
	private JTextField fluorophoresPerLabelField;
	private JTextField averageBlinkingNumberField;
	private JTextField averagePhotonOutputField;
	private JTextField locPrecisionXYField;
	private JTextField locPrecisionZField;
	private JTextField psfSizeField;
	private JTextField epitopeDensityField;
	private JTextField pointSizeField;
	private JTextField colorBField;
	private JTextField colorRField;
	private JTextField colorGField;
	
	private final JLabel loadDataLabel = new JLabel("Please import data or select a representation.");
	private JTable dataSetTable;
	private DataSetTableModel model;
	private Plot3D plot;
	private JPanel plotPanel;
	private Component graphComponent;
	
	/**
	 * contains all current dataSets (displayed in table)
	 */
	private List<DataSet> allDataSets = new ArrayList<DataSet>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SketchGui frame = new SketchGui();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public SketchGui() {
		int fontSize = 16;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		setBounds(100, 100, 288, 970);
		setBounds(100, 100, 1000, 726);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
//		setContentPane(contentPane);
		contentPane.setLayout(null);
		contentPane.setPreferredSize(new Dimension(288, 970));
		Box verticalBox = Box.createVerticalBox();
		verticalBox.setName("");
		verticalBox.setFont(new Font("Dialog", Font.ITALIC, 89));
		verticalBox.setBounds(12, 190, 240, 720);
		verticalBox.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "parameters", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		verticalBox.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		verticalBox.setAlignmentX(Component.RIGHT_ALIGNMENT);
		contentPane.add(verticalBox);
		
		Box verticalBox_7 = Box.createVerticalBox();
		verticalBox_7.setBorder(new TitledBorder(null, "simulation parameters", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		verticalBox.add(verticalBox_7);
		
		Box verticalBox_5 = Box.createVerticalBox();
		verticalBox_7.add(verticalBox_5);
		
		Box verticalBox_1 = Box.createVerticalBox();
		verticalBox_5.add(verticalBox_1);
		verticalBox_1.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Structural parameters", TitledBorder.LEADING, TitledBorder.TOP, new Font("Dialog",Font.PLAIN,fontSize), null));
		
		Box horizontalBox = Box.createHorizontalBox();
		verticalBox_1.add(horizontalBox);
		
		JLabel lblNewLabel = new JLabel("Epitope density");
		horizontalBox.add(lblNewLabel);
		
		Component horizontalGlue = Box.createHorizontalGlue();
		horizontalBox.add(horizontalGlue);
		
		epitopeDensityField = new JTextField();
		epitopeDensityField.setMinimumSize(new Dimension(6, 10));
		epitopeDensityField.setMaximumSize(new Dimension(60, 22));
		epitopeDensityField.setColumns(5);
		horizontalBox.add(epitopeDensityField);
		
		Box horizontalBox_1 = Box.createHorizontalBox();
		verticalBox_1.add(horizontalBox_1);
		
		JLabel lblRadiusOfFilaments = new JLabel("radius of filaments");
		horizontalBox_1.add(lblRadiusOfFilaments);
		
		Component horizontalGlue_1 = Box.createHorizontalGlue();
		horizontalBox_1.add(horizontalGlue_1);
		
		radiusOfFilamentsField = new JTextField();
		radiusOfFilamentsField.setMinimumSize(new Dimension(6, 10));
		radiusOfFilamentsField.setMaximumSize(new Dimension(60, 22));
		radiusOfFilamentsField.setColumns(5);
		horizontalBox_1.add(radiusOfFilamentsField);
		
		Box horizontalBox_2 = Box.createHorizontalBox();
		verticalBox_1.add(horizontalBox_2);
		
		Box horizontalBox_3 = Box.createHorizontalBox();
		verticalBox_1.add(horizontalBox_3);
		
		Component verticalGlue = Box.createVerticalGlue();
		verticalBox_5.add(verticalGlue);
		
		Box verticalBox_2 = Box.createVerticalBox();
		verticalBox_5.add(verticalBox_2);
		verticalBox_2.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "structure and label dependent parameters", TitledBorder.LEADING, TitledBorder.TOP, new Font("Dialog",Font.PLAIN,fontSize), null));
		
		Box horizontalBox_4 = Box.createHorizontalBox();
		verticalBox_2.add(horizontalBox_4);
		
		JLabel lblLabelingEfficiency = new JLabel("labeling efficiency");
		horizontalBox_4.add(lblLabelingEfficiency);
		
		Component horizontalGlue_4 = Box.createHorizontalGlue();
		horizontalBox_4.add(horizontalGlue_4);
		
		labelingEfficiencyField = new JTextField();
		labelingEfficiencyField.setMinimumSize(new Dimension(6, 10));
		labelingEfficiencyField.setMaximumSize(new Dimension(60, 22));
		labelingEfficiencyField.setColumns(5);
		horizontalBox_4.add(labelingEfficiencyField);
		
		Box horizontalBox_5 = Box.createHorizontalBox();
		verticalBox_2.add(horizontalBox_5);
		
		JLabel lblMeanAngle = new JLabel("mean angle");
		horizontalBox_5.add(lblMeanAngle);
		
		Component horizontalGlue_5 = Box.createHorizontalGlue();
		horizontalBox_5.add(horizontalGlue_5);
		
		meanAngleField = new JTextField();
		meanAngleField.setMinimumSize(new Dimension(6, 10));
		meanAngleField.setMaximumSize(new Dimension(60, 22));
		meanAngleField.setColumns(5);
		horizontalBox_5.add(meanAngleField);
		
		Box horizontalBox_6 = Box.createHorizontalBox();
		verticalBox_2.add(horizontalBox_6);
		
		JLabel lblBackgroundLabel = new JLabel("background label");
		horizontalBox_6.add(lblBackgroundLabel);
		
		Component horizontalGlue_6 = Box.createHorizontalGlue();
		horizontalBox_6.add(horizontalGlue_6);
		
		backgroundLabelField = new JTextField();
		backgroundLabelField.setMinimumSize(new Dimension(6, 10));
		backgroundLabelField.setMaximumSize(new Dimension(60, 22));
		backgroundLabelField.setColumns(5);
		horizontalBox_6.add(backgroundLabelField);
		
		Box horizontalBox_7 = Box.createHorizontalBox();
		verticalBox_2.add(horizontalBox_7);
		
		Component verticalGlue_1 = Box.createVerticalGlue();
		verticalBox_5.add(verticalGlue_1);
		
		Box verticalBox_3 = Box.createVerticalBox();
		verticalBox_5.add(verticalBox_3);
		verticalBox_3.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Label dependent parameters", TitledBorder.LEADING, TitledBorder.TOP, new Font("Dialog",Font.PLAIN,fontSize), null));
		
		Box horizontalBox_8 = Box.createHorizontalBox();
		verticalBox_3.add(horizontalBox_8);
		
		JLabel lblMeanDistanceLabel = new JLabel("label length");
		horizontalBox_8.add(lblMeanDistanceLabel);
		
		Component horizontalGlue_8 = Box.createHorizontalGlue();
		horizontalBox_8.add(horizontalGlue_8);
		
		labelLengthField = new JTextField();
		labelLengthField.setMinimumSize(new Dimension(6, 10));
		labelLengthField.setMaximumSize(new Dimension(60, 22));
		labelLengthField.setColumns(5);
		horizontalBox_8.add(labelLengthField);
		
		Box horizontalBox_9 = Box.createHorizontalBox();
		verticalBox_3.add(horizontalBox_9);
		
		JLabel lblFluorophoresPerLabel = new JLabel("fluorophores per label");
		horizontalBox_9.add(lblFluorophoresPerLabel);
		
		Component horizontalGlue_9 = Box.createHorizontalGlue();
		horizontalBox_9.add(horizontalGlue_9);
		
		fluorophoresPerLabelField = new JTextField();
		fluorophoresPerLabelField.setMinimumSize(new Dimension(6, 10));
		fluorophoresPerLabelField.setMaximumSize(new Dimension(60, 22));
		fluorophoresPerLabelField.setColumns(5);
		horizontalBox_9.add(fluorophoresPerLabelField);
		
		Box horizontalBox_10 = Box.createHorizontalBox();
		verticalBox_3.add(horizontalBox_10);
		
		JLabel lblAverageBlinkingNumber = new JLabel("average blinking number");
		horizontalBox_10.add(lblAverageBlinkingNumber);
		
		Component horizontalGlue_10 = Box.createHorizontalGlue();
		horizontalBox_10.add(horizontalGlue_10);
		
		averageBlinkingNumberField = new JTextField();
		averageBlinkingNumberField.setMinimumSize(new Dimension(6, 10));
		averageBlinkingNumberField.setMaximumSize(new Dimension(60, 22));
		averageBlinkingNumberField.setColumns(5);
		horizontalBox_10.add(averageBlinkingNumberField);
		
		Box horizontalBox_11 = Box.createHorizontalBox();
		verticalBox_3.add(horizontalBox_11);
		
		JLabel lblAveragePhotonOutput = new JLabel("average photon output");
		horizontalBox_11.add(lblAveragePhotonOutput);
		
		Component horizontalGlue_11 = Box.createHorizontalGlue();
		horizontalBox_11.add(horizontalGlue_11);
		
		averagePhotonOutputField = new JTextField();
		averagePhotonOutputField.setMinimumSize(new Dimension(6, 10));
		averagePhotonOutputField.setMaximumSize(new Dimension(60, 22));
		averagePhotonOutputField.setColumns(5);
		horizontalBox_11.add(averagePhotonOutputField);
		
		Component verticalGlue_2 = Box.createVerticalGlue();
		verticalBox_5.add(verticalGlue_2);
		
		Box verticalBox_4 = Box.createVerticalBox();
		verticalBox_5.add(verticalBox_4);
		verticalBox_4.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Setup and label dependent parameters", TitledBorder.LEADING, TitledBorder.TOP, new Font("Dialog",Font.PLAIN,fontSize), null));
		
		Box horizontalBox_12 = Box.createHorizontalBox();
		verticalBox_4.add(horizontalBox_12);
		
		JLabel lblLabelingResolutionXy = new JLabel("localizaton precision xy");
		horizontalBox_12.add(lblLabelingResolutionXy);
		
		Component horizontalGlue_13 = Box.createHorizontalGlue();
		horizontalBox_12.add(horizontalGlue_13);
		
		locPrecisionXYField = new JTextField();
		locPrecisionXYField.setMinimumSize(new Dimension(6, 10));
		locPrecisionXYField.setMaximumSize(new Dimension(60, 22));
		locPrecisionXYField.setColumns(5);
		horizontalBox_12.add(locPrecisionXYField);
		
		Box horizontalBox_13 = Box.createHorizontalBox();
		verticalBox_4.add(horizontalBox_13);
		
		JLabel lblLocalizaitonPrecisionZ = new JLabel("localizaiton precision z");
		horizontalBox_13.add(lblLocalizaitonPrecisionZ);
		
		Component horizontalGlue_14 = Box.createHorizontalGlue();
		horizontalBox_13.add(horizontalGlue_14);
		
		locPrecisionZField = new JTextField();
		locPrecisionZField.setMinimumSize(new Dimension(6, 10));
		locPrecisionZField.setMaximumSize(new Dimension(60, 22));
		locPrecisionZField.setColumns(5);
		horizontalBox_13.add(locPrecisionZField);
		
		Box horizontalBox_14 = Box.createHorizontalBox();
		verticalBox_4.add(horizontalBox_14);
		
		JLabel lblMergeClosePsfs = new JLabel("Merge close PSFs");
		horizontalBox_14.add(lblMergeClosePsfs);
		
		Component horizontalGlue_17 = Box.createHorizontalGlue();
		horizontalBox_14.add(horizontalGlue_17);
		
		JCheckBox mergePSFBox = new JCheckBox("");
		horizontalBox_14.add(mergePSFBox);
		
		Component horizontalGlue_18 = Box.createHorizontalGlue();
		horizontalBox_14.add(horizontalGlue_18);
		
		Component horizontalGlue_20 = Box.createHorizontalGlue();
		horizontalBox_14.add(horizontalGlue_20);
		
		Component horizontalGlue_21 = Box.createHorizontalGlue();
		horizontalBox_14.add(horizontalGlue_21);
		
		Component horizontalGlue_22 = Box.createHorizontalGlue();
		horizontalBox_14.add(horizontalGlue_22);
		
		Component horizontalGlue_19 = Box.createHorizontalGlue();
		horizontalBox_14.add(horizontalGlue_19);
		
		Component horizontalGlue_15 = Box.createHorizontalGlue();
		horizontalBox_14.add(horizontalGlue_15);
		
		Box horizontalBox_15 = Box.createHorizontalBox();
		verticalBox_4.add(horizontalBox_15);
		
		JLabel lblPsfSize = new JLabel("PSF size");
		horizontalBox_15.add(lblPsfSize);
		
		Component horizontalGlue_16 = Box.createHorizontalGlue();
		horizontalBox_15.add(horizontalGlue_16);
		
		psfSizeField = new JTextField();
		psfSizeField.setMinimumSize(new Dimension(6, 10));
		psfSizeField.setMaximumSize(new Dimension(60, 22));
		psfSizeField.setColumns(5);
		horizontalBox_15.add(psfSizeField);
		
		JButton calcButton = new JButton("calculate");
		calcButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		calcButton.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		calcButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				calculate();
			}
		});
		verticalBox.add(calcButton);
		
		Box verticalBox_6 = Box.createVerticalBox();
		verticalBox_6.setBorder(new TitledBorder(null, "Visualization Parameter", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		verticalBox.add(verticalBox_6);
		
		Box horizontalBox_16 = Box.createHorizontalBox();
		verticalBox_6.add(horizontalBox_16);
		
		JLabel lblPointSize = new JLabel("Point size");
		horizontalBox_16.add(lblPointSize);
		
		Component horizontalGlue_2 = Box.createHorizontalGlue();
		horizontalBox_16.add(horizontalGlue_2);
		
		pointSizeField = new JTextField();
		pointSizeField.setMinimumSize(new Dimension(6, 10));
		pointSizeField.setMaximumSize(new Dimension(60, 22));
		pointSizeField.setColumns(5);
		horizontalBox_16.add(pointSizeField);
		
		Box horizontalBox_17 = Box.createHorizontalBox();
		verticalBox_6.add(horizontalBox_17);
		
		JLabel lblColorRgb = new JLabel("color rgb");
		horizontalBox_17.add(lblColorRgb);
		
		Component horizontalGlue_3 = Box.createHorizontalGlue();
		horizontalBox_17.add(horizontalGlue_3);
		
		colorRField = new JTextField();
		colorRField.setMinimumSize(new Dimension(6, 10));
		colorRField.setMaximumSize(new Dimension(60, 22));
		colorRField.setColumns(3);
		horizontalBox_17.add(colorRField);
		
		colorGField = new JTextField();
		colorGField.setMinimumSize(new Dimension(6, 10));
		colorGField.setMaximumSize(new Dimension(60, 22));
		colorGField.setColumns(3);
		horizontalBox_17.add(colorGField);
		
		colorBField = new JTextField();
		colorBField.setMinimumSize(new Dimension(6, 10));
		colorBField.setMaximumSize(new Dimension(60, 22));
		colorBField.setColumns(3);
		horizontalBox_17.add(colorBField);
		
		Box horizontalBox_18 = Box.createHorizontalBox();
		verticalBox_6.add(horizontalBox_18);
		
		JLabel lblShowStormPoints = new JLabel("show STORM Points");
		horizontalBox_18.add(lblShowStormPoints);
		
		Component horizontalGlue_7 = Box.createHorizontalGlue();
		horizontalBox_18.add(horizontalGlue_7);
		
		JCheckBox showStormPointsBox = new JCheckBox("");
		horizontalBox_18.add(showStormPointsBox);
		
		Box horizontalBox_20 = Box.createHorizontalBox();
		verticalBox_6.add(horizontalBox_20);
		
		JLabel lblShowAntibodies = new JLabel("show Antibodies");
		horizontalBox_20.add(lblShowAntibodies);
		
		Component horizontalGlue_12 = Box.createHorizontalGlue();
		horizontalBox_20.add(horizontalGlue_12);
		
		JCheckBox showAntibodiesBox = new JCheckBox("");
		horizontalBox_20.add(showAntibodiesBox);
		
		Box horizontalBox_21 = Box.createHorizontalBox();
		verticalBox_6.add(horizontalBox_21);
		
		JLabel lblShowEm = new JLabel("show EM");
		horizontalBox_21.add(lblShowEm);
		
		Component horizontalGlue_23 = Box.createHorizontalGlue();
		horizontalBox_21.add(horizontalGlue_23);
		
		JCheckBox showEmBox = new JCheckBox("");
		horizontalBox_21.add(showEmBox);
		
		JButton visButton = new JButton("visualize");
		visButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		visButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				visualize();
			}
		});
		verticalBox.add(visButton);
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(contentPane);
		JScrollPane jsp = new JScrollPane(contentPane);
		
		model = new DataSetTableModel();
        dataSetTable = new JTable(model);
        dataSetTable.getColumnModel().getColumn(0).setMinWidth(100);
        model.addTableModelListener(this);
        
		dataSetTable.setBounds(12, 12, 240, 166);
		contentPane.add(dataSetTable);
		panel.add(jsp);
		jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		getContentPane().add(panel, BorderLayout.EAST);
		
		plotPanel = new JPanel(new BorderLayout());
		plotPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		getContentPane().add(plotPanel, BorderLayout.CENTER);
		plotPanel.setLayout(new BorderLayout());
		
		loadDataLabel.setHorizontalAlignment(SwingConstants.CENTER);
		plotPanel.add(loadDataLabel, BorderLayout.CENTER);
		
		JToolBar toolBar = new JToolBar();
		getContentPane().add(toolBar, BorderLayout.NORTH);
		
		JButton importFileButton = new JButton("Import file");
		toolBar.add(importFileButton);
		importFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.setFileFilter(new TriangleLineFilter());
				chooser.setFileSelectionMode(0);
				int returnVal = chooser.showOpenDialog(getContentPane()); //replace null with your swing container
				File file;
				if(returnVal == JFileChooser.APPROVE_OPTION) {     
					proceedFileImport(chooser.getSelectedFile());
				}
			}
		});
		
		JButton importProjectButton = new JButton("Import project");
		toolBar.add(importProjectButton);
		importProjectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setAcceptAllFileFilterUsed(false);
				fc.setFileFilter(new ProjectFileFilter());
				fc.setFileSelectionMode(0);
				int returnValue = fc.showOpenDialog(getContentPane());
				if(returnValue == JFileChooser.APPROVE_OPTION) {
					String path = fc.getSelectedFile().getAbsolutePath();
					Project p = FileManager.openProjectFromFile(path);
					allDataSets.clear();
					model.data.clear();
					model.visibleSets.clear();
					allDataSets = p.dataSets;
					for(DataSet s : allDataSets) {
						model.visibleSets.add(Boolean.FALSE);
					}
					model.data.addAll(p.dataSets);
					model.fireTableDataChanged();
					
					// TODO: replace with clean implementation (booleans)
					plot.addAllDataSets(allDataSets);
					plotPanel.removeAll();
					graphComponent = (Component) plot.createChart().getCanvas();
					plotPanel.add(graphComponent);
					plotPanel.revalidate();
					plotPanel.repaint();
					graphComponent.revalidate();
				}
			}
		});
		
		plot = new Plot3D();
		configureTableListener();
		
		JButton saveProjectButton = new JButton("Save project");
		toolBar.add(saveProjectButton);
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
		DataSet data = ParserWrapper.parseFileOfType(file.getAbsolutePath(), type);
		if(data.dataType.equals(DataType.TRIANGLES)) {
			System.out.println("Triangles parsed correctly.");
		}
		else if(type.equals(DataType.LINES)) {
			System.out.println("Lines parsed correctly.");
		}
		data.setName(file.getName());
		allDataSets.add(data);
		model.data.add(data);
		model.visibleSets.add(Boolean.FALSE);
		model.fireTableDataChanged();
//		Plotter plotter = new Plotter(data, type);
//		plot.addAllDataSets(allDataSets);
//		plotPanel.removeAll();
//		graphComponent = (Component) plot.createChart().getCanvas();
//		plotPanel.add(graphComponent);
//		plotPanel.revalidate();
//		plotPanel.repaint();
//		graphComponent.revalidate();
	}

	/**
	 * Configures the mouse listener for the dataset table. 
	 * When a line is clicked, its ParameterSet is loaded to the configuration panel.
	 */
	private void configureTableListener() {
		// TODO Auto-generated method stub
		dataSetTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1) {
	        	      JTable target = (JTable)e.getSource();
	        	      int row = target.getSelectedRow();
	        	      int column = target.getSelectedColumn();
	        	      System.out.println("row/col :" + row + " | " + column);
	        	      if(column == 0) {
	        	    	  loadParameterSetOfRow(row);
	        	      }
				}
			}
		});
	
	}
	
	/**
	 * invoked by visualize button
	 */
	private void visualize() {
		
	}
	
	/**
	 * invoked by calculate button
	 */
	private void calculate() {
		
	}
	
	/**
	 * Loads the parameter set of the selected row into the configuration panel 
	 * @param row - row clicked in dataSetTable
	 *
	 */
	private void loadParameterSetOfRow(int row) {
		ParameterSet set = allDataSets.get(row).parameterSet;
//		set.loa;	     
//		set.aoa;       
//		set.bspnm;     
//		set.pabs; 		
//		set.abpf;		 
		radiusOfFilamentsField.setText(set.rof.toString());		 
//		set.fpab;      
//		set.colorEM;   
//		set.colorSTORM;
//		set.colorAB;   
		locPrecisionXYField.setText(set.sxy.toString());       
		locPrecisionZField.setText(set.sz.toString());        
//		set.doc;       
//		set.nocpsmm;   
//		set.docpsnm;   
//		set.bd;        
//		set.bspsnm;  
	}
	
	@Override
	public void tableChanged(TableModelEvent e) {
		
	}
}
