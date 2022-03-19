import elements.Ray;
import elements.Wall;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Main {

    // Misc graphics vars
    private JFrame planeFrame;
    private JFrame casterFrame;

    private Canvas planeCanvas;
    private Canvas casterCanvas;

    private BufferStrategy planeBufferStrategy;
    private BufferStrategy casterBufferStrategy;

    private Graphics graphics;
    private Graphics2D g2D;

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

    public final static int HEIGHT = 800;
    public final static int WIDTH = 800;

    public final static int RAY_COUNT = 800;

    // Length of each ray
    public final static float GR = (int) Math.sqrt(Math.pow(HEIGHT, 2) + Math.pow(WIDTH, 2));

    // Field of View
    public final static int FOV = 60;
    public static int raysCastIndexEnd;
    public static int raysCastIndexStart;
    public static int centerConeIndex;
    public static int strafeConeIndex;

    // Angle relative to x=0 which raysCastIndex starts
    // Used when rotating the camera
    public static float raysCastRotationAngle;

    public Main() {
        initialize();
        Engine.genWalls();
        Engine.initialize();
        run();

    }

    private void initialize() {
        // Initialize walls and rays
        walls = new ArrayList<>();
        rays = new ArrayList<>();

        // Set up initial camera left and rightmost index's depending on FOV
        raysCastRotationAngle = 0;
        raysCastIndexEnd = (int) (((FOV/360.0) * RAY_COUNT) + ((raysCastRotationAngle/360.0) * RAY_COUNT));
        raysCastIndexStart = (int) ((raysCastRotationAngle/360.0) * RAY_COUNT);

        // Set up plane frame
        planeFrame = new JFrame("RayCaster - Plane");
        /* Sets whether or not paint messages received
         * from the operating system should be ignored. */
        planeFrame.setIgnoreRepaint(true);
        planeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        planeFrame.setResizable(false);


        // Set up caster frame
        casterFrame = new JFrame("RayCaster - Caster");
        casterFrame.setIgnoreRepaint(true);
        casterFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        casterFrame.setResizable(false);

        // Set default location for initial ray location
        mousePoint = new Point(WIDTH/2, HEIGHT/2);

        // Create planeCanvas (which is used for painting to the planeFrame)
        planeCanvas = new Canvas();
        planeCanvas.setIgnoreRepaint(true);
        planeCanvas.setSize(WIDTH, HEIGHT);
        planeCanvas.setBackground(Color.WHITE);

        // Create casterCanvas
        casterCanvas = new Canvas();
        casterCanvas.setIgnoreRepaint(true);
        casterCanvas.setSize(WIDTH, HEIGHT);
        casterCanvas.setBackground(Color.WHITE);

        // Add planeCanvas to planeFrame
        Container planeContent = new Container();
        planeContent.add(planeCanvas);
        planeContent.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        planeFrame.setContentPane(planeContent);
        planeFrame.pack();

        // Add casterCanvas to casterFrame
        Container casterContent = new Container();
        casterContent.add(casterCanvas);
        casterContent.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        casterFrame.setContentPane(casterContent);
        casterFrame.pack();

        // Add Mouse Listener to planeCanvas
        planeCanvas.addMouseListener(new MouseAdapter() {
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
        ControlListener cl = new ControlListener();
        planeCanvas.addKeyListener(cl);

        casterCanvas.addKeyListener(cl);

        // Set up the BufferStrategy for double buffering
        planeCanvas.createBufferStrategy(2);
        planeBufferStrategy = planeCanvas.getBufferStrategy();

        // Set up double buffering for caster
        casterCanvas.createBufferStrategy(2);
        casterBufferStrategy = casterCanvas.getBufferStrategy();

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

        // Center planeFrame to display
        planeFrame.setLocationRelativeTo(null);
        planeFrame.setBackground(Color.BLACK);
        planeFrame.setVisible(true);

        casterFrame.setLocationRelativeTo(null);
        casterFrame.setBackground(Color.BLACK);
        casterFrame.setVisible(true);

    }

    private void run() {
        while (isRunning) {
            try {
                drawPlane();
                // Draw buffered image
                graphics = planeBufferStrategy.getDrawGraphics();
                graphics.drawImage(buffer, 0, 0, null);
                if (!planeBufferStrategy.contentsLost()) planeBufferStrategy.show();

                drawCaster();
                graphics = casterBufferStrategy.getDrawGraphics();
                graphics.drawImage(buffer, 0, 0, null);
                if (!casterBufferStrategy.contentsLost()) casterBufferStrategy.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void drawPlane() throws Exception {
        // Vars for Debug
        int fps = 0;
        int frames = 0;
        long totalTime = 0;
        long curTime = System.currentTimeMillis();
        long lastTime;


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
            if (leftClickBool) mousePoint = planeCanvas.getMousePosition();
            if (rightClickCount == 2) {
                Wall w = new Wall(wp1, wp2);
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
            for (Ray ray : rays) {
                g2D.setColor(ray.getColor());
                g2D.draw(ray.getLine2D());
            }

            // Draw Walls
            for (Wall wall : walls) {
                g2D.setColor(wall.getColor());
                g2D.draw(wall.getLine2D());
            }

            // Draw example

            double d = (360.0/Main.RAY_COUNT)*(Math.PI/180);
            elements.Point p1 = new elements.Point(mousePoint);
            int x = (int)(p1.getX() + (20*Math.cos(d*centerConeIndex)));
            int y = (int)(p1.getY() + (20*Math.sin(d*centerConeIndex)));
            elements.Point p2 = new elements.Point(x, y);

            g2D.setColor(Color.BLUE);
            g2D.draw(rays.get(raysCastIndexStart).getLine2D());
            g2D.setColor(Color.GREEN);
            g2D.draw(rays.get(raysCastIndexEnd).getLine2D());
            g2D.setColor(Color.RED);
            g2D.fillOval(p2.getX(), p2.getY(), 5, 5);

            // display debug stats in planeFrame
            g2D.setFont(new Font("Courier New", Font.PLAIN, 12));
            g2D.setColor(Color.GREEN);
            g2D.drawString(String.format("FPS: %s", fps), 20, 20);
            g2D.drawString(String.format("X: %s", mousePoint.getX()), 20, 30);
            g2D.drawString(String.format("Y: %s", mousePoint.getY()), 20, 40);

    }

    private void drawCaster() {
        g2D = buffer.createGraphics();

        // Padding for lines depending on FOV (360 = 0x, 180 = 2x, 90 = 4x ... etc)
        int padding = 360/FOV;
        g2D.setColor(new Color(82, 194, 255));
        g2D.fillRect(0, 0, WIDTH, HEIGHT/2);

        g2D.setColor(new Color(120, 120, 120));
        g2D.fillRect(0, HEIGHT/2, WIDTH, HEIGHT);

        // Recalculate drawn rays depending on FOV and rotation angle
        // If end is out bounds

        ArrayList<Ray> castRays = new ArrayList<>();

        if (raysCastRotationAngle+FOV<360) {
            raysCastIndexEnd = (int) (((FOV/360.0) * RAY_COUNT) + ((raysCastRotationAngle/360.0) * RAY_COUNT));
            raysCastIndexStart = (int) ((raysCastRotationAngle/360.0) * RAY_COUNT);
            centerConeIndex = (int) ((((FOV/2)/360.0))*RAY_COUNT + ((raysCastRotationAngle/360.0)*RAY_COUNT));
            strafeConeIndex = (int) (((90/360.0)*RAY_COUNT) + ((((FOV/2) + raysCastRotationAngle)/360.0))*RAY_COUNT);
            for (int i = raysCastIndexStart; i < raysCastIndexEnd; i++) {
                castRays.add(rays.get(i));
            }
        } else {
            raysCastIndexStart = (int) ((raysCastRotationAngle/360.0) * RAY_COUNT);
            raysCastIndexEnd = (int) (((FOV/360.0))*RAY_COUNT + ((raysCastRotationAngle-360)/360.0)*RAY_COUNT);
            centerConeIndex = (int) ((((FOV/2)/360.0))*RAY_COUNT + (((raysCastRotationAngle - 360)/360.0)*RAY_COUNT));
            strafeConeIndex = (int) (((90/360.0)*RAY_COUNT) + ((((FOV/2) + raysCastRotationAngle - 360)/360.0))*RAY_COUNT);
            for (int i = raysCastIndexStart; i < RAY_COUNT; i++) castRays.add(rays.get(i));
            for (int i = 0; i < raysCastIndexEnd; i++) castRays.add(rays.get(i));

        }

//        System.out.println(raysCastIndexStart);
//        System.out.println(raysCastIndexEnd);

        for (int i = 0; i < castRays.size(); i++) {
            Wall hw1 = new Wall();
            Wall hw2 = new Wall();

            g2D.setStroke(new BasicStroke(padding));

            int l = castRays.get(i).getLength();

            float newLength = (((l/GR)*HEIGHT)-HEIGHT)/2;

            // Top to mid
            hw1.setP1(new elements.Point(i*padding, HEIGHT/2));
            hw1.setP2(new elements.Point(i*padding, HEIGHT/2.0 - newLength));

            // Mid to Bottom
            hw2.setP1(new elements.Point(i*padding, HEIGHT/2));
            hw2.setP2(new elements.Point(i*padding, HEIGHT/2.0 + newLength));

            g2D.setColor(castRays.get(i).getColor());
            g2D.draw(hw1.getLine2D());
            g2D.draw(hw2.getLine2D());

        }

    }

    private static void drawBeam(Graphics2D g2D) {
        int[] xs = new int[3];
        int[] ys = new int[3];
        Ray r1, r2;

        for (int i = 0; i < RAY_COUNT; i++) {
            if (i == rays.size()-1) r2 = rays.get(0);
            else r2 = rays.get(i+1);

            r1 = rays.get(i);

            xs[0] = r1.getP1().getX();
            ys[0] = r1.getP1().getY();

            if (r1.getColPoint()!=null) {
                xs[1] = r1.getColPoint().getX();
                ys[1] = r1.getColPoint().getY();
            } else {
                xs[1] = r1.getP2().getX();
                ys[1] = r1.getP2().getY();
            }

            // If the line has a collision
            if (r2.getColPoint()!=null) {
                // provide the collision point, not casted
                xs[2] = r2.getColPoint().getX();
                ys[2] = r2.getColPoint().getY();
            } else {
                xs[2] = r2.getP2().getX();
                ys[2] = r2.getP2().getY();
            }

            g2D.setPaint(new GradientPaint(r1.getP1().getX(), r1.getP1().getY(), Color.WHITE, r1.getP2().getX(), r1.getP2().getY(), Color.BLACK));
            g2D.fillPolygon(new Polygon(xs, ys, 3));
        }
    }

    public static void main(String[] args) {
	    Main rayCaster = new Main();
    }
}
