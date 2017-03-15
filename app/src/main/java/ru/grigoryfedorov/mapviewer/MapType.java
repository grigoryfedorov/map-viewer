package ru.grigoryfedorov.mapviewer;


import java.util.Locale;

public class MapType {
    private final String baseUrl;
    private final int tileWidth;
    private final int tileHeight;

    public static final int DEFAULT_TILE_SIZE = 256;

    public static final MapType OSM_CYCLE = new MapType("http://b.tile.opencyclemap.org/cycle/");
    public static final MapType OSM = new MapType("http://b.tile.openstreetmap.org/");

    public MapType(String baseUrl) {
        this(baseUrl, DEFAULT_TILE_SIZE, DEFAULT_TILE_SIZE);
    }

    public MapType(String baseUrl, int tileWidth, int tileHeight) {
        this.baseUrl = baseUrl;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public String getTileRequestUrl(Tile tile) {
        return String.format(Locale.US, "%s%d/%d/%d.png", baseUrl, tile.getZoom(), tile.getX(), tile.getY());
    }



}
