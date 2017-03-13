package ru.grigoryfedorov.mapviewer;


import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.LruCache;

class AndroidLruMemoryCache implements MemoryCache {

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
