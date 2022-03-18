import elements.Ray;
import elements.Wall;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;

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

    public static boolean keyPressed;

    // Endpoints for walls created by mouse click
    public static Point2D wp1;
    public static Point2D wp2;

    public final static int HEIGHT = 500;
    public final static int WIDTH = 500;

    public final static int RAY_COUNT = 500;

    // Length of each ray
    public final static float GR = (int) Math.sqrt(Math.pow(HEIGHT, 2) + Math.pow(WIDTH, 2));

    // Field of View
    public final static int FOV = 90;
    public int raysCastIndexEnd;
    public int raysCastIndexStart;

    // Angle relative to x=0 which raysCastIndex starts
    // Used when rotating the camera
    public float raysCastRotationAngle;

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

        casterCanvas.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                keyPressed=true;
                switch(e.getKeyChar()) {
                    case 'w':
//                        Ray r = rays.get(raysCastIndexStart + (((FOV/2)/360)*RAY_COUNT));
                        double d = (360.0/Main.RAY_COUNT)*(Math.PI/180);
                        int x = (int)(mousePoint.getX() + (20*Math.cos(((FOV/2.0)*(Math.PI/180))*raysCastIndexStart)));
                        int y = (int)(mousePoint.getY() + (20*Math.sin(((FOV/2.0)*(Math.PI/180))*raysCastIndexStart)));
                        System.out.printf("x:%d\ty:%d\n", x, y);
                        mousePoint.setLocation(x, y);
                        break;

                    case 's':
                        mousePoint.setLocation(mousePoint.getX()-20, mousePoint.getY());
                        System.out.printf("x:%f\ty:%f\n", mousePoint.getX()-20, mousePoint.getY());
                        break;
                    case 'a':
                        mousePoint.setLocation(mousePoint.getX(), mousePoint.getY()+20);
                        break;

                    case 'd':
                        mousePoint.setLocation(mousePoint.getX(), mousePoint.getY()-20);
                        break;

                    case 'q':
                        if (raysCastRotationAngle<360) raysCastRotationAngle = raysCastRotationAngle + 10;
                        if (raysCastRotationAngle>359) raysCastRotationAngle = 0;
                        break;

                    case 'e':
                        if (raysCastRotationAngle<1) raysCastRotationAngle = 360;
                        if (raysCastRotationAngle>0) raysCastRotationAngle = raysCastRotationAngle - 10;
                        break;

                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

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

//    private void rotateRays(int n) {
//        while (n>0) {
//            for (int i = 0; i < RAY_COUNT; i++) {
//
//                if (i != RAY_COUNT-1) swapRay(i, i + 1);
//                else swapRay(0, i);
//            }
//            n--;
//        }
//    }
//
//    private void swapRay(int i, int j) {
//        Ray temp = rays.get(i);
//        rays.set(i, rays.get(j));
//        rays.set(j, temp);
//    }

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

            //                drawBeam(g2D);

            // Draw Walls
            for (Wall wall : walls) {
                g2D.setColor(wall.getColor());
                g2D.draw(wall.getLine2D());
            }

            // Draw example

            int x = (int)(mousePoint.getX() + (20*Math.cos(((FOV/2.0)*(Math.PI/180))*raysCastIndexStart)));
            int y = (int)(mousePoint.getY() + (20*Math.sin(((FOV/2.0)*(Math.PI/180))*raysCastIndexStart)));

            g2D.setColor(Color.BLUE);
            g2D.draw(rays.get(raysCastIndexStart).getLine2D());
            g2D.setColor(Color.GREEN);
            g2D.draw(rays.get(raysCastIndexEnd).getLine2D());
            g2D.setColor(Color.RED);
            g2D.fillOval(x, y, 10, 10);


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



        if (raysCastRotationAngle+FOV<360) {
            raysCastIndexEnd = (int) (((FOV/360.0) * RAY_COUNT) + ((raysCastRotationAngle/360.0) * RAY_COUNT));
            raysCastIndexStart = (int) ((raysCastRotationAngle/360.0) * RAY_COUNT);
        } else {
            raysCastIndexStart = (int) ((raysCastRotationAngle/360.0) * RAY_COUNT);
            raysCastIndexEnd = (int) (((FOV/360.0)-360)*RAY_COUNT + ((raysCastRotationAngle/360.0)*RAY_COUNT));
        }
//        System.out.printf("Angle:%f\tFOV:%d\ttotal:%f\n", raysCastRotationAngle, FOV, raysCastRotationAngle+FOV);


        // TODO: NEED WRAP AROUND FOR ANGLE AND INDEX

        ArrayList<Ray> castRays = new ArrayList<>();
        for (int i = raysCastIndexStart; i < raysCastIndexEnd; i++) {
            castRays.add(rays.get(i));
        }

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
//            if(i == 0) {
//                System.out.print(rays.get(i));
//                System.out.println(rays.get(i).getLength());
//            }

            g2D.setColor(castRays.get(i).getColor());

            g2D.draw(hw1.getLine2D());
            g2D.draw(hw2.getLine2D());
//            spacer++;

        }

//        for (int i = raysCastIndexStart; i < raysCastIndexEnd; i++) {
//            Wall hw1 = new Wall();
//            Wall hw2 = new Wall();
//
//            g2D.setStroke(new BasicStroke(padding));
//
//            int l = rays.get(i).getLength();
//
//            float newLength = (((l/GR)*HEIGHT)-HEIGHT)/2;
//
//            // Top to mid
//            hw1.setP1(new elements.Point(i*padding, HEIGHT/2));
//            hw1.setP2(new elements.Point(i*padding, HEIGHT/2.0 - newLength));
//
//            // Mid to Bottom
//            hw2.setP1(new elements.Point(i*padding, HEIGHT/2));
//            hw2.setP2(new elements.Point(i*padding, HEIGHT/2.0 + newLength));
////            if(i == 0) {
////                System.out.print(rays.get(i));
////                System.out.println(rays.get(i).getLength());
////            }
//
//            g2D.setColor(rays.get(i).getColor());
//
//            g2D.draw(hw1.getLine2D());
//            g2D.draw(hw2.getLine2D());
////            spacer++;
//
//        }
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

//            Point2D p = new Line(xs[1], ys[1], xs[2], ys[2]).midpoint();
            g2D.setPaint(new GradientPaint(r1.getP1().getX(), r1.getP1().getY(), Color.WHITE, r1.getP2().getX(), r1.getP2().getY(), Color.BLACK));
            g2D.fillPolygon(new Polygon(xs, ys, 3));
        }
    }

    public static void main(String[] args) {
	    Main rayCaster = new Main();
    }
}
