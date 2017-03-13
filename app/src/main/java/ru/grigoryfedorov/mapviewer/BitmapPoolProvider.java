package ru.grigoryfedorov.mapviewer;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;


public interface BitmapPoolProvider {
    @Nullable
    public Bitmap get();
}
