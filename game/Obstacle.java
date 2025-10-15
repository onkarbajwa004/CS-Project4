package game;

import java.awt.*;

/**
 * CLASS: Obstacle
 * DESCRIPTION: This class represents the logic and creation of Obstacles in the game.
 *              If the players car collides with an obstacle, the game ends.
 *              Extends Polygon, implements Updatable (for move/paint).
 * AUTHORS: Onkar Bajwa, Natheer Muwonge
 */

class Obstacle extends Polygon implements Updatable {
    private double speedY = 4.0; // pixels/frame upward

    /**
     * Constructs a new Obstacle with the specified shape, position, and rotation.
     * 
     * @param inShape Point array holding points that make the shape of the obstacle
     * @param inPosition Point that has the initial position of the obstacle
     * @param inRotation Initial degree of rotation for the obstacle
     */
    Obstacle(Point[] inShape, Point inPosition, double inRotation) {
        super(inShape, inPosition, inRotation);
    }

    /**
     * Updates the obstacle's position by moving it upward.
     */
    @Override
    public void move() {
        this.position.y -= speedY;
    }

    /**
     * Check if the obstacle has scrolled completely off the top of the screen
     * 
     * @param screenHeight Height of the game window
     * @return true if bottom edge of the obstacle is off the screen
     */
    public boolean isOffscreen(int screenHeight) {
        Point[] pts = this.getPoints();
        double maxY = pts[0].y;
        for (int i = 1; i < pts.length; i++) {
            if (pts[i].y > maxY) maxY = pts[i].y;
        }
        return maxY < 0;
    }

    /**
     * Renders the obstacles on the screen as red polygons
     * 
     * @param g graphics used for rendering
     */
    @Override
    public void paint(Graphics g) {
        Point[] pts = this.getPoints();
        int n = pts.length;
        int[] xs = new int[n];
        int[] ys = new int[n];
        for (int i = 0; i < n; i++) {
            xs[i] = (int) Math.round(pts[i].x);
            ys[i] = (int) Math.round(pts[i].y);
        }
        g.setColor(new Color(220, 70, 70));
        g.fillPolygon(xs, ys, n);
    }

    // Optional tuning

    /**
     * Sets upward speed of the obstacles. Can be used for difficulty adjustment
     * 
     * @param v New speed value
     */
    public void setSpeedY(double v) { 
        speedY = v; 
    }

    /**
     * Gets the current upward speed of the obstacles
     * 
     * @return Current speed of obstacles
     */
    public double getSpeedY() { 
        return speedY; 
    }
}
