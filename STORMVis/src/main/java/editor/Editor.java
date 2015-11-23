package editor;

import gui.DataTypeDetector.DataType;
import inout.FileManager;
import inout.ImageFileFilter;
import inout.ProjectFileFilter;

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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

import table.DataSetSelectionTable;
import table.DataSetSelectionTableModel;
import table.DataSetTableModel;
import model.DataSet;
import model.LineDataSet;
import model.ParameterSet;
import model.Project;
import model.SerializableImage;
import model.TriangleDataSet;
import java.awt.Component;

public class Editor implements KeyListener, TableModelListener {
	
	private static boolean SCROLLMODE = false;
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
	private JTextField nameField = new JTextField(50);
	private JButton newSetButton;
	private JButton deleteLastButton;
	
	private DataSetTableModel model;
	
	private List<DataSet> allDataSets = new ArrayList<DataSet>();
	private Color currentDrawingColor = Color.RED;
	
	private Float lastValuePxNmRatio=1.f;
	
	private static String EXTENSION = ".wimp";
	private JLabel lblNewLabel;
	
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
//        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
        imgPanel.setOpaque(true);
        imgPanel.setLayout(new BorderLayout(0, 0));
        
        lblNewLabel = new JLabel("Please import an image");
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        imgPanel.add(lblNewLabel, BorderLayout.CENTER);
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
    		        SCROLLMODE=true;
    		        updateBoundsOfComponents();
    			}
        	}
        });
            
        addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				
				final DataSetSelectionTableModel model = new DataSetSelectionTableModel();
				final DataSetSelectionTable selectionTable = new DataSetSelectionTable(model);
				model.data.addAll(allDataSets);
				/**
				 * closed lines or open lines
				 */
				
				model.selectableDataType = DataType.LINES;
				
				LineDataSet set = (LineDataSet) model.data.get(0);
				
	    		  set = drawPanel.addCurrentPointsToLineDataSet(set);
	    		  allDataSets.set(0, set);
	    		  model.data.set(0, set);
	    		  drawPanel.drawManager.currentPoints.clear();
	    		  drawPanel.repaint();
	    		  model.fireTableDataChanged();
				
//				selectionTable.addMouseListener(new MouseAdapter() {
//		        	  public void mouseClicked(MouseEvent e) {
//		        	    if (e.getClickCount() == 2) {
//		        	      JTable target = (JTable)e.getSource();
//		        	      int row = target.getSelectedRow();
//		        	      int column = target.getSelectedColumn();
//		        	      boolean selectable = model.isCellSelectable(row, column);
//		        	      if(selectable) {
//		        	    	  if(model.selectableDataType == DataType.LINES) {
//		        	    		  LineDataSet set = (LineDataSet) model.data.get(row);
//		        	    		  set = drawPanel.addCurrentPointsToLineDataSet(set);
//		        	    		  allDataSets.set(row, set);
//		        	    		  model.data.set(row, set);
//		        	    		  drawPanel.drawManager.currentPoints.clear();
//		        	    		  drawPanel.repaint();
//		        	    		  model.fireTableDataChanged();
//		        	    	  }
//		        	    	  else if (model.selectableDataType == DataType.TRIANGLES) {
//		        	    		  TriangleDataSet set = (TriangleDataSet) model.data.get(row);
//		        	    		  set = drawPanel.addCurrentPointsToTriangleDataSet(set);
//		        	    		  allDataSets.set(row, set);
//		        	    		  model.data.set(row, set);
//		        	    		  drawPanel.drawManager.currentPoints.clear();
//		        	    		  drawPanel.repaint();
//		        	    		  model.fireTableDataChanged();
//		        	    	  }
//		        	    	  Window win = SwingUtilities.getWindowAncestor(selectionTable);
//		        	    	  win.setVisible(false);
//		        	    	  nameField.setText("");
//		        	    	  
//		        	    	  drawPanel.closeCurrentLine = false;
//		        	    	  imgPanel.requestFocus();
//		        	      }
//		        	    }
//		        	  }
//		        	});
//				
//				final JComponent[] inputs = new JComponent[] {
//						new JLabel("You can either create a new data set or add your lines to an existing data set of the same type."),
//						newSetButton,
//						new JLabel("Dataset name:"),
//						nameField,
//						new JLabel("Existing datasets:"),
//						selectionTable,
//				};
//				Object[] options = {"Cancel"};
//				JOptionPane.showOptionDialog(null, inputs, "Save Options",  JOptionPane.PLAIN_MESSAGE,  JOptionPane.PLAIN_MESSAGE, null,options,options[0]);
//				//JOptionPane.showMessageDialog(null, inputs, "Save Options", JOptionPane.PLAIN_MESSAGE);
				imgPanel.requestFocus();
			}
		});
        
        JLabel lblNmpx = new JLabel("nm/px");
        pixelNmField = new JTextField();
//        pixelNmField.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				Float newValuePxNmRatio = Float.parseFloat(pixelNmField.getText());
//				rescaleData(lastValuePxNmRatio, newValuePxNmRatio);
//				drawPanel.drawManager.ratio = newValuePxNmRatio;
//				lastValuePxNmRatio = newValuePxNmRatio;
//				drawPanel.repaint();
//			}
//		});
        pixelNmField.setHorizontalAlignment(SwingConstants.LEFT);
        pixelNmField.setText("1.0");
        pixelNmField.setColumns(10);
        
        /*
         * Buttons
         */
        
        deleteLastButton = new JButton("Delete Last Point");
        deleteLastButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				drawPanel.drawManager.removeLastPoint();
				drawPanel.repaint();
				imgPanel.requestFocus();
			}
		});
        
        model = new DataSetTableModel();
        dataSetTable = new JTable(model);
        dataSetTable.getColumnModel().getColumn(0).setMinWidth(100);
        model.addTableModelListener(this);
        
        /*
         * Save and load
         */
        
        JButton saveProjectButton = new JButton("Save As WIMP");
        saveProjectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				int returnValue = fc.showSaveDialog(f);
				if(returnValue == JFileChooser.APPROVE_OPTION) {
					String path = fc.getSelectedFile().getAbsolutePath();
					String name = fc.getSelectedFile().getName();
					if (!path.endsWith(EXTENSION)) {
					    path += EXTENSION;
					}
					if(name.endsWith(EXTENSION)){
						name = name.substring(0, name.length()-5);
					}
					System.out.println("Path to write project: " + path);
					System.out.println("project name: " + name);
					((LineDataSet)allDataSets.get(0)).rescaleData(Float.valueOf(pixelNmField.getText()));
					FileManager.writeWIMPFile((LineDataSet) allDataSets.get(0), path);
//					Project p = new Project(name, allDataSets,  Float.parseFloat(pixelNmField.getText()));
//					p.setOriginalImage(new SerializableImage(imgPanel.getOriginalImage()));
//					FileManager.writeProjectToFile(p, path);
				}
			}
		});
        
        JToggleButton tglbtnNewToggleButton = new JToggleButton("Toggle Scroll Mode");
        tglbtnNewToggleButton.addActionListener(new ActionListener(){
        	@Override
			public void actionPerformed(ActionEvent e) {
        		if(((JToggleButton)e.getSource()).isSelected()) {
    				SCROLLMODE = false;
    				drawPanel.setVisible(SCROLLMODE);
    				jsp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    				jsp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    			}
    			else {
    				SCROLLMODE = true;
    				drawPanel.setVisible(SCROLLMODE);
    				jsp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    				jsp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
    			}
			}
        });
        
        GroupLayout gl_controlPanel = new GroupLayout(controlPanel);
        gl_controlPanel.setHorizontalGroup(
        	gl_controlPanel.createParallelGroup(Alignment.LEADING)
        		.addGroup(gl_controlPanel.createSequentialGroup()
        			.addContainerGap()
        			.addGroup(gl_controlPanel.createParallelGroup(Alignment.LEADING)
        				.addGroup(Alignment.TRAILING, gl_controlPanel.createSequentialGroup()
        					.addGap(6)
        					.addComponent(dataSetTable, GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE))
        				.addComponent(deleteLastButton, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
        				.addComponent(addButton, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
        				.addGroup(Alignment.TRAILING, gl_controlPanel.createSequentialGroup()
        					.addGap(6)
        					.addComponent(lblNmpx, GroupLayout.PREFERRED_SIZE, 53, GroupLayout.PREFERRED_SIZE)
        					.addPreferredGap(ComponentPlacement.RELATED)
        					.addComponent(pixelNmField, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE))
        				.addComponent(importImageButton, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
        				.addComponent(saveProjectButton, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
        				.addComponent(tglbtnNewToggleButton))
        			.addContainerGap())
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
        			.addComponent(tglbtnNewToggleButton)
        			.addGap(39)
        			.addComponent(dataSetTable, GroupLayout.PREFERRED_SIZE, 166, GroupLayout.PREFERRED_SIZE)
        			.addPreferredGap(ComponentPlacement.UNRELATED)
        			.addComponent(saveProjectButton)
        			.addContainerGap(261, Short.MAX_VALUE))
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
    	drawPanel.drawManager.ratio = Float.parseFloat(pixelNmField.getText());
		System.out.println("ratio on save: " + drawPanel.drawManager.ratio);
		
		 // lines open: line object generated
		LineDataSet newSet = new LineDataSet(new ParameterSet());
		newSet = drawPanel.addCurrentPointsToLineDataSet(newSet);
		newSet.setName("Filaments");
		newSet.setColor(currentDrawingColor);
		newSet.setDataType(DataType.LINES);
		System.out.println("all:" + allDataSets.size());
		allDataSets.add(newSet);
		model.visibleSets.add(Boolean.TRUE);
		model.data.add(newSet);
		drawPanel.drawManager.currentPoints.clear();
		drawPanel.repaint();
		System.out.println("all2:" + allDataSets.size());
		model.fireTableDataChanged();
		
		
        nameField.setText("");
        
        drawPanel.closeCurrentLine = false;
        imgPanel.requestFocus();
        newSetButton = new JButton("Create new dataset");
		newSetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
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
	
	protected void rescaleData(Float lastValuePxNmRatio,
			Float newValuePxNmRatio) {
		for(DataSet s : allDataSets) {
			if(s.getDataType()==DataType.LINES){
				((LineDataSet)s).rescaleData(newValuePxNmRatio/lastValuePxNmRatio);
			}
			else{
				((TriangleDataSet)s).rescaleData(newValuePxNmRatio/lastValuePxNmRatio);
			}
		}
		
	}

	private void updateBoundsOfComponents() {
		superPanel.removeAll();
		superPanel.setPreferredSize(imgPanel.getPreferredSize());
		superPanel.setLayout(new BorderLayout(0, 0));
		superPanel.add(drawPanel);
        superPanel.add(imgPanel);
		drawPanel.setBounds(0,0,(int) imgPanel.getPreferredSize().getWidth(),(int) imgPanel.getPreferredSize().getHeight());
		jsp = new JScrollPane(imgPanel,ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
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