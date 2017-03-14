package ru.grigoryfedorov.mapviewer.pool;


import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BitmapPool implements BitmapPoolProvider, BitmapPoolConsumer {
    private static final String LOG = BitmapPool.class.getSimpleName();
    private final Set<SoftReference<Bitmap>> reusableBitmaps;

    public BitmapPool() {
        this.reusableBitmaps = Collections.synchronizedSet(new HashSet<SoftReference<Bitmap>>());
    }

    @Override
    public void add(Bitmap bitmap) {
        reusableBitmaps.add(new SoftReference<>(bitmap));
//        Log.i(LOG, "new reusable bitmap");
    }

    @Override
    @Nullable
    public Bitmap get() {
        Bitmap bitmap = null;

        synchronized (reusableBitmaps) {
            if (!reusableBitmaps.isEmpty()) {
                // since we have all bitmaps of same type and this project is simplified task
                // we do not check is reusable bitmap fits new one
                // https://developer.android.com/topic/performance/graphics/manage-memory.html

                Iterator<SoftReference<Bitmap>> iterator = reusableBitmaps.iterator();
                bitmap = iterator.next().get();
                iterator.remove();
            }
        }

        return bitmap;
    }
}
