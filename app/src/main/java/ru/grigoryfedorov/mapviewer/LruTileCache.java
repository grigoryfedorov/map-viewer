package ru.grigoryfedorov.mapviewer;


import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

class LruTileCache implements TileCache {

    private static final String TAG = LruTileCache.class.getSimpleName();
    private LruCache<Tile, Bitmap> tiles;

    LruTileCache(int size) {
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
}
