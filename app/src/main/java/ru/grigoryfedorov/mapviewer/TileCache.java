package ru.grigoryfedorov.mapviewer;


import android.graphics.Bitmap;
import android.support.annotation.Nullable;

interface TileCache {

    void put(Tile tile, Bitmap bitmap);

    @Nullable
    Bitmap get(Tile tile);

    void resize(int size);
}
