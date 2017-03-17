package ru.grigoryfedorov.mapview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


public class MapView extends View {
    private static final String TAG = MapView.class.getSimpleName();

    private static final int ZOOM = 16;
    private static final Rect MAP_BORDERS_DEFAULT = new Rect(33198, 22539, 33248, 22589);

    private int tileWidth;
    private int tileHeight;


    private ScrollController scrollController;
    private TileDrawer tileDrawer;
    private TileVisibilityChecker tileVisibilityChecker;

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

        scrollController = createScrollController(context);

        tileVisibilityChecker = new TileVisibilityChecker(mapType, scrollController);

        TileProvider tileProvider = new TileProviderImpl(getContext(), mapType, tileVisibilityChecker);
        tileProvider.setInvalidateListener(new InvalidateListener() {
            @Override
            public void onInvalidateNeeded() {
                postInvalidate();
            }
        });

        tileDrawer = new TileDrawer(tileWidth, tileHeight, ZOOM);
        tileDrawer.setTileProvider(tileProvider);
    }

    private ScrollController createScrollController(Context context) {
        ScrollController scrollController = new ScrollControllerImpl(context);

        Rect mapBorders = MAP_BORDERS_DEFAULT;

        Rect mapBordersInPixels = new Rect(mapBorders.left * tileWidth,
                mapBorders.top * tileHeight,
                mapBorders.right * tileWidth,
                mapBorders.bottom * tileHeight);

        scrollController.setGlobalBorders(mapBordersInPixels);
        scrollController.setCurrentCoordinates(mapBordersInPixels.centerX(), mapBordersInPixels.centerY());
        scrollController.setInvalidateListener(new InvalidateListener() {
            @Override
            public void onInvalidateNeeded() {
                invalidate();
            }
        });

        return scrollController;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        tileDrawer.setScreenSize(w, h);
        tileVisibilityChecker.setScreenSize(w, h);

        scrollController.setOverFling(w / 2, h / 2);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return scrollController.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        scrollController.onDraw();

        tileDrawer.drawTiles(canvas, scrollController.getCurrentCoordinates());
    }

}
