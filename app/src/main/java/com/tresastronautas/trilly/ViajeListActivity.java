package com.tresastronautas.trilly;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class ViajeListActivity extends AppCompatActivity {

    private ParseUser currentUser;
    private List<ParseObject> viajesList;
    private ImageView mapa_pruebas;
    private List<LatLng> routeInLat;
    private FloatingActionButton viajelist_fab;
    private ViajesAdapter viajesAdapter;
    private List<Viaje> viajes;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/lato-regular-webfont.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        setContentView(R.layout.activity_viaje_list);
        currentUser = StaticThings.getCurrentUser();
        viajes = new ArrayList();
        recyclerView = (RecyclerView) findViewById(R.id.viajes_recycler_view);
        viajesAdapter = new ViajesAdapter(viajes);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(viajesAdapter);
        prepareLayout();
    }

    public void prepareLayout() {
        ParseQuery<ParseObject> query = new ParseQuery(ParseConstants.Ruta.NAME.val());
        query.whereMatches(ParseConstants.Ruta.USER.val(), currentUser.getObjectId());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                viajesList = objects;
                for (ParseObject viaj : viajesList) {
                    final ParseObject path = viaj.getParseObject(ParseConstants.Ruta.PATH.val());
                    try {
                        path.fetch();
                        routeInLat = RouteDecoder.getRouteFromHex(path.getString(ParseConstants.Path.DATA.val()));
                        Viaje v = new Viaje(routeInLat,
                                viaj.getDouble(ParseConstants.Ruta.KM.val()),
                                viaj.getDouble(ParseConstants.Ruta.TIME.val()),
                                viaj.getDouble(ParseConstants.Ruta.CAL.val()),
                                0.0,
                                viaj.getParseGeoPoint(ParseConstants.Ruta.ORIGIN.val()),
                                viaj.getString(ParseConstants.Ruta.FECHA.val()));
                        viajes.add(v);
                        viajesAdapter.notifyDataSetChanged();
                    } catch (Exception m) {
                        m.printStackTrace();
                    }
                }
            }
        });
        viajelist_fab = (FloatingActionButton) findViewById(R.id.viajelist_fab);
        viajelist_fab.setImageDrawable(new IconDrawable(this, Iconify.IconValue.zmdi_chevron_left)
                .colorRes(android.R.color.white));
        viajelist_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//        mapa_pruebas = (ImageView) findViewById(R.id.mapa_pruebas);
//        String encodedPoly = PolyUtil.encode(routeInLat);
//        String url = "https://maps.googleapis.com/maps/api/staticmap?size=600x300&path=weight:5%7Ccolor:0x00C466%7Cenc:" + encodedPoly;
//        Log.d("URL", url);
//        Picasso.with(this)
//                .load(url)
//                .into(mapa_pruebas);

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
