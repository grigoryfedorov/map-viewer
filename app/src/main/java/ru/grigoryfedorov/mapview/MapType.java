package ru.grigoryfedorov.mapview;


import java.util.Locale;

public class MapType {
    private final String baseUrl;
    private final int tileWidth;
    private final int tileHeight;
    private final String[] subDomains;

    private int i;
    private final Object lock = new Object();

    private static final int DEFAULT_ZOOM = 16;
    public static final int DEFAULT_TILE_SIZE = 256;

    public static final MapType OSM_CYCLE = new MapType("tile.opencyclemap.org/cycle", new String[]{"a", "b", "c"});
    public static final MapType OSM = new MapType("tile.openstreetmap.org", new String[]{"a", "b", "c"});
    public static final MapType OPEN_RAIL_WAY = new MapType("tiles.openrailwaymap.org/standard", new String[]{"a", "b", "c"}, 512, 512);
    private int zoom;

    public MapType(String baseUrl, String[] subDomains) {
        this(baseUrl, subDomains, DEFAULT_TILE_SIZE, DEFAULT_TILE_SIZE);
    }

    public MapType(String baseUrl, String[] subDomains, int tileWidth, int tileHeight) {
        this.baseUrl = baseUrl;
        this.subDomains = subDomains;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.zoom = DEFAULT_ZOOM;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public String getTileRequestUrl(Tile tile) {
        return String.format(Locale.US, "http://%s.%s/%d/%d/%d.png",
                getSubDomain(), baseUrl, tile.getZoom(), tile.getX(), tile.getY());
    }

    private String getSubDomain() {
        synchronized (lock) {
            i++;
            i = i % subDomains.length;
            return subDomains[i];
        }
    }


    public int getZoom() {
        return zoom;
    }
}
