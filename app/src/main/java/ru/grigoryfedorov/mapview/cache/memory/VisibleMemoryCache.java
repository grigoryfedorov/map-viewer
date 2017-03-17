package ru.grigoryfedorov.mapview.cache.memory;


import android.graphics.Bitmap;
import android.graphics.Rect;
import android.support.annotation.Nullable;

import java.util.concurrent.ConcurrentHashMap;

import ru.grigoryfedorov.mapview.Tile;
import ru.grigoryfedorov.mapview.pool.BitmapPoolConsumer;

public class VisibleMemoryCache implements MemoryCache {


    private BitmapPoolConsumer bitmapPoolConsumer;

    private ConcurrentHashMap<Tile, Bitmap> map;

    public VisibleMemoryCache() {
        map = new ConcurrentHashMap<>();
    }

    @Override
    public void put(Tile tile, Bitmap bitmap) {
        put(tile, bitmap, null);
    }

    @Override
    public void put(Tile tile, Bitmap bitmap, @Nullable Rect tileVisibleRect) {
        map.put(tile, bitmap);

        if (tileVisibleRect != null) {
            reuseInvisibleBitmaps(tileVisibleRect);
        }
    }

    private void reuseInvisibleBitmaps(Rect visibleTileRectangle) {
        for (Tile cachedTile : map.keySet()) {
            if (!visibleTileRectangle.contains(cachedTile.getX(), cachedTile.getY())) {
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
