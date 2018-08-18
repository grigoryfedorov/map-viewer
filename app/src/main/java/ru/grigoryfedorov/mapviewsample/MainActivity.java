package ru.grigoryfedorov.mapviewsample;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import ru.grigoryfedorov.mapview.MapType;
import ru.grigoryfedorov.mapview.MapView;
import ru.grigoryfedorov.mapview.R;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapView mapView = (MapView) findViewById(R.id.map_view);

        int leftX = 33150;
        int topY = 22493;
        int rectSize = 100;

        Rect mapBorders = new Rect(leftX, topY, leftX + rectSize, topY + rectSize);

        mapView.setMap(MapType.OSM, mapBorders);

        setStatusBarTranslucent();
    }

    protected void setStatusBarTranslucent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

}
