package game;

public interface Collidable {
    /*
    *Check if this object collides with another Polygon
    *@param other The other polygon to check collision with
    *@return true if collision is detected, false otherwise
    */
    boolean collides(Polygon other);
}
