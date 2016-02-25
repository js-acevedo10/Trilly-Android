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

    public String email, firstName, lastName, id, picture;
    public Profile fbProfile;
    public ParseUser currentUser;
    public CircleImageView menu_imagen_perfil;
    public TextView main_texto_saludo, menu_texto_nombre;
    public LineProgressBar progressBar;
    public boolean fbGet;
    public FloatingActionButton mFab;
    public LinearLayout mNavBar, fNavBar;
    public Boolean navExtended = false;
    public ExtendedButton bgrupo, bviajes, bestadisticas, bajustes;

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
        if (!navExtended) {
            super.onBackPressed();
            finish();
        } else {
            actionNavBar(getCurrentFocus());
        }
    }

    public void prepareLayout() {
        setContentView(R.layout.activity_main);
        menu_imagen_perfil = (CircleImageView) findViewById(R.id.menu_circle_profile);
        main_texto_saludo = (TextView) findViewById(R.id.label_nombre_main);
        menu_texto_nombre = (TextView) findViewById(R.id.menu_texto_nombre);
        progressBar = (LineProgressBar) findViewById(R.id.horizontal_progress_main);
        fNavBar = (LinearLayout) findViewById(R.id.nav_bar_main);
        bajustes = (ExtendedButton) findViewById(R.id.menu_boton_ajustes);
        bestadisticas = (ExtendedButton) findViewById(R.id.menu_boton_estadisticas);
        bgrupo = (ExtendedButton) findViewById(R.id._menu_boton_grupo);
        bviajes = (ExtendedButton) findViewById(R.id.menu_boton_viajes);
        mFab = (FloatingActionButton) findViewById(R.id.fab_main);
        mFab.setImageDrawable(new IconDrawable(this, Iconify.IconValue.zmdi_menu).colorRes(android.R.color.white));
        mNavBar = (LinearLayout) findViewById(R.id.nav_bar_main);
        mNavBar.setBackgroundResource(R.drawable.menu_imagen_bg);
        mNavBar.setVisibility(View.INVISIBLE);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int p = fNavBar.getWidth();
                bajustes.setWidth(p);
                bestadisticas.setWidth(p);
                bgrupo.setWidth(p);
                bviajes.setWidth(p);
                FabTransformation.with(mFab)
                        .duration(30)
//                        .setListener(new FabTransformation.OnTransformListener() {
//                            @Override
//                            public void onStartTransform() {
//
//                            }
//
//                            @Override
//                            public void onEndTransform() {
//                                Blurry.with(getApplicationContext()).radius(25).sampling(2).onto((ViewGroup) findViewById(R.id.main_full_layout));
//                            }
//                        })
                        .transformTo(mNavBar);
                navExtended = true;
            }
        });
    }

    public void actionNavBar(View view) {
        if (navExtended) {
            FabTransformation.with(mFab)
                    .duration(30)
//                    .setListener(new FabTransformation.OnTransformListener() {
//                        @Override
//                        public void onStartTransform() {
//
//                        }
//
//                        @Override
//                        public void onEndTransform() {
//                            Blurry.delete((ViewGroup) findViewById(R.id.main_full_layout));
//                        }
//                    })
                    .transformFrom(mNavBar);
            navExtended = false;
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

    public void empezarViaje(View view) {
        if (navExtended) {
            actionNavBar(getCurrentFocus());
        }
    }

    public void verEstadisticas(View view) {
        if (navExtended) {
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
                        try {
                            Log.d("JSON", graphResponse.getRawResponse());
                            email = graphResponse.getJSONObject().getString("email");
                        } catch (JSONException a) {
                            a.printStackTrace();
                        }
                        try {
                            id = graphResponse.getJSONObject().getString("id");
                            String n = graphResponse.getJSONObject().getString("name");
                            firstName = n.split(" ").length > 0 ? n.split(" ")[0] : n;
                            if (!firstName.equals(n)) {
                                lastName = n.substring(firstName.toCharArray().length + 1, n.toCharArray().length);
                            } else {
                                lastName = " ";
                            }
                            main_texto_saludo.setText(R.string.main_saludo + " " + firstName + "!");
                            menu_texto_nombre.setText(firstName);
                        } catch(JSONException b) {
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
        main_texto_saludo.setText("Hola " + firstName + "!");
        menu_texto_nombre.setText(firstName);
        email = currentUser.getEmail()!=null?currentUser.getEmail():"no@email.com";
        id = currentUser.getString("facebookID");
        picture = currentUser.getString("picture");
        ProfilePhotoAsync profilePhotoAsync = new ProfilePhotoAsync(fbProfile);
        profilePhotoAsync.execute();
    }

    public void saveNewUser(String pic) {
        this.picture = pic;
        currentUser = ParseUser.getCurrentUser();
        currentUser.put("facebookID", id);
        currentUser.put("nombre", firstName);
        currentUser.put("apellido", lastName);
        currentUser.put("picture", picture);
        if(email != null) {
            currentUser.setEmail(email);
        }
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
