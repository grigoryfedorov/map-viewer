package ru.grigoryfedorov.mapview.cache.memory;


import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import ru.grigoryfedorov.mapview.Tile;
import ru.grigoryfedorov.mapview.pool.BitmapPoolConsumer;

public interface MemoryCache {

    void put(Tile tile, Bitmap bitmap);

    @Nullable
    Bitmap get(Tile tile);

    void resize(int size);

    void setBitmapPoolConsumer(@Nullable BitmapPoolConsumer bitmapPoolConsumer);
}
