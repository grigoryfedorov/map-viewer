package ru.grigoryfedorov.mapviewer;


import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public final class BitmapOptionsProvider {
    @NonNull
    static BitmapFactory.Options getOptions(@Nullable BitmapPoolProvider bitmapPoolProvider) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        if (bitmapPoolProvider != null) {
            options.inBitmap = bitmapPoolProvider.get();
        }
        return options;
    }
}
