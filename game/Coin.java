package game;

public class Coin extends Polygon implements Collidable {
    private static final int MOVE_SPEED = 3;
    private int value;  

     public Coin(Point[] inShape, Point inPosition, double inRotation) {
        super(inShape, inPosition, inRotation);
        this.value = 1; // Each coin worth 1 point
    }


    @Override
    public boolean collides(Polygon other) {
        Point[] myPoints = this.getPoints();
        Point[] otherPoints = other.getPoints();
        
        // Check if any of my points are in the other polygon
        for (Point p : myPoints) {
            if (other.contains(p)) {
                return true;
            }
        }
        
        // Check if any of other's points are in my polygon
        for (Point p : otherPoints) {
            if (this.contains(p)) {
                return true;
            }
        }
        
        return false;
    }
}
