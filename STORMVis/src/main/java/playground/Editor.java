package playground;

import gui.TriangleLineFilter;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import model.DataSet;
import model.LineDataSet;
import model.ParameterSet;

public class Editor implements KeyListener {
	
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
	
	
	public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
					new Editor();
				} catch (IOException e) {
					// TODO Auto-generated catch block
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
    			int returnVal = chooser.showOpenDialog(f); //replace null with your swing container
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
//				final JButton newSetButton = new JButton("Create new dataset");
//				newSetButton.addActionListener(new ActionListener() {
//					public void actionPerformed(ActionEvent e) {
//						System.out.println("new set & dismiss pane");
//						Window win = SwingUtilities.getWindowAncestor(newSetButton);
//			            win.setVisible(false);
//					}
//				});
//				final JComponent[] inputs = new JComponent[] {
//						new JLabel("You can either create a new data set or add your lines to an existing data set of the same type."),
//						newSetButton,
//				};
//				JOptionPane.showMessageDialog(null, inputs, "Save Options", JOptionPane.PLAIN_MESSAGE);
//				LineDataSet s = new LineDataSet(new ParameterSet());
//				s = drawPanel.addCurrentPointsToLineDataSet(s);
//				System.out.println("Data[0] size " + s.data.get(0).size());
			}
		});
        
        JLabel lblNmpx = new JLabel("nm/px");
        pixelNmField = new JTextField();
        pixelNmField.setHorizontalAlignment(SwingConstants.LEFT);
        pixelNmField.setText("0.0");
        pixelNmField.setColumns(10);
        
        /*
         * Buttons
         */
        
        JButton deleteLastButton = new JButton("delete last point");
        deleteLastButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				drawPanel.drawManager.removeLastPoint();
				drawPanel.repaint();
			}
		});
        
        JToggleButton toggleClose = new JToggleButton("close lines");
        
        JButton saveProjectButton = new JButton("save project");
        
        /*
         * DUMMY // TODO: subclass
         */
        DataSetTableModel model = new DataSetTableModel();
        dataSetTable = new JTable(model);
        DataSet sample = new LineDataSet(new ParameterSet());
        sample.setName("data sample1");
        DataSet sample1 = new LineDataSet(new ParameterSet());
        sample1.setName("data sample2");
        model.addRow(sample);
        model.addRow(sample1);
        model.visibleSets.add(Boolean.FALSE);
        model.visibleSets.add(Boolean.FALSE);
        
        GroupLayout gl_controlPanel = new GroupLayout(controlPanel);
        gl_controlPanel.setHorizontalGroup(
        	gl_controlPanel.createParallelGroup(Alignment.LEADING)
        		.addGroup(gl_controlPanel.createSequentialGroup()
        			.addContainerGap()
        			.addGroup(gl_controlPanel.createParallelGroup(Alignment.LEADING)
        				.addGroup(Alignment.TRAILING, gl_controlPanel.createSequentialGroup()
        					.addGap(6)
        					.addComponent(dataSetTable, GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
        					.addGap(12))
        				.addGroup(gl_controlPanel.createSequentialGroup()
        					.addGroup(gl_controlPanel.createParallelGroup(Alignment.LEADING)
        						.addComponent(deleteLastButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        						.addComponent(addButton, GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
        						.addGroup(gl_controlPanel.createSequentialGroup()
        							.addGap(6)
        							.addComponent(lblNmpx, GroupLayout.PREFERRED_SIZE, 53, GroupLayout.PREFERRED_SIZE)
        							.addPreferredGap(ComponentPlacement.RELATED)
        							.addComponent(pixelNmField, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE))
        						.addComponent(importImageButton, GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
        						.addComponent(toggleClose, GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
        						.addComponent(saveProjectButton, GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE))
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
        			.addComponent(saveProjectButton)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addComponent(dataSetTable, GroupLayout.PREFERRED_SIZE, 166, GroupLayout.PREFERRED_SIZE)
        			.addContainerGap(317, Short.MAX_VALUE))
        );
        controlPanel.setLayout(gl_controlPanel);
        
        f.setPreferredSize(new Dimension(960, 720));
        imgPanel.addKeyListener(this);
        f.pack();
        f.setVisible(true);
//        zoomFactor = 1.f;
//        imgPanel.zoom(zoomFactor);
        updateBoundsOfComponents();
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
		// TODO Auto-generated method stub
	}

	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		String plus = "+";
		String minus = "-";
		String scroll = "s";
		String key = String.valueOf(e.getKeyChar());
		if(key.equals(plus)) {
			System.out.println("zoom in");
			zoomFactor += 0.05f;
			imgPanel.zoom(zoomFactor);
			updateBoundsOfComponents();
		}
		else if(key.equals(minus)) {
			System.out.println("zoom out");
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
		System.out.println("Zoom: " + zoomFactor);
		drawPanel.zoomFactor = zoomFactor;
		drawPanel.repaint();
	}
}