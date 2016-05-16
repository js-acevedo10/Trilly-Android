package com.tresastronautas.trilly;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.TextView;

import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.squareup.picasso.Picasso;
import com.tresastronautas.trilly.Helpers.ParseConstants;
import com.tresastronautas.trilly.Helpers.StaticThings;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProfileActivity extends AppCompatActivity {

    private TextView perfil_label_nombre, perfil_label_arboles_dinamico, perfil_label_kilometros;
    private CircleImageView perfil_circle_profile;
    private FloatingActionButton perfil_fab;
    private AppCompatButton perfil_boton_ver_grupos;
    private ParseObject statistics, selectedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/lato-regular-webfont.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
        setContentView(R.layout.activity_profile);
        selectedUser = StaticThings.getSelectedUser();
        statistics = selectedUser.getParseObject(ParseConstants.User.STATS.val());
        statistics.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                prepareLayout();
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
        perfil_label_nombre.setText(selectedUser.getString(ParseConstants.User.FIRST.val()));
        perfil_label_arboles_dinamico = (TextView) findViewById(R.id.perfil_label_arboles_dinamico);
        perfil_label_arboles_dinamico.setText(getString(R.string.perfil_arboles_dinamico, Math.floor(statistics.getDouble(ParseConstants.Estadistica.SAVED_TREES.val()))));
        perfil_label_kilometros = (TextView) findViewById(R.id.perfil_label_kilometros);
        perfil_label_kilometros.setText(getString(R.string.perfil_kilometros, statistics.getDouble(ParseConstants.Estadistica.KM.val())));
        perfil_circle_profile = (CircleImageView) findViewById(R.id.perfil_circle_profile);
        Picasso.with(getApplicationContext())
                .load(selectedUser.getString(ParseConstants.User.PIC.val()))
                .into(perfil_circle_profile);
        perfil_boton_ver_grupos = (AppCompatButton) findViewById(R.id.perfil_boton_ver_grupos);
        perfil_boton_ver_grupos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGroupListActivity(v);
            }
        });
    }

    public void startGroupListActivity(View view) {
        Intent intent = new Intent(this, GroupListActivity.class);
        StaticThings.setSelectedUser(selectedUser);
        startActivity(intent);
    }

    public void profileBack(View view) {
        finish();
    }
}
