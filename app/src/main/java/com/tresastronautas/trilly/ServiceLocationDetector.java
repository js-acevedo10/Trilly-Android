package com.tresastronautas.trilly;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.location.LocationResult;

/**
 * Created by JuanSantiagoAcev on 28/02/16!
 */
public class ServiceLocationDetector extends IntentService {

    private static final String TAG = ServiceLocationDetector.class.getSimpleName();

    public ServiceLocationDetector() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (LocationResult.hasResult(intent)) {
            LocationResult locationResult = LocationResult.extractResult(intent);
            Location location = locationResult.getLastLocation();
            if (location != null && location.getAccuracy() <= 40) {
                Intent i = new Intent(Constants.LOCATION_ACTION);
                i.putExtra(Constants.LOCATION_EXTRA, location);
                LocalBroadcastManager.getInstance(this).sendBroadcast(i);
            }
        }
    }

}
