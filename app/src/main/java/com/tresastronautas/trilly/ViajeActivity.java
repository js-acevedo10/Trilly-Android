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
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ViajeActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, ResultCallback<Status> {

    public static final String TAG = ViajeActivity.class.getSimpleName();
    public static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 2301;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 23012;
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
    private Intent mIntentService;
    private PendingIntent mPendingIntent;
    private int contadorActionDialog = 0;
    private double metrosRecorridos = 0;
    private Location lActual;
    private Marker marker;

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
        mActivityBroadcastReceiver = new ActivityDetectionBroadcastReceiver();
        mLocationDetectionBroadcastReceiver = new LocationDetectionBroadcastReceiver();
        routePoints = new ArrayList<LatLng>();
        setupMap();
        mIntentService = new Intent(this, LocationService.class);
        mPendingIntent = PendingIntent.getService(this, 1, mIntentService, 0);
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
                .setSmallestDisplacement(8);
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
        stopViaje(getCurrentFocus());
    }

    //------------------------------------------------------------------------
    //-------------------------------- MAPAS ---------------------------------
    //------------------------------------------------------------------------

    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.mapa_imagen_punto_final);
        if (marker != null) {
            marker.remove();
        }
        marker = mMap.addMarker(new MarkerOptions().position(latLng).icon(icon));
        routePoints.add(latLng);
        route.setPoints(routePoints);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        calcularDistancia(location);
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

    private void setupMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
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
        Log.i(TAG, "Location services connected.");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    ViajeActivity.MY_PERMISSION_ACCESS_COARSE_LOCATION);
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, mPendingIntent);
        requestActivityUpdates();
        if (location == null) {

        } else {
            handleNewLocation(location);
            mMap.moveCamera(CameraUpdateFactory.zoomTo(zoom));
        }
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

    //------------------------------------------------------------------------
    //-------------------------------- LOGICA --------------------------------
    //------------------------------------------------------------------------

    public void prepareLayout() {
        viaje_texto_kilometros_dinamico = (TextView) findViewById(R.id.viaje_texto_kilometros_dinamico);
        viaje_texto_kilometros_dinamico.setText(getString(R.string.viaje_kilometros_dinamico, metrosRecorridos / 1000));
    }

    public void stopViaje(View view) {
        if (contadorActionDialog == 0) {
            contadorActionDialog = 1;
            new AlertDialog.Builder(ViajeActivity.this)
                    .setMessage("¿Deseas terminar el viaje?")
                    .setCancelable(false)
                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (mGoogleApiClient.isConnected()) {
                                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, mPendingIntent);
                                LocalBroadcastManager.getInstance(ViajeActivity.this).unregisterReceiver(mActivityBroadcastReceiver);
                                LocalBroadcastManager.getInstance(ViajeActivity.this).unregisterReceiver(mLocationDetectionBroadcastReceiver);
                                mGoogleApiClient.disconnect();
                            }
                            contadorActionDialog = 0;
                            finish();
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

    public void requestActivityUpdates() {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, "GoogleApiClient not yet connected", Toast.LENGTH_SHORT).show();
        } else {
            ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mGoogleApiClient, 0, getActivityDetectionPendingIntent()).setResultCallback(this);
        }
    }

    private PendingIntent getActivityDetectionPendingIntent() {
        Intent intent = new Intent(this, ActivitiesIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public String getDetectedActivity(int detectedActivityType) {
        Resources resources = this.getResources();
        switch (detectedActivityType) {
            case DetectedActivity.IN_VEHICLE:
                return resources.getString(R.string.in_vehicle);
            case DetectedActivity.ON_BICYCLE:
                return resources.getString(R.string.on_bicycle);
            case DetectedActivity.ON_FOOT:
                return resources.getString(R.string.on_foot);
            case DetectedActivity.RUNNING:
                return resources.getString(R.string.running);
            case DetectedActivity.WALKING:
                return resources.getString(R.string.walking);
            case DetectedActivity.STILL:
                return resources.getString(R.string.still);
            case DetectedActivity.TILTING:
                return resources.getString(R.string.tilting);
            case DetectedActivity.UNKNOWN:
                return resources.getString(R.string.unknown);
            default:
                return resources.getString(R.string.unidentifiable_activity, detectedActivityType);
        }
    }

    public class LocationDetectionBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location = intent.getParcelableExtra(Constants.LOCATION_EXTRA);
//            if (location != null && location.getAccuracy() <= 30) {
            if (location != null) {
                zoom = mMap.getCameraPosition().zoom >= 18 ? mMap.getCameraPosition().zoom : zoom;
                handleNewLocation(location);
            }
        }
    }

    public class ActivityDetectionBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<DetectedActivity> detectedActivities = intent.getParcelableArrayListExtra(Constants.STRING_EXTRA);
            String activityString = "";
            for (DetectedActivity activity : detectedActivities) {
                activityString += "Activity: " + getDetectedActivity(activity.getType()) + ", Confidence: " + activity.getConfidence() + "%\n";
                Log.d(TAG, "Activity: " + getDetectedActivity(activity.getType()) + ", Confidence: " + activity.getConfidence());
            }
        }
    }
}
