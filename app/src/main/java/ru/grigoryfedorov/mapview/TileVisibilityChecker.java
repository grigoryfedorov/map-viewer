package ru.grigoryfedorov.mapview;


import android.graphics.Point;

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

    /**
     * Check if tile currently visible on the screen
     *
     * This method must be thread safe
     *
     * @param tile tile to check
     * @return true if need to draw
     */
    public boolean needDraw(Tile tile) {
        Point current = coordinatesProvider.getCurrentCoordinates();

        int startTileX = current.x / tileWidth;
        int startTileY = current.y / tileHeight;

        return tile.getX() >= startTileX
                && tile.getX() < startTileX + tilesCountX
                && tile.getY() >= startTileY
                && tile.getY() < startTileY + tilesCountY;
    }

    public Point get() {
        return coordinatesProvider.getCurrentCoordinates();
    }

    public boolean needDraw(Tile tile, Point current) {
        int startTileX = current.x / tileWidth;
        int startTileY = current.y / tileHeight;

        return tile.getX() >= startTileX
                && tile.getX() < startTileX + tilesCountX
                && tile.getY() >= startTileY
                && tile.getY() < startTileY + tilesCountY;
    }

    public void setScreenSize(int width, int height) {
        tilesCountX = getTileCount(width, tileWidth);
        tilesCountY = getTileCount(height, tileHeight);
    }

    static int getTileCount(int viewSize, int tileSize) {
        return viewSize / tileSize + (viewSize % tileSize == 0 ? 0 : 1) + 2;
    }
}
