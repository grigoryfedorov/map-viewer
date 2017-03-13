package ru.grigoryfedorov.mapviewer.cache.persistent;


import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import ru.grigoryfedorov.mapviewer.Tile;
import ru.grigoryfedorov.mapviewer.pool.BitmapPoolProvider;

public interface PersistentCache {
    void put(Tile tile, Bitmap bitmap);

    @Nullable
    Bitmap get(Tile tile);

    void setBitmapPoolProvider(@Nullable BitmapPoolProvider bitmapPoolProvider);
}
