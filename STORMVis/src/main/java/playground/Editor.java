package playground;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.JButton;
import java.awt.GridLayout;
import javax.swing.JTextField;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.FlowLayout;
import javax.swing.JToggleButton;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class Editor implements KeyListener {
	
	private static boolean SCROLLMODE = true;
	private JPanel superPanel;
	private DrawPanel myPan;
	private ImagePanel imgPanel;
	private float zoomFactor;
	private JScrollPane jsp;
	private JPanel controlPanel;
	private JButton importImageButton;
	private JButton addButton;
	private JTextField pixelNmField;
	
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
        JFrame f = new JFrame("Editor");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        superPanel = new JPanel();
        myPan = new DrawPanel();
        myPan.setOpaque(false);
        imgPanel = new ImagePanel();
        FlowLayout flowLayout = (FlowLayout) imgPanel.getLayout();
        flowLayout.setAlignOnBaseline(true);
        imgPanel.setOpaque(true);
        superPanel.setPreferredSize(imgPanel.getPreferredSize());
        myPan.setPreferredSize(imgPanel.getPreferredSize());
        myPan.setBounds(0,0,(int) imgPanel.getPreferredSize().getWidth(),(int) imgPanel.getPreferredSize().getHeight());
        f.getContentPane().add(superPanel);
        
        controlPanel = new JPanel();
        f.getContentPane().add(controlPanel, BorderLayout.EAST);
        
        importImageButton = new JButton("Import Image");
        importImageButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        	}
        });
        
        addButton = new JButton("add");
        
        JLabel lblNmpx = new JLabel("nm/px");
        
        pixelNmField = new JTextField();
        pixelNmField.setHorizontalAlignment(SwingConstants.LEFT);
        pixelNmField.setText("0.0");
        pixelNmField.setColumns(10);
        
        /*
         * Buttons
         */
        
        JButton deleteLastButton = new JButton("delete last point");
        
        JToggleButton toggleClose = new JToggleButton("close lines");
        
        JButton saveProjectButton = new JButton("save project");
        
        /*
         * Layout
         */
        GroupLayout gl_controlPanel = new GroupLayout(controlPanel);
        gl_controlPanel.setHorizontalGroup(
        	gl_controlPanel.createParallelGroup(Alignment.LEADING)
        		.addGroup(gl_controlPanel.createSequentialGroup()
        			.addContainerGap()
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
        			.addComponent(toggleClose)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addComponent(saveProjectButton)
        			.addContainerGap(489, Short.MAX_VALUE))
        );
        controlPanel.setLayout(gl_controlPanel);
        
        
        f.setPreferredSize(new Dimension(960, 720));
        imgPanel.addKeyListener(this);
        f.pack();
        f.setVisible(true);
        zoomFactor = 1.f;
        imgPanel.zoom(zoomFactor);
        updateBoundsOfComponents();
    } 
	
	private void updateBoundsOfComponents() {
		superPanel.removeAll();
		superPanel.setPreferredSize(imgPanel.getPreferredSize());
		superPanel.setLayout(new BorderLayout(0, 0));
		superPanel.add(myPan);
        superPanel.add(imgPanel);
		myPan.setBounds(0,0,(int) imgPanel.getPreferredSize().getWidth(),(int) imgPanel.getPreferredSize().getHeight());
		jsp = new JScrollPane(imgPanel,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        superPanel.add(jsp);
        superPanel.repaint();
        superPanel.revalidate();
        imgPanel.repaint();
		imgPanel.revalidate();
		myPan.setOpaque(false);
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
			imgPanel.zoom(zoomFactor);
			updateBoundsOfComponents();
		}
		else if(key.equals(scroll)) {
			System.out.println("X: " +jsp.getHorizontalScrollBar().getValue());
			System.out.println("Y: " +jsp.getVerticalScrollBar().getValue());
			if(SCROLLMODE) {
				SCROLLMODE = false;
				myPan.setVisible(SCROLLMODE);
			}
			else {
				SCROLLMODE = true;
				myPan.setVisible(SCROLLMODE);
			}
		}
		System.out.println("Zoom: " + zoomFactor);
	}
}

