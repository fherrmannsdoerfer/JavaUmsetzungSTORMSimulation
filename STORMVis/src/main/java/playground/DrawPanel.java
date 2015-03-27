package playground;

import gui.DataTypeDetector.DataType;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import model.DataSet;
import model.LineDataSet;

import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Point2D;

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
    
    public DrawManager drawManager;
    public int scrollOffsetX;
    public int scrollOffsetY;
    public float zoomFactor;
    
    public Color drawingColor;
    public List<DataSet> dataSetsToVisualize = new ArrayList<DataSet>();
    private List<PointDrawnListener> listeners = new ArrayList<PointDrawnListener>();

    public DrawPanel() {
        setBorder(BorderFactory.createLineBorder(Color.black));
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
//                moveSquare(e.getX(),e.getY());
                System.out.println("x|y : " + e.getX() +" " + e.getY());
                drawManager.currentPoints.add(new Point2D((int) ((e.getX()+ scrollOffsetX)/zoomFactor),(int) ((e.getY()+ scrollOffsetY)/zoomFactor)));
                repaint();
            }
        });
        zoomFactor = 1.f;
        scrollOffsetX = 0;
        scrollOffsetY = 0;
        drawManager = new DrawManager();
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
    	super.paintComponent(g);
//    	if(!start) {
//    		super.paintComponent(g);      
//    		g.setColor(Color.RED);
//    		g.fillRect(squareX,squareY,squareW,squareH);
//    		g.setColor(Color.BLACK);
//    		g.drawRect(squareX,squareY,squareW,squareH);
//    	}
    	int offset = -5;
    	if(drawManager.currentPoints.size() != 0) {
    		List<Point2D> transformedPoints = new ArrayList<Point2D>();
    		for(Point2D drawPoint : drawManager.currentPoints) {
    			Point2D actualPoint = new Point2D((int) (drawPoint.x*zoomFactor) - scrollOffsetX, (int) (drawPoint.y*zoomFactor) - scrollOffsetY);
    			transformedPoints.add(actualPoint);
    			g.setColor(drawingColor);
    			g.fillRect(actualPoint.x+offset,actualPoint.y+offset,squareW,squareH);
    			g.setColor(Color.BLACK);
    			g.drawRect(actualPoint.x+offset,actualPoint.y+offset,squareW,squareH);
    		}
    		for(int i = 0; i < transformedPoints.size(); i++) {
    			g.setColor(drawingColor);
    			if(i < (transformedPoints.size()-1)) {
    				Point2D p1 = transformedPoints.get(i);
    				Point2D p2 = transformedPoints.get(i+1);
    				Graphics2D g2 = (Graphics2D) g;
    				g2.setStroke(new BasicStroke(2.f));
    				g2.drawLine(p1.x,p1.y,p2.x,p2.y);
    			}
    		}
    	}
    	
    	if(dataSetsToVisualize.size() != 0) {
    		float ratio = drawManager.ratio;
    		for(DataSet s : dataSetsToVisualize) {
    			if(s.getDataType() == DataType.LINES) {
    				LineDataSet set = (LineDataSet) s;
    				for(ArrayList<Coord3d> obj : set.data) {
    					List<Point2D> transformedPoints = new ArrayList<Point2D>();
    					for(int i = 0; i < obj.size(); i++) {
    						Coord3d coord = obj.get(i);
    						Point2D actualPoint = new Point2D((int) (coord.x*zoomFactor/ratio) - scrollOffsetX, (int) (coord.y*zoomFactor/ratio) - scrollOffsetY);
    		    			transformedPoints.add(actualPoint);
    		    			g.setColor(s.color);
    		    			g.fillRect(actualPoint.x+offset,actualPoint.y+offset,squareW,squareH);
    		    			g.setColor(Color.BLACK);
//    		    			g.drawRect(actualPoint.x+offset,actualPoint.y+offset,squareW,squareH);
    					}
    					for(int i = 0; i < transformedPoints.size(); i++) {
    		    			g.setColor(s.color);
    		    			if(i < (transformedPoints.size()-1)) {
    		    				Point2D p1 = transformedPoints.get(i);
    		    				Point2D p2 = transformedPoints.get(i+1);
    		    				Graphics2D g2 = (Graphics2D) g;
    		    				g2.setStroke(new BasicStroke(2.f));
    		    				g2.drawLine(p1.x,p1.y,p2.x,p2.y);
    		    			}
    		    		}
    					transformedPoints = null;
    				}
    			}
    		}
    	}
    	
    	pointNumerChanged();
//    	start = false;
    }  
    
    public LineDataSet addCurrentPointsToLineDataSet(LineDataSet s) {
    	s =  drawManager.addCurrentPointsToLineDataSet(s);
    	return s;
    }
    
    public void addListener(PointDrawnListener pl) {
    	listeners.add(pl);
    }
    
    public void pointNumerChanged() {
    	for(PointDrawnListener pl : listeners) {
    		pl.pointNumberChanged();
    	}
    }
    
}