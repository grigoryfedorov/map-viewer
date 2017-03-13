package ru.grigoryfedorov.mapviewer;


import android.graphics.Bitmap;

public interface BitmapPoolConsumer {
    public void add(Bitmap bitmap);
}
