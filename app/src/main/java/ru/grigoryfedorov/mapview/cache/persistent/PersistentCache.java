package ru.grigoryfedorov.mapview.cache.persistent;


import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import ru.grigoryfedorov.mapview.Tile;
import ru.grigoryfedorov.mapview.pool.BitmapPoolProvider;

public interface PersistentCache {
    void put(Tile tile, Bitmap bitmap);

    @Nullable
    Bitmap get(Tile tile);

    void setBitmapPoolProvider(@Nullable BitmapPoolProvider bitmapPoolProvider);
}
