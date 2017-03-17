package ru.grigoryfedorov.mapview;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;

public class TileDrawer {
    private final int tileWidth;
    private final int tileHeight;

    private Rect bitmapRect;
    private int tilesCountX;
    private int tilesCountY;
    private int zoom;

    private TileProvider tileProvider;

    public TileDrawer(int tileWidth, int tileHeight, int zoom) {
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.zoom = zoom;

        bitmapRect = new Rect(0, 0, tileWidth, tileHeight);
    }

    public void setTileProvider(TileProvider tileProvider) {
        this.tileProvider = tileProvider;
    }

    void drawTiles(Canvas canvas, Point current) {

        int tileX = current.x / tileWidth;
        int tileY = current.y / tileHeight;

        Rect clipBounds = canvas.getClipBounds();

        int offsetX = -(current.x % tileWidth);
        int offsetY = -(current.y % tileHeight);

        bitmapRect.offsetTo(offsetX, offsetY);

        for (int x = 0; x < tilesCountX; x++) {

            bitmapRect.offsetTo(offsetX + x * tileWidth, offsetY - tileHeight);
            tileX++;

            for (int y = 0; y < tilesCountY; y++) {


                bitmapRect.offset(0, tileHeight);

                if (!Rect.intersects(clipBounds, bitmapRect)) {
                    continue;
                }

                Bitmap bitmap = tileProvider.getTile(Tile.getTile(zoom, tileX, tileY + y));

                canvas.drawBitmap(bitmap, null, bitmapRect, null);
            }
        }
    }

    public void setScreenSize(int width, int height) {
        tilesCountX = TileVisibilityChecker.getTileCount(width, tileWidth);
        tilesCountY = TileVisibilityChecker.getTileCount(height, tileHeight);

        tileProvider.setVisibleTileCount(tilesCountX * tilesCountY);
    }



}
