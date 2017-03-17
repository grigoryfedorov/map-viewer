package ru.grigoryfedorov.mapview;


import android.graphics.Bitmap;

interface TileProvider {
    Bitmap getTile(final Tile tile);

    /**
     * You must provide invalidate listener
     *
     * @param invalidateListener listener will be called when redraw needed
     */
    void setInvalidateListener(InvalidateListener invalidateListener);

    void setVisibleTileCount(int count);
}
