package ru.grigoryfedorov.mapview.cache.memory;


import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.annotation.Nullable;

import java.util.concurrent.ConcurrentHashMap;

import ru.grigoryfedorov.mapview.Tile;
import ru.grigoryfedorov.mapview.TileVisibilityChecker;
import ru.grigoryfedorov.mapview.pool.BitmapPoolConsumer;

public class VisibleMemoryCache implements MemoryCache {


    private final TileVisibilityChecker tileVisibilityChecker;
    private BitmapPoolConsumer bitmapPoolConsumer;

    private ConcurrentHashMap<Tile, Bitmap> map;

    public VisibleMemoryCache(TileVisibilityChecker tileVisibilityChecker) {
        map = new ConcurrentHashMap<>();
        this.tileVisibilityChecker = tileVisibilityChecker;
    }

    @Override
    public void put(Tile tile, Bitmap bitmap) {
        map.put(tile, bitmap);

        reuseInvisibleBitmaps();
    }

    private void reuseInvisibleBitmaps() {
        Point point = tileVisibilityChecker.get();

        for (Tile cachedTile : map.keySet()) {
            if (!tileVisibilityChecker.needDraw(cachedTile, point)) {
                Bitmap bitmapToReuse = map.remove(cachedTile);
                bitmapPoolConsumer.add(bitmapToReuse);
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
