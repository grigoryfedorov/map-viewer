package ru.grigoryfedorov.mapviewer;


import android.content.Context;
import android.graphics.Bitmap;

class TileProvider {

    private static final int TILE_SIZE = 256;
    private static final int TILE_WIDTH = TILE_SIZE;
    private static final int TILE_HEIGHT = TILE_SIZE;

    private static final int ZOOM = 16;

    private final Callback callback;

    private TileCache tileCache;
    private PlaceholderProvider placeholderProvider;

    private BitmapLoader loader;

    interface Callback {
        void onTileUpdated(Tile tile);
    }

    TileProvider(Context context, Callback callback) {
//        this.loader = new GlideLoader(context, TILE_WIDTH, TILE_HEIGHT);
        this.loader = new UrlConnectionLoader();
        this.callback = callback;
        tileCache = new LruTileCache(128);
        placeholderProvider = new PlaceholderProvider(TILE_WIDTH, TILE_HEIGHT);
    }

    int getTileWidth() {
        return TILE_WIDTH;
    }

    int getTileHeight() {
        return TILE_HEIGHT;
    }

    Bitmap getTile(Tile tile) {
        Bitmap bitmap = tileCache.get(tile);

        if (bitmap == null) {
            requestTile(tile);
            bitmap = placeholderProvider.getPlaceholderBitmap();
        }

        return bitmap;
    }

    private void requestTile(final Tile tile) {
        loader.loadBitmap("http://b.tile.opencyclemap.org/cycle/" + ZOOM + "/" + tile.getX() + "/" + tile.getY() + ".png", new BitmapLoader.Callback() {
            @Override
            public void onTileLoaded(Bitmap bitmap) {
                tileCache.put(tile, bitmap);
                callback.onTileUpdated(tile);
            }
        });
    }

    void onSizeChanged(int tilesCountX, int tilesCountY) {
        tileCache.resize(tilesCountX * tilesCountY);
    }
}
