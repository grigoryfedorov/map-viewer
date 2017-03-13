package ru.grigoryfedorov.mapviewer;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

class UrlConnectionLoader implements BitmapLoader {
    private static final String TAG = UrlConnectionLoader.class.getSimpleName();

    @Nullable
    private BitmapPoolProvider bitmapPoolProvider;

    @Override
    public void setBitmapPoolProvider(@Nullable BitmapPoolProvider bitmapPoolProvider) {
        this.bitmapPoolProvider = bitmapPoolProvider;
    }

    @Override
    public Bitmap loadBitmap(final String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input, null, BitmapOptionsProvider.getOptions(bitmapPoolProvider));
        } catch (IOException e) {
            Log.e(TAG, Log.getStackTraceString(e));
            return null;
        }
    }
}
