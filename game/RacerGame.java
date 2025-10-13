package game;

/*
CLASS: YourGameNameoids
DESCRIPTION: Extending Game, YourGameName is all in the paint method.
NOTE: This class is the metaphorical "main method" of your program,
      it is your control center.
*/
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class RacerGame extends Game {
    // debug counter (from your template)
    static int counter = 0;

    // window size
    private static final int WORLD_W = 800, WORLD_H = 600;

    // lanes & obstacle config
    private static final int LANE_COUNT  = 5;
    private static final int LANE_MARGIN = 20;
    private static final int OBSTACLE_W  = 40;
    private static final int OBSTACLE_H  = 40;

    // lane patterns: 1 = spawn obstacle in that lane
    private static final int[][] OBSTACLE_PATTERNS = {
        {1,0,0,0,1},
        {0,1,0,1,0},
        {1,0,1,0,1},
        {0,0,1,0,0},
        {0,1,1,1,0},
        {1,1,0,1,1},
        {1,0,0,1,0},
        {0,1,0,0,1}
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
    private class ObstacleSpawner {
        private final Random rng = new Random();
        private final int spawnIntervalTicks; // frames between spawns
        private int ticksUntilSpawn;
        private int lastPatternIdx = -1;

        ObstacleSpawner(int spawnIntervalTicks) {
            this.spawnIntervalTicks = Math.max(5, spawnIntervalTicks);
            this.ticksUntilSpawn = this.spawnIntervalTicks;
        }

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

        private void maybeDropCoin() {
            if (rng.nextDouble() < 0.45) {
                int laneW = WORLD_W / LANE_COUNT;
                int lane = rng.nextInt(LANE_COUNT);
                int laneCenterX = laneW * lane + laneW / 2;
                double px = laneCenterX - (18 / 2.0);
                double py = WORLD_H + 20;
                coins.add(new Coin(diamondShape(18, 18), new Point(px, py), 0));
            }
        }
    }

    private class Scoreboard {
        void draw(Graphics g) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, 16));
            g.drawString("Score: " + score, 14, 24);
            if (paused)   g.drawString("PAUSED (P)", 14, 44);
            if (gameOver) g.drawString("GAME OVER — press R", 14, 64);
        }
    }

    private final ObstacleSpawner spawner = new ObstacleSpawner(25);
    private final Scoreboard ui = new Scoreboard();

    // ctor
    public RacerGame() {
        super("RacerGame!", WORLD_W, WORLD_H);
        this.setFocusable(true);
        this.requestFocus();

        // player car: triangle, start bottom-center, facing up (270°)
        Point[] carPts = new Point[] {
            new Point(28, 0), new Point(0, 50), new Point(56, 50)
        };
        double carTopLeftX = (WORLD_W - 56) / 2.0;
        double carTopLeftY = WORLD_H - 90;
        car = new Car(carPts, new Point(carTopLeftX, carTopLeftY), 270);

        // movement keys
        this.addKeyListener(car);

        // anonymous class: pause (P) & reset (R)
        this.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_P) paused = !paused;
                if (e.getKeyCode() == KeyEvent.VK_R) resetGame();
            }
        });
    }

    // main
    public static void main (String[] args) {
        RacerGame a = new RacerGame();
        a.repaint();
    }

    // paint loop
    public void paint(Graphics brush) {
        // background
        brush.setColor(Color.black);
        brush.fillRect(0,0,width,height);

        // lane dividers
        brush.setColor(new Color(40,40,40));
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

        // debug counter from template
        counter++;
        brush.setColor(Color.white);
        brush.drawString("Counter is " + counter, 10, 10);

        // UI
        ui.draw(brush);
    }

    // ---------- helpers ----------

    private static Point[] rectShape(int w, int h) {
        return new Point[] {
            new Point(0,0), new Point(w,0), new Point(w,h), new Point(0,h)
        };
    }
    private static Point[] diamondShape(int w, int h) {
        int hw = w/2, hh = h/2;
        return new Point[] {
            new Point(hw,0), new Point(w,hh), new Point(hw,h), new Point(0,hh)
        };
    }

    /** polygon–polygon collision using contains() both ways */
    private boolean polysCollide(Polygon a, Polygon b) {
        for (Point p : a.getPoints()) if (b.contains(p)) return true;
        for (Point p : b.getPoints()) if (a.contains(p)) return true;
        return false;
    }

    /** x-left of a lane’s obstacle (0-based) */
    private double laneLeftX(int laneIndex) {
        double usableWidth = this.width - (LANE_MARGIN * 2.0);
        double laneWidth   = usableWidth / LANE_COUNT;
        return LANE_MARGIN + laneIndex * laneWidth + (laneWidth - OBSTACLE_W) / 2.0;
    }

    /** spawn obstacles for a chosen pattern in one row near the bottom */
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

    /** randomly pick a pattern index different from the previous one */
    private int pickRandomPatternIndex(Random rng, int lastIdx) {
        int idx = rng.nextInt(OBSTACLE_PATTERNS.length);
        if (OBSTACLE_PATTERNS.length > 1) {
            while (idx == lastIdx) idx = rng.nextInt(OBSTACLE_PATTERNS.length);
        }
        return idx;
    }

    /** move obstacles upward and cull off-screen */
    private void updateObstacles() {
        for (Obstacle o : obstacles) o.move();
        obstacles.removeIf(o -> o.isOffscreen(this.height)); // lambda
    }

    private void resetGame() {
        obstacles.clear();
        coins.clear();
        score = 0;
        gameOver = false;
        paused = false;
        car.position.x = (WORLD_W - 56) / 2.0;
        car.position.y = WORLD_H - 90;
        car.rotation = 270;
    }

    private void wrap(Car c) {
        if (c.position.x < -60) c.position.x = WORLD_W;
        if (c.position.x > WORLD_W) c.position.x = -60;
        if (c.position.y < -60) c.position.y = WORLD_H;
        if (c.position.y > WORLD_H) c.position.y = -60;
    }
}
