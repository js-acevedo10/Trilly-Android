package com.tresastronautas.trilly.ListAdapters;

import com.parse.ParseObject;

/**
 * Created by JuanSantiagoAcev on 15/05/16!
 */
public class Member implements Comparable<Member> {

    public String nombre, id, facebookId, googleId, pictureUrl;
    public double arboles;
    public ParseObject statistics;

    public Member() {
    }

    public Member(String nombre, String id, String facebookId, String googleId, String pictureUrl, double arboles) {
        this.nombre = nombre;
        this.id = id;
        this.facebookId = facebookId;
        this.googleId = googleId;
        this.pictureUrl = pictureUrl;
        this.arboles = arboles;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public double getArboles() {
        return arboles;
    }

    public void setArboles(double arboles) {
        this.arboles = arboles;
    }

    public ParseObject getStatistics() {
        return statistics;
    }

    public void setStatistics(ParseObject statistics) {
        this.statistics = statistics;
    }

    @Override
    public int compareTo(Member another) {
        if (this.getArboles() > another.getArboles()) {
            return -1;
        } else if (this.getArboles() < another.getArboles()) {
            return 1;
        }
        return 0;
    }
}
