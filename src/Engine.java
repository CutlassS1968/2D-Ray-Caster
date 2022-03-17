import elements.*;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Engine {

    // Angle of each ray?
    private final static double d = (360.0/Main.RAY_COUNT)*(Math.PI/180);

    // Length of each ray
    private final static int gR = 500;

    // Generate bounds around frame
    public static void genWalls() {
        Point p1 = new Point(0, 0);
        Point p2 = new Point(Main.WIDTH, 0);
        Point p3 = new Point(Main.WIDTH, Main.HEIGHT);
        Point p4 = new Point(0, Main.HEIGHT);

        Main.walls.add(new Wall(p1, p2));
        Main.walls.add(new Wall(p2, p3));
        Main.walls.add(new Wall(p3, p4));
        Main.walls.add(new Wall(p4, p1));
    }

    /**
     * Create an initial state for the rays list
     */
    public static void initialize() {
        /* While I could merge initialize and updateRays by having one function
         * return a list of points, this would mean iterating through the rays
         * twice as opposed to once.. It's ugly but this will have to do */
        Point p1 = new Point(Main.mousePoint);
        // Create each ray in a circle around the mouse point
        for (int i = 0; i < Main.RAY_COUNT; i++) {
            Ray r = new Ray();
            int x = (int)(p1.getX() + (gR*Math.cos(d*i)));
            int y = (int)(p1.getY() + (gR*Math.sin(d*i)));
            Point p2 = new Point(x, y);
            r.setP1(p1);
            r.setP2(p2);
            Main.rays.add(r);
        }
    }

    /**
     * Update ray locations relative to mouse position
     * DOES NOT CHANGE RAYCOLPOINT
     */
    public static void updateRays() {
        Point p1 = new Point(Main.mousePoint);
        // Create each ray in a circle around the mouse point
        for (int i = 0; i < Main.RAY_COUNT; i++) {
            int x = (int)(p1.getX() + (gR*Math.cos(d*i)));
            int y = (int)(p1.getY() + (gR*Math.sin(d*i)));
            // Prevents collided rays from sticking
            if (Main.rays.get(i).getColPoint()!= null) Main.rays.get(i).setColPoint(null);
            Point p2 = new Point(x, y);
            Main.rays.get(i).setP1(p1);
            Main.rays.get(i).setP2(p2);
        }
    }

    /**
     * Calculate intersections of rays and walls
     * @param w wall
     * @param l ray
     * @return intersection point or null
     */
    private static Point intersect(Line w, Line l) {
        float p0_x = (float)w.getP1().getX();
        float p0_y = (float)w.getP1().getY();

        float p1_x = (float)w.getP2().getX();
        float p1_y = (float)w.getP2().getY();

        float p2_x = (float)l.getP1().getX();
        float p2_y = (float)l.getP1().getY();

        float p3_x = (float)l.getP2().getX();
        float p3_y = (float)l.getP2().getY();

        float s1_x, s1_y, s2_x, s2_y;
        s1_x = p1_x - p0_x;     s1_y = p1_y - p0_y;
        s2_x = p3_x - p2_x;     s2_y = p3_y - p2_y;

        float s, t;
        s = (-s1_y * (p0_x - p2_x) + s1_x * (p0_y - p2_y)) / (-s2_x * s1_y + s1_x * s2_y);
        t = ( s2_x * (p0_y - p2_y) - s2_y * (p0_x - p2_x)) / (-s2_x * s1_y + s1_x * s2_y);

        if (s >= 0 && s <= 1 && t >= 0 && t <= 1) {
            // Collision detected
            float i_x, i_y;
            i_x = p0_x + (t * s1_x);
            i_y = p0_y + (t * s1_y);

            return new Point(i_x, i_y);
        }
        // Else return null
        return null;
    }

    public static void checkCollisions() {
        for (int i = 0; i < Main.RAY_COUNT; i++) {
            for (int j = 0; j < Main.walls.size(); j++) {
                Wall w = Main.walls.get(j);
                Ray r = Main.rays.get(i);
                if (intersect(w, r)!=null) { // might be redundant because colpoint is null, and intersect returns null
                    Point p = intersect(w, r);
                    r.setColPoint(p);
                    Main.rays.set(i, r);
                }
            }
        }
    }
}
