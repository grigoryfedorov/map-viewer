package ru.grigoryfedorov.mapview;


import android.content.Context;
import android.graphics.Bitmap;

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

public class TileProviderImpl implements TileProvider {

    private static final String TAG = TileProviderImpl.class.getSimpleName();

    private final MapType mapType;
    private final TileVisibilityChecker tileVisibilityChecker;

    private ExecutorService executorService;
    private final Set<Tile> inProgress;
    private final Set<Tile> planned;

    private MemoryCache memoryCache;
    private PersistentCache persistentCache;
    private PlaceholderProvider placeholderProvider;

    private BitmapLoader loader;

    private InvalidateListener invalidateListener;

    TileProviderImpl(Context context, MapType mapType, TileVisibilityChecker tileVisibilityChecker) {
        this.mapType = mapType;
        this.tileVisibilityChecker = tileVisibilityChecker;

        BitmapPool bitmapPool = new BitmapPool();

        memoryCache = new VisibleMemoryCache();
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

    @Override
    public void setInvalidateListener(InvalidateListener invalidateListener) {
        this.invalidateListener = invalidateListener;
    }

    @Override
    public void setVisibleTileCount(int count) {
        memoryCache.resize(count);
        executorService = Executors.newFixedThreadPool(count);
    }

    @Override
    public Bitmap getTile(final Tile tile) {
        Bitmap bitmap = memoryCache.get(tile);

        if (bitmap != null) {
            return bitmap;
        }

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

                if (!tileVisibilityChecker.needDraw(tile)) {
                    planned.remove(tile);
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
                    memoryCache.put(tile, bitmap, tileVisibilityChecker.getVisibleTileRectangle());
                    if (tileVisibilityChecker.needDraw(tile)) {
                        invalidateListener.onInvalidateNeeded();
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

}
