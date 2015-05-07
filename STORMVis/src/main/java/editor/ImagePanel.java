package editor;

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
		setDefaultImage();
		addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
            	requestFocus();
            }
        });
	}
	
	private void setDefaultImage() {
		originalImage = new BufferedImage(750,750,BufferedImage.TYPE_INT_RGB);
	}

	public void setPathAndReadImage(String path) {
		try {                
			image = ImageIO.read(new File(path));
			originalImage = image;
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		float limit = 1000.f;
		if (image.getWidth() > limit) {
			scaleFactor = limit/image.getWidth();
			System.out.println("1px = " + 1/scaleFactor + " in real life");
			float newHeight = scaleFactor * image.getHeight();
			try {
				image = getScaledImage(image, (int) limit, (int) newHeight);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public BufferedImage getOriginalImage() {
		return originalImage;
	}
	
	public void setImageFromRecoveredProject(BufferedImage img) {
		image = img;
		originalImage = image;
		float limit = 1000.f;
		if (image.getWidth() > limit) {
			scaleFactor = limit/image.getWidth();
			System.out.println("1px = " + 1/scaleFactor + " in real life");
			float newHeight = scaleFactor * image.getHeight();
			try {
				image = getScaledImage(image, (int) limit, (int) newHeight);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Dimension getPreferredSize() {
		if(image != null) {
			return new Dimension(image.getWidth(),image.getHeight());
		}
		return new Dimension(0,0);
    }
	
	public void zoom(float scale) {
		try {
			image=getScaledImage(originalImage, (int) (originalImage.getWidth()*scale),(int) (originalImage.getHeight()*scale));
		} catch (IOException e) {
			e.printStackTrace();
		}
		repaint();
	}
	
	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);           
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