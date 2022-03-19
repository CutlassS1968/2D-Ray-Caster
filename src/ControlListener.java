import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ControlListener implements KeyListener {

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        double d = (360.0 / Main.RAY_COUNT) * (Math.PI / 180);
        ;
        elements.Point p1, p2;
        int x, y;
        int mU = 25;

        switch (e.getKeyChar()) {
            case 'w':
                p1 = new elements.Point(Main.mousePoint);
                x = (int) (p1.getX() + (mU * Math.cos(d * Main.centerConeIndex)));
                y = (int) (p1.getY() + (mU * Math.sin(d * Main.centerConeIndex)));
                p2 = new elements.Point(x, y);
                Main.mousePoint.setLocation(p2.getX(), p2.getY());
                System.out.printf("x:%d\ty:%d\n", x, y);
                break;

            case 's':
                p1 = new elements.Point(Main.mousePoint);
                x = (int) (p1.getX() - (mU * Math.cos(d * Main.centerConeIndex)));
                y = (int) (p1.getY() - (mU * Math.sin(d * Main.centerConeIndex)));
                p2 = new elements.Point(x, y);
                Main.mousePoint.setLocation(p2.getX(), p2.getY());
                System.out.printf("x:%d\ty:%d\n", x, y);
                break;

            case 'a':
                p1 = new elements.Point(Main.mousePoint);
                x = (int) (p1.getX() - (mU * Math.cos(d * Main.strafeConeIndex)));
                y = (int) (p1.getY() - (mU * Math.sin(d * Main.strafeConeIndex)));
                p2 = new elements.Point(x, y);
                Main.mousePoint.setLocation(p2.getX(), p2.getY());
                System.out.printf("x:%d\ty:%d\n", x, y);
                break;

            case 'd':
                p1 = new elements.Point(Main.mousePoint);
                x = (int) (p1.getX() + (mU * Math.cos(d * Main.strafeConeIndex)));
                y = (int) (p1.getY() + (mU * Math.sin(d * Main.strafeConeIndex)));
                p2 = new elements.Point(x, y);
                Main.mousePoint.setLocation(p2.getX(), p2.getY());
                System.out.printf("x:%d\ty:%d\n", x, y);
                break;

            case 'q':
                if (Main.raysCastRotationAngle < 360) Main.raysCastRotationAngle = Main.raysCastRotationAngle + 30;
                if (Main.raysCastRotationAngle > 359) Main.raysCastRotationAngle = 0;
                break;

            case 'e':
                if (Main.raysCastRotationAngle < 1) Main.raysCastRotationAngle = 360;
                if (Main.raysCastRotationAngle > 0) Main.raysCastRotationAngle = Main.raysCastRotationAngle - 30;
                break;

        }
    }


    @Override
    public void keyReleased(KeyEvent e) {

    }
}
