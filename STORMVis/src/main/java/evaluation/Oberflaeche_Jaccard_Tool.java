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
  private JLabel pointsTestSetField = new JLabel();
  private JLabel jLabel8 = new JLabel();
  private JLabel jLabel9 = new JLabel();
  private JLabel jLabel10 = new JLabel();
  private JLabel jLabel11 = new JLabel();
  private JTextField jTextField5 = new JTextField();
  private JTextField tpField = new JTextField();
  private JTextField fpField = new JTextField();
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
  private final JLabel lblRmseXyz = new JLabel("RMSE XYZ");
  private final JTextField textField_3 = new JTextField();
  private final JLabel lblFalseNegative = new JLabel();
  private final JTextField fnField = new JTextField();
  private final JLabel lblPrecision = new JLabel();
  private final JTextField textField_5 = new JTextField();
  private final JLabel lblRecall = new JLabel();
  private final JTextField textField_6 = new JTextField();
  private final JLabel lblPointsGround = new JLabel();
  private final JTextField gtField = new JTextField();
  // Ende Attribute
  
  public Oberflaeche_Jaccard_Tool(String title) { 
    // Frame-Initialisierung
    super(title);
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    int frameWidth = 632; 
    int frameHeight = 292;
    setSize(834, 332);
    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    int x = (d.width - getSize().width) / 2;
    int y = (d.height - getSize().height) / 2;
    setLocation(x, y);
    setResizable(false);
    Container cp = getContentPane();
    cp.setLayout(null);
    // Anfang Komponenten
    
    jButton1.setBounds(367, 134, 107, 25);
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
        tpField.setText(tP);
        String fP = Integer.toString((int) results[4]);
        fpField.setText(fP);
        String jac = Float.toString(results[5]);
        jTextField8.setText(jac);
        String f = Float.toString(results[6]);
        jTextField9.setText(f);
        textField.setText(Float.toString(results[7]));
        textField_1.setText(Float.toString(results[8]));
        textField_2.setText(Float.toString(results[9]));
        textField_3.setText(Float.toString(results[10]));
        fnField.setText(Float.toString(results[11]));
        textField_5.setText(Float.toString(results[3]/(results[3]+results[4])));
        textField_6.setText(Float.toString(results[3]/(results[3]+results[11])));
        gtField.setText(Integer.toString((int) results[13]));
        jTextField5.setText(Float.toString(results[12]));
        System.out.println(results[2]+" "+results[3]+" "+results[4]+" "+results[5]+" "+results[6]+" "+results[7]+" "+results[8]+" "+results[9]+" "+results[10]);
      }
    });
    cp.add(jButton1);
    jLabel1.setBounds(559, 15, 70, 20);
    jLabel1.setText("Tolerance");
    cp.add(jLabel1);
    jTextField1.setBounds(586, 48, 54, 20);
    cp.add(jTextField1);
    jTextField2.setBounds(586, 88, 54, 20);
    cp.add(jTextField2);
    jLabel2.setBounds(41, 15, 78, 20);
    jLabel2.setText("File Names");
    cp.add(jLabel2);
    jTextField3.setBounds(155, 48, 368, 20);
    cp.add(jTextField3);
    jTextField4.setBounds(155, 88, 368, 20);
    cp.add(jTextField4);
    jLabel3.setBounds(559, 48, 46, 20);
    jLabel3.setText("XY");
    cp.add(jLabel3);
    jLabel4.setBounds(559, 88, 46, 20);
    jLabel4.setText("Z");
    cp.add(jLabel4);
    jLabel5.setBounds(41, 48, 102, 20);
    jLabel5.setText("Ground Truth");
    cp.add(jLabel5);
    jLabel6.setBounds(41, 88, 102, 20);
    jLabel6.setText("Test Set");
    cp.add(jLabel6);
    pointsTestSetField.setBounds(31, 183, 102, 20);
    pointsTestSetField.setText("# Points Test Set");
    cp.add(pointsTestSetField);
    jLabel8.setBounds(148, 183, 78, 20);
    jLabel8.setText("True Positive");
    cp.add(jLabel8);
    jLabel9.setBounds(264, 183, 86, 20);
    jLabel9.setText("False Positive");
    cp.add(jLabel9);
    jLabel10.setBounds(380, 183, 94, 20);
    jLabel10.setText("Jaccard-Index");
    cp.add(jLabel10);
    jLabel11.setBounds(496, 183, 94, 20);
    jLabel11.setText("F-Score");
    cp.add(jLabel11);
    jTextField5.setBounds(31, 208, 70, 20);
    cp.add(jTextField5);
    tpField.setBounds(147, 208, 70, 20);
    cp.add(tpField);
    fpField.setBounds(263, 208, 70, 20);
    cp.add(fpField);
    jTextField8.setBounds(379, 208, 70, 20);
    cp.add(jTextField8);
    jTextField9.setBounds(495, 208, 70, 20);
    cp.add(jTextField9);
    
    JLabel lblNewLabel = new JLabel("Shift X");
    lblNewLabel.setBounds(686, 50, 56, 16);
    getContentPane().add(lblNewLabel);
    
    shiftxField = new JTextField();
    shiftxField.setBounds(736, 47, 61, 22);
    getContentPane().add(shiftxField);
    shiftxField.setColumns(5);
    
    JLabel lblNewLabel_1 = new JLabel("Shift y");
    lblNewLabel_1.setBounds(686, 90, 56, 16);
    getContentPane().add(lblNewLabel_1);
    
    shiftyField = new JTextField();
    shiftyField.setBounds(736, 87, 61, 22);
    getContentPane().add(shiftyField);
    shiftyField.setColumns(5);
    lblNewLabel_2.setBounds(612, 187, 56, 16);
    
    getContentPane().add(lblNewLabel_2);
    lblNewLabel_3.setBounds(611, 245, 56, 16);
    
    getContentPane().add(lblNewLabel_3);
    lblNewLabel_4.setBounds(728, 187, 70, 16);
    
    getContentPane().add(lblNewLabel_4);
    textField.setBounds(611, 208, 70, 20);
    
    getContentPane().add(textField);
    textField_1.setBounds(611, 264, 70, 20);
    
    getContentPane().add(textField_1);
    textField_2.setBounds(727, 208, 70, 20);
    
    getContentPane().add(textField_2);
    lblRmseXyz.setBounds(727, 245, 70, 16);
    
    getContentPane().add(lblRmseXyz);
    textField_3.setBounds(727, 264, 70, 20);
    
    getContentPane().add(textField_3);
    lblFalseNegative.setText("False Negative");
    lblFalseNegative.setBounds(263, 241, 86, 20);
    
    getContentPane().add(lblFalseNegative);
    fnField.setBounds(263, 264, 70, 20);
    
    getContentPane().add(fnField);
    lblPrecision.setText("Precision");
    lblPrecision.setBounds(379, 241, 86, 20);
    
    getContentPane().add(lblPrecision);
    textField_5.setBounds(379, 264, 70, 20);
    
    getContentPane().add(textField_5);
    lblRecall.setText("Recall");
    lblRecall.setBounds(495, 241, 86, 20);
    
    getContentPane().add(lblRecall);
    textField_6.setBounds(495, 264, 70, 20);
    
    getContentPane().add(textField_6);
    lblPointsGround.setText("# Points Ground Truth");
    lblPointsGround.setBounds(31, 239, 137, 20);
    
    getContentPane().add(lblPointsGround);
    gtField.setBounds(31, 264, 70, 20);
    
    getContentPane().add(gtField);
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
