package ru.grigoryfedorov.mapviewer;


class Tile {
    private final long x;
    private final long y;

    // TODO: may be provide object pool
    static Tile getTile(long x, long y) {
        return new Tile(x, y);
    }

    private Tile(long x, long y) {
        this.x = x;
        this.y = y;
    }

    long getX() {
        return x;
    }

    long getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tile tile = (Tile) o;

        return x == tile.x && y == tile.y;

    }

    @Override
    public int hashCode() {
        int result = (int) (x ^ (x >>> 32));
        result = 31 * result + (int) (y ^ (y >>> 32));
        return result;
    }
}
