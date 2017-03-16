package ru.grigoryfedorov.mapview;


import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.grigoryfedorov.mapview.pool.BitmapPoolProvider;

public final class BitmapOptionsProvider {
    @NonNull
    public static BitmapFactory.Options getOptions(@Nullable BitmapPoolProvider bitmapPoolProvider) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        options.inSampleSize = 1;
        if (bitmapPoolProvider != null) {
            options.inBitmap = bitmapPoolProvider.get();
        }
        return options;
    }
}
