package ru.grigoryfedorov.mapviewer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;



class PlaceholderProvider {
    private static final String TAG = PlaceholderProvider.class.getSimpleName();

    private static final int SQUARE_SIZE = 16;

    @ColorInt
    private static final int BACKGROUND_COLOR_DEFAULT = Color.rgb(216, 208, 208);
    @ColorInt
    private static final int LINE_COLOR_DEFAULT = Color.rgb(200, 192, 192);

    @ColorInt
    private int backgroundColor = BACKGROUND_COLOR_DEFAULT;
    @ColorInt
    private int lineColor = LINE_COLOR_DEFAULT;

    private final int width;
    private final int height;

    private Bitmap cachedBitmap;

    PlaceholderProvider(int width, int height) {
        this.width = width;
        this.height = height;
    }

    Bitmap getPlaceholderBitmap() {
        if (cachedBitmap == null) {
            cachedBitmap = createBitmap();
        }
        return cachedBitmap;
    }

    private Bitmap createBitmap() {
        final Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        final Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(backgroundColor);

        final Paint paint = new Paint();
        paint.setColor(lineColor);
        paint.setStrokeWidth(0);

        for (int y = 0; y < height; y += SQUARE_SIZE) {
            canvas.drawLine(0, y, width, y, paint);
        }

        for (int x = 0; x < width; x += SQUARE_SIZE) {
            canvas.drawLine(x, 0, x, height, paint);
        }

        return bitmap;
    }
}
