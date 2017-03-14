package ru.grigoryfedorov.mapviewer;


import android.graphics.Point;
import android.graphics.Rect;

public class MapController {

    private Point current;
    private final Rect borders;

    private final Object lock = new Object();

    public MapController(Point current, Rect borders) {
        this.current = current;
        this.borders = borders;
    }

    public void offset(int dx, int dy) {
        synchronized (lock) {
            current.offset(dx, dy);
            checkBorders();
        }
    }

    public void set(int x, int y) {
        synchronized (lock) {
            current.set(x, y);
        }
    }

    public Point getCurrent() {
        synchronized (lock) {
            return new Point(current);
        }
    }

    private void checkBorders() {
        if (current.x < borders.left) {
            current.x = borders.left;
        }

        if (current.y < borders.top) {
            current.y = borders.top;
        }

        if (current.x > borders.right) {
            current.x = borders.right;
        }

        if (current.y > borders.bottom) {
            current.y = borders.bottom;
        }
    }
}
