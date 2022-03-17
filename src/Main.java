import elements.Ray;
import elements.Wall;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

public class Main {

    // Misc graphics vars
    private JFrame frame;
    private Canvas canvas;
    private Graphics graphics;
    private Graphics2D g2D;
    private BufferStrategy b;
    private BufferedImage buffer;
    private GraphicsDevice gd;
    private GraphicsEnvironment ge;
    private GraphicsConfiguration gc;

    // Used when creating walls and "dropping" the caster
    private  boolean leftClickBool = false;
    private int rightClickCount = 0;

    private boolean isRunning = false;

    // Master list of all rays and walls
    public static ArrayList<Wall> walls;
    public static ArrayList<Ray> rays;

    public static Point2D mousePoint;

    // Endpoints for walls created by mouse click
    public static Point2D wp1;
    public static Point2D wp2;

    public final static int HEIGHT = 1000;
    public final static int WIDTH = 1000;

    public final static int RAY_COUNT = 300;

    public Main() {
        initialize();
        Engine.genWalls();
        Engine.initialize();
        draw();

    }

    private void initialize() {
        // Initialize walls and rays
        walls = new ArrayList<>();
        rays = new ArrayList<>();

        frame = new JFrame("RayCaster");
        /* Sets whether or not paint messages received
         * from the operating system should be ignored. */
        frame.setIgnoreRepaint(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        // Set default location for initial ray location
        mousePoint = new Point(WIDTH/2, HEIGHT/2);

        // Create canvas (which is used for painting to the frame)
        canvas = new Canvas();
        canvas.setIgnoreRepaint(true);
        canvas.setSize(WIDTH, HEIGHT);
        canvas.setBackground(Color.WHITE);

        // Add canvas to frame
        Container content = new Container();
        content.add(canvas);
        content.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        frame.setContentPane(content);
        frame.pack();

        // Add Mouse Listener to canvas
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    rightClickCount++;
                    if (rightClickCount == 1) wp1 = e.getPoint();
                    if (rightClickCount == 2) wp2 = e.getPoint();

                }
                if (e.getButton() == MouseEvent.BUTTON1) leftClickBool = !leftClickBool;
            }
        });

        // Set up the BufferStrategy for double buffering
        canvas.createBufferStrategy(2);
        b = canvas.getBufferStrategy();

        // Get graphics configuration
        ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        gd = ge.getDefaultScreenDevice();
        gc = gd.getDefaultConfiguration();

        // Create off-screen drawing surface
        buffer = gc.createCompatibleImage(WIDTH, HEIGHT);

        // Objects needed for rendering
        graphics = null;
        g2D = null;
        isRunning = true;

        // Center frame to display
        frame.setLocationRelativeTo(null);
        frame.setBackground(Color.BLACK);
        frame.setVisible(true);

    }

    private void draw() {
        // Vars for Debug
        int fps = 0;
        int frames = 0;
        long totalTime = 0;
        long curTime = System.currentTimeMillis();
        long lastTime = curTime;

        while (isRunning) {
            try {
                lastTime = curTime;
                curTime = System.currentTimeMillis();
                totalTime += curTime - lastTime;
                if (totalTime > 1000) {
                    totalTime -= 1000;
                    fps = frames;
                    frames = 0;
                }
                ++frames;

//                buffer = gc.createCompatibleImage(WIDTH, HEIGHT);
                g2D = buffer.createGraphics();

                g2D.setColor(Color.BLACK);
                g2D.fillRect(0, 0, WIDTH, HEIGHT);

                g2D.setColor(Color.WHITE);

                // Recalculate mouse position/instruction
                if (leftClickBool) mousePoint = canvas.getMousePosition();
                if (rightClickCount == 2) {
                    Wall w = new Wall(wp1, wp2);
                    w.setColor(Color.RED);
                    walls.add(w);
                    rightClickCount = 0;
                    wp1 = null;
                    wp2 = null;
                }

                // Update ray locations
                Engine.updateRays();

                // Update ray collisions
                Engine.checkCollisions();

                // Draw Rays
                for (Ray ray : rays) g2D.draw(ray.getLine2D());
//                drawBeam(g2D);

                // Draw Walls
                for (Wall wall : walls) {
                    g2D.setColor(wall.getColor());
                    g2D.draw(wall.getLine2D());
                }

                // display debug stats in frame
                g2D.setFont(new Font("Courier New", Font.PLAIN, 12));
                g2D.setColor(Color.GREEN);
                g2D.drawString(String.format("FPS: %s", fps), 20, 20);
                g2D.drawString(String.format("X: %s", mousePoint.getX()), 20, 30);
                g2D.drawString(String.format("Y: %s", mousePoint.getY()), 20, 40);

                // Draw buffered image
                graphics = b.getDrawGraphics();
                graphics.drawImage(buffer, 0, 0, null);
                if (!b.contentsLost()) b.show();

//                Thread.sleep(5);

            } catch (NullPointerException ignored) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void drawBeam(Graphics2D g2D) {
        int[] xs = new int[3];
        int[] ys = new int[3];
        Ray r1, r2;

        for (int i = 0; i < RAY_COUNT; i++) {
            r1 = rays.get(i);

            xs[0] = r1.getP1().getX();
            ys[0] = r1.getP1().getY();

            xs[1] = r1.getP2().getX();
            ys[1] = r1.getP2().getY();

            if (i == rays.size()-1) r2 = rays.get(0);
            else r2 = rays.get(i+1);

            // If the line has a collision
            if (r2.getColPoint()!=null) {
                // provide the collision point, not casted
                xs[2] = r2.getColPoint().getX();
                ys[2] = r2.getColPoint().getY();
            } else {
                xs[2] = r2.getP2().getX();
                ys[2] = r2.getP2().getY();
            }

//            Point2D p = new Line(xs[1], ys[1], xs[2], ys[2]).midpoint();
            g2D.setPaint(new GradientPaint(r1.getP1().getX(), r1.getP1().getY(), Color.WHITE, r1.getP2().getX(), r1.getP2().getY(), Color.BLACK));
            g2D.fillPolygon(new Polygon(xs, ys, 3));
        }
    }

    public static void main(String[] args) {
	    Main rayCaster = new Main();
    }
}
