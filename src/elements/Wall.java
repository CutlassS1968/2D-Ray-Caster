package elements;

import java.awt.*;
import java.awt.geom.Point2D;

public class Wall extends Line {

    private Color color;

    public Wall() {
        color = Color.BLACK;
    }

    public Wall(Point p1, Point p2) {
        super(p1, p2);
        color = Color.BLACK;
    }

    public Wall(Point2D p1, Point2D p2) {
        super(p1, p2);
        color = Color.BLACK;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
