package ru.grigoryfedorov.mapviewer;


import android.graphics.Bitmap;

public interface BitmapLoader {

    interface Callback {
        void onTileLoaded(Bitmap resource);
    }

    void loadBitmap(String url, Callback callback);
}
