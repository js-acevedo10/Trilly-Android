package com.tresastronautas.trilly;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.location.LocationResult;

/**
 * Created by JuanSantiagoAcev on 28/02/16!
 */
public class LocationService extends IntentService {

    private String TAG = this.getClass().getSimpleName();

    public LocationService() {
        super("Fused Location");
    }

    public LocationService(String name) {
        super("Fused Location");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (LocationResult.hasResult(intent)) {
            LocationResult locationResult = LocationResult.extractResult(intent);
            Location location = locationResult.getLastLocation();
            if (location != null) {
                Intent i = new Intent(Constants.LOCATION_ACTION);
                i.putExtra(Constants.LOCATION_EXTRA, location);
                LocalBroadcastManager.getInstance(this).sendBroadcast(i);
//                Log.d(TAG, location.toString());
            }
        }
    }

}
