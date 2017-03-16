package ru.grigoryfedorov.mapview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.OverScroller;


public class MapView extends View implements TileProvider.Callback {
    private static final String TAG = MapView.class.getSimpleName();

    private static final int ZOOM = 16;
    private static final Rect MAP_BORDERS_DEFAULT = new Rect(33198, 22539, 33248, 22589);

    private GestureDetector gestureDetector;
    private TileProvider tileProvider;

    private int tilesCountX;
    private int tilesCountY;

    private OverScroller scroller;

    private int tileWidth;
    private int tileHeight;


    private Rect bitmapRect;
    private Rect screenRect;
    private SyncPoint currentCoordinatesInPixels;
    private Rect borders;

    public MapView(Context context) {
        this(context, null, 0);
    }

    public MapView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MapView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        MapType mapType = MapType.OSM_CYCLE;

        tileWidth = mapType.getTileWidth();
        tileHeight = mapType.getTileHeight();

        Rect mapBorders = MAP_BORDERS_DEFAULT;

        borders = new Rect(mapBorders.left * tileWidth,
                mapBorders.top * tileHeight,
                mapBorders.right * tileWidth,
                mapBorders.bottom * tileHeight);

        currentCoordinatesInPixels = new SyncPoint(borders.centerX(), borders.centerY());

        tileProvider = new TileProvider(getContext(), this, currentCoordinatesInPixels, mapType);



        bitmapRect = new Rect(0, 0, tileWidth, tileHeight);

        scroller = new OverScroller(context);

        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                scroller.forceFinished(true);
                invalidate();

                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                Point current = currentCoordinatesInPixels.get();

                boolean isSpringBack = scroller.springBack(current.x + (int)distanceX, current.y + (int)distanceY, borders.left, borders.right, borders.top, borders.bottom);

                if (!isSpringBack) {
                    currentCoordinatesInPixels.offset((int)distanceX, (int)distanceY);
                }

                invalidate();

                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Point current = currentCoordinatesInPixels.get();
                scroller.fling(current.x, current.y, (int)-velocityX, (int)-velocityY,
                        borders.left, borders.right, borders.top, borders.bottom, screenRect.width() / 2, screenRect.height() / 2);

                invalidate();
                return true;
            }
        });

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        screenRect = new Rect(0, 0, w, h);

        tilesCountX = getTileCount(w, tileWidth) + 2;
        tilesCountY = getTileCount(h, tileHeight) + 2;

        tileProvider.onSizeChanged(tilesCountX, tilesCountY);

        Log.d(TAG, "onSizeChanged w " + w + " h " + h + " oldw " + oldw + " oldh " + oldh + " tile count " + tilesCountX + " x " + tilesCountY);

    }

    int getTileCount(int viewSize, int tileSize) {
        return viewSize / tileSize + (viewSize % tileSize == 0 ? 0 : 1);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public void onTileUpdated(Tile tile) {
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (scroller.computeScrollOffset()) {
            currentCoordinatesInPixels.set(scroller.getCurrX(), scroller.getCurrY());
            invalidate();
        }

        Point current = currentCoordinatesInPixels.get();

        int tileX = current.x / tileWidth;
        int tileY = current.y / tileHeight;


        int offsetX = -(current.x % tileWidth);
        int offsetY = -(current.y % tileHeight);

        bitmapRect.offsetTo(offsetX, offsetY);

        for (int x = 0; x < tilesCountX; x++) {

            bitmapRect.offsetTo(offsetX + x * tileWidth, offsetY - tileHeight);
            tileX++;

            for (int y = 0; y < tilesCountY; y++) {


                bitmapRect.offset(0, tileHeight);

                if (!Rect.intersects(screenRect, bitmapRect)) {
                    continue;
                }

                Bitmap bitmap = tileProvider.getTile(Tile.getTile(ZOOM, tileX, tileY + y));

                canvas.drawBitmap(bitmap, null, bitmapRect, null);
            }
        }
    }

}
