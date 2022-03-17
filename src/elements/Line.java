package elements;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class Line {
    protected Point p1;
    protected Point p2;

    public Line(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    public Line(Point2D p1, Point2D p2) {
        this.p1 = new Point(p1.getX(), p1.getY());
        this.p2 = new Point(p2.getX(), p2.getY());
    }

    public Line() {
        this.p1 = null;
        this.p2 = null;
    }

    public Line2D getLine2D() {
        return new Line2D.Float(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }

    public void setP1(Point p1) {
        this.p1 = p1;
    }

    public Point getP1() {
        return p1;
    }

    public void setP2(Point p2) {
        this.p2 = p2;
    }

    public Point getP2() {
        return p2;
    }

}
