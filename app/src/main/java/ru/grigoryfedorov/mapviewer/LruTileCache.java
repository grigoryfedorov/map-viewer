package ru.grigoryfedorov.mapviewer;


import android.graphics.Bitmap;
import android.util.LruCache;

class LruTileCache implements TileCache {

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
        tiles.resize(size);
    }
}
