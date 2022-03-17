package elements;

import java.awt.geom.Line2D;

public class Ray extends Line {

    private Point colPoint;

    public Ray() {
        colPoint = null;
    }

    public Ray(Point p1, Point p2) {
        super(p1, p2);
        colPoint = null;
    }

    public void setColPoint(Point colPoint) {
        this.colPoint = colPoint;
    }

    public Point getColPoint() {
        return colPoint;
    }

    // Returns the line of collision
    public Line2D getColLine2D() {
        return new Line2D.Float(p1.getX(), p1.getY(), colPoint.getX(), colPoint.getY());
    }
}
