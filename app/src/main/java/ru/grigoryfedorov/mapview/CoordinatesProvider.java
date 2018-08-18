package ru.grigoryfedorov.mapview;


import android.graphics.Point;

interface CoordinatesProvider {

    /**
     * Get current global coordinates scrolled after touch events
     *
     * @return current global coordinates
     */
    Point getCurrentCoordinates();
}
