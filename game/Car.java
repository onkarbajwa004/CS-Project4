package game;

import java.awt.*;
import java.awt.event.*;

/**
 * CLASS: Car
 * DESCRIPTION: This class represents the player contolled Car in the game
 *              using steering and rotation
 *              Extends Polygon, implements Updatable (for move/paint) and KeyListener.
 * AUTHORS: Onkar Bajwa, Natheer Muwonge
 */
public class Car extends Polygon implements KeyListener, Updatable {
    private double stepSize = 3.3;  // pixels per frame
    private double rotStep = 3.4;  // degrees per frame (target turn rate)
    private double rotVelocity = 0.0; // smoothed turn velocity
    private boolean forward, left, right;

    /**
     * Constructs a new Car with the specified shape, position, and rotation.
     * 
     * @param inShape Point array holding points that make the shape of the car
     * @param inPosition Point that has the initial position of the car
     * @param inRotation Initial degree of rotation for the car
     */
    public Car(Point[] inShape, Point inPosition, double inRotation) {
        super(inShape, inPosition, inRotation);
    }

    /**
     * Updates the car's position and rotation based on current input state.
     * Forward movement is applied in the direction the car is currently facing.
     */
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

    /**
     * Renders the car on the screen with a directional indicator.
     * Draws the car body as a filled polygon and a white line showing heading direction.
     * 
     * @param g Graphics used for rendering
     */
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

    /**
     * Handles key typed events (not used but required by KeyListener interface).
     * 
     * @param e The KeyEvent triggered by typing a key
     */
    @Override public void keyTyped(KeyEvent e) {}

    /**
     * Handles key press events to update car movement state.
     * Responds to W/UP (forward), A/LEFT (turn left), and D/RIGHT (turn right).
     * 
     * @param e The KeyEvent triggered by pressing a key
     */
    @Override public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W: case KeyEvent.VK_UP:    forward = true; break;
            case KeyEvent.VK_A: case KeyEvent.VK_LEFT:  left = true;    break;
            case KeyEvent.VK_D: case KeyEvent.VK_RIGHT: right = true;   break;
        }
    }

    /**
     * Handles key release events to update car movement state.
     * Stops the corresponding movement when keys are released.
     * 
     * @param e The KeyEvent triggered by releasing a key
     */
    @Override public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W: case KeyEvent.VK_UP:    forward = false; break;
            case KeyEvent.VK_A: case KeyEvent.VK_LEFT:  left = false;    break;
            case KeyEvent.VK_D: case KeyEvent.VK_RIGHT: right = false;   break;
        }
    }
}
