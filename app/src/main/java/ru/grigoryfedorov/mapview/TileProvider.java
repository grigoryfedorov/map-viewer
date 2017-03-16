package ru.grigoryfedorov.mapview;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.grigoryfedorov.mapview.bitmaploader.BitmapLoader;
import ru.grigoryfedorov.mapview.bitmaploader.UrlConnectionLoader;
import ru.grigoryfedorov.mapview.cache.memory.MemoryCache;
import ru.grigoryfedorov.mapview.cache.memory.VisibleMemoryCache;
import ru.grigoryfedorov.mapview.cache.persistent.FileCache;
import ru.grigoryfedorov.mapview.cache.persistent.PersistentCache;
import ru.grigoryfedorov.mapview.pool.BitmapPool;

public class TileProvider {

    private static final String TAG = TileProvider.class.getSimpleName();

    private final Callback callback;
    private final ScrollController scrollController;
    private final MapType mapType;
    private ExecutorService executorService;
    private final Set<Tile> inProgress;
    private final Set<Tile> planned;

    private MemoryCache memoryCache;
    private PersistentCache persistentCache;
    private PlaceholderProvider placeholderProvider;

    private BitmapLoader loader;
    private int tilesCountX;
    private int tilesCountY;


    interface Callback {
        void onTileUpdated(Tile tile);
    }

    TileProvider(Context context, Callback callback, ScrollController currentCoordinates, MapType mapType) {
        this.callback = callback;
        this.scrollController = currentCoordinates;
        this.mapType = mapType;

        BitmapPool bitmapPool = new BitmapPool();

        memoryCache = new VisibleMemoryCache(this);
        memoryCache.setBitmapPoolConsumer(bitmapPool);

        persistentCache = new FileCache(context);
        persistentCache.setBitmapPoolProvider(bitmapPool);
        loader = new UrlConnectionLoader();
        loader.setBitmapPoolProvider(bitmapPool);

        placeholderProvider = new PlaceholderProvider(mapType.getTileWidth(), mapType.getTileHeight());

        executorService = Executors.newFixedThreadPool(1);

        inProgress = Collections.newSetFromMap(new ConcurrentHashMap<Tile, Boolean>());
        planned = Collections.newSetFromMap(new ConcurrentHashMap<Tile, Boolean>());
    }

    Bitmap getTile(final Tile tile) {
        Bitmap bitmap = memoryCache.get(tile);

        if (bitmap != null) {
            return bitmap;
        }

//        Log.i(TAG, "Cache miss!");

        request(tile);

        return placeholderProvider.getPlaceholderBitmap();
    }

    private void request(final Tile tile) {

        if (planned.contains(tile)) {
            return;
        }

        planned.add(tile);

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                if (memoryCache.get(tile) != null) {
                    planned.remove(tile);
                    inProgress.remove(tile);
                    return;
                }

                if (!needDraw(tile, scrollController.getCurrentCoordinates())) {
                    planned.remove(tile);
//                    Log.i(TAG, "Cancel tile request - not need to draw");
                    return;
                }

                if (inProgress.contains(tile)) {
                    return;
                }

                inProgress.add(tile);

                Bitmap bitmap = persistentCache.get(tile);

                if (bitmap == null) {
                    bitmap = requestTile(tile);
                    if (bitmap != null) {
                        persistentCache.put(tile, bitmap);
                    }
                }

                if (bitmap != null) {
                    memoryCache.put(tile, bitmap);
                    if (needDraw(tile, scrollController.getCurrentCoordinates())) {
                        callback.onTileUpdated(tile);
                    }
                }

                planned.remove(tile);
                inProgress.remove(tile);
            }
        });
    }

    private Bitmap requestTile(final Tile tile) {
        return loader.loadBitmap(mapType.getTileRequestUrl(tile));
    }

    void onSizeChanged(int tilesCountX, int tilesCountY) {
        this.tilesCountX = tilesCountX;
        this.tilesCountY = tilesCountY;

        memoryCache.resize(tilesCountX * tilesCountY);
        executorService = Executors.newFixedThreadPool(tilesCountX * tilesCountY);
    }

    public Point getCurrentCoordinates() {
        return scrollController.getCurrentCoordinates();
    }

    public boolean needDraw(Tile tile, Point current) {
        int startTileX = current.x / mapType.getTileWidth();
        int startTileY = current.y / mapType.getTileHeight();

        return tile.getX() >= startTileX
                && tile.getX() < startTileX + tilesCountX
                && tile.getY() >= startTileY
                && tile.getY() < startTileY + tilesCountY;
    }
}
