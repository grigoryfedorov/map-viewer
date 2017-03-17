package ru.grigoryfedorov.mapview;


import android.graphics.Point;
import android.graphics.Rect;

public class TileVisibilityChecker {
    private CoordinatesProvider coordinatesProvider;

    private final int tileWidth;
    private final int tileHeight;

    private int tilesCountX;
    private int tilesCountY;

    public TileVisibilityChecker(MapType mapType, CoordinatesProvider coordinatesProvider) {
        this.coordinatesProvider = coordinatesProvider;

        tileWidth = mapType.getTileWidth();
        tileHeight = mapType.getTileHeight();
    }

    public Rect getVisibleTileRectangle() {
        Point current = coordinatesProvider.getCurrentCoordinates();

        int startTileX = current.x / tileWidth;
        int startTileY = current.y / tileHeight;

        return new Rect(startTileX, startTileY, startTileX + tilesCountX, startTileY + tilesCountY);
    }

    /**
     * Check if tile currently visible on the screen
     *
     * This method must be thread safe
     *
     * @param tile tile to check
     * @return true if need to draw
     */
    public boolean needDraw(Tile tile) {
        return getVisibleTileRectangle().contains(tile.getX(), tile.getY());
    }

    public void setScreenSize(int width, int height) {
        tilesCountX = getTileCount(width, tileWidth);
        tilesCountY = getTileCount(height, tileHeight);
    }

    static int getTileCount(int viewSize, int tileSize) {
        return viewSize / tileSize + (viewSize % tileSize == 0 ? 0 : 1) + 2;
    }
}
