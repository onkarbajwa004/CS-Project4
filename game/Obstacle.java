package game;

import java.awt.*;

class Obstacle extends Polygon implements Updatable {
    private double speedY = 4.0; // pixels/frame upward

    Obstacle(Point[] shape, Point position, double rotation) {
        super(shape, position, rotation);
    }

    @Override
    public void move() {
        this.position.y -= speedY;
    }

    public boolean isOffscreen(int screenHeight) {
        Point[] pts = this.getPoints();
        double maxY = pts[0].y;
        for (int i = 1; i < pts.length; i++) {
            if (pts[i].y > maxY) maxY = pts[i].y;
        }
        return maxY < 0;
    }

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
    public void setSpeedY(double v) { speedY = v; }
    public double getSpeedY() { return speedY; }
}
