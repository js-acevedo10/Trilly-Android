package com.tresastronautas.trilly;

import android.util.Log;

/**
 * Created by juansantiagoacev on 3/1/16.
 */
public class CheckpointViaje {
    public long startTime, lastTime;
    public double metros;
    public String TAG = CheckpointViaje.class.getSimpleName();

    public CheckpointViaje(long startTime, long lastTime, double metros) {
        this.lastTime = lastTime;
        this.startTime = startTime;
        this.metros = metros;
    }

    public double getTotalTimeInSeconds() {
        Log.d(TAG, "Checkpoint con: " + (lastTime - startTime) / 1000 + " segundos.");
        return (lastTime - startTime) / 1000;
    }

    public double getMetros() {
        Log.d(TAG, "Checkpoint con: " + metros + " metros.");
        return metros;
    }
}
