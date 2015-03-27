package editor;

import gui.DataTypeDetector.DataType;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import model.DataSet;
import model.LineDataSet;
import model.ParameterSet;

public class Editor implements KeyListener, TableModelListener {
	
	private static boolean SCROLLMODE = true;
	private JPanel superPanel;
	private DrawPanel drawPanel;
	private ImagePanel imgPanel;
	private float zoomFactor;
	private JScrollPane jsp;
	private JPanel controlPanel;
	private JButton importImageButton;
	private JButton addButton;
	private JTextField pixelNmField;
	private JTable dataSetTable;
	private JToggleButton toggleClose;
	private JTextField nameField = new JTextField(50);
	private JButton newSetButton;
	private JButton deleteLastButton;
	
	private DataSetTableModel model;
	
	private List<DataSet> allDataSets = new ArrayList<DataSet>();
	private Color currentDrawingColor = Color.RED;
	
	public static void main(String[] args) {
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
	
	public Editor() throws IOException {
        final JFrame f = new JFrame("Editor");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        superPanel = new JPanel();
        drawPanel = new DrawPanel();
        drawPanel.addListener(new PointDrawnListener() {
			@Override
			public void pointNumberChanged() {
				checkForPoints();
			}
		});
        drawPanel.setOpaque(false);
        imgPanel = new ImagePanel();
        FlowLayout flowLayout = (FlowLayout) imgPanel.getLayout();
        flowLayout.setAlignOnBaseline(true);
        imgPanel.setOpaque(true);
        superPanel.setPreferredSize(imgPanel.getPreferredSize());
        drawPanel.setPreferredSize(imgPanel.getPreferredSize());
        drawPanel.setBounds(0,0,(int) imgPanel.getPreferredSize().getWidth(),(int) imgPanel.getPreferredSize().getHeight());
        f.getContentPane().add(superPanel);
        
        controlPanel = new JPanel();
        f.getContentPane().add(controlPanel, BorderLayout.EAST);
        
        importImageButton = new JButton("Import Image");
        importImageButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		System.out.println("import image");
    			JFileChooser chooser = new JFileChooser();
    			chooser.setAcceptAllFileFilterUsed(false);
    			chooser.setFileFilter(new ImageFileFilter());
    			chooser.setFileSelectionMode(0);
    			int returnVal = chooser.showOpenDialog(f);
    			if(returnVal == JFileChooser.APPROVE_OPTION) { 
    				imgPanel.setPathAndReadImage(chooser.getSelectedFile().getAbsolutePath());
    				zoomFactor = 1.f;
    		        imgPanel.zoom(zoomFactor);
    		        updateBoundsOfComponents();
    			}
        	}
        });
        
        addButton = new JButton("add");
        addButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				
				DataSetSelectionTableModel model = new DataSetSelectionTableModel();
				DataSetSelectionTable selectionTable = new DataSetSelectionTable(model);
				model.data = allDataSets;
				
				final JComponent[] inputs = new JComponent[] {
						new JLabel("You can either create a new data set or add your lines to an existing data set of the same type."),
						newSetButton,
						new JLabel("Dataset name:"),
						nameField,
						selectionTable,
				};
				JOptionPane.showMessageDialog(null, inputs, "Save Options", JOptionPane.PLAIN_MESSAGE);
				imgPanel.requestFocus();
			}
		});
        
        JLabel lblNmpx = new JLabel("nm/px");
        pixelNmField = new JTextField();
        pixelNmField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				drawPanel.drawManager.ratio = Float.parseFloat(pixelNmField.getText());
				drawPanel.repaint();
			}
		});
        pixelNmField.setHorizontalAlignment(SwingConstants.LEFT);
        pixelNmField.setText("1.0");
        pixelNmField.setColumns(10);
        
        /*
         * Buttons
         */
        
        deleteLastButton = new JButton("delete last point");
        deleteLastButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				drawPanel.drawManager.removeLastPoint();
				drawPanel.repaint();
				imgPanel.requestFocus();
			}
		});
        
        toggleClose = new JToggleButton("close lines");
        toggleClose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(toggleClose.isSelected()) {
					drawPanel.closeCurrentLine = true;
					drawPanel.repaint();
					imgPanel.requestFocus();
				}
				else {
					drawPanel.closeCurrentLine = false;
					drawPanel.repaint();
					imgPanel.requestFocus();
				}
			}
		});
        
        JButton saveProjectButton = new JButton("save project");
        model = new DataSetTableModel();
        dataSetTable = new JTable(model);
        dataSetTable.getColumnModel().getColumn(0).setMinWidth(100);
        model.addTableModelListener(this);
        JButton btnChooseColor = new JButton("choose color");
        btnChooseColor.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		currentDrawingColor = JColorChooser.showDialog(f, "Choose drawing color", currentDrawingColor);
        		drawPanel.drawingColor = currentDrawingColor;
        		drawPanel.repaint();
        		imgPanel.requestFocus();
        	}
        });
        
        GroupLayout gl_controlPanel = new GroupLayout(controlPanel);
        gl_controlPanel.setHorizontalGroup(
        	gl_controlPanel.createParallelGroup(Alignment.LEADING)
        		.addGroup(gl_controlPanel.createSequentialGroup()
        			.addContainerGap()
        			.addGroup(gl_controlPanel.createParallelGroup(Alignment.LEADING)
        				.addGroup(gl_controlPanel.createSequentialGroup()
        					.addGap(6)
        					.addComponent(dataSetTable, GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
        					.addContainerGap())
        				.addGroup(Alignment.TRAILING, gl_controlPanel.createSequentialGroup()
        					.addGroup(gl_controlPanel.createParallelGroup(Alignment.LEADING)
        						.addComponent(deleteLastButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        						.addComponent(addButton, GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
        						.addGroup(gl_controlPanel.createSequentialGroup()
        							.addGap(6)
        							.addComponent(lblNmpx, GroupLayout.PREFERRED_SIZE, 53, GroupLayout.PREFERRED_SIZE)
        							.addPreferredGap(ComponentPlacement.RELATED)
        							.addComponent(pixelNmField, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE))
        						.addComponent(importImageButton, GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
        						.addComponent(toggleClose, GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE))
        					.addContainerGap())
        				.addGroup(gl_controlPanel.createSequentialGroup()
        					.addComponent(saveProjectButton, GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
        					.addContainerGap())
        				.addGroup(gl_controlPanel.createSequentialGroup()
        					.addComponent(btnChooseColor, GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
        					.addContainerGap())))
        );
        gl_controlPanel.setVerticalGroup(
        	gl_controlPanel.createParallelGroup(Alignment.LEADING)
        		.addGroup(gl_controlPanel.createSequentialGroup()
        			.addContainerGap()
        			.addComponent(importImageButton)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addGroup(gl_controlPanel.createParallelGroup(Alignment.BASELINE)
        				.addComponent(lblNmpx, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
        				.addComponent(pixelNmField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addComponent(addButton)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addComponent(deleteLastButton)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addComponent(toggleClose)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addComponent(btnChooseColor)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addComponent(dataSetTable, GroupLayout.PREFERRED_SIZE, 166, GroupLayout.PREFERRED_SIZE)
        			.addPreferredGap(ComponentPlacement.UNRELATED)
        			.addComponent(saveProjectButton)
        			.addContainerGap(282, Short.MAX_VALUE))
        );
        controlPanel.setLayout(gl_controlPanel);
        
        /*
         * Textfield for new dataSet name
         */
        
        nameField.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				nameFieldValueChanged();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				nameFieldValueChanged();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				nameFieldValueChanged();
			}
		});
        
        /*
         * Button for new dataSet
         */
        
        newSetButton = new JButton("Create new dataset");
		newSetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("new set & dismiss pane");
				if(toggleClose.isSelected()) {
					
				}
				else {
					drawPanel.drawManager.ratio = Float.parseFloat(pixelNmField.getText());
					System.out.println("ratio on save: " + drawPanel.drawManager.ratio);
					LineDataSet newSet = new LineDataSet(new ParameterSet());
					newSet = drawPanel.addCurrentPointsToLineDataSet(newSet);
					newSet.setName(nameField.getText());
					newSet.setColor(currentDrawingColor);
					newSet.setDataType(DataType.LINES);
					allDataSets.add(newSet);
					model.visibleSets.add(Boolean.FALSE);
					model.data.add(newSet);
					drawPanel.drawManager.currentPoints.clear();
					drawPanel.repaint();
					System.out.println("new set size: " + newSet.data.size());
					model.fireTableDataChanged();
				}
				Window win = SwingUtilities.getWindowAncestor(newSetButton);
	            win.setVisible(false);
	            nameField.setText("");
	            imgPanel.requestFocus();
			}
		});
        
        f.setPreferredSize(new Dimension(960, 720));
        imgPanel.addKeyListener(this);
        f.pack();
        f.setVisible(true);

        drawPanel.drawingColor = currentDrawingColor;
        updateBoundsOfComponents();
        nameFieldValueChanged();
        checkForPoints();
    } 
	
	private void updateBoundsOfComponents() {
		superPanel.removeAll();
		superPanel.setPreferredSize(imgPanel.getPreferredSize());
		superPanel.setLayout(new BorderLayout(0, 0));
		superPanel.add(drawPanel);
        superPanel.add(imgPanel);
		drawPanel.setBounds(0,0,(int) imgPanel.getPreferredSize().getWidth(),(int) imgPanel.getPreferredSize().getHeight());
		jsp = new JScrollPane(imgPanel,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jsp.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				drawPanel.scrollOffsetY = jsp.getVerticalScrollBar().getValue();
			}
		});
		jsp.getHorizontalScrollBar().addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				drawPanel.scrollOffsetX = jsp.getHorizontalScrollBar().getValue();
			}
		});
        superPanel.add(jsp);
        superPanel.repaint();
        superPanel.revalidate();
        imgPanel.repaint();
		imgPanel.revalidate();
		drawPanel.setOpaque(false);
        imgPanel.requestFocus();
	}

	public void keyTyped(KeyEvent e) {
		// Auto-generated method stub
	}

	public void keyReleased(KeyEvent e) {
		// Auto-generated method stub

	}

	public void keyPressed(KeyEvent e) {
		String plus = "+";
		String minus = "-";
		String scroll = "s";
		String key = String.valueOf(e.getKeyChar());
		if(key.equals(plus)) {
			zoomFactor += 0.05f;
			imgPanel.zoom(zoomFactor);
			updateBoundsOfComponents();
		}
		else if(key.equals(minus)) {
			zoomFactor -= 0.05f;
			if(zoomFactor <= 0.01f) {
				zoomFactor = 0.01f;
				imgPanel.zoom(zoomFactor);
			}
			else {
				imgPanel.zoom(zoomFactor);
			}
			updateBoundsOfComponents();
		}
		else if(key.equals(scroll)) {
			System.out.println("X: " +jsp.getHorizontalScrollBar().getValue());
			System.out.println("Y: " +jsp.getVerticalScrollBar().getValue());
			if(SCROLLMODE) {
				SCROLLMODE = false;
				drawPanel.setVisible(SCROLLMODE);
			}
			else {
				SCROLLMODE = true;
				drawPanel.setVisible(SCROLLMODE);
			}
		}
		drawPanel.zoomFactor = zoomFactor;
		drawPanel.repaint();
	}
	
	/*
	 *  "Helper methods"
	 */
	
	public void nameFieldValueChanged() {
		if(nameField.getText().trim().isEmpty()) {
			newSetButton.setEnabled(false);
		}
		else {
			newSetButton.setEnabled(true);
		}
	}
	
	public void checkForPoints() {
		if(drawPanel.drawManager.currentPoints.size() == 0) {
			addButton.setEnabled(false);
			deleteLastButton.setEnabled(false);
		}
		else {
			addButton.setEnabled(true);
			deleteLastButton.setEnabled(true);
		}
	}
	
	public void setSelectedListsForDrawing() {
		List<DataSet> sets = new ArrayList<DataSet>();
		for(int i = 0; i < model.data.size(); i++) {
			if(model.visibleSets.get(i) == Boolean.TRUE) {
				sets.add(model.data.get(i));
			}
		}
		drawPanel.dataSetsToVisualize = sets;
		drawPanel.repaint();
		imgPanel.requestFocus();
	}
	
	/*
	 * Table Listener method
	 */

	@Override
	public void tableChanged(TableModelEvent e) {
		// TODO Auto-generated method stub
		setSelectedListsForDrawing();
	}
}