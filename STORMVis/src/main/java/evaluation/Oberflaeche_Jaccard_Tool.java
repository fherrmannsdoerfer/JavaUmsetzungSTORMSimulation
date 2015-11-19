package evaluation;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
  *
  * Beschreibung
  *
  * @version 1.0 vom 16.11.2015
  * @author 
  */

public class Oberflaeche_Jaccard_Tool extends JFrame {
  // Anfang Attribute
  private JButton jButton1 = new JButton();
  private JLabel jLabel1 = new JLabel();
  private JTextField jTextField1 = new JTextField();
  private JTextField jTextField2 = new JTextField();
  private JLabel jLabel2 = new JLabel();
  private JTextField jTextField3 = new JTextField();
  private JTextField jTextField4 = new JTextField();
  private JLabel jLabel3 = new JLabel();
  private JLabel jLabel4 = new JLabel();
  private JLabel jLabel5 = new JLabel();
  private JLabel jLabel6 = new JLabel();
  private JLabel jLabel7 = new JLabel();
  private JLabel jLabel8 = new JLabel();
  private JLabel jLabel9 = new JLabel();
  private JLabel jLabel10 = new JLabel();
  private JLabel jLabel11 = new JLabel();
  private JTextField jTextField5 = new JTextField();
  private JTextField jTextField6 = new JTextField();
  private JTextField jTextField7 = new JTextField();
  private JTextField jTextField8 = new JTextField();
  private JTextField jTextField9 = new JTextField();
  private JTextField shiftxField;
  private JTextField shiftyField;
  private final JLabel lblNewLabel_2 = new JLabel("Delta X");
  private final JLabel lblNewLabel_3 = new JLabel("Delta Y");
  private final JLabel lblNewLabel_4 = new JLabel("RMSE XY");
  private final JTextField textField = new JTextField();
  private final JTextField textField_1 = new JTextField();
  private final JTextField textField_2 = new JTextField();
  private final JLabel label = new JLabel("RMSE total");
  private final JTextField textField_3 = new JTextField();
  // Ende Attribute
  
  public Oberflaeche_Jaccard_Tool(String title) { 
    // Frame-Initialisierung
    super(title);
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    int frameWidth = 632; 
    int frameHeight = 292;
    setSize(1000, 292);
    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    int x = (d.width - getSize().width) / 2;
    int y = (d.height - getSize().height) / 2;
    setLocation(x, y);
    setResizable(false);
    Container cp = getContentPane();
    cp.setLayout(null);
    // Anfang Komponenten
    
    jButton1.setBounds(32, 128, 107, 25);
    jButton1.setText("Evaluate");
    jButton1.setMargin(new Insets(2, 2, 2, 2));
    jButton1.addActionListener(new ActionListener() { 
      public void actionPerformed(ActionEvent evt) { 
        jButton1_ActionPerformed(evt);
        String sTolXY = jTextField1.getText();
        float tolXY = Float.parseFloat(sTolXY);
        String sTolZ = jTextField2.getText();
        float tolZ = Float.parseFloat(sTolZ);
        float shiftx = Float.parseFloat(shiftxField.getText());
        float shifty = Float.parseFloat(shiftyField.getText());
        int[] p = {0,1,2,3,4}; //default permutation vector
        float[] results = JaccardCalculator.calcMeasure(jTextField3.getText(), jTextField4.getText(), tolXY, tolZ, shiftx, shifty, p, p);
        String rP = Integer.toString((int) results[2]);
        jTextField5.setText(rP);
        String tP = Integer.toString((int) results[3]);
        jTextField6.setText(tP);
        String fP = Integer.toString((int) results[4]);
        jTextField7.setText(fP);
        String jac = Float.toString(results[5]);
        jTextField8.setText(jac);
        String f = Float.toString(results[6]);
        jTextField9.setText(f);
        textField.setText(Float.toString(results[7]));
        textField_1.setText(Float.toString(results[8]));
        textField_2.setText(Float.toString(results[9]));
        textField_3.setText(Float.toString(results[10]));
        
      }
    });
    cp.add(jButton1);
    jLabel1.setBounds(16, 16, 70, 20);
    jLabel1.setText("tolerance");
    cp.add(jLabel1);
    jTextField1.setBounds(98, 48, 54, 20);
    cp.add(jTextField1);
    jTextField2.setBounds(98, 88, 54, 20);
    cp.add(jTextField2);
    jLabel2.setBounds(510, 16, 78, 20);
    jLabel2.setText("file names");
    cp.add(jLabel2);
    jTextField3.setBounds(546, 48, 254, 20);
    cp.add(jTextField3);
    jTextField4.setBounds(546, 88, 254, 20);
    cp.add(jTextField4);
    jLabel3.setBounds(56, 48, 46, 20);
    jLabel3.setText("xy");
    cp.add(jLabel3);
    jLabel4.setBounds(56, 88, 46, 20);
    jLabel4.setText("z");
    cp.add(jLabel4);
    jLabel5.setBounds(432, 48, 102, 20);
    jLabel5.setText("groundtruth");
    cp.add(jLabel5);
    jLabel6.setBounds(432, 88, 102, 20);
    jLabel6.setText("reconstruction");
    cp.add(jLabel6);
    jLabel7.setBounds(36, 176, 102, 20);
    jLabel7.setText("recognised points");
    cp.add(jLabel7);
    jLabel8.setBounds(142, 176, 78, 20);
    jLabel8.setText("true positive");
    cp.add(jLabel8);
    jLabel9.setBounds(248, 176, 86, 20);
    jLabel9.setText("false positive");
    cp.add(jLabel9);
    jLabel10.setBounds(354, 176, 94, 20);
    jLabel10.setText("Jaccard-index");
    cp.add(jLabel10);
    jLabel11.setBounds(460, 176, 94, 20);
    jLabel11.setText("f-score");
    cp.add(jLabel11);
    jTextField5.setBounds(36, 208, 70, 20);
    cp.add(jTextField5);
    jTextField6.setBounds(142, 208, 70, 20);
    cp.add(jTextField6);
    jTextField7.setBounds(248, 208, 70, 20);
    cp.add(jTextField7);
    jTextField8.setBounds(354, 208, 70, 20);
    cp.add(jTextField8);
    jTextField9.setBounds(460, 208, 70, 20);
    cp.add(jTextField9);
    
    JLabel lblNewLabel = new JLabel("shift X");
    lblNewLabel.setBounds(266, 50, 56, 16);
    getContentPane().add(lblNewLabel);
    
    shiftxField = new JTextField();
    shiftxField.setBounds(316, 47, 61, 22);
    getContentPane().add(shiftxField);
    shiftxField.setColumns(5);
    
    JLabel lblNewLabel_1 = new JLabel("shift y");
    lblNewLabel_1.setBounds(266, 90, 56, 16);
    getContentPane().add(lblNewLabel_1);
    
    shiftyField = new JTextField();
    shiftyField.setBounds(316, 87, 61, 22);
    getContentPane().add(shiftyField);
    shiftyField.setColumns(5);
    lblNewLabel_2.setBounds(566, 178, 56, 16);
    
    getContentPane().add(lblNewLabel_2);
    lblNewLabel_3.setBounds(672, 178, 56, 16);
    
    getContentPane().add(lblNewLabel_3);
    lblNewLabel_4.setBounds(778, 178, 70, 16);
    
    getContentPane().add(lblNewLabel_4);
    textField.setBounds(566, 208, 70, 20);
    
    getContentPane().add(textField);
    textField_1.setBounds(672, 208, 70, 20);
    
    getContentPane().add(textField_1);
    textField_2.setBounds(778, 208, 70, 20);
    
    getContentPane().add(textField_2);
    label.setBounds(884, 178, 70, 16);
    
    getContentPane().add(label);
    textField_3.setBounds(884, 208, 70, 20);
    
    getContentPane().add(textField_3);
    // Ende Komponenten
    
    setVisible(true);
  } // end of public Oberflaeche_Jaccard_Tool
  
  // Anfang Methoden
  
  public static void main(String[] args) {
    new Oberflaeche_Jaccard_Tool("Oberflaeche_Jaccard_Tool");
  } // end of main
  
  public void jButton1_ActionPerformed(ActionEvent evt) {
    // TODO hier Quelltext einfügen
  } // end of jButton1_ActionPerformed
} // end of class Oberflaeche_Jaccard_Tool
