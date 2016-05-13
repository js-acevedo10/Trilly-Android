package com.tresastronautas.trilly;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;
import com.tresastronautas.trilly.Helpers.ParseConstants;
import com.tresastronautas.trilly.Helpers.StaticThings;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProfileActivity extends AppCompatActivity {

    private ParseUser currentUser;
    private TextView perfil_label_nombre, perfil_label_arboles_dinamico, perfil_label_kilometros;
    private CircleImageView perfil_circle_profile;
    private FloatingActionButton perfil_fab;
    private ParseObject statistics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/lato-regular-webfont.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
        setContentView(R.layout.activity_profile);
        currentUser = StaticThings.getCurrentUser();
        statistics = currentUser.getParseObject(ParseConstants.User.STATS.val());
        prepareLayout();
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
        perfil_label_arboles_dinamico.setText(getString(R.string.perfil_arboles_dinamico, Math.floor(statistics.getDouble(ParseConstants.Estadistica.SAVED_TREES.val()))));
        perfil_label_kilometros = (TextView) findViewById(R.id.perfil_label_kilometros);
        perfil_label_kilometros.setText(getString(R.string.perfil_kilometros, statistics.getDouble(ParseConstants.Estadistica.KM.val())));
        perfil_circle_profile = (CircleImageView) findViewById(R.id.perfil_circle_profile);
        Picasso.with(getApplicationContext())
                .load(currentUser.getString(ParseConstants.User.PIC.val()))
                .into(perfil_circle_profile);
    }

    public void startGroupListActivity(View view) {
        Intent intent = new Intent(this, GroupListActivity.class);
        StaticThings.setCurrentUser(currentUser);
        startActivity(intent);
    }

    public void profileBack(View view) {
        finish();
    }
}
