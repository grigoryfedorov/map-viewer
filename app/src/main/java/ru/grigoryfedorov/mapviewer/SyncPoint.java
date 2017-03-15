package ru.grigoryfedorov.mapviewer;


import android.graphics.Point;

public class SyncPoint {

    private Point point;

    private final Object lock = new Object();

    public SyncPoint(Point point) {
        this.point = point;
    }

    public void offset(int dx, int dy) {
        synchronized (lock) {
            point.offset(dx, dy);
        }
    }

    public void set(int x, int y) {
        synchronized (lock) {
            point.set(x, y);
        }
    }

    public Point get() {
        synchronized (lock) {
            return new Point(point);
        }
    }
}
