package ru.grigoryfedorov.mapview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


public class MapView extends View {
    private static final String TAG = MapView.class.getSimpleName();

    private static final Rect MAP_BORDERS_DEFAULT = new Rect(33198, 22539, 33248, 22589);

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
    }

    public void setMapType(MapType mapType) {
        init(mapType);
    }

    private void init(MapType mapType) {
        scrollController = createScrollController(mapType);

        tileVisibilityChecker = new TileVisibilityChecker(mapType, scrollController);

        TileProvider tileProvider = new TileProviderImpl(getContext(), mapType, tileVisibilityChecker);
        tileProvider.setInvalidateListener(new InvalidateListener() {
            @Override
            public void onInvalidateNeeded() {
                postInvalidate();
            }
        });

        tileDrawer = new TileDrawer(mapType);
        tileDrawer.setTileProvider(tileProvider);
    }

    private ScrollController createScrollController(MapType mapType) {
        ScrollController scrollController = new ScrollControllerImpl(getContext());

        Rect mapBorders = MAP_BORDERS_DEFAULT;

        Rect mapBordersInPixels = new Rect(mapBorders.left * mapType.getTileWidth(),
                mapBorders.top * mapType.getTileHeight(),
                mapBorders.right * mapType.getTileWidth(),
                mapBorders.bottom * mapType.getTileHeight());

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

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        //begin boilerplate code so parent classes can restore state
        if(!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState savedState = (SavedState)state;
        super.onRestoreInstanceState(savedState.getSuperState());
        //end

        scrollController.setCurrentCoordinates(
                savedState.savedCoordinates.x,
                savedState.savedCoordinates.y);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        //begin boilerplate code that allows parent classes to save state
        Parcelable superState = super.onSaveInstanceState();

        SavedState savedState = new SavedState(superState);
        //end

        savedState.savedCoordinates = scrollController.getCurrentCoordinates();

        return savedState;
    }

    private static class SavedState extends BaseSavedState {
        Point savedCoordinates;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.savedCoordinates = in.readParcelable(getClass().getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeParcelable(savedCoordinates, 0);
        }

        //required field that makes Parcelables from a Parcel
        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }

}
