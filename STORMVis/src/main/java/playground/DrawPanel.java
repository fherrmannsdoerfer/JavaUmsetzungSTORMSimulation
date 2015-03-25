package playground;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

class DrawPanel extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int squareX = 0;
    private int squareY = 0;
    private int squareW = 10;
    private int squareH = 10;
    private boolean start = true;

    public DrawPanel() {
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
//    	start = false;
    }  
}