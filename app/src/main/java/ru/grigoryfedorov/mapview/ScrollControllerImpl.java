package ru.grigoryfedorov.mapview;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.OverScroller;

/**
 * {@link ScrollController} implementation using {@link GestureDetector} and {@link OverScroller}]
 *
 * Current coordinates getter and setter are thread safe.
 */
public class ScrollControllerImpl implements ScrollController {
    private final OverScroller scroller;
    private final GestureDetector gestureDetector;
    private SyncPoint currentCoordinates;
    private Rect borders;
    private InvalidateListener invalidateListener;

    private int overFlingX;
    private int overFlingY;

    public ScrollControllerImpl(Context context) {
        gestureDetector = new GestureDetector(context, new GestureListener());
        scroller = new OverScroller(context);

        currentCoordinates = new SyncPoint();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public void onDraw() {
        if (scroller.computeScrollOffset()) {
            currentCoordinates.set(scroller.getCurrX(), scroller.getCurrY());
            invalidateListener.onInvalidateNeeded();
        }
    }

    @Override
    public void setGlobalBorders(Rect borders) {
        this.borders = borders;
    }

    @Override
    public void setCurrentCoordinates(int x, int y) {
        currentCoordinates.set(x, y);
    }

    @Override
    public Point getCurrentCoordinates() {
        return currentCoordinates.get();
    }

    @Override
    public void setInvalidateListener(InvalidateListener invalidateListener) {
        this.invalidateListener = invalidateListener;
    }

    @Override
    public void setOverFling(int overX, int overY) {
        this.overFlingX = overX;
        this.overFlingY = overY;
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            scroller.forceFinished(true);
            invalidateListener.onInvalidateNeeded();

            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Point current = currentCoordinates.get();

            boolean isSpringBack = scroller.springBack(current.x + (int)distanceX, current.y + (int)distanceY, borders.left, borders.right, borders.top, borders.bottom);

            if (!isSpringBack) {
                currentCoordinates.offset((int)distanceX, (int)distanceY);
            }

            invalidateListener.onInvalidateNeeded();

            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Point current = currentCoordinates.get();
            scroller.fling(current.x, current.y, (int)-velocityX, (int)-velocityY,
                    borders.left, borders.right, borders.top, borders.bottom, overFlingX, overFlingY);

            invalidateListener.onInvalidateNeeded();
            return true;
        }
    }

}
