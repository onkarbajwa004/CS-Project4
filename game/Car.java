package game;

<<<<<<< HEAD
import java.awt.event.*;
import java.awt.*;

public class Car extends Polygon implements KeyListener, Updatable, Collidable {
    private boolean leftPressed;
    private boolean rightPressed;
    private static final int MOVE_SPEED = 6;

    /**
     * Constructor for Car
     * @param inShape Array of points that define the shape of the car
     * @param inPosition Starting position
     * @param inRotation Initial rotation
     */
    public Car(Point[] inShape, Point inPosition, double inRotation) {
        super(inShape, inPosition, inRotation);
        leftPressed = false;
        rightPressed = false;
    }

    /**
     * Car movement based on keyboard input
     * Can only move left or right
     */
    @Override
    public void move() {
        if (leftPressed) {
            position.x -= MOVE_SPEED;
        }
        if (rightPressed) {
            position.x += MOVE_SPEED;
        }
    }

    /**
     * Check collision with another Polygon
     * @param other The other polygon to check collision with
     * @return true if collision is detected
     */
    @Override
    public boolean collides(Polygon other) {
        for (Point p : this.getPoints()) {
            if (other.contains(p)) return true;
        }
     
        for (Point p : other.getPoints()) {
            if (this.contains(p)) return true;
        }
        
        return false;
    }

    /**
     * Paint the car on the canvas
     * @param brush Graphics object for drawing
     */
    @Override
    public void paint(Graphics brush) {
        Point[] points = this.getPoints();
        int[] xPoints = new int[points.length];
        int[] yPoints = new int[points.length];
        
        for (int i = 0; i < points.length; i++) {
            xPoints[i] = (int) points[i].x;
            yPoints[i] = (int) points[i].y;  //Set both arrays with x and y values of coordinates
        }
        
        // Draw car body
        brush.setColor(new Color(0, 0, 255)); // Blue
        brush.fillPolygon(xPoints, yPoints, points.length);
        
        // Draw outline
        brush.setColor(Color.GREEN);
        brush.drawPolygon(xPoints, yPoints, points.length);
    }

    /** 
     * Method for handling KeyPressed event from KeyListener interface
     * @param k KeyEvent inputted
    */
    @Override
    public void keyPressed(KeyEvent k) {
        int keyPress = k.getKeyCode();
        if (keyPress == KeyEvent.VK_LEFT || keyPress == KeyEvent.VK_A) {
            leftPressed = true;
        }
        if (keyPress == KeyEvent.VK_RIGHT || keyPress == KeyEvent.VK_D) {
            rightPressed = true;
        }
    }

    /** 
     * Method for handling KeyReleased event from KeyListener interface
     * @param k KeyEvent inputted
    */
    @Override
    public void keyReleased(KeyEvent k) {
        int keyPress = k.getKeyCode();
        if (keyPress == KeyEvent.VK_LEFT || keyPress == KeyEvent.VK_A) {
            leftPressed = false;
        }
        if (keyPress == KeyEvent.VK_RIGHT || keyPress == KeyEvent.VK_D) {
            rightPressed = false;
        }
    }
    /**
     * Not needed for our project, but added because of KeyListener requirements
     * @param k KeyEvent inputted
     */
    @Override
    public void keyTyped(KeyEvent k) {
        // Not used but required by KeyListener interface
    }
=======
import java.awt.*;
import java.awt.event.*;

public class Car extends Polygon implements KeyListener, Updatable {
    private double stepSize = 2.4;   // pixels per frame (slower)
    private double rotStep  = 2.5;   // degrees per frame (smoother)
    private double rotVelocity = 0.0; // for smoothing
    private boolean forward, left, right;

    public Car(Point[] shape, Point position, double initialRotationDeg) {
        super(shape, position, initialRotationDeg);
    }

    @Override
    public void move() {
        // smooth rotation velocity
        if (left)  rotVelocity = -rotStep;
        else if (right) rotVelocity = rotStep;
        else rotVelocity *= 0.8; // damping
        this.rotation += rotVelocity;

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
        for (int i = 0; i < n; i++) { xs[i] = (int)Math.round(pts[i].x); ys[i] = (int)Math.round(pts[i].y); }
        g.setColor(new Color(120,200,120)); // greener car
        g.fillPolygon(xs, ys, n);

        // heading indicator
        g.setColor(Color.WHITE);
        double rad = Math.toRadians(this.rotation);
        g.drawLine(
            (int)Math.round(position.x), (int)Math.round(position.y),
            (int)Math.round(position.x + 20*Math.cos(rad)),
            (int)Math.round(position.y + 20*Math.sin(rad))
        );
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
>>>>>>> ff855d8 (Gameplay: reset car to top-center on reset)
}
