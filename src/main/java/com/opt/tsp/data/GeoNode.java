package com.opt.tsp.data;

public class GeoNode extends IntNode {
    private double x;
    private double y;

    public GeoNode(int value, double x, double y) {
        super(value);
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public String toString() {
        return "GeoNode{" +
                "x=" + x +
                ", y=" + y +
                ", value=" + getValue() +
                '}';
    }
}
