package com.tresastronautas.trilly.Helpers;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

/**
 * Created by JuanSantiagoAcev on 28/02/16!
 */
public class ServiceActivityDetector extends IntentService {

    private static final String TAG = ServiceActivityDetector.class.getSimpleName();

    public ServiceActivityDetector() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
        Intent i = new Intent(Constants.STRING_ACTION);
        ArrayList<DetectedActivity> detectedActivities = (ArrayList<DetectedActivity>) result.getProbableActivities();
        i.putExtra(Constants.STRING_EXTRA, detectedActivities);
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }
}
