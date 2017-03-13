package ru.grigoryfedorov.mapviewer;


import android.graphics.Bitmap;
import android.support.annotation.Nullable;

public interface BitmapLoader {

    Bitmap loadBitmap(String url);
    void setBitmapPoolProvider(@Nullable BitmapPoolProvider bitmapPoolProvider);
}
