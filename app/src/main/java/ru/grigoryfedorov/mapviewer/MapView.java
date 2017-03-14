package ru.grigoryfedorov.mapviewer;

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


    private GestureDetector gestureDetector;
    private TileProvider tileProvider;

    private int tilesCountX;
    private int tilesCountY;

    private static final int ZOOM = 16;

    private static final int START_TILE_X = 33198;
    private static final int START_TILE_Y = 22539;

    private static final int MIN_TILE_X = 33148;
    private static final int MIN_TILE_Y = 22489;

    private static final int MAX_TILE_X = 33248;
    private static final int MAX_TILE_Y = 22589;


    private OverScroller scroller;

    private int tileWidth;
    private int tileHeight;


    private Rect bitmapRect;
    private Rect screenRect;
    private MapController mapController;
    private Rect borders;


    public MapView(Context context) {
        this(context, null, 0);
    }

    public MapView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MapView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        tileProvider = new TileProvider(getContext(), this);

        tileWidth = tileProvider.getTileWidth();
        tileHeight = tileProvider.getTileHeight();


        Point start = new Point(START_TILE_X * tileWidth, START_TILE_Y * tileHeight);

        borders = new Rect(MIN_TILE_X * tileWidth,
                MIN_TILE_Y * tileHeight,
                MAX_TILE_X * tileWidth,
                MAX_TILE_Y * tileHeight);

        mapController = new MapController(start, borders);





        bitmapRect = new Rect(0, 0, tileWidth, tileHeight);

        scroller = new OverScroller(context);

        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                Log.d(TAG, "GestureDetector onDown e " +  e);

                scroller.forceFinished(true);
                postInvalidateOnAnimation();

                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                Log.d(TAG, "onScroll x: " + distanceX + " y: " + distanceY);

                mapController.offset((int)distanceX, (int)distanceY);

                postInvalidateOnAnimation();

                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Log.d(TAG, "onFling x: " + velocityX + " y: " + velocityY);


                scroller.forceFinished(true);
                Point current = mapController.getCurrent();
                scroller.fling(current.x, current.y, (int)-velocityX, (int)-velocityY,
                        borders.left, borders.right, borders.top, borders.bottom);

                postInvalidateOnAnimation();
                return true;
            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }



    @Override
    protected void onDraw(Canvas canvas) {
        if (scroller.computeScrollOffset()) {
            mapController.set(scroller.getCurrX(), scroller.getCurrY());
            postInvalidateOnAnimation();
        }

        Point current = mapController.getCurrent();

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
    public void onTileUpdated(Tile tile) {
        drawTileIfNeeded(tile);
    }

    private void drawTileIfNeeded(Tile tile) {
        Point current = mapController.getCurrent();

        int startTileX = current.x / tileWidth;
        int startTileY = current.y / tileHeight;

        if (tile.getX() >= startTileX
                && tile.getX() < startTileX + tilesCountX
                && tile.getY() >= startTileY
                && tile.getY() < startTileY + tilesCountY) {
            int offsetX = -(current.x % tileWidth);
            int offsetY = -(current.y % tileHeight);

            int left = offsetX + (tile.getX() - startTileX) * tileWidth;
            int top = offsetY + (tile.getY() - startTileY) * tileHeight;

            postInvalidate(left, top, left + tileWidth, top + tileHeight);
        }
    }
}
