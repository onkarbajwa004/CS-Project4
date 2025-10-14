package game;

import java.awt.*;

public class Obstacle extends Polygon implements Collidable{
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
        position.y += MOVE_SPEED;
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
}
