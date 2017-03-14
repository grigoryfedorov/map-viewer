package ru.grigoryfedorov.mapviewer;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.grigoryfedorov.mapviewer.bitmaploader.BitmapLoader;
import ru.grigoryfedorov.mapviewer.bitmaploader.UrlConnectionLoader;
import ru.grigoryfedorov.mapviewer.cache.memory.MemoryCache;
import ru.grigoryfedorov.mapviewer.cache.memory.VisibleMemoryCache;
import ru.grigoryfedorov.mapviewer.cache.persistent.FileCache;
import ru.grigoryfedorov.mapviewer.cache.persistent.PersistentCache;
import ru.grigoryfedorov.mapviewer.pool.BitmapPool;

public class TileProvider {

    private static final int TILE_SIZE = 256;
    private static final int TILE_WIDTH = TILE_SIZE;
    private static final int TILE_HEIGHT = TILE_SIZE;
    private static final String TAG = TileProvider.class.getSimpleName();

    private final Callback callback;
    private final MapController mapController;
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

    TileProvider(Context context, Callback callback, MapController mapController) {
        this.callback = callback;
        this.mapController = mapController;

        BitmapPool bitmapPool = new BitmapPool();

        memoryCache = new VisibleMemoryCache(mapController, this);
        memoryCache.setBitmapPoolConsumer(bitmapPool);

        persistentCache = new FileCache(context);
        persistentCache.setBitmapPoolProvider(bitmapPool);
        loader = new UrlConnectionLoader();
        loader.setBitmapPoolProvider(bitmapPool);

        placeholderProvider = new PlaceholderProvider(TILE_WIDTH, TILE_HEIGHT);

        executorService = Executors.newFixedThreadPool(1);

        inProgress = Collections.newSetFromMap(new ConcurrentHashMap<Tile, Boolean>());
        planned = Collections.newSetFromMap(new ConcurrentHashMap<Tile, Boolean>());
    }

    static int getTileWidth() {
        return TILE_WIDTH;
    }

    static int getTileHeight() {
        return TILE_HEIGHT;
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

                if (!needDraw(tile, mapController.getCurrent())) {
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
                    callback.onTileUpdated(tile);
                }

                planned.remove(tile);
                inProgress.remove(tile);
            }
        });
    }

    private Bitmap requestTile(final Tile tile) {
        return loader.loadBitmap("http://b.tile.opencyclemap.org/cycle/" + tile.getZoom() + "/" + tile.getX() + "/" + tile.getY() + ".png");
    }

    void onSizeChanged(int tilesCountX, int tilesCountY) {
        this.tilesCountX = tilesCountX;
        this.tilesCountY = tilesCountY;

        memoryCache.resize(tilesCountX * tilesCountY);
        executorService = Executors.newFixedThreadPool(tilesCountX * tilesCountY);
    }

    public boolean needDraw(Tile tile, Point current) {
        int startTileX = current.x / getTileWidth();
        int startTileY = current.y / getTileHeight();

        return tile.getX() >= startTileX
                && tile.getX() < startTileX + tilesCountX
                && tile.getY() >= startTileY
                && tile.getY() < startTileY + tilesCountY;
    }
}
