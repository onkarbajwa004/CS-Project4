package game;

import java.awt.*;
public class Obstacle extends Polygon implements Collidable, Updatable{
    private static final int MOVE_SPEED = 3;

    /**
     * Constructor for Obstacle object
     * @param inShape Array of points that define the shape of the car
     * @param inPosition Starting position
     * @param inRotation Initial rotation
     */
    public Obstacle(Point[] inShape, Point inPosition, double inRotation) {
        super(inShape, inPosition, inRotation);
    }

    /**
     * Move the obstacle upward (negative y direction)
     */
     public void move() {
        position.y -= MOVE_SPEED;
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
     * Check if obstacle is off screen (above the top)
     * @param height Height of the game window
     * @return true if obstacle is completely off screen
     */
    public boolean isOffscreen(int height) {
        return position.y < -50 || position.y > height + 50; //Above or below screen
    }

    /**
     * Paint the obstacle on the canvas
     * @param brush Graphics object for drawing
     */
    @Override
    public void paint(Graphics brush) {
        Point[] points = this.getPoints();
        int[] xPoints = new int[points.length];
        int[] yPoints = new int[points.length];
        
        for (int i = 0; i < points.length; i++) {
            xPoints[i] = (int) points[i].x;
            yPoints[i] = (int) points[i].y;
        }
        
        // Draw obstacle body
        brush.setColor(new Color(220, 20, 20)); // Red
        brush.fillPolygon(xPoints, yPoints, points.length);
        
        // Draw outline
        brush.setColor(new Color(255, 100, 100)); // Light red
        brush.drawPolygon(xPoints, yPoints, points.length);
    }

}
