package com.tresastronautas.trilly;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.konifar.fab_transformation.FabTransformation;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.natasa.progressviews.LineProgressBar;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    public String firstName, lastName, id, picture;
    public Profile fbProfile;
    public ParseUser currentUser;
    public CircleImageView menu_imagen_perfil;
    public TextView main_texto_saludo, menu_texto_nombre, main_texto_gas, main_texto_arboles;
    public LineProgressBar main_progressBar;
    public FloatingActionButton mFab;
    public LinearLayout menu_layout_navBar, menu_background_navBar;
    public ExtendedButton menu_boton_grupo, menu_boton_viajes, menu_boton_estadisticas, menu_boton_ajustes;
    public Boolean navigationExtended = false;
    public boolean fbGet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/lato-regular-webfont.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
        checkUser();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        prepareLayout();
        if (resultCode == LoginActivity.NUEVO_USUARIO) {
            getDetailsFromFacebook();
        } else if (resultCode == LoginActivity.VIEJO_USUARIO) {
            getDetailsFromParse();
        } else if (resultCode == AjustesActivity.RESULT_CERRAR_SESION) {
            cerrarSesion();
        } else if (resultCode == Activity.RESULT_CANCELED) {

        }
    }

    @Override
    public void onBackPressed() {
        if (!navigationExtended) {
            super.onBackPressed();
            finish();
        } else {
            closeNavBar(getCurrentFocus());
        }
    }

    public void prepareLayout() {
        setContentView(R.layout.activity_main);
        menu_imagen_perfil = (CircleImageView) findViewById(R.id.menu_circle_profile);
        main_texto_saludo = (TextView) findViewById(R.id.perfil_label_nombre);
        menu_texto_nombre = (TextView) findViewById(R.id.menu_texto_nombre);
        main_texto_arboles = (TextView) findViewById(R.id.main_label_dynamic_arboles);
        main_texto_arboles.setText(getString(R.string.main_arboles_dinamico, 0));
        main_texto_gas = (TextView) findViewById(R.id.main_label_dynamic_gasolina);
        main_texto_gas.setText(getString(R.string.main_gas_dinamico, 0.0));
        main_progressBar = (LineProgressBar) findViewById(R.id.main_progress_horizontal);
        menu_background_navBar = (LinearLayout) findViewById(R.id.menu_navBar);
        menu_boton_ajustes = (ExtendedButton) findViewById(R.id.menu_boton_ajustes);
        menu_boton_estadisticas = (ExtendedButton) findViewById(R.id.menu_boton_estadisticas);
        menu_boton_grupo = (ExtendedButton) findViewById(R.id._menu_boton_grupo);
        menu_boton_viajes = (ExtendedButton) findViewById(R.id.menu_boton_viajes);
        mFab = (FloatingActionButton) findViewById(R.id.main_fab);
        mFab.setImageDrawable(new IconDrawable(this, Iconify.IconValue.zmdi_menu).colorRes(android.R.color.white));
        menu_layout_navBar = (LinearLayout) findViewById(R.id.menu_navBar);
        menu_layout_navBar.setBackgroundResource(R.drawable.menu_imagen_bg);
        menu_layout_navBar.setVisibility(View.INVISIBLE);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int p = menu_background_navBar.getWidth();
                menu_boton_ajustes.setWidth(p);
                menu_boton_estadisticas.setWidth(p);
                menu_boton_grupo.setWidth(p);
                menu_boton_viajes.setWidth(p);
                FabTransformation.with(mFab)
                        .duration(30)
                        .transformTo(menu_layout_navBar);
                navigationExtended = true;
            }
        });
    }

    public void closeNavBar(View view) {
        if (navigationExtended) {
            FabTransformation.with(mFab)
                    .duration(30)
                    .transformFrom(menu_layout_navBar);
            navigationExtended = false;
        }
    }

    public void checkUser() {
        if (ParseUser.getCurrentUser() != null) {
            prepareLayout();
            getDetailsFromParse();
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, 1);
            currentUser = ParseUser.getCurrentUser();
        }
    }

    //----------------------------------------------------------------------------------------------
    //-------------------------------------BUTTON METHODS-------------------------------------------
    //----------------------------------------------------------------------------------------------

    public void startPerfilActivity(View view) {
        closeNavBar(getCurrentFocus());
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("user_id", currentUser.getObjectId());
        startActivity(intent);
    }

    public void startGroupListActivity(View view) {
        closeNavBar(getCurrentFocus());
        Intent intent = new Intent(this, GroupListActivity.class);
        intent.putExtra("user_id", currentUser.getObjectId());
        startActivity(intent);
    }

    public void startAjustesActivity(View view) {
        closeNavBar(getCurrentFocus());
        Intent intent = new Intent(this, AjustesActivity.class);
        intent.putExtra("user_id", currentUser.getObjectId());
        startActivityForResult(intent, 1);
    }

    public void startViajeActivity(View view) {
        if (navigationExtended) {
            closeNavBar(getCurrentFocus());
        } else {
            Intent intent = new Intent(this, ViajeActivity.class);
            intent.putExtra("user_id", currentUser.getObjectId());
            startActivity(intent);
        }
    }

    public void startEstadisticasActivity(View view) {
        if (navigationExtended) {
            closeNavBar(getCurrentFocus());
        } else {

        }
    }

    public void cerrarSesion() {
        ParseUser.logOut();
        currentUser = null;
        checkUser();
    }

    //----------------------------------------------------------------------------------------------
    //-------------------------------------PARSE METHODS--------------------------------------------
    //----------------------------------------------------------------------------------------------

    public void getDetailsFromFacebook() {
        fbGet = true;
        fbProfile = Profile.getCurrentProfile();
        Bundle permissions = new Bundle();
        permissions.putString("fields", "id, firstName, email");
        fbProfile = Profile.getCurrentProfile();
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse graphResponse) {
                            Log.d("JSON", graphResponse.getRawResponse());
                        try {
                            id = graphResponse.getJSONObject().getString("id");
                            String n = graphResponse.getJSONObject().getString("name");
                            firstName = n.split(" ").length > 0 ? n.split(" ")[0] : n;
                            if (!firstName.equals(n)) {
                                lastName = n.substring(firstName.toCharArray().length + 1, n.toCharArray().length);
                            } else {
                                lastName = " ";
                            }
                            main_texto_saludo.setText(getString(R.string.main_saludo, firstName));
                            menu_texto_nombre.setText(firstName);
                            saveNewUser();
                        } catch (JSONException b) {
                            b.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
    }

    public void getDetailsFromParse() {
        fbGet = false;
        fbProfile = Profile.getCurrentProfile();
        currentUser = ParseUser.getCurrentUser();
        firstName = currentUser.getString(ParseConstants.User.FIRST.val());
        lastName = currentUser.getString(ParseConstants.User.LAST.val());
        main_texto_saludo.setText(getString(R.string.main_saludo, firstName));
        menu_texto_nombre.setText(firstName);
        id = currentUser.getString(ParseConstants.User.FBID.val());
        picture = currentUser.getString(ParseConstants.User.PIC.val());
        Picasso.with(getApplicationContext())
                .load(picture)
                .into(menu_imagen_perfil);
    }

    public void saveNewUser() {
        picture = fbProfile.getProfilePictureUri(500, 500).toString();
        Picasso.with(getApplicationContext()).load(picture).into(menu_imagen_perfil);
        currentUser = ParseUser.getCurrentUser();
        currentUser.put(ParseConstants.User.FBID.val(), id);
        currentUser.put(ParseConstants.User.FIRST.val(), firstName);
        currentUser.put(ParseConstants.User.LAST.val(), lastName);
        currentUser.put(ParseConstants.User.PIC.val(), picture);
        currentUser.put(ParseConstants.User.KG.val(), 0);
        ParseObject statistics = new ParseObject(ParseConstants.Estadistica.NAME.val());
        statistics.put(ParseConstants.Estadistica.USER.val(), currentUser);
        statistics.put(ParseConstants.Estadistica.KM.val(), 0);
        statistics.put(ParseConstants.Estadistica.TIME.val(), 0);
        statistics.put(ParseConstants.Estadistica.CAL.val(), 0);
        statistics.put(ParseConstants.Estadistica.CO2.val(), 0);
        statistics.put(ParseConstants.Estadistica.CURRENT_TREE.val(), 0);
        statistics.put(ParseConstants.Estadistica.SAVED_TREES.val(), 0);
        statistics.put(ParseConstants.Estadistica.GAS.val(), 0);
        statistics.put(ParseConstants.Estadistica.MONEY.val(), 0);
        currentUser.put(ParseConstants.User.STATS.val(), statistics);
        try {
            currentUser.save();
            currentUser.pin(currentUser.getObjectId());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
