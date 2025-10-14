package game;

import java.awt.*;

public class Coin extends Polygon implements Collidable, Updatable {
    private static final int MOVE_SPEED = 3;
    boolean collected;

    /**
     * Constructor for coin object
     * @param inShape Array of points that define the shape of the car
     * @param inPosition Starting position
     * @param inRotation Initial rotation
     */
     public Coin(Point[] inShape, Point inPosition, double inRotation) {
        super(inShape, inPosition, inRotation);
        this.collected = false;
    }

    /**
     * Mark this coin as collected
     */
    public void collect() {
        this.collected = true;
    }

    /**
     * Check if coin has been collected
     * @return true if collected
     */
    public boolean isCollected() {
        return collected;
    }

    /**
     * Move the coin upward (negative y direction)
     */
   
    public void move() {
        if (!collected) {
            position.y -= MOVE_SPEED;
            // Add slight rotation for visual effect
            this.rotate(3);
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
     * Paint the coin on the canvas
     * @param brush Graphics object for drawing
     */
    @Override
    public void paint(Graphics brush) {
        if (collected) {
            return; // Don't draw collected coins
        }
        
        Point[] points = this.getPoints();
        int[] xPoints = new int[points.length];
        int[] yPoints = new int[points.length];
        
        for (int i = 0; i < points.length; i++) {
            xPoints[i] = (int) points[i].x;
            yPoints[i] = (int) points[i].y;
        }
        
        // Draw coin body
        brush.setColor(new Color(255, 215, 0)); // Gold
        brush.fillPolygon(xPoints, yPoints, points.length);
        
        // Draw coin outline for better visibility
        brush.setColor(new Color(255, 165, 0)); // Orange
        brush.drawPolygon(xPoints, yPoints, points.length);
    }
}
