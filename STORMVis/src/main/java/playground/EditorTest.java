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

public class EditorTest implements KeyListener {
	
	private static boolean SCROLLMODE = true;
	private JPanel superPanel;
	private MyPanel myPan;
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
					new EditorTest();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
            }
        });
    }
	
	public EditorTest() throws IOException {
        JFrame f = new JFrame("Editor");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        
        superPanel = new JPanel();
        myPan = new MyPanel();
        myPan.setOpaque(false);
        imgPanel = new ImagePanel();
        FlowLayout flowLayout = (FlowLayout) imgPanel.getLayout();
        flowLayout.setAlignOnBaseline(true);
        imgPanel.setOpaque(true);
//        imgPanel.setBounds(0,0,1000,1000);
        superPanel.setPreferredSize(imgPanel.getPreferredSize());
        myPan.setPreferredSize(imgPanel.getPreferredSize());
        myPan.setBounds(0,0,(int) imgPanel.getPreferredSize().getWidth(),(int) imgPanel.getPreferredSize().getHeight());
//        jsp = new JScrollPane(imgPanel,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
//                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//        superPanel.add(jsp);
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
        
        JButton deleteLastButton = new JButton("delete last point");
        
        JToggleButton toggleClose = new JToggleButton("close lines");
        
        JButton saveProjectButton = new JButton("save project");
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

class MyPanel extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int squareX = 0;
    private int squareY = 0;
    private int squareW = 10;
    private int squareH = 10;
    private boolean start = true;

    public MyPanel() {
        setBorder(BorderFactory.createLineBorder(Color.black));
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                moveSquare(e.getX(),e.getY());
                System.out.println("x|y : " + e.getX() +" " + e.getY());
            }
        });

        setLayout(new BorderLayout());
    }
    
    private void moveSquare(int x, int y) {
        int OFFSET = 1;
        if ((squareX!=x) || (squareY!=y)) {
            repaint(squareX,squareY,squareW+OFFSET,squareH+OFFSET);
            squareX=x;
            squareY=y;
            repaint(squareX,squareY,squareW+OFFSET,squareH+OFFSET);
        } 
    }
    

    public Dimension getPreferredSize() {
        return new Dimension(500,500);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
    	if(!start) {
    		super.paintComponent(g);      
    		g.setColor(Color.RED);
    		g.fillRect(squareX,squareY,squareW,squareH);
    		g.setColor(Color.BLACK);
    		g.drawRect(squareX,squareY,squareW,squareH);
    	}
    	start = false;
    }  
}

class ImagePanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BufferedImage image;
	private BufferedImage originalImage;
	public float scaleFactor = 1.f;
	public ImagePanel() throws IOException {
		try {                
			image = ImageIO.read(new File("/Users/maximilianscheurer/Desktop/bildMitStruktur.png"));
			originalImage = image;
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		float limit = 1000.f;
		if (image.getWidth() > limit) {
			scaleFactor = limit/image.getWidth();
			System.out.println("1px = " + 1/scaleFactor + " in real life");
			float newHeight = scaleFactor * image.getHeight();
			image = getScaledImage(image, (int) limit, (int) newHeight);
		}
	}
	
	public Dimension getPreferredSize() {
        return new Dimension(image.getWidth(),image.getHeight());
    }
	
	public void zoom(float scale) {
		try {
			image=getScaledImage(originalImage, (int) (originalImage.getWidth()*scale),(int) (originalImage.getHeight()*scale));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		repaint();
	}
	
	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null); // see javadoc for more info on the parameters            
    }
	
	public static BufferedImage getScaledImage(BufferedImage image, int width, int height) throws IOException {
	    int imageWidth  = image.getWidth();
	    int imageHeight = image.getHeight();

	    double scaleX = (double)width/imageWidth;
	    double scaleY = (double)height/imageHeight;
	    AffineTransform scaleTransform = AffineTransform.getScaleInstance(scaleX, scaleY);
	    AffineTransformOp bilinearScaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BILINEAR);

	    return bilinearScaleOp.filter(
	        image,
	        new BufferedImage(width, height, image.getType()));
	}
	
}
