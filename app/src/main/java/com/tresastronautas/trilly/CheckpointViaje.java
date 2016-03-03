package com.tresastronautas.trilly;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by juansantiagoacev on 3/1/16.
 */
public class CheckpointViaje {
    public long startTime, lastTime;
    public double metros;
    public String TAG = CheckpointViaje.class.getSimpleName();


    public List<LatLng> puntosRecorridos;

    public CheckpointViaje(long startTime, long lastTime, double metros, List<LatLng> puntosRecorridos) {
        this.lastTime = lastTime;
        this.startTime = startTime;
        this.metros = metros;
        this.puntosRecorridos = puntosRecorridos;
    }

    public double getTotalTimeInSeconds() {
        Log.d(TAG, "Checkpoint con: " + (lastTime - startTime) / 1000 + " segundos.");
        return (lastTime - startTime) / 1000;
    }

    public double getMetros() {
        Log.d(TAG, "Checkpoint con: " + metros + " metros.");
        return metros;
    }

    public List<LatLng> getPuntosRecorridos() {
        return puntosRecorridos;
    }
}
