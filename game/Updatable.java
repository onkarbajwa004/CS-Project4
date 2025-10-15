package game;

import java.awt.Graphics;

/**
 * INTERFACE: Updatable
 * DESCRIPTION: Defines the contract for game objects that need to render or update frames
 * AUTHORS: Natheer Muwonge, Onkar Bajwa
 */
public interface Updatable {
    /**
     * Renders the object's position and state for the current frame.
     * Called once per frame before rendering to account for changes in position, rotation, etc..
     */
    void move();

    /**
     * Renders the onject onto the screen.
     * Called once per frame after every object has been updated.
     * 
     * @param g Graphics used for drawing the game window
     */
    void paint(Graphics g);
}
