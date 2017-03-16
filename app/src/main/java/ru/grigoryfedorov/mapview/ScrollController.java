package ru.grigoryfedorov.mapview;


import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

/**
 * Calculates global coordinates as reaction to touch event.
 *
 * Used to move screen view port in global coordinates.
 *
 */
interface ScrollController {

    /**
     * Set global borders in pixels.
     *
     * @param rect - rectangle larger than screen size where scroll will happen
     */
    void setGlobalBorders(Rect rect);

    /**
     * Change current coordinates, e.g. set initial
     *
     * @param x - global x coordinate in pixels
     * @param y - global y coordinate in pixels
     */
    void setCurrentCoordinates(int x, int y);

    /**
     * You must provide invalidate listener
     *
     * @param invalidateListener listener will be called when redraw needed
     */
    void setInvalidateListener(InvalidateListener invalidateListener);

    /**
     * Set optional overfling effect - coordinates will be moved over borders
     *
     * @param overX Overfling range. If > 0, horizontal overfling in either
     *            direction will be possible.
     * @param overY Overfling range. If > 0, vertical overfling in either
     *            direction will be possible.
     */
    void setOverFling(int overX, int overY);

    /**
     * Use it in your {@link View#onTouchEvent(MotionEvent)}
     *
     * @param event motion event passed to view
     * @return true if event was consumed
     */
    boolean onTouchEvent(MotionEvent event);

    /**
     * Call it in your {@link View#onDraw(android.graphics.Canvas)} to calculate animated scroll
     */
    void onDraw();

    /**
     * Get current global coordinates scrolled after touch events
     *
     * @return current global coordinates
     */
    Point getCurrentCoordinates();

}
