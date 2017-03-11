package ru.grigoryfedorov.mapviewer;


import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

class TileProvider {

    private static final int TILE_SIZE = 256;
    private static final int TILE_WIDTH = TILE_SIZE;
    private static final int TILE_HEIGHT = TILE_SIZE;

    private static final int ZOOM = 16;

    private final Callback callback;
    private final Context context;

    private TileCache tileCache;
    private PlaceholderProvider placeholderProvider;

    interface Callback {
        void onTileUpdated(Tile tile);
    }

    TileProvider(Context context, Callback callback) {
        this.context = context;
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
        Glide.with(context)
                .load("http://b.tile.opencyclemap.org/cycle/" + ZOOM + "/" + tile.getX() + "/" + tile.getY() + ".png")
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(TILE_WIDTH, TILE_HEIGHT) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        tileCache.put(tile, resource);
                        callback.onTileUpdated(tile);
                    }
                });
    }

    void onSizeChanged(int tilesCountX, int tilesCountY) {
        tileCache.resize(tilesCountX * tilesCountY);
    }
}
