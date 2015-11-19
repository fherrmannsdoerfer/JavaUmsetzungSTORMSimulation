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
import java.util.Random;

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

import parsing.CalibrationFileParser;
import model.DataSet;
import model.LineDataSet;
import model.ParameterSet;
import model.Project;
import model.SerializableImage;
import model.TriangleDataSet;
import table.DataSetTableModel;
import calc.Calc;
import calc.CreateStack;
import calc.STORMCalculator;

import javax.swing.JToggleButton;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.BoxLayout;

import java.awt.CardLayout;

import javax.swing.JSlider;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.DefaultComboBoxModel;


/**
 * @brief Sketch of GUI 
 * 
 * This is the main program that implements both plotter and calculator for STORM simulation.
 * It can load new data from the editor or parse files on the file system.
 * 
 * 
 */

public class Gui extends JFrame implements TableModelListener,PropertyChangeListener,ThreadCompleteListener {

	private JPanel contentPane;
	private JLabel epitopeDensityLabel;
	private JTextField radiusOfFilamentsField; //rof
	private JTextField labelingEfficiencyField; //pabs
	private JTextField meanAngleField; //aoa
	private JTextField backgroundLabelField; //ilpmm3 aus StormPointFinder
	private JTextField labelLengthField; //loa
	private JTextField fluorophoresPerLabelField; //fpab
	private JTextField dutyCycleField; //abpf
	private JTextField averagePhotonOutputField; // 
	private JTextField locPrecisionXYField; //sxy
	private JTextField locPrecisionZField; //sz
	private JFormattedTextField epitopeDensityField; //bspnm oder bspsnm je nachdem ob Linien oder Dreiecke
	private JTextField pointSizeField; //
	
	JLabel lblRadiusOfFilaments;
	
	public CreatePlot nt;
	
	private JCheckBox showEmBox;
	private JCheckBox showStormPointsBox;
	private JCheckBox showAntibodiesBox;
	private JCheckBox applyBleachBox;
	private JCheckBox reproducibleOutputchkBox;
	private JCheckBox coupleSigmaIntensityBox;
	
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
	
	private JCheckBox distributePSFOverFrames;
	JCheckBox ensureSinglePSFchkBox;
	JComboBox tiffStackModeComboBox;
	
	private JComboBox exampleComboBox;
	
	public STORMCalculator calc;
	
	private Random random;
	
	int fontSize = 12;
	Font usedFont = new Font("Dialog",Font.BOLD,fontSize);
	Gui selfReference;
	
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
	private JTextField recordedFramesField;
	private JTextField lineWidthField;
	private JLabel numberOfVisibleLocalizationsLabel;
	
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
	private JTextField angularDistributionField;
	private JTextField pxToNmField;
	private JTextField framerateField;
	JTextField meanBlinkingDurationField;
	private JTextField naField;
	private JTextField wavelengthField;
	private JTextField fokusField;
	private JTextField defokusField;
	private JTextField sigmaBgField;
	private JTextField constOffsetField;
	private JTextField emptyPixelsOnRimField;
	private JTextField quantumEfficiencyField;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JTextField emGainField;
	private JTextField windowsizePSFRenderingField;
	private JTextField electronsPerDnField;
	
	boolean allowShift = false;
	
	JPanel createTiffStackPanel;
	JPanel nativeSimulationPanel;
	JPanel twoDPSFPanel;
	JPanel threeDPSFPanel;
	private JTextField minIntensityField;
	private JTextField pixelSizeField;
	private JTextField sigmaSizeField;
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
					frame.setTitle("SuReSim");
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
		this.selfReference = this;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, 1600, 1070);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setPreferredSize(new Dimension(250, 500));
		
		model = new DataSetTableModel();
        model.addTableModelListener(this);
		
		JPanel panel = new JPanel();
		panel.setLayout(new CardLayout(0, 0));
		panel.add(contentPane);
		JScrollPane jsp = new JScrollPane(contentPane);
		jsp.setMinimumSize(new Dimension(27, 400));
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));
				ButtonGroup group = new ButtonGroup();
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
			
			Box verticalBox_24 = Box.createVerticalBox();
			contentPane.add(verticalBox_24);
			dataSetTable = new JTable(model);
			verticalBox_24.add(dataSetTable);
			dataSetTable.setMinimumSize(new Dimension(30, 150));
			dataSetTable.setPreferredSize(new Dimension(130, 140));
			
			JTabbedPane tabbedPane_2 = new JTabbedPane(JTabbedPane.TOP);
			verticalBox_24.add(tabbedPane_2);
			
			JPanel panel_2 = new JPanel();
			tabbedPane_2.addTab("Simulation Parameter", null, panel_2, null);
			panel_2.setLayout(new CardLayout(0, 0));
			
			JScrollPane scrollPane_1 = new JScrollPane();
			panel_2.add(scrollPane_1, "name_8612746230277");
			
			Box verticalBox_23 = Box.createVerticalBox();
			verticalBox_23.setMaximumSize(new Dimension(222222, 222220));
			verticalBox_23.setPreferredSize(new Dimension(290, 650));
			verticalBox_23.setMinimumSize(new Dimension(300, 850));
			scrollPane_1.setViewportView(verticalBox_23);
			
			progressBar = new JProgressBar();
			verticalBox_23.add(progressBar);
			progressBar.setPreferredSize(new Dimension(300, 20));
			progressBar.setStringPainted(true);
			
			JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
			tabbedPane.setMaximumSize(new Dimension(32767, 450));
			verticalBox_23.add(tabbedPane);
			
			Box verticalBox_7 = Box.createVerticalBox();
			tabbedPane.addTab("Basic Settings I", null, verticalBox_7, null);
			
			Box verticalBox_1 = Box.createVerticalBox();
			verticalBox_7.add(verticalBox_1);
			verticalBox_1.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Structural Parameters", TitledBorder.LEADING, TitledBorder.TOP, usedFont, null));
			
			Box horizontalBox = Box.createHorizontalBox();
			verticalBox_1.add(horizontalBox);
			
			epitopeDensityLabel = new JLabel("<html>Epitope Density (nm<sup>-2</sup>)</html>");
			horizontalBox.add(epitopeDensityLabel);
			
			Component horizontalGlue = Box.createHorizontalGlue();
			horizontalBox.add(horizontalGlue);
			
			epitopeDensityField = new JFormattedTextField();
			epitopeDensityField.getDocument().addDocumentListener(new MyDocumentListener(epitopeDensityField));
			epitopeDensityField.setHorizontalAlignment(SwingConstants.RIGHT);
			epitopeDensityField.setMinimumSize(new Dimension(6, 10));
			epitopeDensityField.setMaximumSize(new Dimension(60, 22));
			epitopeDensityField.setColumns(5);
			horizontalBox.add(epitopeDensityField);
			
			Box horizontalBox_1 = Box.createHorizontalBox();
			verticalBox_1.add(horizontalBox_1);
			
			lblRadiusOfFilaments = new JLabel("Radius Of Filaments (nm)");
			horizontalBox_1.add(lblRadiusOfFilaments);
			
			Component horizontalGlue_1 = Box.createHorizontalGlue();
			horizontalBox_1.add(horizontalGlue_1);
			
			radiusOfFilamentsField = new JTextField();
			radiusOfFilamentsField.getDocument().addDocumentListener(new MyDocumentListener(radiusOfFilamentsField));
			radiusOfFilamentsField.setHorizontalAlignment(SwingConstants.RIGHT);
			radiusOfFilamentsField.setMinimumSize(new Dimension(6, 10));
			radiusOfFilamentsField.setMaximumSize(new Dimension(60, 22));
			radiusOfFilamentsField.setColumns(5);
			horizontalBox_1.add(radiusOfFilamentsField);
			
			Box horizontalBox_2 = Box.createHorizontalBox();
			verticalBox_1.add(horizontalBox_2);
			
			Box horizontalBox_3 = Box.createHorizontalBox();
			verticalBox_1.add(horizontalBox_3);
			
			Box verticalBox_2 = Box.createVerticalBox();
			verticalBox_7.add(verticalBox_2);
			verticalBox_2.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Label and Structure Dependent Parameters", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
			
			Box horizontalBox_4 = Box.createHorizontalBox();
			verticalBox_2.add(horizontalBox_4);
			
			JLabel lblLabelingEfficiency = new JLabel("Labeling Efficiency (%)");
			horizontalBox_4.add(lblLabelingEfficiency);
			
			Component horizontalGlue_4 = Box.createHorizontalGlue();
			horizontalBox_4.add(horizontalGlue_4);
			
			labelingEfficiencyField = new JTextField();
			labelingEfficiencyField.getDocument().addDocumentListener(new MyDocumentListener(labelingEfficiencyField));
			labelingEfficiencyField.setHorizontalAlignment(SwingConstants.RIGHT);
			labelingEfficiencyField.setMinimumSize(new Dimension(6, 10));
			labelingEfficiencyField.setMaximumSize(new Dimension(60, 22));
			labelingEfficiencyField.setColumns(5);
			horizontalBox_4.add(labelingEfficiencyField);
			
			Box horizontalBox_7 = Box.createHorizontalBox();
			verticalBox_2.add(horizontalBox_7);
			
			Box horizontalBox_10 = Box.createHorizontalBox();
			verticalBox_2.add(horizontalBox_10);
			
			JLabel lblAverageBlinkingNumber = new JLabel("On-Off Duty Cycle");
			horizontalBox_10.add(lblAverageBlinkingNumber);
			
			Component horizontalGlue_10 = Box.createHorizontalGlue();
			horizontalBox_10.add(horizontalGlue_10);
			
			dutyCycleField = new JTextField();
			dutyCycleField.getDocument().addDocumentListener(new MyDocumentListener(dutyCycleField));
			
			dutyCycleField.setHorizontalAlignment(SwingConstants.RIGHT);
			dutyCycleField.setMinimumSize(new Dimension(6, 10));
			dutyCycleField.setMaximumSize(new Dimension(60, 22));
			dutyCycleField.setColumns(5);
			horizontalBox_10.add(dutyCycleField);
			
			Box verticalBox_3 = Box.createVerticalBox();
			verticalBox_7.add(verticalBox_3);
			verticalBox_3.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Recording Parameters", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
			
			Box horizontalBox_22 = Box.createHorizontalBox();
			verticalBox_3.add(horizontalBox_22);
			
			JLabel lblRecordedFrames = new JLabel("Recorded Frames");
			horizontalBox_22.add(lblRecordedFrames);
			
			Component horizontalGlue_26 = Box.createHorizontalGlue();
			horizontalBox_22.add(horizontalGlue_26);
			
			recordedFramesField = new JTextField();
			recordedFramesField.getDocument().addDocumentListener(new MyDocumentListener(recordedFramesField));
			recordedFramesField.setHorizontalAlignment(SwingConstants.RIGHT);
			recordedFramesField.setMinimumSize(new Dimension(6, 10));
			recordedFramesField.setMaximumSize(new Dimension(60, 22));
			recordedFramesField.setColumns(5);
			horizontalBox_22.add(recordedFramesField);
			
			Box verticalBox_9 = Box.createVerticalBox();
			tabbedPane.addTab("Basic Settings II", null, verticalBox_9, null);
			
			Box verticalBox_10 = Box.createVerticalBox();
			verticalBox_10.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Label Dependent Parameters", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
			verticalBox_9.add(verticalBox_10);
			
			Box horizontalBox_5 = Box.createHorizontalBox();
			verticalBox_10.add(horizontalBox_5);
			
			JLabel lblMeanAngle = new JLabel("Binding Angle (Degree)");
			horizontalBox_5.add(lblMeanAngle);
			
			Component horizontalGlue_5 = Box.createHorizontalGlue();
			horizontalBox_5.add(horizontalGlue_5);
			
			meanAngleField = new JTextField();
			meanAngleField.getDocument().addDocumentListener(new MyDocumentListener(meanAngleField));
			meanAngleField.setHorizontalAlignment(SwingConstants.RIGHT);
			meanAngleField.setMinimumSize(new Dimension(6, 10));
			meanAngleField.setMaximumSize(new Dimension(60, 22));
			meanAngleField.setColumns(5);
			horizontalBox_5.add(meanAngleField);
			
			JLabel lblNewLabel_7 = new JLabel("");
			horizontalBox_5.add(lblNewLabel_7);
			
			Box horizontalBox_40 = Box.createHorizontalBox();
			verticalBox_10.add(horizontalBox_40);
			
			JLabel lblNewLabel_8 = new JLabel("Sigma Of Angular Distribution");
			horizontalBox_40.add(lblNewLabel_8);
			
			Component horizontalGlue_41 = Box.createHorizontalGlue();
			horizontalBox_40.add(horizontalGlue_41);
			
			angularDistributionField = new JTextField();
			angularDistributionField.getDocument().addDocumentListener(new MyDocumentListener(angularDistributionField));
			angularDistributionField.setHorizontalAlignment(SwingConstants.RIGHT);
			angularDistributionField.setMinimumSize(new Dimension(60, 22));
			angularDistributionField.setMaximumSize(new Dimension(60, 22));
			horizontalBox_40.add(angularDistributionField);
			angularDistributionField.setColumns(5);
			
			Box horizontalBox_8 = Box.createHorizontalBox();
			verticalBox_10.add(horizontalBox_8);
			
			JLabel lblMeanDistanceLabel = new JLabel("Label Epitope Distance (nm)");
			horizontalBox_8.add(lblMeanDistanceLabel);
			
			Component horizontalGlue_8 = Box.createHorizontalGlue();
			horizontalBox_8.add(horizontalGlue_8);
			
			labelLengthField = new JTextField();
			labelLengthField.getDocument().addDocumentListener(new MyDocumentListener(labelLengthField));
			labelLengthField.setHorizontalAlignment(SwingConstants.RIGHT);
			labelLengthField.setMinimumSize(new Dimension(6, 10));
			labelLengthField.setMaximumSize(new Dimension(60, 22));
			labelLengthField.setColumns(5);
			horizontalBox_8.add(labelLengthField);
			
			Box horizontalBox_9 = Box.createHorizontalBox();
			verticalBox_10.add(horizontalBox_9);
			
			JLabel lblFluorophoresPerLabel = new JLabel("Fluorophores Per Label");
			horizontalBox_9.add(lblFluorophoresPerLabel);
			
			Component horizontalGlue_9 = Box.createHorizontalGlue();
			horizontalBox_9.add(horizontalGlue_9);
			
			fluorophoresPerLabelField = new JTextField();
			fluorophoresPerLabelField.getDocument().addDocumentListener(new MyDocumentListener(fluorophoresPerLabelField));
			fluorophoresPerLabelField.setHorizontalAlignment(SwingConstants.RIGHT);
			fluorophoresPerLabelField.setMinimumSize(new Dimension(6, 10));
			fluorophoresPerLabelField.setMaximumSize(new Dimension(60, 22));
			fluorophoresPerLabelField.setColumns(5);
			horizontalBox_9.add(fluorophoresPerLabelField);
			
			Box horizontalBox_29 = Box.createHorizontalBox();
			verticalBox_10.add(horizontalBox_29);
			
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
			bleachConstantField.getDocument().addDocumentListener(new MyDocumentListener(bleachConstantField));
			bleachConstantField.setMinimumSize(new Dimension(6, 10));
			bleachConstantField.setMaximumSize(new Dimension(60, 22));
			bleachConstantField.setHorizontalAlignment(SwingConstants.RIGHT);
			bleachConstantField.setColumns(5);
			horizontalBox_29.add(bleachConstantField);
			
			Box verticalBox_11 = Box.createVerticalBox();
			verticalBox_11.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Background", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
			verticalBox_9.add(verticalBox_11);
			
			Box horizontalBox_6 = Box.createHorizontalBox();
			verticalBox_11.add(horizontalBox_6);
			
			JLabel lblBackgroundLabel = new JLabel("<html>Background Label (&mu;m<sup>-3</sup>)</html>");
			horizontalBox_6.add(lblBackgroundLabel);
			
			Component horizontalGlue_6 = Box.createHorizontalGlue();
			horizontalBox_6.add(horizontalGlue_6);
			
			backgroundLabelField = new JTextField();
			backgroundLabelField.getDocument().addDocumentListener(new MyDocumentListener(backgroundLabelField));
			horizontalBox_6.add(backgroundLabelField);
			backgroundLabelField.setHorizontalAlignment(SwingConstants.RIGHT);
			backgroundLabelField.setMinimumSize(new Dimension(6, 10));
			backgroundLabelField.setMaximumSize(new Dimension(60, 22));
			backgroundLabelField.setColumns(5);
			
			Box horizontalBox_15 = Box.createHorizontalBox();
			verticalBox_11.add(horizontalBox_15);
			
			Box horizontalBox_14 = Box.createHorizontalBox();
			verticalBox_11.add(horizontalBox_14);
			
			Component horizontalGlue_17 = Box.createHorizontalGlue();
			horizontalBox_14.add(horizontalGlue_17);
			
			Component horizontalGlue_15 = Box.createHorizontalGlue();
			horizontalGlue_15.setPreferredSize(new Dimension(100, 0));
			horizontalBox_14.add(horizontalGlue_15);
			
			reproducibleOutputchkBox = new JCheckBox("Reproducible Output");
			verticalBox_11.add(reproducibleOutputchkBox);
			
			JComboBox comboBox = new JComboBox();
			comboBox.setModel(new DefaultComboBoxModel(new String[] {"Direct Simulation", "Create Tiff-Stack"}));
			comboBox.setMaximumSize(new Dimension(32767, 22));
			comboBox.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					JComboBox cb = (JComboBox) e.getSource();
					if (cb.getSelectedIndex() == 0){
						nativeSimulationPanel.setVisible(true);
						createTiffStackPanel.setVisible(false);
					}
					else{
						nativeSimulationPanel.setVisible(false);
						createTiffStackPanel.setVisible(true);
					}
				}
				
			});
			
			Component verticalStrut = Box.createVerticalStrut(20);
			verticalBox_23.add(verticalStrut);
			verticalBox_23.add(comboBox);
			
			createTiffStackPanel = new JPanel();
			verticalBox_23.add(createTiffStackPanel);
			createTiffStackPanel.setLayout(new CardLayout(0, 0));
			
			Box verticalBox_19 = Box.createVerticalBox();
			createTiffStackPanel.add(verticalBox_19, "name_11053082788701");
			createTiffStackPanel.setVisible(false);
			
			Box verticalBox_21 = Box.createVerticalBox();
			verticalBox_21.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "PSF Parameters", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
			verticalBox_19.add(verticalBox_21);
			
			Box horizontalBox_11 = Box.createHorizontalBox();
			verticalBox_21.add(horizontalBox_11);
			
			JLabel lblAveragePhotonOutput = new JLabel("Mean Photon Output");
			horizontalBox_11.add(lblAveragePhotonOutput);
			
			Component horizontalGlue_11 = Box.createHorizontalGlue();
			horizontalBox_11.add(horizontalGlue_11);
			
			averagePhotonOutputField = new JTextField();
			averagePhotonOutputField.getDocument().addDocumentListener(new MyDocumentListener(averagePhotonOutputField));
			averagePhotonOutputField.setHorizontalAlignment(SwingConstants.RIGHT);
			averagePhotonOutputField.setMaximumSize(new Dimension(100, 22));
			averagePhotonOutputField.setColumns(5);
			horizontalBox_11.add(averagePhotonOutputField);
			
			Box horizontalBox_47 = Box.createHorizontalBox();
			verticalBox_21.add(horizontalBox_47);
			
			JLabel lblNewLabel_11 = new JLabel("Minimal Photon Count");
			horizontalBox_47.add(lblNewLabel_11);
			
			Component horizontalGlue_40 = Box.createHorizontalGlue();
			horizontalBox_47.add(horizontalGlue_40);
			
			minIntensityField = new JTextField();
			minIntensityField.setHorizontalAlignment(SwingConstants.RIGHT);
			minIntensityField.setMaximumSize(new Dimension(100, 22));
			horizontalBox_47.add(minIntensityField);
			minIntensityField.setColumns(5);
			
			Box horizontalBox_43 = Box.createHorizontalBox();
			verticalBox_21.add(horizontalBox_43);
			
			JLabel lblMeanBlinkingDuration = new JLabel("Mean Blinking Duration in (s)");
			horizontalBox_43.add(lblMeanBlinkingDuration);
			
			Component horizontalGlue_44 = Box.createHorizontalGlue();
			horizontalBox_43.add(horizontalGlue_44);
			
			meanBlinkingDurationField = new JTextField();
			meanBlinkingDurationField.setHorizontalAlignment(SwingConstants.RIGHT);
			meanBlinkingDurationField.setMaximumSize(new Dimension(100, 22));
			
			meanBlinkingDurationField.setColumns(5);
			horizontalBox_43.add(meanBlinkingDurationField);
			
			Box verticalBox_20 = Box.createVerticalBox();
			verticalBox_20.setBorder(new TitledBorder(null, "Camera Parameters", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			verticalBox_19.add(verticalBox_20);
			
			Box horizontalBox_41 = Box.createHorizontalBox();
			verticalBox_20.add(horizontalBox_41);
			
			JLabel lblNewLabel_9 = new JLabel("Pixel To Nm Ratio");
			horizontalBox_41.add(lblNewLabel_9);
			
			Component horizontalGlue_42 = Box.createHorizontalGlue();
			horizontalBox_41.add(horizontalGlue_42);
			
			pxToNmField = new JTextField();
			pxToNmField.setHorizontalAlignment(SwingConstants.RIGHT);
			pxToNmField.setMaximumSize(new Dimension(100, 22));
			horizontalBox_41.add(pxToNmField);
			pxToNmField.setColumns(5);
			
			Box horizontalBox_42 = Box.createHorizontalBox();
			verticalBox_20.add(horizontalBox_42);
			
			JLabel lblNewLabel_10 = new JLabel("<html>Framerate (s<sup>-1</sup>)</html>");
			horizontalBox_42.add(lblNewLabel_10);
			
			Component horizontalGlue_43 = Box.createHorizontalGlue();
			horizontalBox_42.add(horizontalGlue_43);
			
			framerateField = new JTextField();
			framerateField.setHorizontalAlignment(SwingConstants.RIGHT);
			framerateField.setMaximumSize(new Dimension(100, 22));
			horizontalBox_42.add(framerateField);
			framerateField.setColumns(5);
			
			Box horizontalBox_48 = Box.createHorizontalBox();
			verticalBox_20.add(horizontalBox_48);
			
			JLabel lblSigmaBackground = new JLabel("Readout Noise (in DN)");
			horizontalBox_48.add(lblSigmaBackground);
			
			Component horizontalGlue_49 = Box.createHorizontalGlue();
			horizontalBox_48.add(horizontalGlue_49);
			
			sigmaBgField = new JTextField();
			sigmaBgField.setHorizontalAlignment(SwingConstants.RIGHT);
			sigmaBgField.setMaximumSize(new Dimension(100, 22));
			sigmaBgField.setColumns(5);
			horizontalBox_48.add(sigmaBgField);
			
			Box horizontalBox_49 = Box.createHorizontalBox();
			verticalBox_20.add(horizontalBox_49);
			
			JLabel lblConstantOffset = new JLabel("Constant Offset");
			horizontalBox_49.add(lblConstantOffset);
			
			Component horizontalGlue_50 = Box.createHorizontalGlue();
			horizontalBox_49.add(horizontalGlue_50);
			
			constOffsetField = new JTextField();
			constOffsetField.setHorizontalAlignment(SwingConstants.RIGHT);
			constOffsetField.setMaximumSize(new Dimension(100, 22));
			constOffsetField.setColumns(5);
			horizontalBox_49.add(constOffsetField);
			
			Box horizontalBox_53 = Box.createHorizontalBox();
			horizontalBox_53.setAlignmentY(Component.CENTER_ALIGNMENT);
			verticalBox_20.add(horizontalBox_53);
			
			JLabel lblEmGain = new JLabel("EM Gain");
			horizontalBox_53.add(lblEmGain);
			
			Component horizontalGlue_16 = Box.createHorizontalGlue();
			horizontalBox_53.add(horizontalGlue_16);
			
			emGainField = new JTextField();
			emGainField.setHorizontalAlignment(SwingConstants.RIGHT);
			emGainField.setMaximumSize(new Dimension(100, 22));
			emGainField.setColumns(5);
			horizontalBox_53.add(emGainField);
			
			Box horizontalBox_56 = Box.createHorizontalBox();
			verticalBox_20.add(horizontalBox_56);
			
			JLabel lblWindowsizePsfRendering = new JLabel("Quantum Efficiency");
			horizontalBox_56.add(lblWindowsizePsfRendering);
			
			Component horizontalGlue_52 = Box.createHorizontalGlue();
			horizontalBox_56.add(horizontalGlue_52);
			
			quantumEfficiencyField = new JTextField();
			horizontalBox_56.add(quantumEfficiencyField);
			quantumEfficiencyField.setHorizontalAlignment(SwingConstants.RIGHT);
			quantumEfficiencyField.setMaximumSize(new Dimension(100, 22));
			quantumEfficiencyField.setColumns(5);
			
			Box horizontalBox_51 = Box.createHorizontalBox();
			verticalBox_20.add(horizontalBox_51);
			
			JLabel lblNewLabel_12 = new JLabel("electrons/DN");
			horizontalBox_51.add(lblNewLabel_12);
			
			Component horizontalGlue_58 = Box.createHorizontalGlue();
			horizontalBox_51.add(horizontalGlue_58);
			
			electronsPerDnField = new JTextField();
			electronsPerDnField.setHorizontalAlignment(SwingConstants.RIGHT);
			electronsPerDnField.setMaximumSize(new Dimension(100, 22));
			horizontalBox_51.add(electronsPerDnField);
			electronsPerDnField.setColumns(5);
			
			Box verticalBox_12 = Box.createVerticalBox();
			verticalBox_12.setBorder(new TitledBorder(null, "Rendering Parameters", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			verticalBox_19.add(verticalBox_12);
			
			Box horizontalBox_54 = Box.createHorizontalBox();
			verticalBox_12.add(horizontalBox_54);
			
			JLabel lblWindowsizePsfRendering_1 = new JLabel("Windowsize PSF Rendering");
			horizontalBox_54.add(lblWindowsizePsfRendering_1);
			
			Component horizontalGlue_55 = Box.createHorizontalGlue();
			horizontalBox_54.add(horizontalGlue_55);
			
			windowsizePSFRenderingField = new JTextField();
			windowsizePSFRenderingField.setHorizontalAlignment(SwingConstants.RIGHT);
			windowsizePSFRenderingField.setMaximumSize(new Dimension(100, 22));
			windowsizePSFRenderingField.setColumns(5);
			horizontalBox_54.add(windowsizePSFRenderingField);
			
			Box horizontalBox_50 = Box.createHorizontalBox();
			verticalBox_12.add(horizontalBox_50);
			
			JLabel lblEmptyPixelsOn = new JLabel("Empty Pixels On Rim");
			horizontalBox_50.add(lblEmptyPixelsOn);
			
			Component horizontalGlue_51 = Box.createHorizontalGlue();
			horizontalBox_50.add(horizontalGlue_51);
			
			emptyPixelsOnRimField = new JTextField();
			emptyPixelsOnRimField.setHorizontalAlignment(SwingConstants.RIGHT);
			emptyPixelsOnRimField.setMaximumSize(new Dimension(100, 22));
			emptyPixelsOnRimField.setColumns(5);
			horizontalBox_50.add(emptyPixelsOnRimField);
			
			Component verticalStrut_1 = Box.createVerticalStrut(20);
			verticalBox_19.add(verticalStrut_1);
			
			tiffStackModeComboBox = new JComboBox();
			verticalBox_19.add(tiffStackModeComboBox);
			tiffStackModeComboBox.setMaximumSize(new Dimension(32767, 22));
			tiffStackModeComboBox.setModel(new DefaultComboBoxModel(new String[] {"2D PSFs", "3D PSFs"}));
			tiffStackModeComboBox.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					JComboBox cb = (JComboBox) e.getSource();
					if (cb.getSelectedIndex() == 0){
						twoDPSFPanel.setVisible(true);
						threeDPSFPanel.setVisible(false);
					}
					else{
						twoDPSFPanel.setVisible(false);
						threeDPSFPanel.setVisible(true);
					}
					
				}
				
			});
			
			twoDPSFPanel = new JPanel();
			verticalBox_19.add(twoDPSFPanel);
			twoDPSFPanel.setLayout(new CardLayout(0, 0));
			
			Box verticalBox_4 = Box.createVerticalBox();
			verticalBox_4.setAlignmentY(Component.TOP_ALIGNMENT);
			verticalBox_4.setAlignmentX(Component.CENTER_ALIGNMENT);
			twoDPSFPanel.add(verticalBox_4, "name_11840657027630");
			
			Box horizontalBox_44 = Box.createHorizontalBox();
			verticalBox_4.add(horizontalBox_44);
			
			JLabel lblNumericalAperture = new JLabel("Numerical Aperture");
			horizontalBox_44.add(lblNumericalAperture);
			
			Component horizontalGlue_45 = Box.createHorizontalGlue();
			horizontalBox_44.add(horizontalGlue_45);
			
			naField = new JTextField();
			naField.setHorizontalAlignment(SwingConstants.RIGHT);
			horizontalBox_44.add(naField);
			naField.setMaximumSize(new Dimension(100, 22));
			naField.setColumns(5);
			
			Component verticalStrut_2 = Box.createVerticalStrut(20);
			verticalStrut_2.setPreferredSize(new Dimension(0, 2));
			verticalStrut_2.setMinimumSize(new Dimension(0, 2));
			verticalBox_4.add(verticalStrut_2);
			
			Box horizontalBox_45 = Box.createHorizontalBox();
			verticalBox_4.add(horizontalBox_45);
			
			JLabel lblWavelength = new JLabel("Wavelength");
			horizontalBox_45.add(lblWavelength);
			
			Component horizontalGlue_46 = Box.createHorizontalGlue();
			horizontalBox_45.add(horizontalGlue_46);
			
			wavelengthField = new JTextField();
			wavelengthField.setHorizontalAlignment(SwingConstants.RIGHT);
			wavelengthField.setMaximumSize(new Dimension(100, 22));
			wavelengthField.setColumns(5);
			horizontalBox_45.add(wavelengthField);
			
			Component verticalStrut_4 = Box.createVerticalStrut(20);
			verticalStrut_4.setPreferredSize(new Dimension(0, 2));
			verticalStrut_4.setMinimumSize(new Dimension(0, 2));
			verticalBox_4.add(verticalStrut_4);
			
			Box horizontalBox_57 = Box.createHorizontalBox();
			verticalBox_4.add(horizontalBox_57);
			
			JLabel lblDefokus = new JLabel("Defocus");
			horizontalBox_57.add(lblDefokus);
			
			Component horizontalGlue_48 = Box.createHorizontalGlue();
			horizontalBox_57.add(horizontalGlue_48);
			
			defokusField = new JTextField();
			horizontalBox_57.add(defokusField);
			defokusField.setHorizontalAlignment(SwingConstants.RIGHT);
			defokusField.setMaximumSize(new Dimension(100, 22));
			defokusField.setColumns(5);
			
			Component verticalStrut_3 = Box.createVerticalStrut(20);
			verticalStrut_3.setPreferredSize(new Dimension(0, 2));
			verticalStrut_3.setMinimumSize(new Dimension(0, 2));
			verticalBox_4.add(verticalStrut_3);
			
			Box horizontalBox_46 = Box.createHorizontalBox();
			verticalBox_4.add(horizontalBox_46);
			
			JLabel lblFokus = new JLabel("Focus");
			horizontalBox_46.add(lblFokus);
			
			Component horizontalGlue_3 = Box.createHorizontalGlue();
			horizontalBox_46.add(horizontalGlue_3);
			
			fokusField = new JTextField();
			fokusField.setHorizontalAlignment(SwingConstants.RIGHT);
			fokusField.setMaximumSize(new Dimension(100, 22));
			fokusField.setColumns(5);
			horizontalBox_46.add(fokusField);
			
			Component verticalGlue_7 = Box.createVerticalGlue();
			verticalBox_4.add(verticalGlue_7);
			
			Component verticalGlue_9 = Box.createVerticalGlue();
			verticalBox_4.add(verticalGlue_9);
			
			Component verticalGlue_10 = Box.createVerticalGlue();
			verticalBox_4.add(verticalGlue_10);
			
			threeDPSFPanel = new JPanel();
			verticalBox_19.add(threeDPSFPanel);
			threeDPSFPanel.setVisible(false);
			
			Box verticalBox_18 = Box.createVerticalBox();
			threeDPSFPanel.add(verticalBox_18);
			
			JButton importCalibrationFileButton = new JButton("Import Calibration File");
			verticalBox_18.add(importCalibrationFileButton);
			importCalibrationFileButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					if (allDataSets.size()>0){
						JFileChooser fc = new JFileChooser();
						int returnValue = fc.showOpenDialog(getContentPane());
						if(returnValue == JFileChooser.APPROVE_OPTION) {
							String path = fc.getSelectedFile().getAbsolutePath();
							String name = fc.getSelectedFile().getName();
							CalibrationFileParser cfp = new CalibrationFileParser(path);
							try {
								allDataSets.get(currentRow).getParameterSet().setCalibrationFile(cfp.parse());
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
						
					}
				}
			});
			
			Box verticalBox_22 = Box.createVerticalBox();
			verticalBox_22.setBorder(new TitledBorder(null, "PSF Settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			verticalBox_19.add(verticalBox_22);
			
			Box horizontalBox_55 = Box.createHorizontalBox();
			verticalBox_22.add(horizontalBox_55);
			
			ensureSinglePSFchkBox = new JCheckBox("Ensure Single PSFs");
			horizontalBox_55.add(ensureSinglePSFchkBox);
			
			Component horizontalGlue_47 = Box.createHorizontalGlue();
			horizontalBox_55.add(horizontalGlue_47);
			
			distributePSFOverFrames = new JCheckBox("Distribute PSFs Over Frames");
			horizontalBox_55.add(distributePSFOverFrames);
			
			JButton exportTiffStackButton = new JButton("Export Tiffstack");
			exportTiffStackButton.setAlignmentX(Component.CENTER_ALIGNMENT);
			verticalBox_22.add(exportTiffStackButton);
			exportTiffStackButton.addActionListener(new ActionListener() {
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
						
						setUpRandomNumberGenerator();
						CreateTiffStack cts = new CreateTiffStack(allDataSets.get(currentRow), path, random, selfReference);
						cts.addPropertyChangeListener(selfReference);
						cts.execute();
//					CreateStack.createTiffStack(allDataSets.get(currentRow).stormData, 1/set.getPixelToNmRatio(),
//							set.getEmptyPixelsOnRim(),set.getEmGain(), borders, random,
//							set.getElectronPerAdCount(), set.getFrameRate(), set.getMeanBlinkingTime(), set.getWindowsizePSF(),
//							modelNumber,set.getQuantumEfficiency(), set.getNa(), set.getPsfwidth(), set.getFokus(), set.getDefokus(), set.getSigmaBg(),
//							set.getConstOffset(), set.getCalibrationFile(), path,set.isEnsureSinglePSF(), set.isDistributePSFoverFrames());
						FileManager.writeLogFile(allDataSets.get(currentRow).getParameterSet(), path.substring(0, path.length()-4)+"TiffStack",borders,true);
					}
					
				}
			});
			
			nativeSimulationPanel = new JPanel();
			verticalBox_23.add(nativeSimulationPanel);
			nativeSimulationPanel.setLayout(new CardLayout(0, 0));
			
			Box verticalBox_5 = Box.createVerticalBox();
			nativeSimulationPanel.add(verticalBox_5, "name_11059150642572");
			
			Box horizontalBox_12 = Box.createHorizontalBox();
			verticalBox_5.add(horizontalBox_12);
			
			JLabel lblLabelingResolutionXy = new JLabel("Localization Precision XY (nm)");
			horizontalBox_12.add(lblLabelingResolutionXy);
			
			Component horizontalGlue_13 = Box.createHorizontalGlue();
			horizontalBox_12.add(horizontalGlue_13);
			
			locPrecisionXYField = new JTextField();
			locPrecisionXYField.getDocument().addDocumentListener(new MyDocumentListener(locPrecisionXYField));
			locPrecisionXYField.setHorizontalAlignment(SwingConstants.RIGHT);
			locPrecisionXYField.setMaximumSize(new Dimension(60, 22));
			locPrecisionXYField.setColumns(5);
			horizontalBox_12.add(locPrecisionXYField);
			
			Box horizontalBox_13 = Box.createHorizontalBox();
			verticalBox_5.add(horizontalBox_13);
			
			JLabel lblLocalizaitonPrecisionZ = new JLabel("Localization Precision Z (nm)");
			horizontalBox_13.add(lblLocalizaitonPrecisionZ);
			
			Component horizontalGlue_14 = Box.createHorizontalGlue();
			horizontalBox_13.add(horizontalGlue_14);
			
			locPrecisionZField = new JTextField();
			locPrecisionZField.getDocument().addDocumentListener(new MyDocumentListener(locPrecisionZField));
			locPrecisionZField.setHorizontalAlignment(SwingConstants.RIGHT);
			locPrecisionZField.setMaximumSize(new Dimension(60, 22));
			locPrecisionZField.setColumns(5);
			horizontalBox_13.add(locPrecisionZField);
			
			Box horizontalBox_19 = Box.createHorizontalBox();
			verticalBox_5.add(horizontalBox_19);
			
			JLabel lblNewLabel_5 = new JLabel("Constant Localization Precision");
			horizontalBox_19.add(lblNewLabel_5);
			
			Component horizontalGlue_37 = Box.createHorizontalGlue();
			horizontalGlue_37.setSize(new Dimension(10, 0));
			horizontalBox_19.add(horizontalGlue_37);
			
			coupleSigmaIntensityBox = new JCheckBox("");
			coupleSigmaIntensityBox.setSelected(true);
			horizontalBox_19.add(coupleSigmaIntensityBox);
			
			Component horizontalGlue_18 = Box.createHorizontalGlue();
			horizontalBox_19.add(horizontalGlue_18);
			
			Box horizontalBox_38 = Box.createHorizontalBox();
			verticalBox_5.add(horizontalBox_38);
			
			JLabel lblDetectionEfficiency = new JLabel("Detection Efficiency (%)");
			horizontalBox_38.add(lblDetectionEfficiency);
			
			Component horizontalGlue_22 = Box.createHorizontalGlue();
			horizontalBox_38.add(horizontalGlue_22);
			
			detectionEfficiencyField = new JTextField();
			detectionEfficiencyField.getDocument().addDocumentListener(new MyDocumentListener(detectionEfficiencyField));
			detectionEfficiencyField.setMinimumSize(new Dimension(6, 10));
			detectionEfficiencyField.setMaximumSize(new Dimension(60, 22));
			detectionEfficiencyField.setHorizontalAlignment(SwingConstants.RIGHT);
			detectionEfficiencyField.setColumns(5);
			horizontalBox_38.add(detectionEfficiencyField);
			
			JButton calcButton = new JButton("Calculate");
			verticalBox_5.add(calcButton);
			calcButton.setAlignmentX(Component.CENTER_ALIGNMENT);
			
			Component verticalStrut_5 = Box.createVerticalStrut(20);
			verticalBox_5.add(verticalStrut_5);
			
			Box horizontalBox_52 = Box.createHorizontalBox();
			verticalBox_5.add(horizontalBox_52);
			
			JLabel lblNewLabel_13 = new JLabel("Pixelsize in Nm");
			horizontalBox_52.add(lblNewLabel_13);
			
			Component horizontalGlue_24 = Box.createHorizontalGlue();
			horizontalBox_52.add(horizontalGlue_24);
			
			pixelSizeField = new JTextField();
			pixelSizeField.setMaximumSize(new Dimension(60, 22));
			horizontalBox_52.add(pixelSizeField);
			pixelSizeField.setColumns(5);
			
			Box horizontalBox_58 = Box.createHorizontalBox();
			verticalBox_5.add(horizontalBox_58);
			
			JLabel lblNewLabel_14 = new JLabel("Width Of Rendered PSF (nm)");
			horizontalBox_58.add(lblNewLabel_14);
			
			Component horizontalGlue_38 = Box.createHorizontalGlue();
			horizontalBox_58.add(horizontalGlue_38);
			
			sigmaSizeField = new JTextField();
			sigmaSizeField.setMaximumSize(new Dimension(60, 22));
			horizontalBox_58.add(sigmaSizeField);
			sigmaSizeField.setColumns(5);
			
			JButton exportButton = new JButton("Export");
			exportButton.setAlignmentX(Component.CENTER_ALIGNMENT);
			verticalBox_5.add(exportButton);
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
						borders = getBorders();
						
						copyFields();
						FileManager.ExportToFile(allDataSets.get(currentRow), path, viewStatus,borders, 
								allDataSets.get(currentRow).getParameterSet().getPixelsize(),allDataSets.get(currentRow).getParameterSet().getSigmaRendering());
					}
				}
			});
			calcButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					calculate();
				}
			});
			
			Component verticalGlue_1 = Box.createVerticalGlue();
			verticalBox_23.add(verticalGlue_1);
			
			JPanel panel_1 = new JPanel();
			panel_2.add(panel_1, "name_8612779840202");
			
			Box verticalBox_13 = Box.createVerticalBox();
			tabbedPane_2.addTab("Visualization Parameter", null, verticalBox_13, null);
			
			Box verticalBox = Box.createVerticalBox();
			verticalBox.setMaximumSize(new Dimension(222222, 222220));
			verticalBox_13.add(verticalBox);
			verticalBox.setPreferredSize(new Dimension(250, 800));
			verticalBox.setMinimumSize(new Dimension(250, 600));
			verticalBox.setName("");
			verticalBox.setFont(new Font("Dialog", Font.ITALIC, 89));
			verticalBox.setAlignmentX(Component.CENTER_ALIGNMENT);
			
			Box horizontalBox_37 = Box.createHorizontalBox();
			verticalBox.add(horizontalBox_37);
			
			Box verticalBox_6 = Box.createVerticalBox();
			verticalBox_6.setBorder(new TitledBorder(null, "Visualization Parameter", TitledBorder.LEADING, TitledBorder.TOP, usedFont, null));
			verticalBox.add(verticalBox_6);
			
			JTabbedPane tabbedPane_1 = new JTabbedPane(JTabbedPane.TOP);
			tabbedPane_1.setMaximumSize(new Dimension(32767, 300));
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
			pointSizeField.getDocument().addDocumentListener(new MyDocumentListener(pointSizeField));
			pointSizeField.setHorizontalAlignment(SwingConstants.RIGHT);
			pointSizeField.setMinimumSize(new Dimension(6, 10));
			pointSizeField.setMaximumSize(new Dimension(60, 22));
			pointSizeField.setColumns(5);
			horizontalBox_16.add(pointSizeField);
			
			Component verticalGlue_2 = Box.createVerticalGlue();
			verticalBox_16.add(verticalGlue_2);
			
			Box horizontalBox_24 = Box.createHorizontalBox();
			verticalBox_16.add(horizontalBox_24);
			
			JLabel lblLineWidth = new JLabel("Line Width");
			horizontalBox_24.add(lblLineWidth);
			
			Component horizontalGlue_31 = Box.createHorizontalGlue();
			horizontalBox_24.add(horizontalGlue_31);
			
			lineWidthField = new JTextField();
			lineWidthField.getDocument().addDocumentListener(new MyDocumentListener(lineWidthField));
			lineWidthField.setMinimumSize(new Dimension(6, 10));
			lineWidthField.setMaximumSize(new Dimension(60, 22));
			lineWidthField.setHorizontalAlignment(SwingConstants.RIGHT);
			lineWidthField.setColumns(5);
			horizontalBox_24.add(lineWidthField);
			
			Component verticalGlue_3 = Box.createVerticalGlue();
			verticalBox_16.add(verticalGlue_3);
			
			Box horizontalBox_18 = Box.createHorizontalBox();
			verticalBox_16.add(horizontalBox_18);
			
			JLabel lblShowStormPoints = new JLabel("Show SMLM Points");
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
			
			Component verticalGlue_4 = Box.createVerticalGlue();
			verticalBox_16.add(verticalGlue_4);
			
			Box horizontalBox_20 = Box.createHorizontalBox();
			verticalBox_16.add(horizontalBox_20);
			
			JLabel lblShowAntibodies = new JLabel("Show Labels");
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
			
			Component verticalGlue_5 = Box.createVerticalGlue();
			verticalBox_16.add(verticalGlue_5);
			
			Box horizontalBox_21 = Box.createHorizontalBox();
			verticalBox_16.add(horizontalBox_21);
			
			JLabel lblShowEm = new JLabel("Show Structure");
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
			
			Component verticalGlue_6 = Box.createVerticalGlue();
			verticalBox_16.add(verticalGlue_6);
			
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
					verticalBox_8.setAlignmentY(Component.BOTTOM_ALIGNMENT);
					horizontalBox_30.add(verticalBox_8);
					
					JLabel lblRenderingQuality = new JLabel("Rendering Quality");
					verticalBox_8.add(lblRenderingQuality);
					
					
					radioNicest = new JRadioButton("Nicest");
					verticalBox_8.add(radioNicest);
					radioNicest.setSelected(false);
					
					radioAdvanced = new JRadioButton("Advanced");
					verticalBox_8.add(radioAdvanced);
					
					radioIntermediate = new JRadioButton("Intermediate");
					verticalBox_8.add(radioIntermediate);
					radioIntermediate.setSelected(true);
					
					radioFastest = new JRadioButton("Fastest");
					verticalBox_8.add(radioFastest);
					group.add(radioNicest);
					group.add(radioAdvanced);
					group.add(radioIntermediate);
					group.add(radioFastest);
					
					Component verticalGlue_8 = Box.createVerticalGlue();
					verticalBox_8.add(verticalGlue_8);
					
					keepBordersChkBox = new JCheckBox("Keep Borders");
					verticalBox_8.add(keepBordersChkBox);
					
					Component horizontalGlue_36 = Box.createHorizontalGlue();
					horizontalBox_30.add(horizontalGlue_36);
					
					Box verticalBox_15 = Box.createVerticalBox();
					verticalBox_15.setAlignmentY(Component.BOTTOM_ALIGNMENT);
					horizontalBox_30.add(verticalBox_15);
					
					JLabel lblNewLabel = new JLabel("x min (nm)");
					verticalBox_15.add(lblNewLabel);
					
					Box horizontalBox_31 = Box.createHorizontalBox();
					verticalBox_15.add(horizontalBox_31);
					
					xminField = new JTextField();
					xminField.getDocument().addDocumentListener(new MyDocumentListener(xminField));
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
						}
					});
					
					horizontalBox_31.add(xminSlider);
					
					JLabel lblNewLabel_1 = new JLabel("x max (nm)");
					verticalBox_15.add(lblNewLabel_1);
					
					Box horizontalBox_32 = Box.createHorizontalBox();
					verticalBox_15.add(horizontalBox_32);
					
					xmaxField = new JTextField();
					xmaxField.getDocument().addDocumentListener(new MyDocumentListener(xmaxField));
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
						}
					});
					horizontalBox_32.add(xmaxSlider);
					
					JLabel lblNewLabel_2 = new JLabel("y min (nm)");
					verticalBox_15.add(lblNewLabel_2);
					
					Box horizontalBox_33 = Box.createHorizontalBox();
					verticalBox_15.add(horizontalBox_33);
					
					yminField = new JTextField();
					yminField.getDocument().addDocumentListener(new MyDocumentListener(yminField));
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
						}
					});
					horizontalBox_33.add(yminSlider);
					
					JLabel lblNewLabel_3 = new JLabel("y max (nm)");
					verticalBox_15.add(lblNewLabel_3);
					
					Box horizontalBox_34 = Box.createHorizontalBox();
					verticalBox_15.add(horizontalBox_34);
					
					ymaxField = new JTextField();
					ymaxField.getDocument().addDocumentListener(new MyDocumentListener(ymaxField));
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
						}
					});
					horizontalBox_34.add(ymaxSlider);
					
					JLabel lblNewLabel_4 = new JLabel("z min (nm)");
					verticalBox_15.add(lblNewLabel_4);
					
					Box horizontalBox_35 = Box.createHorizontalBox();
					verticalBox_15.add(horizontalBox_35);
					
					zminField = new JTextField();
					zminField.getDocument().addDocumentListener(new MyDocumentListener(zminField));
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
						}
					});
					horizontalBox_35.add(zminSlider);
					
					JLabel lblZMin = new JLabel("z max (nm)");
					verticalBox_15.add(lblZMin);
					
					Box horizontalBox_36 = Box.createHorizontalBox();
					verticalBox_15.add(horizontalBox_36);
					
					zmaxField = new JTextField();
					zmaxField.getDocument().addDocumentListener(new MyDocumentListener(zmaxField));
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
			
			saveViewpointButton = new JToggleButton("Fix Viewpoint");
			saveViewpointButton.setAlignmentX(Component.CENTER_ALIGNMENT);
			verticalBox.add(saveViewpointButton);
			
			JButton visButton = new JButton("Visualize");
			verticalBox.add(visButton);
			visButton.setAlignmentX(Component.CENTER_ALIGNMENT);
			
			Component verticalGlue = Box.createVerticalGlue();
			verticalBox.add(verticalGlue);
			
			Box horizontalBox_39 = Box.createHorizontalBox();
			verticalBox.add(horizontalBox_39);
			
			visButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
//				Thread t = new Thread(){
//					@Override
//					public void run(){
							visualize(); 
//					}
//				};
//				t.start();
				}
			});
			dataSetTable.getColumnModel().getColumn(0).setMinWidth(100);
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
		plotPanel.add(loadDataLabel, BorderLayout.SOUTH);
		
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
		
		Component rigidArea = Box.createRigidArea(new Dimension(20, 20));
		toolBar.add(rigidArea);
		
		toolBar.add(saveProjectButton);
		
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
		
		Component horizontalGlue_25 = Box.createHorizontalGlue();
		toolBar.add(horizontalGlue_25);
		toolBar.add(exampleComboBox);
		
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
		
		Component rigidArea_4 = Box.createRigidArea(new Dimension(20, 20));
		toolBar.add(rigidArea_4);
		toolBar.add(aboutButton);
		
		JPanel panel_3 = new JPanel();
		getContentPane().add(panel_3, BorderLayout.SOUTH);
		panel_3.setLayout(new CardLayout(0, 0));
		
		Box horizontalBox_17 = Box.createHorizontalBox();
		panel_3.add(horizontalBox_17, "name_13635831669473");
		
		JLabel lblNewLabel_6 = new JLabel("Number Of Visible Localizations:  ");
		horizontalBox_17.add(lblNewLabel_6);
		
		numberOfVisibleLocalizationsLabel = new JLabel("0");
		horizontalBox_17.add(numberOfVisibleLocalizationsLabel);
		
		Component horizontalGlue_53 = Box.createHorizontalGlue();
		horizontalBox_17.add(horizontalGlue_53);
		configureTableListener();
		
		calc = new STORMCalculator(null,null);
		calc.addListener((ThreadCompleteListener)this);
		nt = new CreatePlot(null);
	}
	
	
	protected void updateCounter() {
		int nbrVisLocs = 0;
		if (allDataSets.get(currentRow).stormData!= null){
			nbrVisLocs = Calc.countVisibleLocs(borders, allDataSets.get(currentRow));
		}
		numberOfVisibleLocalizationsLabel.setText(nbrVisLocs+"");
		
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
		setSelectedListsForDrawing();
		//visualizeAllSelectedData();
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
		data.setProgressBar(this.progressBar);
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
			if (allowShift){
				ArrayList<Float> shifts = new ArrayList<Float>();
				if (allDataSets.get(0).dataType == DataType.LINES){
					LineDataSet lines = (LineDataSet) allDataSets.get(0);
					shifts = Calc.findShiftLines(lines.data);
					shiftX = -shifts.get(0);
					shiftY = -shifts.get(1);
					shiftZ = -shifts.get(2);
				}
				else if (allDataSets.get(0).dataType == DataType.TRIANGLES){
					TriangleDataSet triangles = (TriangleDataSet) allDataSets.get(0);
					shifts = Calc.findShiftTriangles(triangles.primitives);
					shiftX = -shifts.get(0);
					shiftY = -shifts.get(1);
					shiftZ = -shifts.get(2);
				}
				else if (allDataSets.get(0).dataType == DataType.POINTS){
					
				}
			}
			else{
				shiftX = 0;
				shiftY =0;
				shiftZ=0;
			}
		}
		if (allDataSets.get(allDataSets.size()-1).dataType == DataType.LINES){
			LineDataSet lines = (LineDataSet) allDataSets.get(allDataSets.size()-1);
			lines.shiftData(shiftX,shiftY,shiftZ);
		}
		else if (allDataSets.get(allDataSets.size()-1).dataType == DataType.TRIANGLES){
			TriangleDataSet triangles = (TriangleDataSet) allDataSets.get(allDataSets.size()-1);
			triangles.shiftData(shiftX,shiftY,shiftZ);
		}
		if (allDataSets.size()==1){
			allDataSets.get(0).getParameterSet().setGeneralVisibility(Boolean.TRUE);
			//visualize();
		}
		model.fireTableDataChanged();
		setSelectedListsForDrawing();
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
			angularDistributionField.setText(String.format(Locale.ENGLISH,"%.2f", set.getSoa()*180.f/Math.PI));
			detectionEfficiencyField.setText(String.format(Locale.ENGLISH,"%.2f",set.getDeff()*100)); //pabs
			backgroundLabelField.setText(set.getIlpmm3().toString()); //ilpmm3 aus StormPointFinder                                                                   
			labelLengthField.setText(set.getLoa().toString()); //loa                                                                                               
			fluorophoresPerLabelField.setText(set.getFpab().toString()); //fpab                                                                                     
			dutyCycleField.setText(set.getDutyCycle().toString()); //kOn
			bleachConstantField.setText(set.getBleachConst().toString());
			recordedFramesField.setText(String.valueOf(set.getFrames())); //frames
			averagePhotonOutputField.setText(Integer.toString(set.getMeanPhotonNumber()));                                                                                             
			locPrecisionXYField.setText(set.getSxy().toString()); //sxy                                                                                            
			locPrecisionZField.setText(set.getSz().toString()); //sz   
			wavelengthField.setText(set.getPsfwidth().toString()); //psfwidth aus StormPointFinder         
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
			
			applyBleachBox.setSelected(set.getApplyBleaching());
			coupleSigmaIntensityBox.setSelected(!set.getCoupleSigmaIntensity());
			pxToNmField.setText(String.format(Locale.ENGLISH,"%.2f",set.getPixelToNmRatio()));
			framerateField.setText(Double.toString(set.getFrameRate()));
			sigmaBgField.setText(String.format(Locale.ENGLISH,"%.2f",set.getSigmaBg()));
			constOffsetField.setText(Double.toString(set.getConstOffset()));
			emGainField.setText(Double.toString(set.getEmGain()));
			quantumEfficiencyField.setText(String.format(Locale.ENGLISH,"%.2f",set.getQuantumEfficiency()));
			windowsizePSFRenderingField.setText(Integer.toString(set.getWindowsizePSF()));
			emptyPixelsOnRimField.setText(Integer.toString(set.getEmptyPixelsOnRim()));
			naField.setText(String.format(Locale.ENGLISH,"%.4f",set.getNa()));
			fokusField.setText(Double.toString(set.getFokus()));
			defokusField.setText(Double.toString(set.getDefokus()));
			if (set.isTwoDPSF()){
				tiffStackModeComboBox.setSelectedIndex(0);
			}
			else{
				tiffStackModeComboBox.setSelectedIndex(1);
			}
			electronsPerDnField.setText(Float.toString(set.getElectronPerAdCount()));
			meanBlinkingDurationField.setText(Float.toString(set.getMeanBlinkingTime()));
			ensureSinglePSFchkBox.setSelected(set.isEnsureSinglePSF());
			distributePSFOverFrames.setSelected(set.isDistributePSFoverFrames());
			minIntensityField.setText(set.getMinIntensity()+"");
			sigmaSizeField.setText(set.getSigmaRendering()+"");
			pixelSizeField.setText(set.getPixelsize()+"");
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
	}
	
	/**
	 * invoked by calculate button
	 * runs the calculation for the current dataset
	 * @throws Exception 
	 */
	private void calculate(){
		calculate(allDataSets.get(currentRow),false);
	}
	
	void calculate(DataSet currDataSet, boolean forTiffStack) {
		setUpRandomNumberGenerator();
		if (allDataSets.size()<1){
			return;
		}
		copyFields();
		if (forTiffStack){
			currDataSet.getParameterSet().setSxy(0.0001f);
			currDataSet.getParameterSet().setSz(0.0001f); //set localization precision to 0 to get the fluorophore centers instead of STORM localizations
		}
		calc = new STORMCalculator(currDataSet,this.random);
		//calc = new STORMCalculator(allDataSets.get(currentRow));
		calc.addListener(this);
		calc.addPropertyChangeListener(this);
		calc.execute();
		// When calc has finished, grab the new dataset
		//allDataSets.set(currentRow, calc.getCurrentDataSet());
		//visualizeAllSelectedData();
	}
	

	private void setUpRandomNumberGenerator() {
		if (reproducibleOutputchkBox.isSelected()){
			this.random = new Random(2);
		} else {
			this.random = new Random(System.currentTimeMillis());
		}
	}
	
//	public double getNextRandomDoubleNumber(){
//		return this.random.nextDouble();
//	}
//	public int getNextRandomIntNumber(){
//		return this.random.nextInt();
//	}

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
			setSelectedListsForDrawing();
			//visualizeAllSelectedData();
			updateCounter();
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
			
	}
	
	@Override
	public void tableChanged(TableModelEvent e) {
		//visualizeAllSelectedData();
		//setSelectedListsForDrawing();
	}
	
	/**
//	 * Checks which data sets are generally visible and creates a new Plot3D with the dataSets
//	 */
	public void setSelectedListsForDrawing() {
		List<DataSet> sets = new ArrayList<DataSet>();
		for(int i = 0; i < model.data.size(); i++) {
			allDataSets.get(i).setProgressBar(progressBar);
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
			nt = new CreatePlot(plot);
//			nt.addListener((ProgressBarUpdateListener)this);
			nt.addListener((ThreadCompleteListener)this);
			//nt.addPropertyChangeListener(this);
			System.out.println("nt.exec");
//			calc = new STORMCalculator(this.allDataSets.get(currentRow), random);
//			calc.execute();
			progressBar.setToolTipText("Visualizing...");
			nt.doInBackground();
			
//			plot.dataSets.clear();
//			plot.addAllDataSets(sets);
//			plotPanel.removeAll();
//			plot.createChart();
//			graphComponent = (Component) plot.createChart().getCanvas();
//			plotPanel.add(graphComponent);
//			plotPanel.revalidate();
//			plotPanel.repaint();
//			graphComponent.revalidate();
//			Thread t = new Thread(){
//			@Override
//				public void run(){
//					plot.run();
//				}
//			};
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			t.start();
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
//	
	private void visualizeAllSelectedData() {
		List<DataSet> sets = new ArrayList<DataSet>();
		model.visibleSets.clear();
		for(int i = 0; i < allDataSets.size(); i++) {
			allDataSets.get(currentRow).setProgressBar(progressBar);
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
//			plot.dataSets.clear();
//			plot.addAllDataSets(sets);
//			plotPanel.removeAll();
//			plot.createChart();
//			graphComponent = (Component) plot.createChart().getCanvas();
//			plotPanel.add(graphComponent);
//			plotPanel.revalidate();
//			plotPanel.repaint();
//			graphComponent.revalidate();
			plot.dataSets.clear();
			plot.addAllDataSets(sets);
			plotPanel.removeAll();
			nt = new CreatePlot(plot);
//			nt.addListener((ProgressBarUpdateListener)this);
//			nt.addListener((ThreadCompleteListener)this);
			//nt.addPropertyChangeListener(this);
			System.out.println("do in bg");
			nt.doInBackground();
//			Thread t = new Thread(){
//				@Override
//				public void run(){
//					plot.run();
//				}
//			};
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			t.start();
		
//			try{
//				plot.run();
//			}
//			catch (IllegalThreadStateException e){System.out.println("Thread already started");}
			
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
	
	ArrayList<Float> getBorders(){
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
				xminField.setText(String.format(Locale.ENGLISH,"%.1f", maxDims.get(0)-1));
				xmaxField.setText(String.format(Locale.ENGLISH,"%.1f", maxDims.get(1)+1));
				yminField.setText(String.format(Locale.ENGLISH,"%.1f", maxDims.get(2)-1));
				ymaxField.setText(String.format(Locale.ENGLISH,"%.1f", maxDims.get(3)+1));
				zminField.setText(String.format(Locale.ENGLISH,"%.1f", maxDims.get(4)-1));
				zmaxField.setText(String.format(Locale.ENGLISH,"%.1f", maxDims.get(5)+1));
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
			s.setProgressBar(this.progressBar);
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
		if ("progress".equals(evt.getPropertyName())) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
		}
	}
	
	class MyDocumentListener implements DocumentListener{
		JTextField tf;
		MyDocumentListener(JTextField tf){
			this.tf = tf;
		}
		  public void changedUpdate(DocumentEvent e) {
			  SwingUtilities.invokeLater(warn);
		  }
		  public void removeUpdate(DocumentEvent e) {
			  SwingUtilities.invokeLater(warn);
		  }
		  public void insertUpdate(DocumentEvent e) {
			  SwingUtilities.invokeLater(warn);
		  }
		  Runnable warn = new Runnable() {
		        @Override
		        public void run() {
		        	try{ Float.parseFloat(tf.getText());}
					catch (NumberFormatException e){
					   JOptionPane.showMessageDialog(null,
					      "Error: Please enter a valid number", "Error Massage",
					      JOptionPane.ERROR_MESSAGE);
					   tf.setText("0");
					}		        
	        	}
		    };       
		   

	}

	@Override
	public void notifyOfThreadComplete(Object obj) {
		if (obj.getClass().equals(CreatePlot.class)){
			graphComponent = (Component) plot.getChart().getCanvas();
			plotPanel.add(graphComponent);
			plotPanel.revalidate();
			plotPanel.repaint();
			graphComponent.revalidate();
		}
		if(obj.getClass().equals(STORMCalculator.class)){
			setSelectedListsForDrawing();
		}
	}
	
	
	public void copyFields(){
		allDataSets.get(currentRow).getParameterSet().setPabs((float) (new Float(labelingEfficiencyField.getText())/100.));
		allDataSets.get(currentRow).getParameterSet().setAoa((float) ((new Float(meanAngleField.getText()))/180*Math.PI));
		allDataSets.get(currentRow).getParameterSet().setSoa((float) ((new Float(angularDistributionField.getText()))/180*Math.PI));
		allDataSets.get(currentRow).getParameterSet().setDeff((float) (new Float(detectionEfficiencyField.getText())/100)); 
		allDataSets.get(currentRow).getParameterSet().setIlpmm3(new Float(backgroundLabelField.getText()));
		allDataSets.get(currentRow).getParameterSet().setLoa(new Float(labelLengthField.getText()));
		allDataSets.get(currentRow).getParameterSet().setFpab(new Float(fluorophoresPerLabelField.getText()));
		allDataSets.get(currentRow).getParameterSet().setDutyCycle(new Float(dutyCycleField.getText()));
		allDataSets.get(currentRow).getParameterSet().setBleachConst(new Float(bleachConstantField.getText()));
		allDataSets.get(currentRow).getParameterSet().setFrames((new Float(recordedFramesField.getText())).intValue());
		allDataSets.get(currentRow).getParameterSet().setSxy(new Float(locPrecisionXYField.getText()));
		allDataSets.get(currentRow).getParameterSet().setSz(new Float(locPrecisionZField.getText()));
		allDataSets.get(currentRow).getParameterSet().setPsfwidth(new Float(wavelengthField.getText()));
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
		allDataSets.get(currentRow).getParameterSet().setApplyBleaching(applyBleachBox.isSelected());
		allDataSets.get(currentRow).getParameterSet().setCoupleSigmaIntensity(!coupleSigmaIntensityBox.isSelected());
		
		allDataSets.get(currentRow).getParameterSet().setPixelToNmRatio(new Float(pxToNmField.getText()));
		allDataSets.get(currentRow).getParameterSet().setFrameRate(new Float(framerateField.getText()));
		allDataSets.get(currentRow).getParameterSet().setSigmaBg(new Float(sigmaBgField.getText()));
		allDataSets.get(currentRow).getParameterSet().setConstOffset(new Float(constOffsetField.getText()));
		allDataSets.get(currentRow).getParameterSet().setEmGain(new Float(emGainField.getText()));
		allDataSets.get(currentRow).getParameterSet().setQuantumEfficiency(new Float(quantumEfficiencyField.getText()));
		allDataSets.get(currentRow).getParameterSet().setWindowsizePSF(new Integer(windowsizePSFRenderingField.getText()));
		allDataSets.get(currentRow).getParameterSet().setEmptyPixelsOnRim(new Integer(emptyPixelsOnRimField.getText()));
		allDataSets.get(currentRow).getParameterSet().setNa(new Float(naField.getText()));
		allDataSets.get(currentRow).getParameterSet().setFokus(new Float(fokusField.getText()));
		allDataSets.get(currentRow).getParameterSet().setDefokus(new Float(defokusField.getText()));
		if (tiffStackModeComboBox.getSelectedIndex() == 0){
			allDataSets.get(currentRow).getParameterSet().setTwoDPSF(true);
		}
		else{
			allDataSets.get(currentRow).getParameterSet().setTwoDPSF(false);
		}
		allDataSets.get(currentRow).getParameterSet().setElectronPerAdCount(new Float(electronsPerDnField.getText()));
		allDataSets.get(currentRow).getParameterSet().setMeanBlinkingTime(new Float(meanBlinkingDurationField.getText()));
		allDataSets.get(currentRow).getParameterSet().setDistributePSFoverFrames(distributePSFOverFrames.isSelected());
		allDataSets.get(currentRow).getParameterSet().setEnsureSinglePSF(ensureSinglePSFchkBox.isSelected());
		allDataSets.get(currentRow).getParameterSet().setMinIntensity(new Integer(minIntensityField.getText()));
		allDataSets.get(currentRow).getParameterSet().setPixelsize(new Float(pixelSizeField.getText()));
		allDataSets.get(currentRow).getParameterSet().setSigmaRendering(new Float(sigmaSizeField.getText()));
	}

	
}
