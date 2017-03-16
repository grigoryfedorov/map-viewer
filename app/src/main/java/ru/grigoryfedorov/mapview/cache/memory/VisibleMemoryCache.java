package ru.grigoryfedorov.mapview.cache.memory;


import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.annotation.Nullable;

import java.util.concurrent.ConcurrentHashMap;

import ru.grigoryfedorov.mapview.Tile;
import ru.grigoryfedorov.mapview.TileProvider;
import ru.grigoryfedorov.mapview.pool.BitmapPoolConsumer;

public class VisibleMemoryCache implements MemoryCache {


    private final TileProvider tileProvider;
    private BitmapPoolConsumer bitmapPoolConsumer;

    private ConcurrentHashMap<Tile, Bitmap> map;

    public VisibleMemoryCache(TileProvider tileProvider) {
        map = new ConcurrentHashMap<>();
        this.tileProvider = tileProvider;
    }

    @Override
    public void put(Tile tile, Bitmap bitmap) {
        map.put(tile, bitmap);

        Point current = tileProvider.getCurrentCoordinates();

        for (Tile cachedTile : map.keySet()) {
            if (!tileProvider.needDraw(cachedTile, current)) {
                bitmapPoolConsumer.add(map.remove(cachedTile));
            }
        }
    }

    @Nullable
    @Override
    public Bitmap get(Tile tile) {
        return map.get(tile);
    }

    @Override
    public void resize(int size) {

    }

    @Override
    public void setBitmapPoolConsumer(@Nullable BitmapPoolConsumer bitmapPoolConsumer) {
        this.bitmapPoolConsumer = bitmapPoolConsumer;
    }
}
