package ru.grigoryfedorov.mapviewer.pool;


import android.graphics.Bitmap;

public interface BitmapPoolConsumer {
    void add(Bitmap bitmap);
}
