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
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;
import java.awt.CardLayout;


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
	private JTextField radiusOfFilamentsField; //rof
	private JTextField labelingEfficiencyField; //pabs
	private JTextField meanAngleField; //aoa
	private JTextField backgroundLabelField; //ilpmm3 aus StormPointFinder
	private JTextField labelLengthField; //loa
	private JTextField fluorophoresPerLabelField; //fpab
	private JTextField kOnField; //abpf
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
	private JProgressBar progressBar;
	
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
	
	private JCheckBox squaredCoordBox;
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
	
	int viewStatus = 0;
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
		setBounds(10, 10, 1200, 900);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setPreferredSize(new Dimension(288, 500));
		
		ButtonGroup group = new ButtonGroup();
		

		model = new DataSetTableModel();
        model.addTableModelListener(this);
		
		JPanel panel = new JPanel();
		panel.setLayout(new CardLayout(0, 0));
		panel.add(contentPane);
		JScrollPane jsp = new JScrollPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));
		
		Box verticalBox_13 = Box.createVerticalBox();
		contentPane.add(verticalBox_13);
		dataSetTable = new JTable(model);
		dataSetTable.setPreferredSize(new Dimension(150, 250));
		verticalBox_13.add(dataSetTable);
		
				Box verticalBox = Box.createVerticalBox();
				verticalBox.setMaximumSize(new Dimension(222222, 222220));
				verticalBox_13.add(verticalBox);
				verticalBox.setPreferredSize(new Dimension(290, 800));
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
		
		JLabel lblNewLabel = new JLabel("Epitope Density (nm^-2)");
		horizontalBox.add(lblNewLabel);
		
		Component horizontalGlue = Box.createHorizontalGlue();
		horizontalBox.add(horizontalGlue);
		
		epitopeDensityField = new JTextField();
		epitopeDensityField.setHorizontalAlignment(SwingConstants.RIGHT);
		epitopeDensityField.setMinimumSize(new Dimension(6, 10));
		epitopeDensityField.setMaximumSize(new Dimension(60, 22));
		epitopeDensityField.setColumns(5);
		horizontalBox.add(epitopeDensityField);
		
		Component verticalGlue_4 = Box.createVerticalGlue();
		verticalBox_1.add(verticalGlue_4);
		
		Box horizontalBox_1 = Box.createHorizontalBox();
		verticalBox_1.add(horizontalBox_1);
		
		JLabel lblRadiusOfFilaments = new JLabel("Radius Of Filaments (nm)");
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
		
		JLabel lblLabelingEfficiency = new JLabel("Labeling Efficiency");
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
		verticalBox_3.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Label Dependent Parameters", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		
		Box horizontalBox_8 = Box.createHorizontalBox();
		verticalBox_3.add(horizontalBox_8);
		
		JLabel lblMeanDistanceLabel = new JLabel("Label Length (nm)");
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
		verticalBox_4.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Setup And Label Dependent Parameters", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		
		Box horizontalBox_12 = Box.createHorizontalBox();
		verticalBox_4.add(horizontalBox_12);
		
		JLabel lblLabelingResolutionXy = new JLabel("Localizaton Precision XY");
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
		
		JLabel lblLocalizaitonPrecisionZ = new JLabel("Localizaiton Precision Z");
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
		
		Box verticalBox_9 = Box.createVerticalBox();
		tabbedPane.addTab("Advanced Settings", null, verticalBox_9, null);
		
		Box verticalBox_10 = Box.createVerticalBox();
		verticalBox_10.setBorder(new TitledBorder(null, "Label Dependent Settings", TitledBorder.LEADING, TitledBorder.TOP, usedFont, null));
		verticalBox_9.add(verticalBox_10);
		
		Box horizontalBox_5 = Box.createHorizontalBox();
		verticalBox_10.add(horizontalBox_5);
		
		JLabel lblMeanAngle = new JLabel("Mean Binding Angle");
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
		
		JLabel lblAverageBlinkingNumber = new JLabel("k_on Time");
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
		
		JLabel lblKOnTime = new JLabel("k_off Time");
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
		
		Box horizontalBox_22 = Box.createHorizontalBox();
		verticalBox_12.add(horizontalBox_22);
		
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
		
		Component verticalGlue_11 = Box.createVerticalGlue();
		verticalBox_12.add(verticalGlue_11);
		
		Box horizontalBox_11 = Box.createHorizontalBox();
		verticalBox_12.add(horizontalBox_11);
		
		JLabel lblAveragePhotonOutput = new JLabel("Average Photon Output");
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
		
		Box verticalBox_11 = Box.createVerticalBox();
		verticalBox_11.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Background And Distortions", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		verticalBox_9.add(verticalBox_11);
		
		Box horizontalBox_6 = Box.createHorizontalBox();
		verticalBox_11.add(horizontalBox_6);
		
		JLabel lblBackgroundLabel = new JLabel("background label");
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
		
		JLabel lblPsfSize = new JLabel("PSF Size");
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
		
		Box horizontalBox_16 = Box.createHorizontalBox();
		verticalBox_6.add(horizontalBox_16);
		
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
		verticalBox_6.add(verticalGlue_20);
		
		Box horizontalBox_24 = Box.createHorizontalBox();
		verticalBox_6.add(horizontalBox_24);
		
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
		verticalBox_6.add(verticalGlue_17);
		
		Box horizontalBox_18 = Box.createHorizontalBox();
		verticalBox_6.add(horizontalBox_18);
		
		JLabel lblShowStormPoints = new JLabel("Show STORM Points");
		horizontalBox_18.add(lblShowStormPoints);
		
		Component horizontalGlue_7 = Box.createHorizontalGlue();
		horizontalBox_18.add(horizontalGlue_7);
		
		stormColorButton = new JButton("");
		stormColorButton.setMinimumSize(new Dimension(33, 20));
		stormColorButton.setMaximumSize(new Dimension(33, 20));
		stormColorButton.setPreferredSize(new Dimension(40, 40));
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
		horizontalBox_20.add(showAntibodiesBox);
		
		Box horizontalBox_21 = Box.createHorizontalBox();
		verticalBox_6.add(horizontalBox_21);
		
		JLabel lblShowEm = new JLabel("Show EM");
		horizontalBox_21.add(lblShowEm);
		
		Component horizontalGlue_23 = Box.createHorizontalGlue();
		horizontalBox_21.add(horizontalGlue_23);
		
		emColorButton = new JButton("");
		emColorButton.setMinimumSize(new Dimension(33, 20));
		emColorButton.setMaximumSize(new Dimension(33, 20));
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
		
		Box verticalBox_14 = Box.createVerticalBox();
		verticalBox_6.add(verticalBox_14);
		
		Box horizontalBox_25 = Box.createHorizontalBox();
		verticalBox_14.add(horizontalBox_25);
		
		chckbxShowAxes = new JCheckBox("Show Axes");
		chckbxShowAxes.setSelected(true);
		horizontalBox_25.add(chckbxShowAxes);
		chckbxShowAxes.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		Component horizontalGlue_32 = Box.createHorizontalGlue();
		horizontalBox_25.add(horizontalGlue_32);
		
		Box horizontalBox_26 = Box.createHorizontalBox();
		verticalBox_14.add(horizontalBox_26);
		
		squaredCoordBox = new JCheckBox("Squared Coordinate System");
		horizontalBox_26.add(squaredCoordBox);
		squaredCoordBox.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		Component horizontalGlue_33 = Box.createHorizontalGlue();
		horizontalBox_26.add(horizontalGlue_33);
		
				
				Box verticalBox_8 = Box.createVerticalBox();
				verticalBox_8.setAlignmentX(Component.CENTER_ALIGNMENT);
				verticalBox_6.add(verticalBox_8);
				
				Box horizontalBox_19 = Box.createHorizontalBox();
				verticalBox_8.add(horizontalBox_19);
				
				Component horizontalGlue_25 = Box.createHorizontalGlue();
				horizontalBox_19.add(horizontalGlue_25);
				
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
				
				saveViewpointButton = new JToggleButton("Save Viewpoint");
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
		jsp.setPreferredSize(new Dimension(290, 400));
		panel.add(jsp, "name_652625437088073");
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
		
		JButton importFileButton = new JButton("Import File");
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
		toolBar.add(openEditorButton);
		
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
					Project p = new Project(name, allDataSets);
					p.setOriginalImage(loadedImage);
					FileManager.writeProjectToFile(p, path);
				}
			}
		});
		
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
					FileManager.writeProjectionToFile(allDataSets.get(currentRow).stormData, path, viewStatus);
				}
			}
		});
		toolBar.add(exportButton);
		
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
			radiusOfFilamentsField.setText(set.getRof().toString()); //rof                                                                                         
			labelingEfficiencyField.setText(set.getPabs().toString()); //pabs                                                                                       
			meanAngleField.setText(set.getAoa().toString()); //aoa     
			backgroundLabelField.setText(set.getIlpmm3().toString()); //ilpmm3 aus StormPointFinder                                                                   
			labelLengthField.setText(set.getSxy().toString()); //loa                                                                                               
			fluorophoresPerLabelField.setText(set.getFpab().toString()); //fpab                                                                                     
			kOnField.setText(set.getKOn().toString()); //kOn
			kOffField.setText(set.getKOff().toString()); //kOff
			recordedFramesField.setText(String.valueOf(set.getFrames())); //frames
			// TODO: ??
			//		averagePhotonOutputField.setText(set.sxy.toString());                                                                                             
			locPrecisionXYField.setText(set.sxy.toString()); //sxy                                                                                            
			locPrecisionZField.setText(set.sz.toString()); //sz   
			psfSizeField.setText(set.psfwidth.toString()); //psfwidth aus StormPointFinder         
			lineWidthField.setText(set.lineWidth.toString());
			if(allDataSets.get(row).dataType == DataType.LINES) {
				System.out.println(set.bspnm);
				epitopeDensityField.setText(String.format(Locale.ENGLISH,"%.4f", set.bspnm));
			}
			else {
				epitopeDensityField.setText(String.format(Locale.ENGLISH,"%.4f",set.bspsnm));
			}
			
			pointSizeField.setText(set.pointSize.toString());

			showAntibodiesBox.setSelected(set.getAntibodyVisibility());
			showEmBox.setSelected(set.getEmVisibility());
			showStormPointsBox.setSelected(set.getStormVisibility());

			emColorButton.setBackground(set.getEmColor());
			stormColorButton.setBackground(set.getStormColor());
			antibodyColorButton.setBackground(set.getAntibodyColor());

			//emColorButton.setContentAreaFilled(false);
			stormColorButton.setContentAreaFilled(false);
			antibodyColorButton.setContentAreaFilled(false);

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
		
		//emColorButton.setContentAreaFilled(false);
		stormColorButton.setContentAreaFilled(false);
		antibodyColorButton.setContentAreaFilled(false);
		
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
		allDataSets.get(currentRow).setProgressBar(progressBar);
		System.out.println("bspsnm: " + allDataSets.get(currentRow).getParameterSet().getBspsnm());
		allDataSets.get(currentRow).getParameterSet().setRof(new Float(radiusOfFilamentsField.getText()));
		allDataSets.get(currentRow).getParameterSet().setPabs(new Float(labelingEfficiencyField.getText()));
		allDataSets.get(currentRow).getParameterSet().setAoa(new Float(meanAngleField.getText()));
		allDataSets.get(currentRow).getParameterSet().setIlpmm3(new Float(backgroundLabelField.getText()));
		allDataSets.get(currentRow).getParameterSet().setLoa(new Float(labelLengthField.getText()));
		allDataSets.get(currentRow).getParameterSet().setFpab(new Float(fluorophoresPerLabelField.getText()));
		allDataSets.get(currentRow).getParameterSet().setKOn(new Float(kOnField.getText()));
		allDataSets.get(currentRow).getParameterSet().setKOff(new Float(kOffField.getText()));
		allDataSets.get(currentRow).getParameterSet().setFrames(new Integer(recordedFramesField.getText()));
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
		allDataSets.get(currentRow).getParameterSet().setLineWidth(new Float(lineWidthField.getText()));
		allDataSets.get(currentRow).getParameterSet().setMergedPSF(mergePSFBox.isSelected());
		
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
		plot.chartQuality = Quality.Intermediate;
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
		plot.showBox = chckbxShowAxes.isSelected();
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

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
		}
	}
}