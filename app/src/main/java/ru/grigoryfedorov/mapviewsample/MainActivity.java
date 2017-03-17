package ru.grigoryfedorov.mapviewsample;

import android.app.Activity;
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
        mapView.setMapType(MapType.OSM_CYCLE);

        setStatusBarTranslucent();
    }

    protected void setStatusBarTranslucent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

}
