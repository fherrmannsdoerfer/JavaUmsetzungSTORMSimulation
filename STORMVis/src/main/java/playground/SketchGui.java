package playground;

import editor.Editor;
import gui.DataTypeDetector;
import gui.DataTypeDetector.DataType;
import gui.ParserWrapper;
import gui.TriangleLineFilter;
import inout.FileManager;
import inout.ProjectFileFilter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.rendering.canvas.Quality;

import model.DataSet;
import model.ParameterSet;
import model.Project;
import model.SerializableImage;
import table.DataSetTableModel;
import calc.STORMCalculator;

import javax.swing.JToggleButton;


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
	private JTextField radiusOfFilamentsField; //rof
	private JTextField labelingEfficiencyField; //pabs
	private JTextField meanAngleField; //aoa
	private JTextField backgroundLabelField; //ilpmm3 aus StormPointFinder
	private JTextField labelLengthField; //loa
	private JTextField fluorophoresPerLabelField; //fpab
	private JTextField averageBlinkingNumberField; //abpf
	private JTextField averagePhotonOutputField; // TODO: ???
	private JTextField locPrecisionXYField; //sxy
	private JTextField locPrecisionZField; //sz
	private JTextField psfSizeField; //psfwidth aus StormPointFinder
	private JTextField epitopeDensityField; //bspnm oder bspsnm je nachdem ob Linien oder Dreiecke
	private JTextField pointSizeField; //das muesste der Parameter a aus Plotter new Color(coord.x/255.f,coord.y/255.f,coord.z/255.f,a); sein
	
	private JCheckBox showEmBox;
	private JCheckBox showStormPointsBox;
	private JCheckBox showAntibodiesBox;
	private JCheckBox mergePSFBox;
	
	private final JLabel loadDataLabel = new JLabel("Please import data or select a representation.");
	private JTable dataSetTable;
	private DataSetTableModel model;
	private Plot3D plot;
	private JPanel plotPanel;
	private Component graphComponent;
	
	private STORMCalculator calc;
	
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
	
	private JCheckBox squaredCoordBox;
	private JRadioButton radioNicest;
	private JRadioButton radioAdvanced;
	private JRadioButton radioIntermediate;
	private JRadioButton radioFastest;
	private JToggleButton saveViewpointButton;

	/**
	 * file extension for storm project files
	 */
	private static String EXTENSION = ".storm";
	private JTextField textField;
	private JTextField recordedFramesField;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SketchGui frame = new SketchGui();
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
	public SketchGui() {
		int fontSize = 16;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1200, 1200);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		contentPane.setPreferredSize(new Dimension(288, 1080));

		Box verticalBox = Box.createVerticalBox();
		verticalBox.setName("");
		verticalBox.setFont(new Font("Dialog", Font.ITALIC, 89));
		verticalBox.setBounds(12, 183, 240, 866);
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
		
		JLabel lblAverageBlinkingNumber = new JLabel("k off time");
		horizontalBox_10.add(lblAverageBlinkingNumber);
		
		Component horizontalGlue_10 = Box.createHorizontalGlue();
		horizontalBox_10.add(horizontalGlue_10);
		
		averageBlinkingNumberField = new JTextField();
		averageBlinkingNumberField.setMinimumSize(new Dimension(6, 10));
		averageBlinkingNumberField.setMaximumSize(new Dimension(60, 22));
		averageBlinkingNumberField.setColumns(5);
		horizontalBox_10.add(averageBlinkingNumberField);
		
		Box horizontalBox_17 = Box.createHorizontalBox();
		verticalBox_3.add(horizontalBox_17);
		
		JLabel lblKOnTime = new JLabel("k on time");
		horizontalBox_17.add(lblKOnTime);
		
		Component horizontalGlue_3 = Box.createHorizontalGlue();
		horizontalBox_17.add(horizontalGlue_3);
		
		textField = new JTextField();
		textField.setMinimumSize(new Dimension(6, 10));
		textField.setMaximumSize(new Dimension(60, 22));
		textField.setColumns(5);
		horizontalBox_17.add(textField);
		
		Box horizontalBox_22 = Box.createHorizontalBox();
		verticalBox_3.add(horizontalBox_22);
		
		JLabel lblRecordedFrames = new JLabel("recorded frames");
		horizontalBox_22.add(lblRecordedFrames);
		
		Component horizontalGlue_26 = Box.createHorizontalGlue();
		horizontalBox_22.add(horizontalGlue_26);
		
		recordedFramesField = new JTextField();
		recordedFramesField.setMinimumSize(new Dimension(6, 10));
		recordedFramesField.setMaximumSize(new Dimension(60, 22));
		recordedFramesField.setColumns(5);
		horizontalBox_22.add(recordedFramesField);
		
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
		
		mergePSFBox = new JCheckBox("");
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
		
		Box horizontalBox_18 = Box.createHorizontalBox();
		verticalBox_6.add(horizontalBox_18);
		
		JLabel lblShowStormPoints = new JLabel("show STORM Points");
		horizontalBox_18.add(lblShowStormPoints);
		
		Component horizontalGlue_7 = Box.createHorizontalGlue();
		horizontalBox_18.add(horizontalGlue_7);
		
		stormColorButton = new JButton("");
		stormColorButton.setPreferredSize(new Dimension(40,20));
		stormColorButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				allDataSets.get(currentRow).getParameterSet().setStormColor(JColorChooser.showDialog(getContentPane(), "Choose color for Storm points", allDataSets.get(currentRow).getParameterSet().getStormColor()));
				updateButtonColors();
			}
		});
		horizontalBox_18.add(stormColorButton);
		
		showStormPointsBox = new JCheckBox("");
		horizontalBox_18.add(showStormPointsBox);
		
		Box horizontalBox_20 = Box.createHorizontalBox();
		verticalBox_6.add(horizontalBox_20);
		
		JLabel lblShowAntibodies = new JLabel("show Antibodies");
		horizontalBox_20.add(lblShowAntibodies);
		
		Component horizontalGlue_12 = Box.createHorizontalGlue();
		horizontalBox_20.add(horizontalGlue_12);
		
		antibodyColorButton = new JButton("");
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
		horizontalBox_20.add(showAntibodiesBox);
		
		Box horizontalBox_21 = Box.createHorizontalBox();
		verticalBox_6.add(horizontalBox_21);
		
		JLabel lblShowEm = new JLabel("show EM");
		horizontalBox_21.add(lblShowEm);
		
		Component horizontalGlue_23 = Box.createHorizontalGlue();
		horizontalBox_21.add(horizontalGlue_23);
		
		emColorButton = new JButton("");
		emColorButton.setPreferredSize(new Dimension(40, 20));
		emColorButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				allDataSets.get(currentRow).getParameterSet().setEmColor(JColorChooser.showDialog(getContentPane(), "Choose color for EM", allDataSets.get(currentRow).getParameterSet().getEmColor()));
				updateButtonColors();
			}
		});
		horizontalBox_21.add(emColorButton);
		
		showEmBox = new JCheckBox("");
		horizontalBox_21.add(showEmBox);
		
		squaredCoordBox = new JCheckBox("squared coordinate system");
		squaredCoordBox.setAlignmentX(Component.CENTER_ALIGNMENT);
		verticalBox_6.add(squaredCoordBox);
		squaredCoordBox.setSelected(true);

		
		Box verticalBox_8 = Box.createVerticalBox();
		verticalBox_8.setAlignmentX(Component.CENTER_ALIGNMENT);
		verticalBox_6.add(verticalBox_8);
		verticalBox_8.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Plot Quality", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		Box horizontalBox_19 = Box.createHorizontalBox();
		verticalBox_8.add(horizontalBox_19);
		
		Component horizontalGlue_25 = Box.createHorizontalGlue();
		horizontalBox_19.add(horizontalGlue_25);
		
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
		

		model = new DataSetTableModel();
        dataSetTable = new JTable(model);
        dataSetTable.getColumnModel().getColumn(0).setMinWidth(100);
        model.addTableModelListener(this);
        
		dataSetTable.setBounds(12, 12, 240, 166);
		contentPane.add(dataSetTable);
		
		Box horizontalBox_23 = Box.createHorizontalBox();
		verticalBox.add(horizontalBox_23);
		
		Component horizontalGlue_29 = Box.createHorizontalGlue();
		horizontalBox_23.add(horizontalGlue_29);
		
		JButton xyViewButton = new JButton("xy");
		xyViewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setViewPoint(0.,Math.PI/2);
			}
		});
		horizontalBox_23.add(xyViewButton);
		
		Component horizontalGlue_27 = Box.createHorizontalGlue();
		horizontalBox_23.add(horizontalGlue_27);
		
		JButton xzViewButton = new JButton("xz");
		xzViewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setViewPoint(Math.PI/2,0);
			}
		});
		horizontalBox_23.add(xzViewButton);
		
		Component horizontalGlue_28 = Box.createHorizontalGlue();
		horizontalBox_23.add(horizontalGlue_28);
		
		JButton yzViewButton = new JButton("yz");
		yzViewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setViewPoint(0,0);
			}
		});
		horizontalBox_23.add(yzViewButton);
		
		Component horizontalGlue_30 = Box.createHorizontalGlue();
		horizontalBox_23.add(horizontalGlue_30);
		
		JButton visButton = new JButton("visualize");
		verticalBox.add(visButton);
		visButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		visButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				visualize();
			}
		});
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(contentPane);
		JScrollPane jsp = new JScrollPane(contentPane);
		
		saveViewpointButton = new JToggleButton("Save Viewpoint");
		saveViewpointButton.setBounds(12, 1062, 161, 29);
		contentPane.add(saveViewpointButton);
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
					loadedImage = p.getOriginalImage();
					allDataSets.clear();
					model.data.clear();
					model.visibleSets.clear();
					allDataSets.addAll(p.dataSets);
					for(DataSet s : allDataSets) {
						model.visibleSets.add(s.getParameterSet().generalVisibility);
					}
					model.data.addAll(p.dataSets);
					System.out.println("Number of dss: " + allDataSets.size());
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							visualizeAllSelectedData();
						}
					});
				}
			}
		});
		
		plot = new Plot3D();
		configureTableListener();
		
		JButton openEditorButton = new JButton("Open editor");
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
		toolBar.add(openEditorButton);
		
		Component horizontalGlue_24 = Box.createHorizontalGlue();
		toolBar.add(horizontalGlue_24);
		
		JButton saveProjectButton = new JButton("Save project");
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
					Project p = new Project(name, allDataSets);
					p.setOriginalImage(loadedImage);
					FileManager.writeProjectToFile(p, path);
				}
			}
		});
		toolBar.add(saveProjectButton);
		calc = new STORMCalculator(null);
	}
	
	
	private void setViewPoint(double sigma, double theta) {
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
			radiusOfFilamentsField.setText(set.rof.toString()); //rof                                                                                         
			labelingEfficiencyField.setText(set.pabs.toString()); //pabs                                                                                       
			meanAngleField.setText(set.aoa.toString()); //aoa     
			backgroundLabelField.setText(set.ilpmm3.toString()); //ilpmm3 aus StormPointFinder                                                                   
			labelLengthField.setText(set.sxy.toString()); //loa                                                                                               
			fluorophoresPerLabelField.setText(set.fpab.toString()); //fpab                                                                                     
			averageBlinkingNumberField.setText(set.abpf.toString()); //abpf
			// TODO: ??
			//		averagePhotonOutputField.setText(set.sxy.toString());                                                                                             
			locPrecisionXYField.setText(set.sxy.toString()); //sxy                                                                                            
			locPrecisionZField.setText(set.sz.toString()); //sz   
			psfSizeField.setText(set.psfwidth.toString()); //psfwidth aus StormPointFinder                                                                         
			if(allDataSets.get(row).dataType == DataType.LINES) {
				epitopeDensityField.setText(set.bspnm.toString());
			}
			else {
				epitopeDensityField.setText(set.bspsnm.toString());
			}
			
			pointSizeField.setText(set.pointSize.toString());

			showAntibodiesBox.setSelected(set.getAntibodyVisibility());
			showEmBox.setSelected(set.getEmVisibility());
			showStormPointsBox.setSelected(set.getStormVisibility());

			emColorButton.setBackground(set.getEmColor());
			stormColorButton.setBackground(set.getStormColor());
			antibodyColorButton.setBackground(set.getAntibodyColor());


			emColorButton.setOpaque(true);
			stormColorButton.setOpaque(true);
			antibodyColorButton.setOpaque(true);
		}
	}
	
	private void updateButtonColors() {
		ParameterSet set = allDataSets.get(currentRow).parameterSet;
		emColorButton.setBackground(set.getEmColor());
		stormColorButton.setBackground(set.getStormColor());
		antibodyColorButton.setBackground(set.getAntibodyColor());
		
		
		emColorButton.setOpaque(true);
		stormColorButton.setOpaque(true);
		antibodyColorButton.setOpaque(true);
	}
	
	/**
	 * invoked by calculate button
	 * runs the calculation for the current dataset
	 * @throws Exception 
	 */
	private void calculate() {
		System.out.println("bspsnm: " + allDataSets.get(currentRow).getParameterSet().getBspsnm());
		allDataSets.get(currentRow).getParameterSet().setRof(new Float(radiusOfFilamentsField.getText()));
		allDataSets.get(currentRow).getParameterSet().setPabs(new Float(labelingEfficiencyField.getText()));
		allDataSets.get(currentRow).getParameterSet().setAoa(new Float(meanAngleField.getText()));
		allDataSets.get(currentRow).getParameterSet().setIlpmm3(new Float(backgroundLabelField.getText()));
		allDataSets.get(currentRow).getParameterSet().setLoa(new Float(labelLengthField.getText()));
		allDataSets.get(currentRow).getParameterSet().setFpab(new Float(fluorophoresPerLabelField.getText()));
		allDataSets.get(currentRow).getParameterSet().setAbpf(new Float(averageBlinkingNumberField.getText()));
		allDataSets.get(currentRow).getParameterSet().setSxy(new Float(locPrecisionXYField.getText()));
		allDataSets.get(currentRow).getParameterSet().setSz(new Float(locPrecisionZField.getText()));
		allDataSets.get(currentRow).getParameterSet().setPsfwidth(new Float(psfSizeField.getText()));
		if(allDataSets.get(currentRow).dataType == DataType.LINES) {
			allDataSets.get(currentRow).getParameterSet().setBspnm(new Float(epitopeDensityField.getText()));
		}
		else {
			allDataSets.get(currentRow).getParameterSet().setBspsnm(new Float(epitopeDensityField.getText()));
		}
		allDataSets.get(currentRow).getParameterSet().setPointSize(new Float(pointSizeField.getText()));
		
		allDataSets.get(currentRow).getParameterSet().setMergedPSF(mergePSFBox.isSelected());
		
		calc = new STORMCalculator(allDataSets.get(currentRow));
		calc.startCalculation();
		// When calc has finished, grab the new dataset
		allDataSets.set(currentRow, calc.getCurrentDataSet());
		visualizeAllSelectedData();
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
	 * sets the plot quality according to the radiobuttons
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
	
	private void setViewPointAndScale() {
		System.out.println("VP: " + plot.currentChart.getViewPoint().toString());
		System.out.println("scale: " + plot.currentChart.getScale().toString());
		
		plot.viewPoint = plot.currentChart.getViewPoint();
		plot.viewBounds = plot.currentChart.getView().getBounds();
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
			if(allDataSets.get(i).getParameterSet().generalVisibility == true) {
				model.visibleSets.add(Boolean.TRUE);
				sets.add(model.data.get(i));
			}
			else {
				model.visibleSets.add(Boolean.FALSE);
			}
		}
		model.data.clear();
		model.data.addAll(allDataSets);
		
		plot.squared = squaredCoordBox.isSelected();
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
}