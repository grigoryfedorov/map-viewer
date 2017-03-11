package ru.grigoryfedorov.mapviewer;


import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

class GlideLoader implements BitmapLoader {
    private final Context context;

    private final int width;
    private final int height;

    GlideLoader(Context context, int width, int height) {
        this.context = context;
        this.width = width;
        this.height = height;
    }

    @Override
    public void loadBitmap(String url, final BitmapLoader.Callback callback) {
        Glide.with(context)
                .load(url)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(width, height) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        callback.onTileLoaded(resource);
                    }
                });
    }
}
