package elements;

import java.awt.*;
import java.awt.geom.Line2D;

public class Ray extends Line {

    private Point colPoint;

    // Wall ray is intersected with
    private Color color;

    public Ray() {
        colPoint = null;
        color = Color.WHITE;
    }

    public Ray(Point p1, Point p2) {
        super(p1, p2);
        colPoint = null;
        color = Color.WHITE;
    }

    public void setColPoint(Point colPoint) {
        this.colPoint = colPoint;
    }

    public Point getColPoint() {
        return colPoint;
    }

    @Override
    public Line2D getLine2D() {
        if (colPoint==null) {
            return new Line2D.Float(p1.getX(), p1.getY(), p2.getX(), p2.getY());
        }
        return new Line2D.Float(p1.getX(), p1.getY(), colPoint.getX(), colPoint.getY());
    }

    public int getLength() {
        return (int) Math.sqrt(Math.pow((p2.x - p1.x), 2) + Math.pow((p2.y - p1.y), 2));
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

//    // Returns the line of collision
//    public Line2D getColLine2D() {
//        return new Line2D.Float(p1.getX(), p1.getY(), colPoint.getX(), colPoint.getY());
//    }
}
