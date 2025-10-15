package game;

import java.awt.*;

/**
 * Coin: a collectible that scrolls upward and spins.
 * Extends Polygon, implements Updatable (for move/paint) and Collidable.
 */
class Coin extends Polygon implements Updatable, Collidable {
    private static final int MOVE_SPEED = 3;     // pixels/frame upward
    private static final double SPIN_PER_TICK = 4.0; // deg/frame spin

    private boolean collected = false;
    private final int value = 1;                 // per-coin value

    public Coin(Point[] inShape, Point inPosition, double inRotation) {
        super(inShape, inPosition, inRotation);
    }

    /** Has this coin been collected already? */
    public boolean isCollected() { return collected; }

    /** Mark this coin as collected. */
    public void collect() { collected = true; }

    /** Optional getter if you want score += c.getValue(); */
    public int getValue() { return value; }

    // ---- Updatable ----
    @Override
    public void move() {
        if (collected) return;
        this.position.y -= MOVE_SPEED;                 // scroll up
        this.rotation = (this.rotation + SPIN_PER_TICK) % 360; // spin
    }

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

    // ---- Collidable ----
    @Override
    public boolean collides(Polygon other) {
        // any of my points inside other?
        for (Point p : this.getPoints()) if (other.contains(p)) return true;
        // any of other's points inside me?
        for (Point p : other.getPoints()) if (this.contains(p)) return true;
        return false;
    }
}
