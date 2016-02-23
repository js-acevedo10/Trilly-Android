package com.tresastronautas.trilly;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;

/**
 * Created by JuanSantiagoAcev on 23/02/16.
 */
public class Trilly extends Application{

    private static final String APP_ID= "5k7poaTQLaV7KvDyKvNTdQ2hDlvENDrPIj0RxsG0";
    private static final String CLIENT_KEY= "biMQN5CkZErO1KxUl6Bv41T8q88Rrf8rv45nXmKd";
    private static final String SERVER_URI= "http://trilly-parse.herokuapp.com/parse/";

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                        .applicationId(APP_ID)
                        .clientKey(CLIENT_KEY)
                        .server(SERVER_URI)
                        .build()
        );
        ParseFacebookUtils.initialize(this);
    }
}
