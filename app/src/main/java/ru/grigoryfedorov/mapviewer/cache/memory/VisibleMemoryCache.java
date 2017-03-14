package ru.grigoryfedorov.mapviewer.cache.memory;


import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.annotation.Nullable;

import java.util.concurrent.ConcurrentHashMap;

import ru.grigoryfedorov.mapviewer.MapController;
import ru.grigoryfedorov.mapviewer.Tile;
import ru.grigoryfedorov.mapviewer.TileProvider;
import ru.grigoryfedorov.mapviewer.pool.BitmapPoolConsumer;

public class VisibleMemoryCache implements MemoryCache {


    private final MapController mapController;
    private final TileProvider tileProvider;
    private BitmapPoolConsumer bitmapPoolConsumer;

    private ConcurrentHashMap<Tile, Bitmap> map;

    public VisibleMemoryCache(MapController mapController, TileProvider tileProvider) {
        map = new ConcurrentHashMap<>();
        this.mapController = mapController;
        this.tileProvider = tileProvider;
    }

    @Override
    public void put(Tile tile, Bitmap bitmap) {
        map.put(tile, bitmap);

        Point current = mapController.getCurrent();

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
