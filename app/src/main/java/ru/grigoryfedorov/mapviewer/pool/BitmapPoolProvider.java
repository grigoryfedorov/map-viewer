package ru.grigoryfedorov.mapviewer.pool;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;


public interface BitmapPoolProvider {
    @Nullable
    Bitmap get();
}
