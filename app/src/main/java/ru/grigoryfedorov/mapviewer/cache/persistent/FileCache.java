package ru.grigoryfedorov.mapviewer.cache.persistent;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import ru.grigoryfedorov.mapviewer.BitmapOptionsProvider;
import ru.grigoryfedorov.mapviewer.Tile;
import ru.grigoryfedorov.mapviewer.pool.BitmapPoolProvider;

public class FileCache implements PersistentCache {
    private static final String TAG = FileCache.class.getSimpleName();
    private final String cacheDir;

    @Nullable
    private BitmapPoolProvider bitmapPoolProvider;

    public FileCache(Context context) {
        cacheDir = context.getCacheDir().getPath() + File.separator + "cache" + File.separator;
        new File(cacheDir).mkdirs();
    }


    @Override
    public void setBitmapPoolProvider(BitmapPoolProvider bitmapPoolProvider) {
        this.bitmapPoolProvider = bitmapPoolProvider;
    }

    @Override
    public void put(Tile tile, Bitmap bitmap) {
        try {
            writeToFile(getPath(tile), bitmap);
        } catch (IOException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    private File getFile(Tile tile) {
        return new File(getPath(tile));
    }

    @NonNull
    private String getPath(Tile tile) {
        return cacheDir +
                ":" +
                tile.getZoom() +
                ":" +
                tile.getX() +
                ":" +
                tile.getY() +
                ".png";
    }

    private void writeToFile(String path, Bitmap bitmap) throws IOException {
        FileOutputStream out = null;
        try {
            File temp = new File(path + ".temp");
            out = new FileOutputStream(temp);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

            temp.renameTo(new File(path));

        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    @Nullable
    @Override
    public Bitmap get(Tile tile) {
        String path = getPath(tile);

        File file = new File(path);
        if (!file.exists()) {
            return null;
        }

        return BitmapFactory.decodeFile(path, BitmapOptionsProvider.getOptions(bitmapPoolProvider));
    }


}
