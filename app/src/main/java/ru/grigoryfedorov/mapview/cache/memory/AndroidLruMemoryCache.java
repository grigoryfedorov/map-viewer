package ru.grigoryfedorov.mapview.cache.memory;


import android.graphics.Bitmap;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.LruCache;

import ru.grigoryfedorov.mapview.Tile;
import ru.grigoryfedorov.mapview.pool.BitmapPoolConsumer;

public class AndroidLruMemoryCache implements MemoryCache {

    private static final String TAG = AndroidLruMemoryCache.class.getSimpleName();
    private LruCache<Tile, Bitmap> tiles;

    AndroidLruMemoryCache(int size) {
        tiles = new LruCache<>(size);
    }

    @Override
    public void put(Tile tile, Bitmap bitmap) {
        tiles.put(tile, bitmap);
    }

    @Override
    public void put(Tile tile, Bitmap bitmap, @Nullable Rect tileVisibleRect) {
        put(tile, bitmap);
    }

    @Override
    public Bitmap get(Tile tile) {
        return tiles.get(tile);
    }

    @Override
    public void resize(int size) {
        Log.d(TAG, "resize " + size);
        tiles.resize(size);
    }

    @Override
    public void setBitmapPoolConsumer(@Nullable BitmapPoolConsumer bitmapPoolConsumer) {

    }
}
