package playground;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

class ImagePanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BufferedImage image;
	private BufferedImage originalImage;
	public float scaleFactor = 1.f;
	public ImagePanel() throws IOException {
		
		addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
            	requestFocus();
            }
        });
		
		
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