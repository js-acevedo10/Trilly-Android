package com.tresastronautas.trilly;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class ViajeActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, ResultCallback<Status> {

    public static final String TAG = ViajeActivity.class.getSimpleName();
    public static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 2301;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 2302;
    private final static int MINIMO_METROS = 500;
    private final static int MAXIMO_ALERTAS = 5;
    private final static int MINIMO_CERTEZA_ACTIVIDAD = 60;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private List<LatLng> routePoints;
    private float zoom = 18f;
    private Polyline route;
    private TextView viaje_texto_kilometros_dinamico;
    private ParseUser currentUser;
    private ActivityDetectionBroadcastReceiver mActivityBroadcastReceiver;
    private LocationDetectionBroadcastReceiver mLocationDetectionBroadcastReceiver;
    private Intent locationIntent, activityIntent;
    private int contadorActionDialog = 0;
    private double metrosRecorridos = 0;
    private Location lActual;
    private Marker marker;
    private int alertaActividad = 0;
    private long startTime, endTime;
    private CheckpointViaje checkpointViaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viaje);
        String userID = getIntent().getStringExtra("user_id");
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.fromLocalDatastore();
        query.getInBackground(userID, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {
                    currentUser = object;
                }
            }
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    ViajeActivity.MY_PERMISSION_ACCESS_COARSE_LOCATION);
        }
        mActivityBroadcastReceiver = new ActivityDetectionBroadcastReceiver();
        mLocationDetectionBroadcastReceiver = new LocationDetectionBroadcastReceiver();
        routePoints = new ArrayList<LatLng>();
        setupMap();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(ActivityRecognition.API)
                .build();
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000)
                .setFastestInterval(16)
                .setSmallestDisplacement(5);
        prepareLayout();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(mActivityBroadcastReceiver, new IntentFilter(Constants.STRING_ACTION));
        LocalBroadcastManager.getInstance(this).registerReceiver(mLocationDetectionBroadcastReceiver, new IntentFilter(Constants.LOCATION_ACTION));
    }

    public void onResult(Status status) {
        if (status.isSuccess()) {
            Log.e(TAG, "Successfully added activity detection.");
        } else {
            Log.e(TAG, "Error: " + status.getStatusMessage());
        }
    }

    @Override
    public void onBackPressed() {
        finishDialog(getCurrentFocus());
    }

    private void setupMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.setMyLocationEnabled(true);
                if (ActivityCompat.checkSelfPermission(ViajeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ViajeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ViajeActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            ViajeActivity.MY_PERMISSION_ACCESS_COARSE_LOCATION);
                }
                route = mMap.addPolyline(new PolylineOptions());
                route.setWidth(20f);
                route.setColor(R.color.trillyLightBlue);
            }
        });
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Location & Activity services connected.");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    ViajeActivity.MY_PERMISSION_ACCESS_COARSE_LOCATION);
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, getLocationDetectionPendingIntent());
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mGoogleApiClient, 0, getActivityDetectionPendingIntent()).setResultCallback(this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        zoom = mMap.getCameraPosition().zoom;
        handleNewLocation(location);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------
    //-------------------------------- LOGICA --------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------------------------------------

    public void prepareLayout() {
        viaje_texto_kilometros_dinamico = (TextView) findViewById(R.id.viaje_texto_kilometros_dinamico);
        viaje_texto_kilometros_dinamico.setText(getString(R.string.viaje_kilometros_dinamico, 0.0));
    }

    public void finishDialog(View view) {
        if (contadorActionDialog == 0) {
            contadorActionDialog = 1;
            new AlertDialog.Builder(ViajeActivity.this)
                    .setMessage("¿Deseas terminar el viaje?")
                    .setCancelable(false)
                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            stopViaje();
                            contadorActionDialog = 0;
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            contadorActionDialog = 0;
                        }
                    })
                    .show();
        }
    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);
//        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.mapa_imagen_punto_final);
//        if (marker != null) {
//            marker.remove();
//        }
//        marker = mMap.addMarker(new MarkerOptions().position(latLng).icon(icon));
        startTime = SystemClock.elapsedRealtime();
        routePoints.add(latLng);
        route.setPoints(routePoints);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        calcularDistancia(location);
    }

    private void handleNewActivity(DetectedActivity activity) {
        Log.d(TAG, activity.toString());
        if (activity.getType() != DetectedActivity.ON_BICYCLE) {
            alertaActividad++;
            if (((SystemClock.elapsedRealtime() - startTime) / 1000.0) > 10.0) {
                checkpointViaje = new CheckpointViaje(startTime, SystemClock.elapsedRealtime(), metrosRecorridos, routePoints);
                Toast.makeText(ViajeActivity.this, "Parece que NO vas en bici", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Parece que NO vas en bici");
            }
        } else {
            Toast.makeText(ViajeActivity.this, "Parece que SI vas en bici", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Parece que SI vas en bici");
            if (alertaActividad > 0) alertaActividad--;
            if (alertaActividad <= 1) {
                checkpointViaje = null;
            }
        }
        if (alertaActividad > MAXIMO_ALERTAS) {
            stopViaje();
        }
    }

    public void calcularDistancia(Location lNueva) {
        if (lActual == null) {
            lActual = lNueva;
        } else {
            metrosRecorridos += lActual.distanceTo(lNueva);
            viaje_texto_kilometros_dinamico.setText(getString(R.string.viaje_kilometros_dinamico, metrosRecorridos / 1000));
            lActual = lNueva;
        }

    }

    public void stopViaje() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, getLocationDetectionPendingIntent());
            ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(mGoogleApiClient, getActivityDetectionPendingIntent());
            LocalBroadcastManager.getInstance(ViajeActivity.this).unregisterReceiver(mActivityBroadcastReceiver);
            LocalBroadcastManager.getInstance(ViajeActivity.this).unregisterReceiver(mLocationDetectionBroadcastReceiver);
            mGoogleApiClient.disconnect();
        }
        endTime = SystemClock.elapsedRealtime();
        guardarViaje();
        finish();
    }

    private void guardarViaje() {
        String mensaje = "";
        if(checkpointViaje == null) {
            if(metrosRecorridos > MINIMO_METROS) {
                double elapsedSeconds = (endTime - startTime) / 1000.0;
                double elapsedKMetros = metrosRecorridos / 1000.0;
                ParseObject path = new ParseObject(ParseConstants.Path.NAME.val());
                path.put(ParseConstants.Path.DATA.val(), RouteEncoder.getRouteInHex(routePoints));
                ParseObject route = new ParseObject(ParseConstants.Ruta.NAME.val());
                route.put(ParseConstants.Ruta.KM.val(), elapsedKMetros);
                route.put(ParseConstants.Ruta.CAL.val(), 0);
                route.put(ParseConstants.Ruta.PATH.val(), path);
                route.put(ParseConstants.Ruta.TIME.val(), elapsedSeconds);
                route.put(ParseConstants.Ruta.USER.val(), currentUser);
                route.put(ParseConstants.Ruta.ORIGIN.val(), new ParseGeoPoint(routePoints.get(0).latitude, routePoints.get(0).longitude));
                route.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        finish();
                    }
                });
                mensaje = "Carrera de " + elapsedSeconds + " segundos guardada.";
            } else {
                mensaje = "Ooops... Parece que tu viaje fue muy corto.";
            }
        } else {
            if(checkpointViaje.getMetros() > MINIMO_METROS) {
                double elapsedSeconds = checkpointViaje.getTotalTimeInSeconds();
                double elapsedKMetros = checkpointViaje.getMetros() / 1000.0;
                ParseObject path = new ParseObject(ParseConstants.Path.NAME.val());
                path.put(ParseConstants.Path.DATA.val(), RouteEncoder.getRouteInHex(routePoints));
                ParseObject route = new ParseObject(ParseConstants.Ruta.NAME.val());
                route.put(ParseConstants.Ruta.KM.val(), elapsedKMetros);
                route.put(ParseConstants.Ruta.CAL.val(), 0);
                route.put(ParseConstants.Ruta.PATH.val(), path);
                route.put(ParseConstants.Ruta.TIME.val(), elapsedSeconds);
                route.put(ParseConstants.Ruta.USER.val(), currentUser);
                route.put(ParseConstants.Ruta.ORIGIN.val(), new ParseGeoPoint(routePoints.get(0).latitude, routePoints.get(0).longitude));
                route.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        finish();
                    }
                });
                mensaje = "Carrera de " + elapsedSeconds + " segundos guardada.";
            } else {
                mensaje = "Ooops... Parece que tu viaje en bici fue muy corto.";
            }
        }
        Toast.makeText(ViajeActivity.this, mensaje, Toast.LENGTH_SHORT).show();
        Log.d(TAG, mensaje);
        contadorActionDialog = 0;
        metrosRecorridos = 0;
        checkpointViaje = null;
        startTime = 0;
        endTime = 0;
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------
    //-------------------------------- BROADCAST -----------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------------------------------------

    private PendingIntent getLocationDetectionPendingIntent() {
        if (locationIntent == null) {
            locationIntent = new Intent(this, ServiceLocationDetector.class);
        }
        return PendingIntent.getService(this, 0, locationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent getActivityDetectionPendingIntent() {
        if (activityIntent == null) {
            activityIntent = new Intent(this, ServiceActivityDetector.class);
        }
        return PendingIntent.getService(this, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public class LocationDetectionBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location = intent.getParcelableExtra(Constants.LOCATION_EXTRA);
            zoom = mMap.getCameraPosition().zoom >= 18 ? mMap.getCameraPosition().zoom : zoom;
            handleNewLocation(location);
        }
    }

    public class ActivityDetectionBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<DetectedActivity> detectedActivities = intent.getParcelableArrayListExtra(Constants.STRING_EXTRA);
            for (DetectedActivity activity : detectedActivities) {
                if (activity.getConfidence() > MINIMO_CERTEZA_ACTIVIDAD) {
                    handleNewActivity(activity);
                }
            }
        }
    }
}