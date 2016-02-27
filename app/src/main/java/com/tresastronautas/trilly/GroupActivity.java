package com.tresastronautas.trilly;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.facebook.appevents.AppEventsLogger;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class GroupActivity extends AppCompatActivity {

    private ParseUser currentUser;
    private TextView group_text_nombre_grupo, group_text_arboles_dinamico, group_text_kilometros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/lato-regular-webfont.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
        setContentView(R.layout.activity_group);
        String userID = getIntent().getStringExtra("user_id");
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.fromLocalDatastore();
        query.getInBackground(userID, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {
                    currentUser = object;
                    prepareLayout();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void prepareLayout() {
        group_text_nombre_grupo = (TextView) findViewById(R.id.group_text_nombre_grupo);
        group_text_arboles_dinamico = (TextView) findViewById(R.id.group_text_arboles_dinamico);
        group_text_arboles_dinamico.setText(getString(R.string.group_arboles_dinamico, 0));
        group_text_kilometros = (TextView) findViewById(R.id.group_text_kilometros);
        group_text_kilometros.setText(getString(R.string.group_kilometros, 0));
    }

    public void groupBack(View view) {
        finish();
    }
}
