package ru.grigoryfedorov.mapviewer;


import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

class LinkedMapLruMemoryCache implements MemoryCache {

    private final LinkedHashMap<Tile, Bitmap> map;
    private final Object lock = new Object();
    private int maxSize;

    LinkedMapLruMemoryCache(int size) {
        this.maxSize = size;

        this.map = new LinkedHashMap<Tile, Bitmap>() {
            protected boolean removeEldestEntry(Map.Entry<Tile,Bitmap> eldest) {
                return size() > getMaxSize();
            }
        };
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
