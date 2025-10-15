package game;

/**
 * CLASS: RacerGame
 * DESCRIPTION: RacerGame implements the functionality of our game. The game is 
 *              a car that must drive around obstacles and collect coins in order to get a high score
 *              Extends Game
 * AUTHORS: Natheer Muwonge, Onkar Bajwa
 * NOTE: This class is the "main method" of our program
 */
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class RacerGame extends Game {
    // debug counter 
    static int counter = 0;

    // smaller window size
    private static final int WORLD_W = 640, WORLD_H = 480;

    // lanes & obstacle config
    private static final int LANE_COUNT  = 5;
    private static final int LANE_MARGIN = 12;
    private static final int OBSTACLE_W  = 36;
    private static final int OBSTACLE_H  = 36;

    // lane patterns: 1 = spawn obstacle in that lane
    private static final int[][] OBSTACLE_PATTERNS = {
        {1,0,0,0,1},
        {0,1,0,1,0},
        {1,0,1,0,1},
        {0,0,1,0,0}
    };

    // elements
    private Car car;
    private final List<Obstacle> obstacles = new ArrayList<>();
    private final List<Coin> coins = new ArrayList<>();

    // state
    private boolean paused = false;
    private boolean gameOver = false;
    private int score = 0;

    // inner classes

    /**
     * INNER CLASS: ObstacleSpawner
     * DESCRIPTION: Manages timed spawnings of obstacles in random patterns
     */
    private class ObstacleSpawner {
        private final Random rng = new Random();
        // spawn less frequently
        private final int spawnIntervalTicks; // frames between spawns
        private int ticksUntilSpawn;
        private int lastPatternIdx = -1;

        /**
         * Constructs an ObstacleSpawner object with the specified spawn interval
         * 
         * @param spawnIntervalTicks Number of frames between spawns
         */
        ObstacleSpawner(int spawnIntervalTicks) {
            this.spawnIntervalTicks = Math.max(40, spawnIntervalTicks);
            this.ticksUntilSpawn = this.spawnIntervalTicks;
        }
        /**
         * Updates the spawner timer and spawn obstacles every tick.
         * Does nothing if game is paused or over
         */
        void tick() {
            if (paused || gameOver) return;
            if (--ticksUntilSpawn <= 0) {
                int idx = pickRandomPatternIndex(rng, lastPatternIdx);
                lastPatternIdx = idx;
                spawnObstaclesFromPattern(OBSTACLE_PATTERNS[idx]);
                maybeDropCoin();
                ticksUntilSpawn = spawnIntervalTicks;
            }
        }

        /**
         * Randomly spawn a coin in a lane
         * Coins are spawned offscreen at the bottom and scroll upwards
         */
        private void maybeDropCoin() {
            // reduce coin spawn probability a bit (but per instruction, coins unchanged)
            if (rng.nextDouble() < 0.8) {
                int laneW = WORLD_W / LANE_COUNT;
                int lane = rng.nextInt(LANE_COUNT);
                int laneCenterX = laneW * lane + laneW / 2;
                double px = laneCenterX - (18 / 2.0);
                double py = WORLD_H + 20;
                coins.add(new Coin(diamondShape(18, 18), new Point(px, py), 0));
            }
        }
    }

    /**
     * INNER CLASS: Scoreboard
     * DESCRIPTION: Renders the game UI, including the player score, pause status, and game over
     */
    private class Scoreboard {
        /**
         * Draws the scoreboard, pause status, and game over UI
         * @param g Graphics used for rendering
         */
        void draw(Graphics g) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, 16));
            g.drawString("Score: " + score, 14, 24);
            if (paused)   g.drawString("PAUSED (P)", 14, 44);
            if (gameOver) g.drawString("GAME OVER — press R", 14, 64);
        }
    }

    private final ObstacleSpawner spawner = new ObstacleSpawner(90);
    private final Scoreboard ui = new Scoreboard();

    /**
     * Constructs a new RacerGame instance
     * Initializes game window, input listeners, and player car
     * Also sets up keeyboard controls for pause and reset
     */
    public RacerGame() {
        super("RacerGame!", WORLD_W, WORLD_H);
        this.setFocusable(true);
        this.requestFocus();

        // player car: triangle, start top-center facing down (90°)
        Point[] carPts = new Point[] {
            new Point(28, 0), new Point(0, 50), new Point(56, 50)
        };
        double carTopLeftX = (WORLD_W - 56) / 2.0;
        double carTopLeftY = 30; // top-ish
        car = new Car(carPts, new Point(carTopLeftX, carTopLeftY), 90);

        // movement keys
        this.addKeyListener(car);

        // ANONYMOUS CLASS: KeyAdapter for pause (P) & reset (R)
        this.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_P) paused = !paused;
                if (e.getKeyCode() == KeyEvent.VK_R) resetGame();
            }
        });
    }

    /**
     * Main method that executes the game
     * 
     * @param args parameter for main
     */
    public static void main (String[] args) {
        RacerGame a = new RacerGame();
        a.repaint();
    }

    /**
     * Main paint method that draws everything
     * Handles rendering, game updates, etc.
     * 
     * @param brush Graphics used for rendering game elements
     */
    public void paint(Graphics brush) {
        // background
        brush.setColor(new Color(20,20,30));
        brush.fillRect(0,0,width,height);

        // lane dividers
        brush.setColor(new Color(60,60,70));
        int laneW = WORLD_W / LANE_COUNT;
        for (int i = 1; i < LANE_COUNT; i++) {
            int x = i * laneW;
            brush.fillRect(x - 2, 0, 4, WORLD_H);
        }

        if (!paused && !gameOver) {
            spawner.tick();

            car.move();
            for (Coin c : coins) c.move();
            updateObstacles();

            // coin collection
            for (Coin c : coins) {
                if (!c.isCollected() && polysCollide(car, c)) {
                    c.collect();
                    score += 10;
                }
            }
            // obstacle collision
            for (Obstacle o : obstacles) {
                if (polysCollide(car, o)) {
                    gameOver = true;
                    break;
                }
            }
            wrap(car);
        }

        // draw elements
        for (Coin c : coins) c.paint(brush);
        for (Obstacle o : obstacles) o.paint(brush);
        car.paint(brush);

        // debug counter
        counter++;
        brush.setColor(Color.white);
        brush.drawString("Counter is " + counter, 10, 10);

        // UI
        ui.draw(brush);
    }

    // Helper Methods

    /**
     * Creates a rectangle polygon
     * 
     * @param w Width of the rectangle
     * @param h Height of the rectangle
     * @return Array of points that define the rectangle
     */

    private static Point[] rectShape(int w, int h) {
        return new Point[] {
            new Point(0,0), new Point(w,0), new Point(w,h), new Point(0,h)
        };
    }

    /**
     * Creates a diamond polygon
     * 
     * @param w Width of the diamond
     * @param h Height of the diamond
     * @return Array of points defining the diamond
     */
    private static Point[] diamondShape(int w, int h) {
        int hw = w/2, hh = h/2;
        return new Point[] {
            new Point(hw,0), new Point(w,hh), new Point(hw,h), new Point(0,hh)
        };
    }

    /**
     * Detects collision between two polygons
     * 
     * @param a First polygon being checked
     * @param b Second polygon being checked
     * @return true if polygons intersect, false otherwise
     */
    private boolean polysCollide(Polygon a, Polygon b) {
        for (Point p : a.getPoints()) if (b.contains(p)) return true;
        for (Point p : b.getPoints()) if (a.contains(p)) return true;
        return false;
    }

    /**
     * Calculates the left x-coordinate for an obstacle in a lane
     * 
     * @param laneIndex index of the lane
     * @return x-coordinate where the obstacle should be positioned
     */
    private double laneLeftX(int laneIndex) {
        double usableWidth = this.width - (LANE_MARGIN * 2.0);
        double laneWidth   = usableWidth / LANE_COUNT;
        return LANE_MARGIN + laneIndex * laneWidth + (laneWidth - OBSTACLE_W) / 2.0;
    }

    /**
     * Spawns obstacles from a chosen pattern near the bottom of the screen
     * 
     * @param pattern pattern Array where 1 indicates an obstacle spawn in that lane
     */
    private void spawnObstaclesFromPattern(int[] pattern) {
        double spawnY = this.height + 10;  // off-screen bottom
        for (int lane = 0; lane < LANE_COUNT; lane++) {
            if (pattern[lane] == 1) {
                double x = laneLeftX(lane);
                obstacles.add(new Obstacle(
                    rectShape(OBSTACLE_W, OBSTACLE_H),
                    new Point(x, spawnY),
                    0
                ));
            }
        }
    }

    /**
     * Randomly selects a pattern index
     * 
     * @param rng Random number
     * @param lastIdx index of last selected pattern
     * @return index of the newly seelced pattern
     */
    private int pickRandomPatternIndex(Random rng, int lastIdx) {
        int idx = rng.nextInt(OBSTACLE_PATTERNS.length);
        if (OBSTACLE_PATTERNS.length > 1) {
            while (idx == lastIdx) idx = rng.nextInt(OBSTACLE_PATTERNS.length);
        }
        return idx;
    }

    /**
     * Updates all active obstacles and removes ones that have gone offscreen
     * Uses a lamda expression to filter out obstacles
     */
    private void updateObstacles() {
        for (Obstacle o : obstacles) 
            o.move();
        obstacles.removeIf(o -> o.isOffscreen(this.height)); // lambda
    }

    /**
     * Resets the game to its initial state by clearing obstacles, coins,
     * and repositioning the car back to its starting position
     */
    private void resetGame() {
        obstacles.clear();
        coins.clear();
        score = 0;
        gameOver = false;
        paused = false;
        // reset car to top-center facing down (match constructor)
        car.position.x = (WORLD_W - 56) / 2.0;
        car.position.y = 30;
        car.rotation = 90;
    }

    /**
     * Wraps the car around the edges of the screen
     * Allows for the car to go off the screen at one edge, and reappear 
     * on the opposite edge
     * 
     * @param c the car being wrapped around the screen
     */
    private void wrap(Car c) {
        if (c.position.x < -60) c.position.x = WORLD_W;
        if (c.position.x > WORLD_W) c.position.x = -60;
        if (c.position.y < -60) c.position.y = WORLD_H;
        if (c.position.y > WORLD_H) c.position.y = -60;
    }
}
