package ru.grigoryfedorov.mapviewer.cache.memory;


import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import ru.grigoryfedorov.mapviewer.Tile;
import ru.grigoryfedorov.mapviewer.pool.BitmapPoolConsumer;

public interface MemoryCache {

    void put(Tile tile, Bitmap bitmap);

    @Nullable
    Bitmap get(Tile tile);

    void resize(int size);

    void setBitmapPoolConsumer(@Nullable BitmapPoolConsumer bitmapPoolConsumer);
}
