package com.tresastronautas.trilly;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.tresastronautas.trilly.Helpers.StaticThings;
import com.tresastronautas.trilly.ListAdapters.Viaje;


public class ViajeDetailsActivity extends AppCompatActivity {

    private Viaje selectedViaje;
    private TextView viajedetails_text_title, viajedetails_text_km_dynamic, viajedetails_text_duracion_dynamic,
            viajedetails_text_velocidad_dynamic, viajedetails_text_calorias_dynamic;
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
        viajedetails_fab = (FloatingActionButton) findViewById(R.id.viajedetails_fab);
        viajedetails_fab.setImageDrawable(new IconDrawable(this, Iconify.IconValue.zmdi_chevron_left)
                .colorRes(android.R.color.white));
        viajedetails_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        viajedetails_text_title = (TextView) findViewById(R.id.viajedetails_text_title);
        viajedetails_text_title.setText(getString(R.string.viajedetails_title_dynamic, selectedViaje.getFecha()));

        viajedetails_text_km_dynamic = (TextView) findViewById(R.id.viajedetails_text_km_dynamic);
        viajedetails_text_km_dynamic.setText(getString(R.string.viajedetails_km_dynamic,
                selectedViaje.getKm()));

        viajedetails_text_duracion_dynamic = (TextView) findViewById(R.id.viajedetails_text_duracion_dynamic);
        viajedetails_text_duracion_dynamic.setText(getString(R.string.viajedetails_duracion_dynamic,
                calcularHoras(selectedViaje.getTiempo()), calcularMinutos(selectedViaje.getTiempo()), calcularSegundos(selectedViaje.getTiempo())));

        viajedetails_text_velocidad_dynamic = (TextView) findViewById(R.id.viajedetails_text_velocidad_dynamic);
        viajedetails_text_velocidad_dynamic.setText(getString(R.string.viajedetails_velocidad_dynamic,
                selectedViaje.getVelPromedio()));

        viajedetails_text_calorias_dynamic = (TextView) findViewById(R.id.viajedetails_text_calorias_dynamic);
        viajedetails_text_calorias_dynamic.setText(getString(R.string.viajedetails_calorias_dynamic,
                selectedViaje.getCal()));

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
                        .color(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark))
                        .geodesic(true));
                gMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        paintMap();
                    }
                });
            }
        });
    }

    public void paintMap() {
        route.setPoints(selectedViaje.getPath());
        gMap.addMarker(new MarkerOptions().position(selectedViaje.getPath().get(0)).icon(BitmapDescriptorFactory.fromResource(R.drawable.mapa_imagen_punto_inicio)));
        gMap.addMarker(new MarkerOptions().position(selectedViaje.getPath().get(selectedViaje.getPath().size() - 1)).icon(BitmapDescriptorFactory.fromResource(R.drawable.mapa_imagen_punto_final)));
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int i = 0; i < selectedViaje.getPath().size(); i++) {
            builder.include(selectedViaje.getPath().get(i));
        }
        LatLngBounds latLngBounds = builder.build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(latLngBounds, 100);
        gMap.animateCamera(cameraUpdate);
    }

    public int calcularHoras(double tiempo) {
        return (int) Math.floor(tiempo / (60 * 60));
    }

    public int calcularMinutos(double tiempo) {
        int horas = calcularHoras(tiempo);
        return (int) Math.floor((tiempo - (horas * 60 * 60)) / 60);
    }

    public int calcularSegundos(double tiempo) {
        int horas = calcularHoras(tiempo);
        int minutos = calcularMinutos(tiempo);
        return (int) Math.floor(tiempo - (horas * 60 * 60) - (minutos * 60));
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}