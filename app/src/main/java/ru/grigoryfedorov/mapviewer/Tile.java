package ru.grigoryfedorov.mapviewer;


class Tile {
    private final int zoom;
    private final int x;
    private final int y;

    // TODO: may be provide object pool
    static Tile getTile(int zoom, int x, int y) {
        return new Tile(zoom, x, y);
    }

    private Tile(int zoom, int x, int y) {
        this.zoom = zoom;
        this.x = x;
        this.y = y;
    }

    int getZoom() {
        return zoom;
    }

    int getX() {
        return x;
    }

    int getY() {
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
        result = 31 * result + x;
        result = 31 * result + y;
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
