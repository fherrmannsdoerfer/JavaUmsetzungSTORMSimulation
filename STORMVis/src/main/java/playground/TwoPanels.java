package playground;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;


public class TwoPanels {
    public static void main(String[] args) {

        JPanel p = new JPanel();
        // setting layout to null so we can make panels overlap
        p.setLayout(null);

        CirclePanel topPanel = new CirclePanel();
        // drawing should be in blue
        topPanel.setForeground(Color.blue);
        // background should be black, except it's not opaque, so 
        // background will not be drawn
        topPanel.setBackground(Color.black);
        // set opaque to false - background not drawn
        topPanel.setOpaque(false);
        topPanel.setBounds(50, 50, 100, 100);
        // add topPanel - components paint in order added, 
        // so add topPanel first
        p.add(topPanel);

        CirclePanel bottomPanel = new CirclePanel();
        // drawing in green
        bottomPanel.setForeground(Color.green);
        // background in cyan
        bottomPanel.setBackground(Color.cyan);
        // and it will show this time, because opaque is true
        bottomPanel.setOpaque(true);
        bottomPanel.setBounds(30, 30, 100, 100);
        // add bottomPanel last...
        p.add(bottomPanel);

        // frame handling code...
        JFrame f = new JFrame("Two Panels");
        f.setContentPane(p);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(300, 300);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    // Panel with a circle drawn on it.
    private static class CirclePanel extends JPanel {

        // This is Swing, so override paint*Component* - not paint
        protected void paintComponent(Graphics g) {
            // call super.paintComponent to get default Swing 
            // painting behavior (opaque honored, etc.)
            super.paintComponent(g);
            int x = 10;
            int y = 10;
            int width = getWidth() - 20;
            int height = getHeight() - 20;
            g.drawArc(x, y, width, height, 0, 360);
        }
    }
}