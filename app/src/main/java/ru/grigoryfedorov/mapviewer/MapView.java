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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;


public class MapView extends View {
    private static final String TAG = MapView.class.getSimpleName();

    public static final int TILE_SIZE = 256;
    public static final int TILE_WIDTH = TILE_SIZE;
    public static final int TILE_HEIGHT = TILE_SIZE;

    private TileCache tileCache;
    private PlaceholderProvider placeholderProvider;

    private GestureDetector gestureDetector;

    private int tilesCountX;
    private int tilesCountY;

    private static final long START_TILE_X = 33198;
    private static final long START_TILE_Y = 22539;

    private double currentX = START_TILE_X * TILE_WIDTH;
    private double currentY = START_TILE_Y * TILE_HEIGHT;

    private OverScroller scroller;

    public MapView(Context context) {
        this(context, null, 0);
    }

    public MapView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MapView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        tileCache = new LruTileCache(128);
        placeholderProvider = new PlaceholderProvider(TILE_WIDTH, TILE_HEIGHT);

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

                postInvalidateOnAnimation();

                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Log.d(TAG, "onFling x: " + velocityX + " y: " + velocityY);


                scroller.forceFinished(true);
                scroller.fling((int)currentX, (int)currentY, (int)-velocityX, (int)-velocityY, 0, 1000000000, 0, 1000000000);

                postInvalidateOnAnimation();
                return true;
            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    void requestTile(final long x, final long y) {
        Glide.with(getContext())
                .load("http://b.tile.opencyclemap.org/cycle/16/" + x + "/" + y + ".png")
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(TILE_WIDTH, TILE_HEIGHT) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        tileCache.put(Tile.getTile(x, y), resource);
                        drawTileIfNeeded(x, y);
                    }
                });
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

        long startTileX = currentLongX / TILE_WIDTH;
        long startTileY = currentLongY / TILE_HEIGHT;


        int offsetX = (int) -(currentLongX % TILE_WIDTH);
        int offsetY = (int) -(currentLongY % TILE_HEIGHT);

        for (int x = 0; x < tilesCountX; x++) {
            for (int y = 0; y < tilesCountY; y++) {
                Bitmap bitmap = tileCache.get(Tile.getTile(startTileX + x, startTileY + y));

                if (bitmap == null) {
                    requestTile(startTileX + x, startTileY + y);
                    bitmap = placeholderProvider.getPlaceholderBitmap();
                }

                float left = offsetX + x * TILE_WIDTH;
                float top =  offsetY + y * TILE_HEIGHT;

                canvas.drawBitmap(bitmap, left, top, null);
            }
        }
    }


    private void drawTileIfNeeded(long tileX, long tileY) {
        long currentLongX = Math.round(currentX);
        long currentLongY = Math.round(currentY);

        long startTileX = currentLongX / TILE_WIDTH;
        long startTileY = currentLongY / TILE_HEIGHT;

        if (tileX >= startTileX && tileX < startTileX + tilesCountX
                && tileY >= startTileY
                && tileY < startTileY + tilesCountY) {
            int offsetX = (int) -(currentLongX % TILE_WIDTH);
            int offsetY = (int) -(currentLongY % TILE_HEIGHT);

            int left = (int) (offsetX + (tileX - startTileX) * TILE_WIDTH);
            int top = (int) (offsetY + (tileY - startTileY) * TILE_HEIGHT);

            invalidate(left, top, left + TILE_WIDTH, top + TILE_HEIGHT);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        tilesCountX = getTileCount(w, TILE_WIDTH) + 2;
        tilesCountY = getTileCount(h, TILE_HEIGHT) + 2;

        tileCache.resize(tilesCountX * tilesCountY);

        Log.d(TAG, "onSizeChanged w " + w + " h " + h + " oldw " + oldw + " oldh " + oldh + " tile count " + tilesCountX + " x " + tilesCountY);

    }

    int getTileCount(int viewSize, int tileSize) {
        return viewSize / tileSize + (viewSize % tileSize == 0 ? 0 : 1);
    }
}
