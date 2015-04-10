package editor;

import java.awt.image.BufferedImage;
import java.io.Serializable;

public class SerializableImage implements Serializable{
    
    int width; int height; int[] pixels;

    public SerializableImage(BufferedImage bi) { 
         width = bi.getWidth(); 
         height = bi.getHeight(); 
         pixels = new int[width * height]; 
         int[] tmp=bi.getRGB(0,0,width,height,pixels,0,width); 
         pixels = tmp;
    }

    public BufferedImage getImage() { 
         BufferedImage bi = new BufferedImage(width,height, BufferedImage.TYPE_INT_RGB);
         bi.setRGB(0,0,width,height,pixels,0,width);
    return bi; 
    } 
    
}
