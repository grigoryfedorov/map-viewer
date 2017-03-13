package ru.grigoryfedorov.mapviewer;


import android.graphics.Bitmap;
import android.support.annotation.Nullable;

public interface PersistentCache {
    void put(Tile tile, Bitmap bitmap);

    @Nullable
    Bitmap get(Tile tile);

    void setBitmapPoolProvider(@Nullable BitmapPoolProvider bitmapPoolProvider);
}
