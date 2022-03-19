package elements;

import java.awt.geom.Point2D;

public class Point {
    protected int x;
    protected int y;

    public Point() {
    }

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point(float x, float y) {
        this.x = (int) x;
        this.y = (int) y;
    }

    public Point(double x, double y) {
        this.x = (int) x;
        this.y = (int) y;
    }

    public Point(java.awt.Point p) {
        this.x = (int) p.getX();
        this.y = (int) p.getY();
    }

    public Point(java.awt.geom.Point2D p) {
        this.x = (int) p.getX();
        this.y = (int) p.getY();
    }

    // Used when setting point from mouse location
    public void setPoint(java.awt.Point p) {
        this.x = (int) p.getX();
        this.y = (int) p.getY();
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    // These are just so I don't have to cast every time
    public void setX(double x) {
        this.x = (int) x;
    }

    public void setY(double y) {
        this.y = (int) y;
    }
}
