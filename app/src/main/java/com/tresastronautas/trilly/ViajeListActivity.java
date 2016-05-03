package com.tresastronautas.trilly;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class ViajeListActivity extends AppCompatActivity {

    private ParseUser currentUser;
    private List<ParseObject> viajesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/lato-regular-webfont.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        setContentView(R.layout.activity_viaje_list);
        currentUser = StaticThings.getCurrentUser();
        ParseQuery<ParseObject> query = new ParseQuery(ParseConstants.Ruta.NAME.val());
        query.whereMatches(ParseConstants.Ruta.USER.val(), currentUser.getObjectId());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                viajesList = objects;
                Log.d("VIAJESLIST", viajesList.toString());
                ParseObject ruta = viajesList.get(0);
                ParseObject path = ruta.getParseObject(ParseConstants.Ruta.PATH.val());
                Log.d("Path", RouteDecoder.getRouteFromHex(ruta.getString(ParseConstants.Path.DATA.val())).get(0).toString());
            }
        });
        prepareLayout();
    }

    public void prepareLayout() {

    }
}
