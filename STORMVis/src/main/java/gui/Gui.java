package gui;

import editor.Editor;
import gui.DataTypeDetector.DataType;
import inout.FileManager;
import inout.ProjectFileFilter;
import inout.TriangleLineFilter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.rendering.canvas.Quality;

import model.DataSet;
import model.LineDataSet;
import model.ParameterSet;
import model.Project;
import model.SerializableImage;
import model.TriangleDataSet;
import table.DataSetTableModel;
import calc.Calc;
import calc.STORMCalculator;

import javax.swing.JToggleButton;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;

import java.awt.FlowLayout;

import javax.swing.BoxLayout;

import java.awt.CardLayout;

import javax.swing.JSlider;
import javax.swing.ScrollPaneConstants;
import javax.swing.JComboBox;


/**
 * @brief Sketch of GUI 
 * 
 * This is the main program that implements both plotter and calculator for STORM simulation.
 * It can load new data from the editor or parse files on the file system.
 * 
 * 
 */

public class Gui extends JFrame implements TableModelListener,PropertyChangeListener {

	private JPanel contentPane;
	private JLabel epitopeDensityLabel;
	private JTextField radiusOfFilamentsField; //rof
	private JTextField labelingEfficiencyField; //pabs
	private JTextField meanAngleField; //aoa
	private JTextField backgroundLabelField; //ilpmm3 aus StormPointFinder
	private JTextField labelLengthField; //loa
	private JTextField fluorophoresPerLabelField; //fpab
	private JTextField kOnField; //abpf
	private JTextField averagePhotonOutputField; // 
	private JTextField locPrecisionXYField; //sxy
	private JTextField locPrecisionZField; //sz
	private JTextField psfSizeField; //psfwidth aus StormPointFinder
	private JTextField epitopeDensityField; //bspnm oder bspsnm je nachdem ob Linien oder Dreiecke
	private JTextField pointSizeField; //
	
	JLabel lblRadiusOfFilaments;
	
	private JCheckBox showEmBox;
	private JCheckBox showStormPointsBox;
	private JCheckBox showAntibodiesBox;
	private JCheckBox mergePSFBox;
	private JCheckBox applyBleachBox;
	JCheckBox coupleSigmaIntensityBox;
	
	private final JLabel loadDataLabel = new JLabel("Please import data or select a representation.");
	private JTable dataSetTable;
	private DataSetTableModel model;
	private Plot3D plot;
	private JPanel plotPanel;
	private Component graphComponent;
	private JProgressBar progressBar;
	
	private JRadioButton radioNicest;
	private JRadioButton radioAdvanced;
	private JRadioButton radioIntermediate;
	private JRadioButton radioFastest;
	
	private JComboBox exampleComboBox;
	
	public STORMCalculator calc;
	
	int fontSize = 12;
	Font usedFont = new Font("Dialog",Font.BOLD,fontSize);
	
	
	/**
	 * is set if a project with an image from the editor was loaded
	 */
	private SerializableImage loadedImage;
	
	private int currentRow = -1;
	/**
	 * contains all current dataSets (displayed in table)
	 */
	private List<DataSet> allDataSets = new ArrayList<DataSet>();
	
	
	private JButton emColorButton;
	private JButton antibodyColorButton;
	private JButton stormColorButton;
	private JCheckBox chckbxShowAxes;
	private JToggleButton saveViewpointButton;

	/**
	 * file extension for storm project files
	 */
	private static String EXTENSION = ".storm";
	private static String EXTENSIONIMAGEOUTPUT = ".tif";
	private JTextField kOffField;
	private JTextField recordedFramesField;
	private JTextField lineWidthField;
	
	final JToggleButton xyViewButton;
	final JToggleButton xzViewButton;
	final JToggleButton yzViewButton;
	
	int viewStatus = 0; // which projection is used for display and rendering
	
	float shiftX = -1;
	float shiftY = -1;
	float shiftZ = -1;
	
	public Color backgroundColor = Color.BLACK;
	public Color mainColor = Color.WHITE;
	JButton mainColorButton;
	JButton backgroundColorButton;
	JCheckBox chckbxShowTicks;
	JFileChooser chooser = new JFileChooser();
	private JTextField xminField;
	private JTextField xmaxField;
	private JTextField yminField;
	private JTextField ymaxField;
	private JTextField zminField;
	private JTextField zmaxField;
	JSlider xminSlider;
	JSlider xmaxSlider;
	JSlider yminSlider;
	JSlider ymaxSlider;
	JSlider zminSlider;
	JSlider zmaxSlider;
	float xmin =  (float) +9e99;
	float xmax =  (float) -9e99;
	float ymin =  (float) +9e99;
	float ymax =  (float) -9e99;
	float zmin =  (float) +9e99;
	float zmax =  (float) -9e99;
	JCheckBox keepBordersChkBox;
	ArrayList<Float> borders = new ArrayList<Float>();
	private JTextField bleachConstantField;
	private JTextField detectionEfficiencyField;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		 try {
	            // Set System L&F
	        UIManager.setLookAndFeel(
	            UIManager.getSystemLookAndFeelClassName());
	    } 
	    catch (UnsupportedLookAndFeelException e) {
	       // handle exception
	    }
	    catch (ClassNotFoundException e) {
	       // handle exception
	    }
	    catch (InstantiationException e) {
	       // handle exception
	    }
	    catch (IllegalAccessException e) {
	       // handle exception
	    }
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Gui frame = new Gui();
					frame.setTitle("STORMVis");
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
	public Gui() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(10, 10, 1200, 1000);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setPreferredSize(new Dimension(250, 900));
		
		model = new DataSetTableModel();
        model.addTableModelListener(this);
		
		JPanel panel = new JPanel();
		panel.setLayout(new CardLayout(0, 0));
		panel.add(contentPane);
		JScrollPane jsp = new JScrollPane(contentPane);
		jsp.setMinimumSize(new Dimension(27, 400));
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));
		
		Box verticalBox_13 = Box.createVerticalBox();
		contentPane.add(verticalBox_13);
		dataSetTable = new JTable(model);
		dataSetTable.setMinimumSize(new Dimension(30, 150));
		dataSetTable.setPreferredSize(new Dimension(130, 120));
		verticalBox_13.add(dataSetTable);
		
		Box verticalBox = Box.createVerticalBox();
		verticalBox.setMaximumSize(new Dimension(222222, 222220));
		verticalBox_13.add(verticalBox);
		verticalBox.setPreferredSize(new Dimension(290, 900));
		verticalBox.setMinimumSize(new Dimension(290, 600));
		verticalBox.setName("");
		verticalBox.setFont(new Font("Dialog", Font.ITALIC, 89));
		verticalBox.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		verticalBox.add(tabbedPane);
				
		Box verticalBox_7 = Box.createVerticalBox();
		tabbedPane.addTab("Basic Settings", null, verticalBox_7, null);
		
		Box verticalBox_5 = Box.createVerticalBox();
		verticalBox_7.add(verticalBox_5);
		
		Box verticalBox_1 = Box.createVerticalBox();
		verticalBox_5.add(verticalBox_1);
		verticalBox_1.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Structural parameters", TitledBorder.LEADING, TitledBorder.TOP, usedFont, null));
		
		Box horizontalBox = Box.createHorizontalBox();
		verticalBox_1.add(horizontalBox);
		
		epitopeDensityLabel = new JLabel("<html>Epitope Density (nm<sup>-2</sup>)</html>");
		horizontalBox.add(epitopeDensityLabel);
		
		Component horizontalGlue = Box.createHorizontalGlue();
		horizontalBox.add(horizontalGlue);
		
		epitopeDensityField = new JTextField();
		epitopeDensityField.setHorizontalAlignment(SwingConstants.RIGHT);
		epitopeDensityField.setMinimumSize(new Dimension(6, 10));
		epitopeDensityField.setMaximumSize(new Dimension(60, 22));
		epitopeDensityField.setColumns(5);
		horizontalBox.add(epitopeDensityField);
		//horizontalBox.setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{lblNewLabel, horizontalGlue, epitopeDensityField}));
		
		Component verticalGlue_4 = Box.createVerticalGlue();
		verticalBox_1.add(verticalGlue_4);
		
		Box horizontalBox_1 = Box.createHorizontalBox();
		verticalBox_1.add(horizontalBox_1);
		
		lblRadiusOfFilaments = new JLabel("Radius Of Filaments (nm)");
		horizontalBox_1.add(lblRadiusOfFilaments);
		
		Component horizontalGlue_1 = Box.createHorizontalGlue();
		horizontalBox_1.add(horizontalGlue_1);
		
		radiusOfFilamentsField = new JTextField();
		radiusOfFilamentsField.setHorizontalAlignment(SwingConstants.RIGHT);
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
		verticalBox_2.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Labeling Efficiency", TitledBorder.LEADING, TitledBorder.TOP, usedFont, new Color(0, 0, 0)));
		
		Box horizontalBox_4 = Box.createHorizontalBox();
		verticalBox_2.add(horizontalBox_4);
		
		JLabel lblLabelingEfficiency = new JLabel("Labeling Efficiency (%)");
		horizontalBox_4.add(lblLabelingEfficiency);
		
		Component horizontalGlue_4 = Box.createHorizontalGlue();
		horizontalBox_4.add(horizontalGlue_4);
		
		labelingEfficiencyField = new JTextField();
		labelingEfficiencyField.setHorizontalAlignment(SwingConstants.RIGHT);
		labelingEfficiencyField.setMinimumSize(new Dimension(6, 10));
		labelingEfficiencyField.setMaximumSize(new Dimension(60, 22));
		labelingEfficiencyField.setColumns(5);
		horizontalBox_4.add(labelingEfficiencyField);
		
		Component verticalGlue_5 = Box.createVerticalGlue();
		verticalBox_2.add(verticalGlue_5);
		
		Box horizontalBox_7 = Box.createHorizontalBox();
		verticalBox_2.add(horizontalBox_7);
		
		Component verticalGlue_1 = Box.createVerticalGlue();
		verticalBox_5.add(verticalGlue_1);
		
		Box verticalBox_3 = Box.createVerticalBox();
		verticalBox_5.add(verticalBox_3);
		verticalBox_3.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Label Dependent Parameters", TitledBorder.LEADING, TitledBorder.TOP, usedFont, new Color(0, 0, 0)));
		
		Box horizontalBox_8 = Box.createHorizontalBox();
		verticalBox_3.add(horizontalBox_8);
		
		JLabel lblMeanDistanceLabel = new JLabel("Label Epitope Distance (nm)");
		horizontalBox_8.add(lblMeanDistanceLabel);
		
		Component horizontalGlue_8 = Box.createHorizontalGlue();
		horizontalBox_8.add(horizontalGlue_8);
		
		labelLengthField = new JTextField();
		labelLengthField.setHorizontalAlignment(SwingConstants.RIGHT);
		labelLengthField.setMinimumSize(new Dimension(6, 10));
		labelLengthField.setMaximumSize(new Dimension(60, 22));
		labelLengthField.setColumns(5);
		horizontalBox_8.add(labelLengthField);
		
		Component verticalGlue_7 = Box.createVerticalGlue();
		verticalBox_3.add(verticalGlue_7);
		
		Box horizontalBox_9 = Box.createHorizontalBox();
		verticalBox_3.add(horizontalBox_9);
		
		JLabel lblFluorophoresPerLabel = new JLabel("Fluorophores Per Label");
		horizontalBox_9.add(lblFluorophoresPerLabel);
		
		Component horizontalGlue_9 = Box.createHorizontalGlue();
		horizontalBox_9.add(horizontalGlue_9);
		
		fluorophoresPerLabelField = new JTextField();
		fluorophoresPerLabelField.setHorizontalAlignment(SwingConstants.RIGHT);
		fluorophoresPerLabelField.setMinimumSize(new Dimension(6, 10));
		fluorophoresPerLabelField.setMaximumSize(new Dimension(60, 22));
		fluorophoresPerLabelField.setColumns(5);
		horizontalBox_9.add(fluorophoresPerLabelField);
		
		Component verticalGlue_8 = Box.createVerticalGlue();
		verticalBox_3.add(verticalGlue_8);
		
		Component verticalGlue_2 = Box.createVerticalGlue();
		verticalBox_5.add(verticalGlue_2);
		
		Box verticalBox_4 = Box.createVerticalBox();
		verticalBox_5.add(verticalBox_4);
		verticalBox_4.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Setup And Label Dependent Parameters", TitledBorder.LEADING, TitledBorder.TOP, usedFont, new Color(0, 0, 0)));
		
		Box horizontalBox_12 = Box.createHorizontalBox();
		verticalBox_4.add(horizontalBox_12);
		
		JLabel lblLabelingResolutionXy = new JLabel("Localization Precision XY (nm)");
		horizontalBox_12.add(lblLabelingResolutionXy);
		
		Component horizontalGlue_13 = Box.createHorizontalGlue();
		horizontalBox_12.add(horizontalGlue_13);
		
		locPrecisionXYField = new JTextField();
		locPrecisionXYField.setHorizontalAlignment(SwingConstants.RIGHT);
		locPrecisionXYField.setMinimumSize(new Dimension(6, 10));
		locPrecisionXYField.setMaximumSize(new Dimension(60, 22));
		locPrecisionXYField.setColumns(5);
		horizontalBox_12.add(locPrecisionXYField);
		
		Component verticalGlue_12 = Box.createVerticalGlue();
		verticalBox_4.add(verticalGlue_12);
		
		Box horizontalBox_13 = Box.createHorizontalBox();
		verticalBox_4.add(horizontalBox_13);
		
		JLabel lblLocalizaitonPrecisionZ = new JLabel("Localization Precision Z (nm)");
		horizontalBox_13.add(lblLocalizaitonPrecisionZ);
		
		Component horizontalGlue_14 = Box.createHorizontalGlue();
		horizontalBox_13.add(horizontalGlue_14);
		
		locPrecisionZField = new JTextField();
		locPrecisionZField.setHorizontalAlignment(SwingConstants.RIGHT);
		locPrecisionZField.setMinimumSize(new Dimension(6, 10));
		locPrecisionZField.setMaximumSize(new Dimension(60, 22));
		locPrecisionZField.setColumns(5);
		horizontalBox_13.add(locPrecisionZField);
		
		Component verticalGlue_13 = Box.createVerticalGlue();
		verticalBox_4.add(verticalGlue_13);
		
		Box horizontalBox_22 = Box.createHorizontalBox();
		verticalBox_4.add(horizontalBox_22);
		
		JLabel lblRecordedFrames = new JLabel("Recorded Frames");
		horizontalBox_22.add(lblRecordedFrames);
		
		Component horizontalGlue_26 = Box.createHorizontalGlue();
		horizontalBox_22.add(horizontalGlue_26);
		
		recordedFramesField = new JTextField();
		recordedFramesField.setHorizontalAlignment(SwingConstants.RIGHT);
		recordedFramesField.setMinimumSize(new Dimension(6, 10));
		recordedFramesField.setMaximumSize(new Dimension(60, 22));
		recordedFramesField.setColumns(5);
		horizontalBox_22.add(recordedFramesField);
		
		Box verticalBox_9 = Box.createVerticalBox();
		tabbedPane.addTab("Advanced Settings", null, verticalBox_9, null);
		
		Box verticalBox_10 = Box.createVerticalBox();
		verticalBox_10.setBorder(new TitledBorder(null, "Label Dependent Settings", TitledBorder.LEADING, TitledBorder.TOP, usedFont, null));
		verticalBox_9.add(verticalBox_10);
		
		Box horizontalBox_5 = Box.createHorizontalBox();
		verticalBox_10.add(horizontalBox_5);
		
		JLabel lblMeanAngle = new JLabel("Binding Angle (Degree)");
		horizontalBox_5.add(lblMeanAngle);
		
		Component horizontalGlue_5 = Box.createHorizontalGlue();
		horizontalBox_5.add(horizontalGlue_5);
		
		meanAngleField = new JTextField();
		meanAngleField.setHorizontalAlignment(SwingConstants.RIGHT);
		meanAngleField.setMinimumSize(new Dimension(6, 10));
		meanAngleField.setMaximumSize(new Dimension(60, 22));
		meanAngleField.setColumns(5);
		horizontalBox_5.add(meanAngleField);
		
		Component verticalGlue_6 = Box.createVerticalGlue();
		verticalBox_9.add(verticalGlue_6);
		
		Box verticalBox_12 = Box.createVerticalBox();
		verticalBox_12.setBorder(new TitledBorder(null, "Fluorophore Dependent Settings", TitledBorder.LEADING, TitledBorder.TOP, usedFont, null));
		verticalBox_9.add(verticalBox_12);
		
		Box horizontalBox_10 = Box.createHorizontalBox();
		verticalBox_12.add(horizontalBox_10);
		
		JLabel lblAverageBlinkingNumber = new JLabel("k_on Constant");
		horizontalBox_10.add(lblAverageBlinkingNumber);
		
		Component horizontalGlue_10 = Box.createHorizontalGlue();
		horizontalBox_10.add(horizontalGlue_10);
		
		kOnField = new JTextField();
		kOnField.setHorizontalAlignment(SwingConstants.RIGHT);
		kOnField.setMinimumSize(new Dimension(6, 10));
		kOnField.setMaximumSize(new Dimension(60, 22));
		kOnField.setColumns(5);
		horizontalBox_10.add(kOnField);
		
		Component verticalGlue_9 = Box.createVerticalGlue();
		verticalBox_12.add(verticalGlue_9);
		
		Box horizontalBox_17 = Box.createHorizontalBox();
		verticalBox_12.add(horizontalBox_17);
		
		JLabel lblKOnTime = new JLabel("k_off Constant");
		horizontalBox_17.add(lblKOnTime);
		
		Component horizontalGlue_3 = Box.createHorizontalGlue();
		horizontalBox_17.add(horizontalGlue_3);
		
		kOffField = new JTextField();
		kOffField.setHorizontalAlignment(SwingConstants.RIGHT);
		kOffField.setMinimumSize(new Dimension(6, 10));
		kOffField.setMaximumSize(new Dimension(60, 22));
		kOffField.setColumns(5);
		horizontalBox_17.add(kOffField);
		
		Component verticalGlue_10 = Box.createVerticalGlue();
		verticalBox_12.add(verticalGlue_10);
		
		Box horizontalBox_29 = Box.createHorizontalBox();
		verticalBox_12.add(horizontalBox_29);
		
		JLabel lblAllowBleaching = new JLabel("Allow Bleaching");
		horizontalBox_29.add(lblAllowBleaching);
		
		applyBleachBox = new JCheckBox("");
		applyBleachBox.setHorizontalAlignment(SwingConstants.TRAILING);
		horizontalBox_29.add(applyBleachBox);
		
		Component horizontalGlue_20 = Box.createHorizontalGlue();
		horizontalBox_29.add(horizontalGlue_20);
		
		Component horizontalGlue_21 = Box.createHorizontalGlue();
		horizontalBox_29.add(horizontalGlue_21);
		
		JLabel lblBleachConstant = new JLabel("Bleach Constant ");
		horizontalBox_29.add(lblBleachConstant);
		
		Component horizontalGlue_19 = Box.createHorizontalGlue();
		horizontalBox_29.add(horizontalGlue_19);
		
		bleachConstantField = new JTextField();
		bleachConstantField.setMinimumSize(new Dimension(6, 10));
		bleachConstantField.setMaximumSize(new Dimension(60, 22));
		bleachConstantField.setHorizontalAlignment(SwingConstants.RIGHT);
		bleachConstantField.setColumns(5);
		horizontalBox_29.add(bleachConstantField);
		
		Component verticalGlue_30 = Box.createVerticalGlue();
		verticalBox_12.add(verticalGlue_30);
		
		Component verticalGlue_11 = Box.createVerticalGlue();
		verticalBox_12.add(verticalGlue_11);
		
		Box horizontalBox_11 = Box.createHorizontalBox();
		verticalBox_12.add(horizontalBox_11);
		
		JLabel lblAveragePhotonOutput = new JLabel("Median Photon Output");
		horizontalBox_11.add(lblAveragePhotonOutput);
		
		Component horizontalGlue_11 = Box.createHorizontalGlue();
		horizontalBox_11.add(horizontalGlue_11);
		
		averagePhotonOutputField = new JTextField();
		averagePhotonOutputField.setHorizontalAlignment(SwingConstants.RIGHT);
		averagePhotonOutputField.setMinimumSize(new Dimension(6, 10));
		averagePhotonOutputField.setMaximumSize(new Dimension(60, 22));
		averagePhotonOutputField.setColumns(5);
		horizontalBox_11.add(averagePhotonOutputField);
		
		Component verticalGlue_19 = Box.createVerticalGlue();
		verticalBox_12.add(verticalGlue_19);
		
		Box horizontalBox_19 = Box.createHorizontalBox();
		verticalBox_12.add(horizontalBox_19);
		
		JLabel lblNewLabel_5 = new JLabel("Couple Loc. Precision And Intensity ");
		horizontalBox_19.add(lblNewLabel_5);
		
		Component horizontalGlue_37 = Box.createHorizontalGlue();
		horizontalGlue_37.setSize(new Dimension(10, 0));
		horizontalBox_19.add(horizontalGlue_37);
		
		coupleSigmaIntensityBox = new JCheckBox("");
		coupleSigmaIntensityBox.setSelected(true);
		horizontalBox_19.add(coupleSigmaIntensityBox);
		
		Component horizontalGlue_18 = Box.createHorizontalGlue();
		horizontalBox_19.add(horizontalGlue_18);
		
		Box verticalBox_11 = Box.createVerticalBox();
		verticalBox_11.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Background And Distortions", TitledBorder.LEADING, TitledBorder.TOP, usedFont, new Color(0, 0, 0)));
		verticalBox_9.add(verticalBox_11);
		
		Box horizontalBox_6 = Box.createHorizontalBox();
		verticalBox_11.add(horizontalBox_6);
		
		JLabel lblBackgroundLabel = new JLabel("<html>Background Label (&mu;m<sup>-3</sup>)</html>");
		horizontalBox_6.add(lblBackgroundLabel);
		
		Component horizontalGlue_6 = Box.createHorizontalGlue();
		horizontalBox_6.add(horizontalGlue_6);
		
		backgroundLabelField = new JTextField();
		horizontalBox_6.add(backgroundLabelField);
		backgroundLabelField.setHorizontalAlignment(SwingConstants.RIGHT);
		backgroundLabelField.setMinimumSize(new Dimension(6, 10));
		backgroundLabelField.setMaximumSize(new Dimension(60, 22));
		backgroundLabelField.setColumns(5);
		
		Component verticalGlue_15 = Box.createVerticalGlue();
		verticalBox_11.add(verticalGlue_15);
		
		Box horizontalBox_15 = Box.createHorizontalBox();
		verticalBox_11.add(horizontalBox_15);
		
		JLabel lblPsfSize = new JLabel("FWHM Of PSF (nm)");
		horizontalBox_15.add(lblPsfSize);
		
		Component horizontalGlue_16 = Box.createHorizontalGlue();
		horizontalBox_15.add(horizontalGlue_16);
		
		psfSizeField = new JTextField();
		psfSizeField.setHorizontalAlignment(SwingConstants.RIGHT);
		psfSizeField.setMinimumSize(new Dimension(6, 10));
		psfSizeField.setMaximumSize(new Dimension(60, 22));
		psfSizeField.setColumns(5);
		horizontalBox_15.add(psfSizeField);
		
		Component verticalGlue_18 = Box.createVerticalGlue();
		verticalBox_11.add(verticalGlue_18);
		
		Box horizontalBox_14 = Box.createHorizontalBox();
		verticalBox_11.add(horizontalBox_14);
		
		JLabel lblMergeClosePsfs = new JLabel("Merge Close PSFs");
		horizontalBox_14.add(lblMergeClosePsfs);
		
		Component horizontalGlue_17 = Box.createHorizontalGlue();
		horizontalBox_14.add(horizontalGlue_17);
		
		mergePSFBox = new JCheckBox("");
		horizontalBox_14.add(mergePSFBox);
		
		Component horizontalGlue_15 = Box.createHorizontalGlue();
		horizontalGlue_15.setPreferredSize(new Dimension(100, 0));
		horizontalBox_14.add(horizontalGlue_15);
		
		Component verticalGlue_31 = Box.createVerticalGlue();
		verticalBox_11.add(verticalGlue_31);
		
		Box horizontalBox_38 = Box.createHorizontalBox();
		verticalBox_11.add(horizontalBox_38);
		
		JLabel lblDetectionEfficiency = new JLabel("Detection Efficiency (%)");
		horizontalBox_38.add(lblDetectionEfficiency);
		
		Component horizontalGlue_22 = Box.createHorizontalGlue();
		horizontalBox_38.add(horizontalGlue_22);
		
		detectionEfficiencyField = new JTextField();
		detectionEfficiencyField.setMinimumSize(new Dimension(6, 10));
		detectionEfficiencyField.setMaximumSize(new Dimension(60, 22));
		detectionEfficiencyField.setHorizontalAlignment(SwingConstants.RIGHT);
		detectionEfficiencyField.setColumns(5);
		horizontalBox_38.add(detectionEfficiencyField);
		
		JButton calcButton = new JButton("Calculate");
		calcButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		calcButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				calculate();
			}
		});
		
		progressBar = new JProgressBar();
		verticalBox.add(progressBar);
		progressBar.setStringPainted(true);
		
		Component verticalGlue_21 = Box.createVerticalGlue();
		verticalBox.add(verticalGlue_21);
		verticalBox.add(calcButton);
		
		Component verticalGlue_16 = Box.createVerticalGlue();
		verticalBox.add(verticalGlue_16);
		
		Box verticalBox_6 = Box.createVerticalBox();
		verticalBox_6.setBorder(new TitledBorder(null, "Visualization Parameter", TitledBorder.LEADING, TitledBorder.TOP, usedFont, null));
		verticalBox.add(verticalBox_6);
		
		JTabbedPane tabbedPane_1 = new JTabbedPane(JTabbedPane.TOP);
		verticalBox_6.add(tabbedPane_1);
		
		Box verticalBox_16 = Box.createVerticalBox();
		tabbedPane_1.addTab("Basic Settings", null, verticalBox_16, null);
		
		Box horizontalBox_16 = Box.createHorizontalBox();
		verticalBox_16.add(horizontalBox_16);
		
		JLabel lblPointSize = new JLabel("Point Size");
		horizontalBox_16.add(lblPointSize);
		
		Component horizontalGlue_2 = Box.createHorizontalGlue();
		horizontalBox_16.add(horizontalGlue_2);
		
		pointSizeField = new JTextField();
		pointSizeField.setHorizontalAlignment(SwingConstants.RIGHT);
		pointSizeField.setMinimumSize(new Dimension(6, 10));
		pointSizeField.setMaximumSize(new Dimension(60, 22));
		pointSizeField.setColumns(5);
		horizontalBox_16.add(pointSizeField);
		
		Component verticalGlue_20 = Box.createVerticalGlue();
		verticalBox_16.add(verticalGlue_20);
		
		Box horizontalBox_24 = Box.createHorizontalBox();
		verticalBox_16.add(horizontalBox_24);
		
		JLabel lblLineWidth = new JLabel("Line Width");
		horizontalBox_24.add(lblLineWidth);
		
		Component horizontalGlue_31 = Box.createHorizontalGlue();
		horizontalBox_24.add(horizontalGlue_31);
		
		lineWidthField = new JTextField();
		lineWidthField.setMinimumSize(new Dimension(6, 10));
		lineWidthField.setMaximumSize(new Dimension(60, 22));
		lineWidthField.setHorizontalAlignment(SwingConstants.RIGHT);
		lineWidthField.setColumns(5);
		horizontalBox_24.add(lineWidthField);
		
		Component verticalGlue_17 = Box.createVerticalGlue();
		verticalBox_16.add(verticalGlue_17);
		
		Box horizontalBox_18 = Box.createHorizontalBox();
		verticalBox_16.add(horizontalBox_18);
		
		JLabel lblShowStormPoints = new JLabel("Show STORM Points");
		horizontalBox_18.add(lblShowStormPoints);
		
		Component horizontalGlue_7 = Box.createHorizontalGlue();
		horizontalBox_18.add(horizontalGlue_7);
		
		stormColorButton = new JButton("");
		horizontalBox_18.add(stormColorButton);
		stormColorButton.setMinimumSize(new Dimension(33, 20));
		stormColorButton.setMaximumSize(new Dimension(33, 20));
		stormColorButton.setPreferredSize(new Dimension(40, 20));
		
		showStormPointsBox = new JCheckBox("");
		showStormPointsBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				allDataSets.get(currentRow).getParameterSet().setStormVisibility(showStormPointsBox.isSelected());
				visualize();
			}
		});
		horizontalBox_18.add(showStormPointsBox);
		
		Box horizontalBox_20 = Box.createHorizontalBox();
		verticalBox_16.add(horizontalBox_20);
		
		JLabel lblShowAntibodies = new JLabel("Show Antibodies");
		horizontalBox_20.add(lblShowAntibodies);
		
		Component horizontalGlue_12 = Box.createHorizontalGlue();
		horizontalBox_20.add(horizontalGlue_12);
		
		antibodyColorButton = new JButton("");
		antibodyColorButton.setMaximumSize(new Dimension(33, 20));
		antibodyColorButton.setMinimumSize(new Dimension(33, 20));
		antibodyColorButton.setPreferredSize(new Dimension(40,20));
		antibodyColorButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				allDataSets.get(currentRow).getParameterSet().setAntibodyColor(JColorChooser.showDialog(getContentPane(), "Choose color for antibodies", allDataSets.get(currentRow).getParameterSet().getAntibodyColor()));
				updateButtonColors();
			}
		});
		horizontalBox_20.add(antibodyColorButton);
		
		showAntibodiesBox = new JCheckBox("");
		showAntibodiesBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				allDataSets.get(currentRow).getParameterSet().setAntibodyVisibility(showAntibodiesBox.isSelected());
				visualize();
			}
		});
		horizontalBox_20.add(showAntibodiesBox);
		
		Box horizontalBox_21 = Box.createHorizontalBox();
		verticalBox_16.add(horizontalBox_21);
		
		JLabel lblShowEm = new JLabel("Show EM");
		horizontalBox_21.add(lblShowEm);
		
		Component horizontalGlue_23 = Box.createHorizontalGlue();
		horizontalBox_21.add(horizontalGlue_23);
		
		emColorButton = new JButton("");
		horizontalBox_21.add(emColorButton);
		emColorButton.setMinimumSize(new Dimension(33, 20));
		emColorButton.setMaximumSize(new Dimension(33, 20));
		emColorButton.setPreferredSize(new Dimension(40, 20));
		
		showEmBox = new JCheckBox("");
		horizontalBox_21.add(showEmBox);
		showEmBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				allDataSets.get(currentRow).getParameterSet().setEmVisibility(showEmBox.isSelected());
				visualize();
			}
		});
		emColorButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				allDataSets.get(currentRow).getParameterSet().setEmColor(JColorChooser.showDialog(getContentPane(), "Choose color for EM", allDataSets.get(currentRow).getParameterSet().getEmColor()));
				updateButtonColors();
			}
		});
		
		Component verticalGlue_22 = Box.createVerticalGlue();
		verticalBox_16.add(verticalGlue_22);
		
		Box verticalBox_14 = Box.createVerticalBox();
		verticalBox_16.add(verticalBox_14);
		
		Box horizontalBox_25 = Box.createHorizontalBox();
		verticalBox_14.add(horizontalBox_25);
		
		chckbxShowAxes = new JCheckBox("Show Axes");
		chckbxShowAxes.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				visualize();
			}
		});
		chckbxShowAxes.setSelected(true);
		horizontalBox_25.add(chckbxShowAxes);
		chckbxShowAxes.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		chckbxShowTicks = new JCheckBox("Show Ticks");
		chckbxShowTicks.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				visualize();
			}
		});
		chckbxShowTicks.setSelected(true);
		chckbxShowTicks.setAlignmentX(1.0f);
		horizontalBox_25.add(chckbxShowTicks);
		
		Component horizontalGlue_32 = Box.createHorizontalGlue();
		horizontalBox_25.add(horizontalGlue_32);
		
		Box horizontalBox_26 = Box.createHorizontalBox();
		verticalBox_14.add(horizontalBox_26);
		
		Component horizontalGlue_33 = Box.createHorizontalGlue();
		horizontalBox_26.add(horizontalGlue_33);
		
		Box horizontalBox_27 = Box.createHorizontalBox();
		verticalBox_16.add(horizontalBox_27);
		
		JLabel lblBackgroundColor = new JLabel("Background Color");
		horizontalBox_27.add(lblBackgroundColor);
		
		Component horizontalGlue_34 = Box.createHorizontalGlue();
		horizontalBox_27.add(horizontalGlue_34);
		
		backgroundColorButton = new JButton("");
		backgroundColorButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				backgroundColor = JColorChooser.showDialog(getContentPane(), "Choose color for background", backgroundColor);
				//backgroundColor = new org.jzy3d.colors.Color(chosenColor.getRed(), chosenColor.getGreen(), chosenColor.getBlue());
				updateButtonColors();
			}
		});
		backgroundColorButton.setPreferredSize(new Dimension(40, 20));
		backgroundColorButton.setMinimumSize(new Dimension(33, 20));
		backgroundColorButton.setMaximumSize(new Dimension(33, 20));
		horizontalBox_27.add(backgroundColorButton);
		
		Component verticalGlue_23 = Box.createVerticalGlue();
		verticalBox_16.add(verticalGlue_23);
		
		Box horizontalBox_28 = Box.createHorizontalBox();
		verticalBox_16.add(horizontalBox_28);
		
		JLabel lblColorOfAxis = new JLabel("Color Of Axes");
		horizontalBox_28.add(lblColorOfAxis);
		
		Component horizontalGlue_35 = Box.createHorizontalGlue();
		horizontalBox_28.add(horizontalGlue_35);
		
		mainColorButton = new JButton("");
		mainColorButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainColor = JColorChooser.showDialog(getContentPane(), "Choose color for background", mainColor);
				//backgroundColor = new org.jzy3d.colors.Color(chosenColor.getRed(), chosenColor.getGreen(), chosenColor.getBlue());
				updateButtonColors();
			}
		});
		mainColorButton.setPreferredSize(new Dimension(40, 20));
		mainColorButton.setMinimumSize(new Dimension(33, 20));
		mainColorButton.setMaximumSize(new Dimension(33, 20));
		horizontalBox_28.add(mainColorButton);
		
		Box verticalBox_17 = Box.createVerticalBox();
		tabbedPane_1.addTab("Advanced Settings", null, verticalBox_17, null);
		
		Box horizontalBox_30 = Box.createHorizontalBox();
		verticalBox_17.add(horizontalBox_30);
		
				
				Box verticalBox_8 = Box.createVerticalBox();
				horizontalBox_30.add(verticalBox_8);
				verticalBox_8.setAlignmentX(Component.CENTER_ALIGNMENT);
				
				JLabel lblRenderingQuality = new JLabel("Rendering Quality");
				verticalBox_8.add(lblRenderingQuality);
				
				
				radioNicest = new JRadioButton("Nicest");
				verticalBox_8.add(radioNicest);
				radioNicest.setSelected(true);
				
				radioAdvanced = new JRadioButton("Advanced");
				verticalBox_8.add(radioAdvanced);
				
				radioIntermediate = new JRadioButton("Intermediate");
				verticalBox_8.add(radioIntermediate);
				
				radioFastest = new JRadioButton("Fastest");
				verticalBox_8.add(radioFastest);
				ButtonGroup group = new ButtonGroup();
				group.add(radioNicest);
				group.add(radioAdvanced);
				group.add(radioIntermediate);
				group.add(radioFastest);
				
				Component verticalGlue_24 = Box.createVerticalGlue();
				verticalBox_8.add(verticalGlue_24);
				
				keepBordersChkBox = new JCheckBox("Keep Borders");
				verticalBox_8.add(keepBordersChkBox);
				
				Component horizontalGlue_36 = Box.createHorizontalGlue();
				horizontalBox_30.add(horizontalGlue_36);
				
				Box verticalBox_15 = Box.createVerticalBox();
				horizontalBox_30.add(verticalBox_15);
				
				JLabel lblNewLabel = new JLabel("x min (nm)");
				verticalBox_15.add(lblNewLabel);
				
				Box horizontalBox_31 = Box.createHorizontalBox();
				verticalBox_15.add(horizontalBox_31);
				
				xminField = new JTextField();
				xminField.setHorizontalAlignment(SwingConstants.RIGHT);
				xminField.setText("0");
				xminField.setMaximumSize(new Dimension(60, 22));
				xminField.addActionListener(new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent e) {
						updateSlider(Float.parseFloat(xminField.getText()),xminSlider);
					}
					
				});
				horizontalBox_31.add(xminField);
				xminField.setColumns(10);
				
				xminSlider = new JSlider();
				xminSlider.setValue(0);
				xminSlider.addChangeListener(new ChangeListener() { 
					@Override
					public void stateChanged(ChangeEvent arg0) {
						xminField.setText(String.valueOf(xminSlider.getValue()));
						visualize();
					}
				});
				
				horizontalBox_31.add(xminSlider);
				
				Component verticalGlue_25 = Box.createVerticalGlue();
				verticalBox_15.add(verticalGlue_25);
				
				JLabel lblNewLabel_1 = new JLabel("x max (nm)");
				verticalBox_15.add(lblNewLabel_1);
				
				Box horizontalBox_32 = Box.createHorizontalBox();
				verticalBox_15.add(horizontalBox_32);
				
				xmaxField = new JTextField();
				xmaxField.setHorizontalAlignment(SwingConstants.RIGHT);
				xmaxField.setText("0");
				xmaxField.setMaximumSize(new Dimension(60, 22));
				xmaxField.setColumns(10);
				xmaxField.addActionListener(new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent e) {
						updateSlider(Float.parseFloat(xmaxField.getText()),xmaxSlider);
					}
					
				});
				horizontalBox_32.add(xmaxField);
				
				xmaxSlider = new JSlider();
				xmaxSlider.setValue(100);
				xmaxSlider.addChangeListener(new ChangeListener() { 
					@Override
					public void stateChanged(ChangeEvent arg0) {
						xmaxField.setText(String.valueOf(xmaxSlider.getValue()));
						visualize();
					}
				});
				horizontalBox_32.add(xmaxSlider);
				
				Component verticalGlue_26 = Box.createVerticalGlue();
				verticalBox_15.add(verticalGlue_26);
				
				JLabel lblNewLabel_2 = new JLabel("y min (nm)");
				verticalBox_15.add(lblNewLabel_2);
				
				Box horizontalBox_33 = Box.createHorizontalBox();
				verticalBox_15.add(horizontalBox_33);
				
				yminField = new JTextField();
				yminField.setHorizontalAlignment(SwingConstants.RIGHT);
				yminField.setText("0");
				yminField.setMaximumSize(new Dimension(60, 22));
				yminField.setColumns(10);
				yminField.addActionListener(new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent e) {
						updateSlider(Float.parseFloat(yminField.getText()),yminSlider);
					}
					
				});
				horizontalBox_33.add(yminField);
				
				yminSlider = new JSlider();
				yminSlider.setValue(0);
				yminSlider.addChangeListener(new ChangeListener() { 
					@Override
					public void stateChanged(ChangeEvent arg0) {
						yminField.setText(String.valueOf(yminSlider.getValue()));
						visualize();
					}
				});
				horizontalBox_33.add(yminSlider);
				
				Component verticalGlue_27 = Box.createVerticalGlue();
				verticalBox_15.add(verticalGlue_27);
				
				JLabel lblNewLabel_3 = new JLabel("y max (nm)");
				verticalBox_15.add(lblNewLabel_3);
				
				Box horizontalBox_34 = Box.createHorizontalBox();
				verticalBox_15.add(horizontalBox_34);
				
				ymaxField = new JTextField();
				ymaxField.setText("0");
				ymaxField.setHorizontalAlignment(SwingConstants.RIGHT);
				ymaxField.setMaximumSize(new Dimension(60, 22));
				ymaxField.setColumns(10);
				ymaxField.addActionListener(new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent e) {
						updateSlider(Float.parseFloat(ymaxField.getText()),ymaxSlider);
					}
					
				});
				horizontalBox_34.add(ymaxField);
				
				ymaxSlider = new JSlider();
				ymaxSlider.setValue(100);
				ymaxSlider.addChangeListener(new ChangeListener() { 
					@Override
					public void stateChanged(ChangeEvent arg0) {
						ymaxField.setText(String.valueOf(ymaxSlider.getValue()));
						visualize();
					}
				});
				horizontalBox_34.add(ymaxSlider);
				
				Component verticalGlue_28 = Box.createVerticalGlue();
				verticalBox_15.add(verticalGlue_28);
				
				JLabel lblNewLabel_4 = new JLabel("z min (nm)");
				verticalBox_15.add(lblNewLabel_4);
				
				Box horizontalBox_35 = Box.createHorizontalBox();
				verticalBox_15.add(horizontalBox_35);
				
				zminField = new JTextField();
				zminField.setHorizontalAlignment(SwingConstants.RIGHT);
				zminField.setText("0");
				zminField.setMaximumSize(new Dimension(60, 22));
				zminField.setColumns(10);
				zminField.addActionListener(new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent e) {
						updateSlider(Float.parseFloat(zminField.getText()),zminSlider);
					}
					
				});
				horizontalBox_35.add(zminField);
				
				zminSlider = new JSlider();
				zminSlider.setValue(0);
				zminSlider.addChangeListener(new ChangeListener() { 
					@Override
					public void stateChanged(ChangeEvent arg0) {
						zminField.setText(String.valueOf(zminSlider.getValue()));
						visualize();
					}
				});
				horizontalBox_35.add(zminSlider);
				
				Component verticalGlue_29 = Box.createVerticalGlue();
				verticalBox_15.add(verticalGlue_29);
				
				JLabel lblZMin = new JLabel("z max (nm)");
				verticalBox_15.add(lblZMin);
				
				Box horizontalBox_36 = Box.createHorizontalBox();
				verticalBox_15.add(horizontalBox_36);
				
				zmaxField = new JTextField();
				zmaxField.setText("0");
				zmaxField.setHorizontalAlignment(SwingConstants.RIGHT);
				zmaxField.setMaximumSize(new Dimension(60, 22));
				zmaxField.setColumns(10);
				zmaxField.addActionListener(new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent e) {
						updateSlider(Float.parseFloat(zmaxField.getText()),zmaxSlider);
					}
					
				});
				horizontalBox_36.add(zmaxField);
				
				zmaxSlider = new JSlider();
				zmaxSlider.setValue(100);
				zmaxSlider.addChangeListener(new ChangeListener() { 
					@Override
					public void stateChanged(ChangeEvent arg0) {
						zmaxField.setText(String.valueOf(zmaxSlider.getValue()));
						visualize();
					}
				});
				horizontalBox_36.add(zmaxSlider);
		stormColorButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				allDataSets.get(currentRow).getParameterSet().setStormColor(JColorChooser.showDialog(getContentPane(), "Choose color for Storm points", allDataSets.get(currentRow).getParameterSet().getStormColor()));
				updateButtonColors();
			}
		});
				
				
				
				Box horizontalBox_23 = Box.createHorizontalBox();
				verticalBox.add(horizontalBox_23);
				
				Component horizontalGlue_29 = Box.createHorizontalGlue();
				horizontalBox_23.add(horizontalGlue_29);
				
				xyViewButton = new JToggleButton("xy");
				xyViewButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						System.out.println(viewStatus);
						setViewPoint(0,Math.PI/2);
						xzViewButton.setSelected(false);
						yzViewButton.setSelected(false);
						if (viewStatus == 1){
							viewStatus = 0;
							return;
						}
						viewStatus = 1;
					
					}
				});
				horizontalBox_23.add(xyViewButton);
				
				Component horizontalGlue_27 = Box.createHorizontalGlue();
				horizontalBox_23.add(horizontalGlue_27);
				
				xzViewButton = new JToggleButton("xz");
				xzViewButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						System.out.println(viewStatus);
						setViewPoint(Math.PI/2,0);
						xyViewButton.setSelected(false);
						yzViewButton.setSelected(false);
						if (viewStatus == 2){
							viewStatus = 0;
							return;
						}
						viewStatus = 2;
						
					}
				});
			
				horizontalBox_23.add(xzViewButton);
				
				Component horizontalGlue_28 = Box.createHorizontalGlue();
				horizontalBox_23.add(horizontalGlue_28);
				
				yzViewButton = new JToggleButton("yz");
				yzViewButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						System.out.println(viewStatus);
						setViewPoint(0,0);
						xyViewButton.setSelected(false);
						xzViewButton.setSelected(false);
						if (viewStatus == 3){
							viewStatus = 0;
							return;
						}
						viewStatus = 3;
						
					}
				});
				horizontalBox_23.add(yzViewButton);
				
								
				Component horizontalGlue_30 = Box.createHorizontalGlue();
				horizontalBox_23.add(horizontalGlue_30);
				
				Component verticalGlue_3 = Box.createVerticalGlue();
				verticalBox.add(verticalGlue_3);
				
				saveViewpointButton = new JToggleButton("Fix Viewpoint");
				saveViewpointButton.setAlignmentX(Component.CENTER_ALIGNMENT);
				verticalBox.add(saveViewpointButton);
				
				Component verticalGlue_14 = Box.createVerticalGlue();
				verticalBox.add(verticalGlue_14);
				
				JButton visButton = new JButton("Visualize");
				verticalBox.add(visButton);
				visButton.setAlignmentX(Component.CENTER_ALIGNMENT);
				visButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						visualize();
					}
				});
		dataSetTable.getColumnModel().getColumn(0).setMinWidth(100);
		jsp.setAlignmentX(Component.LEFT_ALIGNMENT);
		jsp.setPreferredSize(new Dimension(350, 600));
		panel.add(jsp, "name_652625437088073");
		getContentPane().add(panel, BorderLayout.EAST);
		
		plotPanel = new JPanel(new BorderLayout());
		plotPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		getContentPane().add(plotPanel, BorderLayout.CENTER);
		plotPanel.setLayout(new BorderLayout());
		
		MyDragDropListener myDragDropListener = new MyDragDropListener();

	    // Connect the label with a drag and drop listener
	    new DropTarget(loadDataLabel, myDragDropListener);
	    new DropTarget(contentPane, myDragDropListener);
	    new DropTarget(plotPanel, myDragDropListener);
	    
	    
	    myDragDropListener.addFileDragAndDropListener(new FileDragAndDropListener(){
	    	public void FileDragAndDropEventOccured(FileDragAndDropEvent event){
	    		System.out.println(event.getFile().getPath());
	    		if (new ProjectFileFilter().accept(event.getFile())){
	    			proceedProjectImport(event.getFile());
	    		}
	    		if (new TriangleLineFilter().accept(event.getFile()))
	    		proceedFileImport(event.getFile());
	    	}
	    });
		
		loadDataLabel.setHorizontalAlignment(SwingConstants.CENTER);
		plotPanel.add(loadDataLabel, BorderLayout.CENTER);
		
		JToolBar toolBar = new JToolBar();
		getContentPane().add(toolBar, BorderLayout.NORTH);
		
		JButton importFileButton = new JButton("Import File");
		toolBar.add(importFileButton);
		importFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.setFileFilter(new TriangleLineFilter());
				chooser.setFileSelectionMode(0);
				int returnVal = chooser.showOpenDialog(getContentPane()); //replace null with your swing container
				if(returnVal == JFileChooser.APPROVE_OPTION) {     
					proceedFileImport(chooser.getSelectedFile());
				}
			}
		});
		
		Component rigidArea_1 = Box.createRigidArea(new Dimension(20, 20));
		toolBar.add(rigidArea_1);
		
		JButton importProjectButton = new JButton("Import Project");
		toolBar.add(importProjectButton);
		importProjectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setAcceptAllFileFilterUsed(false);
				fc.setFileFilter(new ProjectFileFilter());
				fc.setFileSelectionMode(0);
				int returnValue = fc.showOpenDialog(getContentPane());
				if (returnValue == JFileChooser.APPROVE_OPTION){
					proceedProjectImport(fc.getSelectedFile());
				}
			}
			
		});
		
		
		plot = new Plot3D();
		
		configureTableListener();
		
		JButton openEditorButton = new JButton("Open Editor");
		openEditorButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
		            public void run() {
		                try {
							new Editor();
						} catch (IOException e) {
							e.printStackTrace();
						} 
		            }
		        });
			}
		});
		
		Component rigidArea_2 = Box.createRigidArea(new Dimension(20, 20));
		toolBar.add(rigidArea_2);
		toolBar.add(openEditorButton);
		
		Component rigidArea_3 = Box.createRigidArea(new Dimension(20, 20));
		toolBar.add(rigidArea_3);
		
		exampleComboBox = new JComboBox();
		exampleComboBox.setMaximumSize(new Dimension(200, 32767));
		exampleComboBox.setMaximumRowCount(7);
		exampleComboBox.addItem("Examples");
		exampleComboBox.addItem("Microtubules 3D");
		exampleComboBox.addItem("Mitochondria 3D");
		exampleComboBox.addItem("Crossing Lines 2D");
		exampleComboBox.addItem("Crossing Lines 3D");
		exampleComboBox.addItem("Shpere 3D");
		exampleComboBox.addItem("Triangles 3D");
		exampleComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (exampleComboBox.getSelectedIndex()>0){
					loadExamples(exampleComboBox.getSelectedIndex());
				}
			}
		});
		toolBar.add(exampleComboBox);
		
		Component horizontalGlue_24 = Box.createHorizontalGlue();
		toolBar.add(horizontalGlue_24);
		
		JButton saveProjectButton = new JButton("Save Project");
		saveProjectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				int returnValue = fc.showSaveDialog(getContentPane());
				if(returnValue == JFileChooser.APPROVE_OPTION) {
					String path = fc.getSelectedFile().getAbsolutePath();
					String name = fc.getSelectedFile().getName();
					if (!path.endsWith(EXTENSION)) {
					    path += EXTENSION;
					}
					if(name.endsWith(EXTENSION)){
						name = name.substring(0, name.length()-6);
					}
					System.out.println("Path to write project: " + path);
					System.out.println("project name: " + name);
					Project p = new Project(name, allDataSets,1.f);
					p.setOriginalImage(loadedImage);
					FileManager.writeProjectToFile(p, path);
				}
			}
		});
		
		Component horizontalGlue_39 = Box.createHorizontalGlue();
		toolBar.add(horizontalGlue_39);
		
		Component horizontalGlue_38 = Box.createHorizontalGlue();
		toolBar.add(horizontalGlue_38);
		
		toolBar.add(saveProjectButton);
		
		JButton exportButton = new JButton("Export");
		exportButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				int returnValue = fc.showSaveDialog(getContentPane());
				if(returnValue == JFileChooser.APPROVE_OPTION) {
					String path = fc.getSelectedFile().getAbsolutePath();
					String name = fc.getSelectedFile().getName();
					if (!path.endsWith(EXTENSIONIMAGEOUTPUT)) {
					    path += EXTENSIONIMAGEOUTPUT;
					}
					if(name.endsWith(EXTENSIONIMAGEOUTPUT)){
						name = name.substring(0, name.length()-4);
					}
					System.out.println("Path to write project: " + path);
					System.out.println("project name: " + name);
					borders.clear();
					borders.add(Float.valueOf(xminField.getText()));
					borders.add(Float.valueOf(xmaxField.getText()));
					borders.add(Float.valueOf(yminField.getText()));
					borders.add(Float.valueOf(ymaxField.getText()));
					borders.add(Float.valueOf(zminField.getText()));
					borders.add(Float.valueOf(zmaxField.getText()));
					FileManager.ExportToFile(allDataSets.get(currentRow), path, viewStatus,borders);
				}
			}
		});
		
		Component rigidArea = Box.createRigidArea(new Dimension(20, 20));
		toolBar.add(rigidArea);
		toolBar.add(exportButton);
		
		Component horizontalGlue_25 = Box.createHorizontalGlue();
		toolBar.add(horizontalGlue_25);
		
		JButton aboutButton = new JButton("About");
		aboutButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				String text = "<html>SuReSim  Copyright (C) 2015  Frank Herrmannsdörfer, Varun Venkataramani, Max Scheurer<p>"
			      + "This program comes with ABSOLUTELY NO WARRANTY;<p>"
			      + "This is free software, and you are welcome to redistribute it under certain conditions. <p>"
			      + "For more details see http://www.gnu.org/licenses/gpl-3.0.html</html>";
					
				JOptionPane.showMessageDialog(null, text, "About", JOptionPane.OK_CANCEL_OPTION);
			}
		});
		toolBar.add(aboutButton);
		
		calc = new STORMCalculator(null);
	}
	
	
	protected void loadExamples(int selectedIndex) {
		DataSet data = ExamplesProvidingClass.getDataset(selectedIndex);
		furtherProceedFileImport(data, data.dataType);
	}

	protected void updateSlider(float value, JSlider slider) {
		slider.setValue((int) value);
	}

	private void setViewPoint(double sigma, double theta) {
		if (allDataSets.size()<1){
			return;
		}
		plot.viewPoint= new Coord3d((float) sigma, (float) theta, plot.currentChart.getViewPoint().z);
		plot.viewBounds = plot.currentChart.getView().getBounds();
		getDrawingParameters();
		visualizeAllSelectedData();
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
		data.setName(file.getName());
		furtherProceedFileImport(data,type);
	}
	
	private void furtherProceedFileImport(DataSet data, DataType type){
		if(data.dataType.equals(DataType.TRIANGLES)) {
			System.out.println("Triangles parsed correctly.");
		}
		else if(type.equals(DataType.LINES)) {
			System.out.println("Lines parsed correctly.");
		}
		else if(type.equals(DataType.PLY)){
			System.out.println("PLY file parsed.");
		}
		
		allDataSets.add(data);
		model.data.add(data);
		model.visibleSets.add(Boolean.TRUE);
		if (shiftX == -1 && shiftY == -1 && shiftZ == -1){
			dataSetTable.setRowSelectionInterval(0, 0);
			loadParameterSetOfRow(0);
			ArrayList<Float> shifts = new ArrayList<Float>();
			if (allDataSets.get(0).dataType == DataType.LINES){
				LineDataSet lines = (LineDataSet) allDataSets.get(0);
				shifts = Calc.findShiftLines(lines.data);
			}
			else{
				TriangleDataSet triangles = (TriangleDataSet) allDataSets.get(0);
				shifts = Calc.findShiftTriangles(triangles.primitives);
			}
			shiftX = -shifts.get(0);
			shiftY = -shifts.get(1);
			shiftZ = -shifts.get(2);
		}
		if (allDataSets.get(allDataSets.size()-1).dataType == DataType.LINES){
			LineDataSet lines = (LineDataSet) allDataSets.get(allDataSets.size()-1);
			lines.shiftData(shiftX,shiftY,shiftZ);
		}
		else{
			TriangleDataSet triangles = (TriangleDataSet) allDataSets.get(allDataSets.size()-1);
			triangles.shiftData(shiftX,shiftY,shiftZ);
		}
		if (allDataSets.size()==1){
			allDataSets.get(0).getParameterSet().setGeneralVisibility(Boolean.TRUE);
			visualize();
		}
		model.fireTableDataChanged();
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
					loadParameterSetOfRow(row);
				}
			}
		});
	
	}
	
	/**
	 * Loads the parameter set of the selected row into the configuration panel 
	 * @param row - row clicked in dataSetTable
	 *
	 */
	
	private void loadParameterSetOfRow(int row) {
		currentRow = row;
		if(currentRow != -1) {
			ParameterSet set = allDataSets.get(row).parameterSet;
			if (allDataSets.get(row).dataType == DataType.LINES){
				radiusOfFilamentsField.setEnabled(true);
				lblRadiusOfFilaments.setEnabled(true);
				radiusOfFilamentsField.setText(set.getRof().toString()); //rof   
				epitopeDensityLabel.setText("<html>Epitope Density (nm<sup>-1</sup>)</html>");
			}
			else{
				radiusOfFilamentsField.setEnabled(false);
				lblRadiusOfFilaments.setEnabled(false);
				epitopeDensityLabel.setText("<html>Epitope Density (nm<sup>-2</sup>)</html>");
			}
			labelingEfficiencyField.setText(String.format(Locale.ENGLISH,"%.2f",set.getPabs()*100)); //pabs                                                                                       
			meanAngleField.setText(String.format(Locale.ENGLISH,"%.2f", set.getAoa()*180.f/Math.PI)); //aoa     
			detectionEfficiencyField.setText(String.format(Locale.ENGLISH,"%.2f",set.getDeff()*100)); //pabs
			backgroundLabelField.setText(set.getIlpmm3().toString()); //ilpmm3 aus StormPointFinder                                                                   
			labelLengthField.setText(set.getLoa().toString()); //loa                                                                                               
			fluorophoresPerLabelField.setText(set.getFpab().toString()); //fpab                                                                                     
			kOnField.setText(set.getKOn().toString()); //kOn
			kOffField.setText(set.getKOff().toString()); //kOff
			bleachConstantField.setText(set.getBleachConst().toString());
			recordedFramesField.setText(String.valueOf(set.getFrames())); //frames
			averagePhotonOutputField.setText(Integer.toString(set.getMeanPhotonNumber()));                                                                                             
			locPrecisionXYField.setText(set.getSxy().toString()); //sxy                                                                                            
			locPrecisionZField.setText(set.getSz().toString()); //sz   
			psfSizeField.setText(set.getPsfwidth().toString()); //psfwidth aus StormPointFinder         
			lineWidthField.setText(set.getLineWidth().toString());
			if(allDataSets.get(row).dataType == DataType.LINES) {
				System.out.println(set.getBspnm());
				epitopeDensityField.setText(String.format(Locale.ENGLISH,"%.4f", set.getBspnm()));
			}
			else {
				epitopeDensityField.setText(String.format(Locale.ENGLISH,"%.4f",set.getBspsnm()));
			}
			
			pointSizeField.setText(set.getPointSize().toString());

			showAntibodiesBox.setSelected(set.getAntibodyVisibility());
			showEmBox.setSelected(set.getEmVisibility());
			showStormPointsBox.setSelected(set.getStormVisibility());
			mergePSFBox.setSelected(set.getMergedPSF());
			applyBleachBox.setSelected(set.getApplyBleaching());
			coupleSigmaIntensityBox.setSelected(set.getCoupleSigmaIntensity());
			updateButtonColors();
		}
	}
	
	private void updateButtonColors() {
		ParameterSet set = allDataSets.get(currentRow).parameterSet;
		emColorButton.setBackground(set.getEmColor());
		stormColorButton.setBackground(set.getStormColor());
		antibodyColorButton.setBackground(set.getAntibodyColor());
		backgroundColorButton.setBackground(backgroundColor);
		mainColorButton.setBackground(mainColor);
		
		emColorButton.setContentAreaFilled(false);
		stormColorButton.setContentAreaFilled(false);
		antibodyColorButton.setContentAreaFilled(false);
		backgroundColorButton.setContentAreaFilled(false);
		mainColorButton.setContentAreaFilled(false);
		
		emColorButton.setOpaque(true);
		stormColorButton.setOpaque(true);
		antibodyColorButton.setOpaque(true);
		backgroundColorButton.setOpaque(true);
		mainColorButton.setOpaque(true);
		visualize();
	}
	
	/**
	 * invoked by calculate button
	 * runs the calculation for the current dataset
	 * @throws Exception 
	 */
	private void calculate() {
		if (allDataSets.size()<1){
			return;
		}
		allDataSets.get(currentRow).setProgressBar(progressBar);
		System.out.println("bspsnm: " + allDataSets.get(currentRow).getParameterSet().getBspsnm());
		allDataSets.get(currentRow).getParameterSet().setPabs((float) (new Float(labelingEfficiencyField.getText())/100.));
		allDataSets.get(currentRow).getParameterSet().setAoa((float) ((new Float(meanAngleField.getText()))/180*Math.PI));
		allDataSets.get(currentRow).getParameterSet().setDeff((float) (new Float(detectionEfficiencyField.getText())/100)); 
		allDataSets.get(currentRow).getParameterSet().setIlpmm3(new Float(backgroundLabelField.getText()));
		allDataSets.get(currentRow).getParameterSet().setLoa(new Float(labelLengthField.getText()));
		allDataSets.get(currentRow).getParameterSet().setFpab(new Float(fluorophoresPerLabelField.getText()));
		allDataSets.get(currentRow).getParameterSet().setKOn(new Float(kOnField.getText()));
		allDataSets.get(currentRow).getParameterSet().setKOff(new Float(kOffField.getText()));
		allDataSets.get(currentRow).getParameterSet().setBleachConst(new Float(bleachConstantField.getText()));
		allDataSets.get(currentRow).getParameterSet().setFrames((new Float(recordedFramesField.getText())).intValue());
		allDataSets.get(currentRow).getParameterSet().setSxy(new Float(locPrecisionXYField.getText()));
		allDataSets.get(currentRow).getParameterSet().setSz(new Float(locPrecisionZField.getText()));
		allDataSets.get(currentRow).getParameterSet().setPsfwidth(new Float(psfSizeField.getText()));
		allDataSets.get(currentRow).getParameterSet().setMeanPhotonNumber((new Float(averagePhotonOutputField.getText())).intValue());
		if(allDataSets.get(currentRow).dataType == DataType.LINES) {
			allDataSets.get(currentRow).getParameterSet().setBspnm(new Float(epitopeDensityField.getText()));
			allDataSets.get(currentRow).getParameterSet().setRof(new Float(radiusOfFilamentsField.getText()));
		}
		else {
			allDataSets.get(currentRow).getParameterSet().setBspsnm(new Float(epitopeDensityField.getText()));
		}
		allDataSets.get(currentRow).getParameterSet().setPointSize(new Float(pointSizeField.getText()));
		allDataSets.get(currentRow).getParameterSet().setLineWidth(new Float(lineWidthField.getText()));
		allDataSets.get(currentRow).getParameterSet().setMergedPSF(mergePSFBox.isSelected());
		allDataSets.get(currentRow).getParameterSet().setApplyBleaching(applyBleachBox.isSelected());
		allDataSets.get(currentRow).getParameterSet().setCoupleSigmaIntensity(coupleSigmaIntensityBox.isSelected());
		
		calc = new STORMCalculator(allDataSets.get(currentRow));
		//calc = new STORMCalculator(allDataSets.get(currentRow));
		calc.addPropertyChangeListener(this);
		calc.execute();
		// When calc has finished, grab the new dataset
		//allDataSets.set(currentRow, calc.getCurrentDataSet());
		//visualizeAllSelectedData();
	}
	

	/**
	 * invoked by visualize button
	 */
	private void visualize() {
		if(currentRow != -1) {
			getDrawingParameters();
			if(saveViewpointButton.isSelected()) {
				setViewPointAndScale();
				
			}
			else {
				plot.viewPoint = null;
				plot.viewBounds = null;
			}
			visualizeAllSelectedData();
		}
	}
	
	private void getDrawingParameters(){
		allDataSets.get(currentRow).getParameterSet().setEmVisibility(showEmBox.isSelected());
		allDataSets.get(currentRow).getParameterSet().setStormVisibility(showStormPointsBox.isSelected());
		allDataSets.get(currentRow).getParameterSet().setAntibodyVisibility(showAntibodiesBox.isSelected());

		allDataSets.get(currentRow).getParameterSet().setPointSize(new Float(pointSizeField.getText()));
		setPlotQuality();
	}
	/**
	 * sets the plot quality according to the radiobuttons, removed since there occured rendering errors
	 * for nicest and advanced
	 */
	
	private void setPlotQuality() {
		if(radioNicest.isSelected()) {
			plot.chartQuality = Quality.Nicest;
		}
		else if(radioAdvanced.isSelected()) {
			plot.chartQuality = Quality.Advanced;
		}
		else if(radioIntermediate.isSelected()) {
			plot.chartQuality = Quality.Intermediate;
		}
		else if(radioFastest.isSelected()) {
			plot.chartQuality = Quality.Fastest;
		}
	}
	//private void setPlotQuality() {
	//	plot.chartQuality = Quality.Advanced;
	//}
	
	private void setViewPointAndScale() {
		System.out.println("VP: " + plot.currentChart.getViewPoint().toString());
		System.out.println("scale: " + plot.currentChart.getScale().toString());
		
		plot.viewPoint = plot.currentChart.getViewPoint();
		plot.viewBounds = plot.currentChart.getView().getBounds();
		
		//System.err.println("Viewpoint wurde überschrieben!!!! Zeile 1372");
		//plot.viewPoint = new Coord3d((float) 0.6871975, (float) 0.30719763, (float) 835.31036);
		
	}
	
	@Override
	public void tableChanged(TableModelEvent e) {
		setSelectedListsForDrawing();
	}
	
	/**
	 * Checks which data sets are generally visible and creates a new Plot3D with the dataSets
	 */
	public void setSelectedListsForDrawing() {
		List<DataSet> sets = new ArrayList<DataSet>();
		for(int i = 0; i < model.data.size(); i++) {
			if(model.visibleSets.get(i) == true) {
				sets.add(model.data.get(i));
				allDataSets.get(i).getParameterSet().setGeneralVisibility(Boolean.TRUE);
			}
			else {
				allDataSets.get(i).getParameterSet().setGeneralVisibility(Boolean.FALSE);
			}
		}
		model.data.clear();
		model.data.addAll(allDataSets);
		updateMinMax();
		if(sets.size() > 0) {
			plot.dataSets.clear();
			plot.addAllDataSets(sets);
			plotPanel.removeAll();
			graphComponent = (Component) plot.createChart().getCanvas();
			plotPanel.add(graphComponent);
			plotPanel.revalidate();
			plotPanel.repaint();
			graphComponent.revalidate();
		}
		else if(sets.size() == 0) {
			System.out.println("empty!!!");
			plot.dataSets.clear();
			plotPanel.removeAll();
			plotPanel.add(loadDataLabel);
			plotPanel.revalidate();
			plotPanel.repaint();
		}
	}	
	
	private void visualizeAllSelectedData() {
		List<DataSet> sets = new ArrayList<DataSet>();
		for(int i = 0; i < allDataSets.size(); i++) {
			if(allDataSets.get(i).getParameterSet().getGeneralVisibility() == true) {
				model.visibleSets.add(Boolean.TRUE);
				sets.add(model.data.get(i));
			}
			else {
				model.visibleSets.add(Boolean.FALSE);
				//sets.add(model.data.get(i));
			}
		}
		model.data.clear();
		model.data.addAll(allDataSets);
		plot.squared = false;
		plot.showBox = chckbxShowAxes.isSelected();
		plot.showTicks = chckbxShowTicks.isSelected();
		plot.backgroundColor = new org.jzy3d.colors.Color(backgroundColor.getRed(),backgroundColor.getGreen(),backgroundColor.getBlue());
		plot.mainColor = new org.jzy3d.colors.Color(mainColor.getRed(),mainColor.getGreen(),mainColor.getBlue());
		updateMinMax();
		plot.borders = getBorders();
		if(sets.size() > 0) {
			plot.dataSets.clear();
			plot.addAllDataSets(sets);
			plotPanel.removeAll();
			graphComponent = (Component) plot.createChart().getCanvas();
			plotPanel.add(graphComponent);
			plotPanel.revalidate();
			plotPanel.repaint();
			graphComponent.revalidate();
		}
		else if(sets.size() == 0) {
			System.out.println("empty!!!");
			plot.dataSets.clear();
			plotPanel.removeAll();
			plotPanel.add(loadDataLabel);
			plotPanel.revalidate();
			plotPanel.repaint();
		}
	}
	
	private ArrayList<Float> getBorders(){
		ArrayList<Float> borders = new ArrayList<Float>();
		borders.add(new Float(xminField.getText()));
		borders.add(new Float(xmaxField.getText()));
		borders.add(new Float(yminField.getText()));
		borders.add(new Float(ymaxField.getText()));
		borders.add(new Float(zminField.getText()));
		borders.add(new Float(zmaxField.getText()));
		return borders;
	}
	
	private void updateMinMax() {
		
		ArrayList<Float> maxDims = new ArrayList<Float>();
		maxDims.add(xmin);
		maxDims.add(xmax);
		maxDims.add(ymin);
		maxDims.add(ymax);
		maxDims.add(zmin);
		maxDims.add(zmax);
		ArrayList<Float> oldDims = new ArrayList<Float>(); // to check if min max dimensions have changed
		for (int i = 0; i<maxDims.size(); i++){
			oldDims.add(maxDims.get(i));
		}
		ArrayList<Float> dims = new ArrayList<Float>();
		
		for(int i = 0; i < allDataSets.size(); i++) {
			if(allDataSets.get(i).getParameterSet().getGeneralVisibility() == true) {
				if (allDataSets.get(i).antiBodyEndPoints != null){
					dims = Calc.findDims(allDataSets.get(i).antiBodyEndPoints);
					maxDims = findBorders(maxDims,dims);
					dims = Calc.findDims(allDataSets.get(i).antiBodyStartPoints);
					maxDims = findBorders(maxDims,dims);
				}
				if (allDataSets.get(i).stormData != null){
					dims = Calc.findDims(allDataSets.get(i).stormData);
					maxDims = findBorders(maxDims,dims);
				}
	
				if(allDataSets.get(i).dataType.equals(DataType.TRIANGLES)) {
					TriangleDataSet triangles = (TriangleDataSet) allDataSets.get(i);
					dims = Calc.findDimsTriangles(triangles.getPrimitives());
					maxDims = findBorders(maxDims,dims);
				}
				else if(allDataSets.get(i).dataType.equals(DataType.LINES)){
					LineDataSet lines = (LineDataSet) allDataSets.get(i);
					dims = Calc.findDimsLines(lines.data);
					maxDims = findBorders(maxDims,dims);
				}
			}
			else {
				
			}
			
		}
		if (oldDims.equals(maxDims)){
				
		}
		else{ // if the dims have changed update sliders
			if (keepBordersChkBox.isSelected()){
				
			}
			else{
				xminSlider.setMinimum(maxDims.get(0).intValue());
				xminSlider.setMaximum(maxDims.get(1).intValue());
				xminSlider.setValue(xminSlider.getMinimum());
				
				xmaxSlider.setMinimum(maxDims.get(0).intValue());
				xmaxSlider.setMaximum(maxDims.get(1).intValue());
				xmaxSlider.setValue(xmaxSlider.getMaximum());
				
				yminSlider.setMinimum(maxDims.get(2).intValue());
				yminSlider.setMaximum(maxDims.get(3).intValue());
				yminSlider.setValue(yminSlider.getMinimum());
				
				ymaxSlider.setMinimum(maxDims.get(2).intValue());
				ymaxSlider.setMaximum(maxDims.get(3).intValue());
				ymaxSlider.setValue(ymaxSlider.getMaximum());
				
				zminSlider.setMinimum(maxDims.get(4).intValue());
				zminSlider.setMaximum(maxDims.get(5).intValue());
				zminSlider.setValue(zminSlider.getMinimum());
				
				zmaxSlider.setMinimum(maxDims.get(4).intValue());
				zmaxSlider.setMaximum(maxDims.get(5).intValue());
				zmaxSlider.setValue(zmaxSlider.getMaximum());
				xmin = maxDims.get(0);
				ymin = maxDims.get(2);
				zmin = maxDims.get(4);
				xmax = maxDims.get(1);
				ymax = maxDims.get(3);
				zmax = maxDims.get(5);
				xminField.setText(String.format(Locale.ENGLISH,"%.1f", maxDims.get(0)));
				xmaxField.setText(String.format(Locale.ENGLISH,"%.1f", maxDims.get(1)));
				yminField.setText(String.format(Locale.ENGLISH,"%.1f", maxDims.get(2)));
				ymaxField.setText(String.format(Locale.ENGLISH,"%.1f", maxDims.get(3)));
				zminField.setText(String.format(Locale.ENGLISH,"%.1f", maxDims.get(4)));
				zmaxField.setText(String.format(Locale.ENGLISH,"%.1f", maxDims.get(5)));
				borders.clear();
				borders.add(xmin);
				borders.add(xmax);
				borders.add(ymin);
				borders.add(ymax);
				borders.add(zmin);
				borders.add(zmax);
			}
		}
		
		plot.borders = getBorders();
	}
	
	private ArrayList<Float> findBorders(ArrayList<Float> oldBorder, ArrayList<Float> candidates){
		ArrayList<Float> newBorders = new ArrayList<Float>();
		for (int i = 0; i < 3; i++){
			if (candidates.get(2*i)< oldBorder.get(2*i)){
				newBorders.add(candidates.get(2*i));
			}
			else{
				newBorders.add(oldBorder.get(2*i));
			}
			if (candidates.get(2*i+1)> oldBorder.get(2*i+1)){
				newBorders.add(candidates.get(2*i+1));
			}
			else{
				newBorders.add(oldBorder.get(2*i+1));
			}
		}
		return newBorders;
	}
	private void proceedProjectImport(File file) {
		String path = file.getAbsolutePath();
		Project p = FileManager.openProjectFromFile(path);
		loadedImage = p.getOriginalImage();
		allDataSets.clear();
		model.data.clear();
		model.visibleSets.clear();
		allDataSets.addAll(p.dataSets);
		for(DataSet s : allDataSets) {
			s.getParameterSet().setGeneralVisibility(true);
			model.visibleSets.add(s.getParameterSet().getGeneralVisibility());
		}
		model.data.addAll(p.dataSets);
		if (allDataSets.size()>0){
			loadParameterSetOfRow(0);
		}
		System.out.println("Number of dss: " + allDataSets.size());
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				visualize();
			}
		});
	}
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
		}
	}
}
