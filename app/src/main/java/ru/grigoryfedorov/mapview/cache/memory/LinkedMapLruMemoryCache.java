package ru.grigoryfedorov.mapview.cache.memory;


import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

import ru.grigoryfedorov.mapview.Tile;
import ru.grigoryfedorov.mapview.pool.BitmapPoolConsumer;

public class LinkedMapLruMemoryCache implements MemoryCache {

    private final LinkedHashMap<Tile, Bitmap> map;
    private final Object lock = new Object();
    private int maxSize;

    @Nullable
    BitmapPoolConsumer bitmapPoolConsumer;

    public LinkedMapLruMemoryCache(int maxSize) {
        this.maxSize = maxSize;

        this.map = new LinkedHashMap<Tile, Bitmap>() {
            protected boolean removeEldestEntry(Map.Entry<Tile,Bitmap> eldest) {
                if (size() > getMaxSize()) {
                    if (bitmapPoolConsumer != null) {
                        bitmapPoolConsumer.add(eldest.getValue());
                    }

                    return true;
                }

                return false;
            }
        };
    }

    @Override
    public void setBitmapPoolConsumer(@Nullable BitmapPoolConsumer bitmapPoolConsumer) {
        this.bitmapPoolConsumer = bitmapPoolConsumer;
    }

    @Override
    public void put(Tile tile, Bitmap bitmap) {
        synchronized (lock) {
            map.put(tile, bitmap);
        }
    }

    @Nullable
    @Override
    public Bitmap get(Tile tile) {
        synchronized (lock) {
            return map.get(tile);
        }
    }

    @Override
    public void resize(int size) {
        this.maxSize = size;
    }

    private int getMaxSize() {
        return maxSize;
    }
}
