package com.tresastronautas.trilly;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseGeoPoint;

import java.util.List;

/**
 * Created by juansantiagoacev on 5/5/16.
 */
public class Viaje {
    public List<LatLng> path;
    public double km, tiempo, cal, velPromedio;
    public ParseGeoPoint origen;

    public String fecha;

    public Viaje(List<LatLng> path, double km, double tiempo, double cal, double velPromedio, ParseGeoPoint origen, String fecha) {
        this.path = path;
        this.km = km;
        this.tiempo = tiempo;
        this.cal = cal;
        this.velPromedio = velPromedio;
        this.origen = origen;
        this.fecha = fecha;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public List<LatLng> getPath() {
        return path;
    }

    public void setPath(List<LatLng> path) {
        this.path = path;
    }

    public double getKm() {
        return km;
    }

    public void setKm(double km) {
        km = km;
    }

    public double getTiempo() {
        return tiempo;
    }

    public void setTiempo(double tiempo) {
        this.tiempo = tiempo;
    }

    public double getCal() {
        return cal;
    }

    public void setCal(double cal) {
        this.cal = cal;
    }

    public double getVelPromedio() {
        return velPromedio;
    }

    public void setVelPromedio(double velPromedio) {
        this.velPromedio = velPromedio;
    }

    public ParseGeoPoint getOrigen() {
        return origen;
    }

    public void setOrigen(ParseGeoPoint origen) {
        this.origen = origen;
    }
}
