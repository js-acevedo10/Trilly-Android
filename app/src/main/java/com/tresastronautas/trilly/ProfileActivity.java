package com.tresastronautas.trilly;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.facebook.appevents.AppEventsLogger;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProfileActivity extends AppCompatActivity {

    private ParseUser currentUser;
    private TextView perfil_label_nombre, perfil_label_arboles_dinamico, perfil_label_kilometros;
    private CircleImageView perfil_circle_profile;
    private FloatingActionButton perfil_fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/lato-regular-webfont.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
        setContentView(R.layout.activity_profile);
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
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void prepareLayout() {
        perfil_fab = (FloatingActionButton) findViewById(R.id.perfil_fab);
        perfil_fab.setImageDrawable(new IconDrawable(this, Iconify.IconValue.zmdi_chevron_left).colorRes(android.R.color.white));
        perfil_label_nombre = (TextView) findViewById(R.id.perfil_label_nombre);
        perfil_label_nombre.setText(currentUser.getString(ParseConstants.User.FIRST.val()));
        perfil_label_arboles_dinamico = (TextView) findViewById(R.id.perfil_label_arboles_dinamico);
        perfil_label_arboles_dinamico.setText(getString(R.string.perfil_arboles_dinamico, currentUser.getDouble(ParseConstants.Estadistica.SAVED_TREES.val())));
        perfil_label_kilometros = (TextView) findViewById(R.id.perfil_label_kilometros);
        perfil_label_kilometros.setText(getString(R.string.perfil_kilometros, currentUser.getDouble(ParseConstants.Estadistica.KM.val())));
        perfil_circle_profile = (CircleImageView) findViewById(R.id.perfil_circle_profile);
        Picasso.with(getApplicationContext())
                .load(currentUser.getString(ParseConstants.User.PIC.val()))
                .into(perfil_circle_profile);
    }

    public void startGroupListActivity(View view) {
        Intent intent = new Intent(this, GroupListActivity.class);
        intent.putExtra("user_id", currentUser.getObjectId());
        startActivity(intent);
    }

    public void profileBack(View view) {
        finish();
    }
}
