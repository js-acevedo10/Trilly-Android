package com.tresastronautas.trilly;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.parse.ParseObject;
import com.tresastronautas.trilly.Helpers.ParseConstants;
import com.tresastronautas.trilly.Helpers.StaticThings;

public class ViajeDetailsActivity extends AppCompatActivity {

    private ParseObject selectedViaje;
    private TextView viajedetails_text_title, viajedetails_text_km_dynamic, viajedetails_text_km_static,
            viajedetails_text_duracion_static, viajedetails_text_duracion_dynamic, viajedetails_text_velocidad_static,
            viajedetails_text_velocidad_dynamic, viajedetails_text_calorias_dynamic, viajedetails_text_calorias_static;
    private FloatingActionButton viajedetails_fab;
    private GoogleMap gMap;
    private Polyline route;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viaje_details);
        selectedViaje = StaticThings.getSelectedViaje();
        prepareLayout();
    }

    public void prepareLayout() {
        viajedetails_text_title = (TextView) findViewById(R.id.viajedetails_text_title);
        viajedetails_text_title.setText(selectedViaje.getString(ParseConstants.Ruta.FECHA.val()));

        viajedetails_text_km_dynamic = (TextView) findViewById(R.id.viajedetails_text_km_dynamic);
        viajedetails_text_km_dynamic.setText(getString(R.string.viajedetails_km_dynamic,
                selectedViaje.getDouble(ParseConstants.Ruta.KM.val())));

        viajedetails_text_duracion_dynamic = (TextView) findViewById(R.id.viajedetails_text_duracion_dynamic);
//        viajedetails_text_duracion_dynamic.setText(getString(R.string.viajedetails_duracion_dynamic,
//                selectedViaje.getDouble(ParseConstants.Ruta.TIME.val())));

        viajedetails_text_velocidad_dynamic = (TextView) findViewById(R.id.viajedetails_text_velocidad_dynamic);
        viajedetails_text_velocidad_dynamic.setText(getString(R.string.viajedetails_velocidad_dynamic,
                (selectedViaje.getDouble(ParseConstants.Ruta.KM.val())) / selectedViaje.getDouble(ParseConstants.Ruta.TIME.val())));

        viajedetails_text_calorias_dynamic = (TextView) findViewById(R.id.viajedetails_text_calorias_dynamic);
        viajedetails_text_calorias_dynamic.setText(getString(R.string.viajedetails_calorias_dynamic,
                selectedViaje.getDouble(ParseConstants.Ruta.CAL.val())));

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.viajedetails_map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                gMap = googleMap;
//                if (ActivityCompat.checkSelfPermission(ViajeDetailsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ViajeDetailsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(ViajeDetailsActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
//                            ViajeActivity.MY_PERMISSION_ACCESS_COARSE_LOCATION);
//                }
//                gMap.setMyLocationEnabled(false);
                route = gMap.addPolyline(new PolylineOptions()
                        .width(20f)
                        .color(R.color.colorPrimaryDark)
                        .geodesic(true));
                paintMap();
            }
        });
    }

    public void paintMap() {

    }
}
