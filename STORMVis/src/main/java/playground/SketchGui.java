package playground;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;


public class SketchGui extends JFrame {

	private JPanel contentPane;
	private JTextField textField_1;
	private JTextField textField_4;
	private JTextField textField_5;
	private JTextField textField_6;
	private JTextField textField_8;
	private JTextField textField_9;
	private JTextField textField_10;
	private JTextField textField_11;
	private JTextField textField_12;
	private JTextField textField_13;
	private JTextField textField_15;
	private JTextField textField_2;
	private JTextField textField;
	private JTextField textField_3;
	private JTextField textField_14;
	private JTextField textField_16;

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
		setBounds(100, 100, 288, 970);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
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
		
		textField_2 = new JTextField();
		textField_2.setMinimumSize(new Dimension(6, 10));
		textField_2.setMaximumSize(new Dimension(60, 22));
		textField_2.setColumns(5);
		horizontalBox.add(textField_2);
		
		Box horizontalBox_1 = Box.createHorizontalBox();
		verticalBox_1.add(horizontalBox_1);
		
		JLabel lblRadiusOfFilaments = new JLabel("radius of filaments");
		horizontalBox_1.add(lblRadiusOfFilaments);
		
		Component horizontalGlue_1 = Box.createHorizontalGlue();
		horizontalBox_1.add(horizontalGlue_1);
		
		textField_1 = new JTextField();
		textField_1.setMinimumSize(new Dimension(6, 10));
		textField_1.setMaximumSize(new Dimension(60, 22));
		textField_1.setColumns(5);
		horizontalBox_1.add(textField_1);
		
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
		
		textField_4 = new JTextField();
		textField_4.setMinimumSize(new Dimension(6, 10));
		textField_4.setMaximumSize(new Dimension(60, 22));
		textField_4.setColumns(5);
		horizontalBox_4.add(textField_4);
		
		Box horizontalBox_5 = Box.createHorizontalBox();
		verticalBox_2.add(horizontalBox_5);
		
		JLabel lblMeanAngle = new JLabel("mean angle");
		horizontalBox_5.add(lblMeanAngle);
		
		Component horizontalGlue_5 = Box.createHorizontalGlue();
		horizontalBox_5.add(horizontalGlue_5);
		
		textField_5 = new JTextField();
		textField_5.setMinimumSize(new Dimension(6, 10));
		textField_5.setMaximumSize(new Dimension(60, 22));
		textField_5.setColumns(5);
		horizontalBox_5.add(textField_5);
		
		Box horizontalBox_6 = Box.createHorizontalBox();
		verticalBox_2.add(horizontalBox_6);
		
		JLabel lblBackgroundLabel = new JLabel("background label");
		horizontalBox_6.add(lblBackgroundLabel);
		
		Component horizontalGlue_6 = Box.createHorizontalGlue();
		horizontalBox_6.add(horizontalGlue_6);
		
		textField_6 = new JTextField();
		textField_6.setMinimumSize(new Dimension(6, 10));
		textField_6.setMaximumSize(new Dimension(60, 22));
		textField_6.setColumns(5);
		horizontalBox_6.add(textField_6);
		
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
		
		textField_8 = new JTextField();
		textField_8.setMinimumSize(new Dimension(6, 10));
		textField_8.setMaximumSize(new Dimension(60, 22));
		textField_8.setColumns(5);
		horizontalBox_8.add(textField_8);
		
		Box horizontalBox_9 = Box.createHorizontalBox();
		verticalBox_3.add(horizontalBox_9);
		
		JLabel lblFluorophoresPerLabel = new JLabel("fluorophores per label");
		horizontalBox_9.add(lblFluorophoresPerLabel);
		
		Component horizontalGlue_9 = Box.createHorizontalGlue();
		horizontalBox_9.add(horizontalGlue_9);
		
		textField_9 = new JTextField();
		textField_9.setMinimumSize(new Dimension(6, 10));
		textField_9.setMaximumSize(new Dimension(60, 22));
		textField_9.setColumns(5);
		horizontalBox_9.add(textField_9);
		
		Box horizontalBox_10 = Box.createHorizontalBox();
		verticalBox_3.add(horizontalBox_10);
		
		JLabel lblAverageBlinkingNumber = new JLabel("average blinking number");
		horizontalBox_10.add(lblAverageBlinkingNumber);
		
		Component horizontalGlue_10 = Box.createHorizontalGlue();
		horizontalBox_10.add(horizontalGlue_10);
		
		textField_10 = new JTextField();
		textField_10.setMinimumSize(new Dimension(6, 10));
		textField_10.setMaximumSize(new Dimension(60, 22));
		textField_10.setColumns(5);
		horizontalBox_10.add(textField_10);
		
		Box horizontalBox_11 = Box.createHorizontalBox();
		verticalBox_3.add(horizontalBox_11);
		
		JLabel lblAveragePhotonOutput = new JLabel("average photon output");
		horizontalBox_11.add(lblAveragePhotonOutput);
		
		Component horizontalGlue_11 = Box.createHorizontalGlue();
		horizontalBox_11.add(horizontalGlue_11);
		
		textField_11 = new JTextField();
		textField_11.setMinimumSize(new Dimension(6, 10));
		textField_11.setMaximumSize(new Dimension(60, 22));
		textField_11.setColumns(5);
		horizontalBox_11.add(textField_11);
		
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
		
		textField_12 = new JTextField();
		textField_12.setMinimumSize(new Dimension(6, 10));
		textField_12.setMaximumSize(new Dimension(60, 22));
		textField_12.setColumns(5);
		horizontalBox_12.add(textField_12);
		
		Box horizontalBox_13 = Box.createHorizontalBox();
		verticalBox_4.add(horizontalBox_13);
		
		JLabel lblLocalizaitonPrecisionZ = new JLabel("localizaiton precision z");
		horizontalBox_13.add(lblLocalizaitonPrecisionZ);
		
		Component horizontalGlue_14 = Box.createHorizontalGlue();
		horizontalBox_13.add(horizontalGlue_14);
		
		textField_13 = new JTextField();
		textField_13.setMinimumSize(new Dimension(6, 10));
		textField_13.setMaximumSize(new Dimension(60, 22));
		textField_13.setColumns(5);
		horizontalBox_13.add(textField_13);
		
		Box horizontalBox_14 = Box.createHorizontalBox();
		verticalBox_4.add(horizontalBox_14);
		
		JLabel lblMergeClosePsfs = new JLabel("Merge close PSFs");
		horizontalBox_14.add(lblMergeClosePsfs);
		
		Component horizontalGlue_17 = Box.createHorizontalGlue();
		horizontalBox_14.add(horizontalGlue_17);
		
		JCheckBox chckbxNewCheckBox = new JCheckBox("");
		horizontalBox_14.add(chckbxNewCheckBox);
		
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
		
		textField_15 = new JTextField();
		textField_15.setMinimumSize(new Dimension(6, 10));
		textField_15.setMaximumSize(new Dimension(60, 22));
		textField_15.setColumns(5);
		horizontalBox_15.add(textField_15);
		
		JButton btnNewButton = new JButton("calculate");
		btnNewButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnNewButton.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		verticalBox.add(btnNewButton);
		
		Box verticalBox_6 = Box.createVerticalBox();
		verticalBox_6.setBorder(new TitledBorder(null, "Visualization Parameter", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		verticalBox.add(verticalBox_6);
		
		Box horizontalBox_16 = Box.createHorizontalBox();
		verticalBox_6.add(horizontalBox_16);
		
		JLabel lblPointSize = new JLabel("Point size");
		horizontalBox_16.add(lblPointSize);
		
		Component horizontalGlue_2 = Box.createHorizontalGlue();
		horizontalBox_16.add(horizontalGlue_2);
		
		textField = new JTextField();
		textField.setMinimumSize(new Dimension(6, 10));
		textField.setMaximumSize(new Dimension(60, 22));
		textField.setColumns(5);
		horizontalBox_16.add(textField);
		
		Box horizontalBox_17 = Box.createHorizontalBox();
		verticalBox_6.add(horizontalBox_17);
		
		JLabel lblColorRgb = new JLabel("color rgb");
		horizontalBox_17.add(lblColorRgb);
		
		Component horizontalGlue_3 = Box.createHorizontalGlue();
		horizontalBox_17.add(horizontalGlue_3);
		
		textField_14 = new JTextField();
		textField_14.setMinimumSize(new Dimension(6, 10));
		textField_14.setMaximumSize(new Dimension(60, 22));
		textField_14.setColumns(3);
		horizontalBox_17.add(textField_14);
		
		textField_16 = new JTextField();
		textField_16.setMinimumSize(new Dimension(6, 10));
		textField_16.setMaximumSize(new Dimension(60, 22));
		textField_16.setColumns(3);
		horizontalBox_17.add(textField_16);
		
		textField_3 = new JTextField();
		textField_3.setMinimumSize(new Dimension(6, 10));
		textField_3.setMaximumSize(new Dimension(60, 22));
		textField_3.setColumns(3);
		horizontalBox_17.add(textField_3);
		
		Box horizontalBox_18 = Box.createHorizontalBox();
		verticalBox_6.add(horizontalBox_18);
		
		JLabel lblShowStormPoints = new JLabel("show STORM Points");
		horizontalBox_18.add(lblShowStormPoints);
		
		Component horizontalGlue_7 = Box.createHorizontalGlue();
		horizontalBox_18.add(horizontalGlue_7);
		
		JCheckBox checkBox = new JCheckBox("");
		horizontalBox_18.add(checkBox);
		
		Box horizontalBox_20 = Box.createHorizontalBox();
		verticalBox_6.add(horizontalBox_20);
		
		JLabel lblShowAntibodies = new JLabel("show Antibodies");
		horizontalBox_20.add(lblShowAntibodies);
		
		Component horizontalGlue_12 = Box.createHorizontalGlue();
		horizontalBox_20.add(horizontalGlue_12);
		
		JCheckBox checkBox_1 = new JCheckBox("");
		horizontalBox_20.add(checkBox_1);
		
		Box horizontalBox_21 = Box.createHorizontalBox();
		verticalBox_6.add(horizontalBox_21);
		
		JLabel lblShowEm = new JLabel("show EM");
		horizontalBox_21.add(lblShowEm);
		
		Component horizontalGlue_23 = Box.createHorizontalGlue();
		horizontalBox_21.add(horizontalGlue_23);
		
		JCheckBox checkBox_2 = new JCheckBox("");
		horizontalBox_21.add(checkBox_2);
		
		JButton btnVisualize = new JButton("visualize");
		btnVisualize.setAlignmentX(Component.CENTER_ALIGNMENT);
		verticalBox.add(btnVisualize);
		
		JEditorPane dtrpnHierListeMit = new JEditorPane();
		dtrpnHierListeMit.setText("Hier Liste mit datasets einfuegen");
		dtrpnHierListeMit.setBounds(12, 27, 240, 139);
		contentPane.add(dtrpnHierListeMit);
	}
}
