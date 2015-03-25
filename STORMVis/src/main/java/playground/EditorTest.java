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

public class EditorTest implements KeyListener {
	
	private static boolean SCROLLMODE = true;
	private JPanel superPanel;
	private MyPanel myPan;
	private ImagePanel imgPanel;
	private float zoomFactor;
	private JScrollPane jsp;
	
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
        System.out.println("Created GUI on EDT? "+
        SwingUtilities.isEventDispatchThread());
        JFrame f = new JFrame("Editor");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        superPanel = new JPanel();
        superPanel.setLayout(new BorderLayout());
        myPan = new MyPanel();
        myPan.setOpaque(false);
        imgPanel = new ImagePanel();
        imgPanel.setOpaque(true);
//        imgPanel.setBounds(0,0,1000,1000);
        superPanel.setPreferredSize(imgPanel.getPreferredSize());
        myPan.setPreferredSize(imgPanel.getPreferredSize());
        superPanel.add(myPan);
        superPanel.add(imgPanel);
        myPan.setBounds(0,0,(int) imgPanel.getPreferredSize().getWidth(),(int) imgPanel.getPreferredSize().getHeight());
        jsp = new JScrollPane(imgPanel,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        superPanel.add(jsp);
        f.add(superPanel);
        f.setPreferredSize(new Dimension(1920, 1080));
        imgPanel.addKeyListener(this);
        f.pack();
        f.setVisible(true);
        imgPanel.requestFocus();
        zoomFactor = 1.f;
        imgPanel.zoom(zoomFactor);
        updateBoundsOfComponents();
    } 
	
	private void updateBoundsOfComponents() {
		superPanel.setPreferredSize(imgPanel.getPreferredSize());
		myPan.setBounds(0,0,(int) imgPanel.getPreferredSize().getWidth(),(int) imgPanel.getPreferredSize().getHeight());
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
	}

}

class MyPanel extends JPanel {

    private int squareX = 0;
    private int squareY = 0;
    private int squareW = 10;
    private int squareH = 10;

    public MyPanel() {

        setBorder(BorderFactory.createLineBorder(Color.black));

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
            	setOpaque(false);
                moveSquare(e.getX(),e.getY());
                System.out.println("x|y : " + e.getX() +" " + e.getY());
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
            	setOpaque(false);
                moveSquare(e.getX(),e.getY());
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
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);      
        g.setColor(Color.RED);
        g.fillRect(squareX,squareY,squareW,squareH);
        g.setColor(Color.BLACK);
        g.drawRect(squareX,squareY,squareW,squareH);
    }  
}

class ImagePanel extends JPanel {
	
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
