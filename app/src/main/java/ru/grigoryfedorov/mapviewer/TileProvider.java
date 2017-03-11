package ru.grigoryfedorov.mapviewer;


import android.content.Context;
import android.graphics.Bitmap;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class TileProvider {

    private static final int TILE_SIZE = 256;
    private static final int TILE_WIDTH = TILE_SIZE;
    private static final int TILE_HEIGHT = TILE_SIZE;

    private final Callback callback;
    private final ExecutorService executorService;
    private final Set<Tile> plannedRequests;

    private TileCache tileCache;
    private PersistentCache persistentCache;
    private PlaceholderProvider placeholderProvider;

    private BitmapLoader loader;

    interface Callback {
        void onTileUpdated(Tile tile);
    }

    TileProvider(Context context, Callback callback) {
        this.loader = new UrlConnectionLoader();
        this.callback = callback;
        tileCache = new LruTileCache(128);
        persistentCache = new FileCache(context);
        placeholderProvider = new PlaceholderProvider(TILE_WIDTH, TILE_HEIGHT);

        executorService = Executors.newFixedThreadPool(70);

        plannedRequests = Collections.newSetFromMap(new ConcurrentHashMap<Tile, Boolean>());
    }

    int getTileWidth() {
        return TILE_WIDTH;
    }

    int getTileHeight() {
        return TILE_HEIGHT;
    }

    Bitmap getTile(final Tile tile) {
        Bitmap bitmap = tileCache.get(tile);

        if (bitmap != null) {
            return bitmap;
        }

        request(tile);

        return placeholderProvider.getPlaceholderBitmap();
    }

    private void request(final Tile tile) {
        if (plannedRequests.contains(tile)) {
            return;
        } else {
            plannedRequests.add(tile);
        }

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = persistentCache.get(tile);

                if (bitmap == null) {
                    bitmap = requestTile(tile);
                    if (bitmap != null) {
                        persistentCache.put(tile, bitmap);
                    }
                }

                if (bitmap != null) {
                    tileCache.put(tile, bitmap);
                    callback.onTileUpdated(tile);
                    plannedRequests.remove(tile);
                }
            }
        });
    }

    private Bitmap requestTile(final Tile tile) {
        return loader.loadBitmap("http://b.tile.opencyclemap.org/cycle/" + tile.getZoom() + "/" + tile.getX() + "/" + tile.getY() + ".png");
    }

    void onSizeChanged(int tilesCountX, int tilesCountY) {
        tileCache.resize(tilesCountX * tilesCountY);
    }
}
