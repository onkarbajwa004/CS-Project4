package game;

import java.awt.*;

/**
 * CLASS: Coin
 * DESCRIPTION: Coins are collectibles that scroll upward and spin.
 *              When coins are collected, the player is rewarded points
 *              Extends Polygon, implements Updatable (for move/paint) and Collidable.
 * AUTHORS: Onkar Bajwa and Natheer Muwonge
 */

class Coin extends Polygon implements Updatable, Collidable {
    private static final int MOVE_SPEED = 3;     // pixels/frame upward
    private static final double SPIN_PER_TICK = 4.0; // deg/frame spin

    private boolean collected = false;
    private final int value = 1;      // Each coin has a value of 1

    /**
     * Constructs a new Coin with the specified shape, position, and rotation. 
     * 
     * @param inShape Point array holding points that make the shape of the coin
     * @param inPosition Point that has the initial position of the coin
     * @param inRotation Initial degree of rotation for the coin
     */
    public Coin(Point[] inShape, Point inPosition, double inRotation) {
        super(inShape, inPosition, inRotation);
    }

    /** 
     * Check if the coin has been collected already 
     * 
     * @return true if coin has been collected
    */
    public boolean isCollected() { return collected; }

    /** 
     * Mark this coin as collected. 
    */
    public void collect() { collected = true; }

    /** 
     * Optional getter if you want score += c.getValue(); 
     * @return int that has value of the coin
    */
    public int getValue() { return value; }

    // Updatable Implementation

    /**
     * If the coin is already collected, no movement occurs.
     * Otherwise, the coin scrolls upwards and rotates while doing so
     */
    @Override
    public void move() {
        if (collected) return;
        this.position.y -= MOVE_SPEED;                 // scroll up
        this.rotation = (this.rotation + SPIN_PER_TICK) % 360; // spin
    }

    /**
     * Renders the coins on the screen as a gold polygon
     * 
     * @param g Graphics used for the rendering
     */
    @Override
    public void paint(Graphics g) {
        if (collected) return;
        Point[] pts = this.getPoints();
        int n = pts.length;
        int[] xs = new int[n];
        int[] ys = new int[n];
        for (int i = 0; i < n; i++) {
            xs[i] = (int)Math.round(pts[i].x);
            ys[i] = (int)Math.round(pts[i].y);
        }
        g.setColor(new Color(255, 215, 0)); // gold
        g.fillPolygon(xs, ys, n);
        g.setColor(Color.BLACK);
        g.drawPolygon(xs, ys, n);
    }

    // Collidable Implementation

    /**
     * Check if the coin collides with another polygon
     * 
     * @param other The other polygon being checking for collision
     * @return true if a collision is detected
     */
    @Override
    public boolean collides(Polygon other) {
        // any of my points inside other?
        for (Point p : this.getPoints()) if (other.contains(p)) return true;
        // any of other's points inside me?
        for (Point p : other.getPoints()) if (this.contains(p)) return true;
        return false;
    }
}
