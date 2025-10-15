package game;

import java.awt.*;
import java.awt.event.*;

public class Car extends Polygon implements KeyListener, Updatable {
    private double stepSize   = 2.4;  // pixels per frame
    private double rotStep    = 2.5;  // degrees per frame (target turn rate)
    private double rotVelocity = 0.0; // smoothed turn velocity
    private boolean forward, left, right;

    public Car(Point[] shape, Point position, double initialRotationDeg) {
        super(shape, position, initialRotationDeg);
    }

    @Override
    public void move() {
        // Turn input: right = +1, left = -1, both = 0
        int turn = (right ? 1 : 0) - (left ? 1 : 0);

        // Ease rotation velocity toward target for smoothness
        double target = turn * rotStep;
        rotVelocity = 0.8 * rotVelocity + 0.2 * target; // damping toward target
        this.rotation += rotVelocity;

        // Forward thrust
        if (forward) {
            double rad = Math.toRadians(this.rotation);
            this.position.x += stepSize * Math.cos(rad);
            this.position.y += stepSize * Math.sin(rad);
        }
    }

    @Override
    public void paint(Graphics g) {
        Point[] pts = this.getPoints();
        int n = pts.length;
        int[] xs = new int[n];
        int[] ys = new int[n];

        // Also compute the current polygon center for the heading line
        double cx = 0, cy = 0;
        for (int i = 0; i < n; i++) {
            xs[i] = (int) Math.round(pts[i].x);
            ys[i] = (int) Math.round(pts[i].y);
            cx += pts[i].x;
            cy += pts[i].y;
        }
        cx /= n; cy /= n;

        // Car body
        g.setColor(new Color(120, 200, 120));
        g.fillPolygon(xs, ys, n);

        // Heading indicator from the transformed center
        g.setColor(Color.WHITE);
        double rad = Math.toRadians(this.rotation);
        int x1 = (int) Math.round(cx);
        int y1 = (int) Math.round(cy);
        int x2 = (int) Math.round(cx + 20 * Math.cos(rad));
        int y2 = (int) Math.round(cy + 20 * Math.sin(rad));
        g.drawLine(x1, y1, x2, y2);
    }

    // KeyListener
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W: case KeyEvent.VK_UP:    forward = true; break;
            case KeyEvent.VK_A: case KeyEvent.VK_LEFT:  left = true;    break;
            case KeyEvent.VK_D: case KeyEvent.VK_RIGHT: right = true;   break;
        }
    }
    @Override public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W: case KeyEvent.VK_UP:    forward = false; break;
            case KeyEvent.VK_A: case KeyEvent.VK_LEFT:  left = false;    break;
            case KeyEvent.VK_D: case KeyEvent.VK_RIGHT: right = false;   break;
        }
    }
}
