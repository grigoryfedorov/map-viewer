package ru.grigoryfedorov.mapviewer;


import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class UrlConnectionLoader implements BitmapLoader {
    private static final String TAG = UrlConnectionLoader.class.getSimpleName();
    private ExecutorService executorService;

    private Set<String> urlsInProgress;
    private Set<String> urlsLoaded;

    UrlConnectionLoader() {
        executorService = Executors.newFixedThreadPool(70);
        urlsInProgress = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
        urlsLoaded = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
    }

    @Override
    public void loadBitmap(final String urlString, final Callback callback) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                if (urlsInProgress.contains(urlString)) {
                    return;
                }

                if (urlsLoaded.contains(urlString)) {
                    return;
                }

                urlsInProgress.add(urlString);

                try {
                    URL url = new URL(urlString);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    callback.onTileLoaded(BitmapFactory.decodeStream(input));
                    urlsLoaded.add(urlString);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    urlsInProgress.remove(urlString);
                }
            }
        });
    }
}
