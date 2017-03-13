package ru.grigoryfedorov.mapviewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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
    private static final long START_TILE_X = 33198;
    private static final long START_TILE_Y = 22539;

    private static final long MIN_TILE_X = 33148;
    private static final long MIN_TILE_Y = 22489;

    private static final long MAX_TILE_X = 33248;
    private static final long MAX_TILE_Y = 22589;

    private double currentX;
    private double currentY;

    private OverScroller scroller;

    private int tileWidth;
    private int tileHeight;

    private long minX;
    private long minY;

    private long maxY;
    private long maxX;


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

        currentX = START_TILE_X * tileWidth;
        currentY = START_TILE_Y * tileHeight;

        minX = MIN_TILE_X * tileWidth;
        minY = MIN_TILE_Y * tileHeight;

        maxX = MAX_TILE_X * tileWidth;
        maxY = MAX_TILE_Y * tileHeight;

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

                currentX += distanceX;
                currentY += distanceY;

                checkBorders();

                postInvalidateOnAnimation();

                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Log.d(TAG, "onFling x: " + velocityX + " y: " + velocityY);


                scroller.forceFinished(true);
                scroller.fling((int)currentX, (int)currentY, (int)-velocityX, (int)-velocityY, (int) minX, (int) maxX, (int) minY, (int) maxY);

                postInvalidateOnAnimation();
                return true;
            }
        });

    }

    void checkBorders() {
        if (currentX < minX) {
            currentX = minX;
        }

        if (currentY < minY) {
            currentY = minY;
        }

        if (currentX > maxX) {
            currentX = maxX;
        }

        if (currentY > maxY) {
            currentY = maxY;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }



    @Override
    protected void onDraw(Canvas canvas) {
        if (scroller.computeScrollOffset()) {
            currentX = scroller.getCurrX();
            currentY = scroller.getCurrY();
            postInvalidateOnAnimation();
        }

        long currentLongX = Math.round(currentX);
        long currentLongY = Math.round(currentY);

        long startTileX = currentLongX / tileWidth;
        long startTileY = currentLongY / tileHeight;


        int offsetX = (int) -(currentLongX % tileWidth);
        int offsetY = (int) -(currentLongY % tileHeight);

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        for (int x = 0; x < tilesCountX; x++) {
            for (int y = 0; y < tilesCountY; y++) {


                float left = offsetX + x * tileWidth;
                float top =  offsetY + y * tileHeight;

                if (left > width
                        || top > height
                        || left < -tileWidth
                        || top < -tileHeight) {
                    continue;
                }


                Bitmap bitmap = tileProvider.getTile(Tile.getTile(ZOOM, startTileX + x, startTileY + y));

                canvas.drawBitmap(bitmap, left, top, null);
            }
        }
    }




    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

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
        long currentLongX = Math.round(currentX);
        long currentLongY = Math.round(currentY);

        long startTileX = currentLongX / tileWidth;
        long startTileY = currentLongY / tileHeight;

        if (tile.getX() >= startTileX
                && tile.getX() < startTileX + tilesCountX
                && tile.getY() >= startTileY
                && tile.getY() < startTileY + tilesCountY) {
            int offsetX = (int) -(currentLongX % tileWidth);
            int offsetY = (int) -(currentLongY % tileHeight);

            int left = (int) (offsetX + (tile.getX() - startTileX) * tileWidth);
            int top = (int) (offsetY + (tile.getY() - startTileY) * tileHeight);

            postInvalidate(left, top, left + tileWidth, top + tileHeight);
        }
    }
}
