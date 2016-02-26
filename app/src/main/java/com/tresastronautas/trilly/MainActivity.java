package com.tresastronautas.trilly;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

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

    public static Bitmap downloadImageBitmap(String url) {
        Bitmap bm = null;
        try {
            URL aUrl = new URL(url);
            URLConnection conn = aUrl.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bm;
    }

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
        if (resultCode == 0) {
            finish();
        } else if (resultCode == 2) {
            getDetailsFromFacebook();
        } else if (resultCode == 1) {
            getDetailsFromParse();
        }
    }

    @Override
    public void onBackPressed() {
        if (!navigationExtended) {
            super.onBackPressed();
            finish();
        } else {
            actionNavBar(getCurrentFocus());
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
        main_texto_gas.setText(getString(R.string.main_gas_dinamico, 0));
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

    public void actionNavBar(View view) {
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
        if (navigationExtended) {
            FabTransformation.with(mFab)
                    .duration(30)
                    .transformFrom(menu_layout_navBar);
            navigationExtended = false;
        }
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("user_id", currentUser.getObjectId());
        startActivity(intent);
    }

    public void empezarViaje(View view) {
        if (navigationExtended) {
            actionNavBar(getCurrentFocus());
        }
    }

    public void verEstadisticas(View view) {
        if (navigationExtended) {
            actionNavBar(getCurrentFocus());
        } else {
            ParseUser.logOut();
            currentUser = null;
            checkUser();
        }
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
                        } catch (JSONException b) {
                            b.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
        ProfilePhotoAsync profilePhotoAsync = new ProfilePhotoAsync(fbProfile);
        profilePhotoAsync.execute();
    }

    public void getDetailsFromParse() {
        fbGet = false;
        fbProfile = Profile.getCurrentProfile();
        currentUser = ParseUser.getCurrentUser();
        firstName = currentUser.getString("nombre");
        lastName = currentUser.getString("apellido");
        main_texto_saludo.setText(getString(R.string.main_saludo, firstName));
        menu_texto_nombre.setText(firstName);
        id = currentUser.getString("facebookID");
        picture = currentUser.getString("picture");
        ProfilePhotoAsync profilePhotoAsync = new ProfilePhotoAsync(fbProfile);
        profilePhotoAsync.execute();
    }

    public void saveNewUser(String pic) {
        this.picture = pic;
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
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public class ProfilePhotoAsync extends AsyncTask<String, String, String> {

        public Bitmap bitmap;
        public String pic;
        Profile profile;

        ProfilePhotoAsync(Profile profile) {
            this.profile = profile == null ? Profile.getCurrentProfile() : profile;
        }

        @Override
        protected String doInBackground(String... strings) {
            pic = profile.getProfilePictureUri(500, 500).toString();
            bitmap = downloadImageBitmap(pic);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            menu_imagen_perfil = (CircleImageView) findViewById(R.id.menu_circle_profile);
            menu_imagen_perfil.setImageBitmap(bitmap);
            if (fbGet) saveNewUser(pic);
            fbGet = false;
        }
    }
}
