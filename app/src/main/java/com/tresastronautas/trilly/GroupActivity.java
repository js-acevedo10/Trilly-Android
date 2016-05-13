package com.tresastronautas.trilly;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.tresastronautas.trilly.Helpers.ExtendedImageButton;
import com.tresastronautas.trilly.Helpers.ParseConstants;
import com.tresastronautas.trilly.Helpers.StaticThings;

import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class GroupActivity extends AppCompatActivity {

    private ParseUser currentUser;
    private FloatingActionButton group_fab;
    private TextView group_text_nombre_grupo, group_text_arboles_dinamico, group_text_kilometros;
    private ExtendedImageButton group_boton_ver_miembros, group_boton_dejar_grupo, group_boton_unirsegrupo;
    private ParseObject selectedGroup;
    private List<ParseObject> userGroups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/lato-regular-webfont.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
        setContentView(R.layout.activity_group);
        currentUser = StaticThings.getCurrentUser();
        selectedGroup = StaticThings.getSelectedGroup();
        userGroups = StaticThings.getUserGroups();
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
        group_fab = (FloatingActionButton) findViewById(R.id.group_fab);
        group_fab.setImageDrawable(new IconDrawable(this, Iconify.IconValue.zmdi_chevron_left)
                .colorRes(android.R.color.white));

        group_text_nombre_grupo = (TextView) findViewById(R.id.group_text_nombre_grupo);
        group_text_arboles_dinamico = (TextView) findViewById(R.id.group_text_arboles_dinamico);
        group_text_kilometros = (TextView) findViewById(R.id.group_text_kilometros);

        group_boton_ver_miembros = (ExtendedImageButton) findViewById(R.id.group_boton_ver_miembros);
        group_boton_dejar_grupo = (ExtendedImageButton) findViewById(R.id.group_boton_dejar_grupo);
        group_boton_unirsegrupo = (ExtendedImageButton) findViewById(R.id.group_boton_unirsegrupo);

        if(userGroups.contains(selectedGroup)) {
            group_boton_dejar_grupo.setVisibility(View.VISIBLE);
            group_boton_unirsegrupo.setVisibility(View.GONE);
        }

        group_boton_dejar_grupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseRelation<ParseObject> relation = currentUser.getRelation(ParseConstants.User.GROUPS.val());
                relation.remove(selectedGroup);
                final ProgressDialog progressDialog = new ProgressDialog(GroupActivity.this);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setIndeterminate(false);
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setMessage(getString(R.string.progressLeavingUserGroup));
                progressDialog.show();
                currentUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        progressDialog.dismiss();
                        group_boton_dejar_grupo.setVisibility(View.GONE);
                        group_boton_unirsegrupo.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        group_boton_unirsegrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseRelation<ParseObject> relation = currentUser.getRelation(ParseConstants.User.GROUPS.val());
                relation.add(selectedGroup);
                final ProgressDialog progressDialog = new ProgressDialog(GroupActivity.this);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setIndeterminate(false);
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setMessage(getString(R.string.progressJoiningUserGroup));
                progressDialog.show();
                currentUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        progressDialog.dismiss();
                        group_boton_dejar_grupo.setVisibility(View.VISIBLE);
                        group_boton_unirsegrupo.setVisibility(View.GONE);
                    }
                });
            }
        });

        group_text_nombre_grupo.setText(selectedGroup.getString(ParseConstants.Grupo.GROUP_NAME.val()));
        group_text_arboles_dinamico.setText(getString(R.string.group_arboles_dinamico,
                selectedGroup.getDouble(ParseConstants.Grupo.SAVED_TREES.val())));
        group_text_kilometros.setText(getString(R.string.group_kilometros,
                selectedGroup.getDouble(ParseConstants.Grupo.KM.val())));
    }

    public void groupBack(View view) {
        if(group_boton_unirsegrupo.getVisibility() == View.VISIBLE) {
            setResult(10);
        } else {
            setResult(9);
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        setResult(10);
        super.onBackPressed();
    }
}
