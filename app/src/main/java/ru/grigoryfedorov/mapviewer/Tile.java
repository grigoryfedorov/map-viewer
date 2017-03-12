package ru.grigoryfedorov.mapviewer;


class Tile {
    private final int zoom;
    private final long x;
    private final long y;

    // TODO: may be provide object pool
    static Tile getTile(int zoom, long x, long y) {
        return new Tile(zoom, x, y);
    }

    private Tile(int zoom, long x, long y) {
        this.zoom = zoom;
        this.x = x;
        this.y = y;
    }

    public int getZoom() {
        return zoom;
    }

    long getX() {
        return x;
    }

    long getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Tile tile = (Tile) o;

        return zoom == tile.zoom && x == tile.x && y == tile.y;
    }

    @Override
    public int hashCode() {
        int result = zoom;
        result = 31 * result + (int) (x ^ (x >>> 32));
        result = 31 * result + (int) (y ^ (y >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Tile{" +
                "zoom=" + zoom +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
