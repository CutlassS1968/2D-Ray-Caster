package elements;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Random;

public class Wall extends Line {

    private Color color;

    public Wall() {
        color = Color.BLACK;
    }

    public Wall(Point p1, Point p2) {
        super(p1, p2);
        Random rand = new Random();
        float r = rand.nextFloat();
        float g = rand.nextFloat();
        float b = rand.nextFloat();
        color = new Color(r, g, b);
    }

    public Wall(Point2D p1, Point2D p2) {
        super(p1, p2);
        Random rand = new Random();
        float r = rand.nextFloat();
        float g = rand.nextFloat();
        float b = rand.nextFloat();
        color = new Color(r, g, b);
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
