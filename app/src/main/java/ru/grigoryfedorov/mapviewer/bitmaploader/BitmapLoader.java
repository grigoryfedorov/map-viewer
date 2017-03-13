package ru.grigoryfedorov.mapviewer.bitmaploader;


import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import ru.grigoryfedorov.mapviewer.pool.BitmapPoolProvider;

public interface BitmapLoader {

    Bitmap loadBitmap(String url);
    void setBitmapPoolProvider(@Nullable BitmapPoolProvider bitmapPoolProvider);
}
