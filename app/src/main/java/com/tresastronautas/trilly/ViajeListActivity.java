package com.tresastronautas.trilly;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.tresastronautas.trilly.Helpers.ParseConstants;
import com.tresastronautas.trilly.Helpers.StaticThings;
import com.tresastronautas.trilly.ListAdapters.Viaje;
import com.tresastronautas.trilly.ListAdapters.ViajesAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class ViajeListActivity extends AppCompatActivity {

    private ParseUser currentUser;
    private List<ParseObject> viajesList;
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
        recyclerView.addOnItemTouchListener(new ViajeItemClickListener(getApplicationContext(), new ViajeItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                startViajeDetailsActivity(viajes.get(position));
            }
        }));
        prepareLayout();
    }

    public void prepareLayout() {
        ParseQuery<ParseObject> query = new ParseQuery(ParseConstants.Ruta.NAME.val());
        query.whereMatches(ParseConstants.Ruta.USER.val(), currentUser.getObjectId());
        final ProgressDialog progressDialog = new ProgressDialog(ViajeListActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(getString(R.string.progressFetchingViajes));
        progressDialog.show();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                viajesList = objects;
                for (final ParseObject viaj : viajesList) {
                    final ParseObject path = viaj.getParseObject(ParseConstants.Ruta.PATH.val());
                    try {
                        path.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject object, ParseException e) {
                                routeInLat = PolyUtil.decode(path.getString(ParseConstants.Path.DATA.val()));
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                Viaje v = new Viaje(routeInLat,
                                        viaj.getDouble(ParseConstants.Ruta.KM.val()),
                                        viaj.getDouble(ParseConstants.Ruta.TIME.val()),
                                        viaj.getDouble(ParseConstants.Ruta.CAL.val()),
                                        viaj.getDouble(ParseConstants.Ruta.VEL.val()),
                                        viaj.getParseGeoPoint(ParseConstants.Ruta.ORIGIN.val()),
                                        simpleDateFormat.format(viaj.getUpdatedAt()));
                                v.setId(viaj.getObjectId().toString());
                                viajes.add(v);
                                viajesAdapter.notifyDataSetChanged();
                            }
                        });
                    } catch (Exception m) {
                        m.printStackTrace();
                    }
                }
                progressDialog.dismiss();
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
    }

    public void startViajeDetailsActivity(Viaje viaje) {
        StaticThings.setSelectedViaje(viaje);
        Intent intent = new Intent(getApplicationContext(), ViajeDetailsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}

class ViajeItemClickListener implements RecyclerView.OnItemTouchListener {
    GestureDetector mGestureDetector;
    private OnItemClickListener mListener;

    public ViajeItemClickListener(Context context, OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
            return true;
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
