package ru.grigoryfedorov.mapview.pool;


import android.graphics.Bitmap;

public interface BitmapPoolConsumer {
    void add(Bitmap bitmap);
}
