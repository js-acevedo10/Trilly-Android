package com.tresastronautas.trilly;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.konifar.fab_transformation.FabTransformation;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.natasa.progressviews.LineProgressBar;
import com.parse.GetCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static final int LOCATION_PERMISSION = 2303;
    public String firstName, lastName, id, picture;
    public Profile fbProfile;
    public ParseUser currentUser;
    public CircleImageView menu_imagen_perfil;
    public TextView main_texto_saludo, menu_texto_nombre, main_texto_gas, main_texto_arboles, main_texto_porcentaje;
    public LineProgressBar main_progressBar;
    public FloatingActionButton mFab;
    public LinearLayout menu_layout_navBar, menu_background_navBar;
    public ExtendedButton menu_boton_grupo, menu_boton_viajes, menu_boton_estadisticas, menu_boton_ajustes;
    public Boolean navigationExtended = false;
    public boolean fbGet;
    public String[] perms = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS};
    private ParseObject statistics;
    private ViewSwitcher viewSwitcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/lato-regular-webfont.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
        checkUser();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Some permissions have been granted
        // ...
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Some permissions have been denied
        // ...
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        currentUser = StaticThings.getCurrentUser();
        if (resultCode == LoginActivity.NUEVO_USUARIO) {
            getDetailsFromFacebook();
            loadView x = new loadView();
            x.execute();
        } else if (resultCode == LoginActivity.VIEJO_USUARIO || resultCode == ViajeActivity.VIAJE_GUARDADO) {
            getDetailsFromParse();
            loadView x = new loadView();
            x.execute();
        } else if (resultCode == AjustesActivity.RESULT_CERRAR_SESION) {
            cerrarSesion();
        } else if (resultCode == LoginActivity.CERRAR_EJECUCION) {
            finish();
        } else {
            checkStats();
        }
    }

    @Override
    public void onBackPressed() {
        if (viewSwitcher.getDisplayedChild() != 0) {
            if (!navigationExtended) {
                super.onBackPressed();
                finish();
            } else {
                closeNavBar(getCurrentFocus());
            }
        }
    }

    public void prepareLayout() {
        menu_imagen_perfil = (CircleImageView) findViewById(R.id.menu_circle_profile);
        Picasso.with(getApplicationContext())
                .load(picture)
                .into(menu_imagen_perfil);
        main_texto_saludo = (TextView) findViewById(R.id.perfil_label_nombre);
        menu_texto_nombre = (TextView) findViewById(R.id.menu_texto_nombre);
        main_texto_arboles = (TextView) findViewById(R.id.main_label_dynamic_arboles);
        main_texto_gas = (TextView) findViewById(R.id.main_label_dynamic_gasolina);
        main_progressBar = (LineProgressBar) findViewById(R.id.main_progress_horizontal);
        main_texto_porcentaje = (TextView) findViewById(R.id.main_texto_porcentaje);
        menu_background_navBar = (LinearLayout) findViewById(R.id.menu_navBar);
        menu_boton_ajustes = (ExtendedButton) findViewById(R.id.menu_boton_ajustes);
        menu_boton_estadisticas = (ExtendedButton) findViewById(R.id.menu_boton_estadisticas);
        menu_boton_grupo = (ExtendedButton) findViewById(R.id._menu_boton_grupo);
        menu_boton_viajes = (ExtendedButton) findViewById(R.id.menu_boton_viajes);
        mFab = (FloatingActionButton) findViewById(R.id.main_fab);
        menu_layout_navBar = (LinearLayout) findViewById(R.id.menu_navBar);

        main_texto_saludo.setText(getString(R.string.main_saludo, firstName));
        menu_texto_nombre.setText(firstName);
        main_texto_arboles.setText(getString(R.string.main_arboles_dinamico, statistics.getDouble(ParseConstants.Estadistica.SAVED_TREES.val())));
        main_texto_gas.setText(getString(R.string.main_gas_dinamico, statistics.getDouble(ParseConstants.Estadistica.GAS.val())));
        main_progressBar.setProgress((float) statistics.getDouble(ParseConstants.Estadistica.CURRENT_TREE.val()));
        main_texto_porcentaje.setText(getString(R.string.main_porcentaje, statistics.getDouble(ParseConstants.Estadistica.CURRENT_TREE.val())) + "%");
        mFab.setImageDrawable(new IconDrawable(this, Iconify.IconValue.zmdi_menu).colorRes(android.R.color.white));
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
        checkPermissions();
    }

    public void checkStats() {
        currentUser.fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                statistics = currentUser.getParseObject(ParseConstants.User.STATS.val());
                main_texto_arboles.setText(getString(R.string.main_arboles_dinamico, statistics.getDouble(ParseConstants.Estadistica.SAVED_TREES.val())));
                main_texto_gas.setText(getString(R.string.main_gas_dinamico, statistics.getDouble(ParseConstants.Estadistica.GAS.val())));
                main_progressBar.setProgress((float) statistics.getDouble(ParseConstants.Estadistica.CURRENT_TREE.val()));
                main_texto_porcentaje.setText(getString(R.string.main_porcentaje, statistics.getDouble(ParseConstants.Estadistica.CURRENT_TREE.val())) + "%");
            }
        });
    }

    @AfterPermissionGranted(LOCATION_PERMISSION)
    public void checkPermissions() {
        if (!EasyPermissions.hasPermissions(getApplicationContext(), perms)) {
            EasyPermissions.requestPermissions(this, getString(R.string.permission_location),
                    LOCATION_PERMISSION, perms);
        }
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
            currentUser = ParseUser.getCurrentUser();
            StaticThings.setCurrentUser(currentUser);
            loadView x = new loadView();
            x.execute();
            getDetailsFromParse();
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, 1);
        }
    }

    public void startPerfilActivity(View view) {
        closeNavBar(getCurrentFocus());
        Intent intent = new Intent(this, ProfileActivity.class);
        StaticThings.setCurrentUser(currentUser);
        intent.putExtra("user_id", currentUser.getObjectId());
        startActivity(intent);
    }

    //----------------------------------------------------------------------------------------------
    //-------------------------------------BUTTON METHODS-------------------------------------------
    //----------------------------------------------------------------------------------------------

    public void startGroupListActivity(View view) {
        closeNavBar(getCurrentFocus());
        Intent intent = new Intent(this, GroupListActivity.class);
        StaticThings.setCurrentUser(currentUser);
        startActivityForResult(intent, 1);
    }

    public void startAjustesActivity(View view) {
        closeNavBar(getCurrentFocus());
        Intent intent = new Intent(this, AjustesActivity.class);
        StaticThings.setCurrentUser(currentUser);
        intent.putExtra("user_id", currentUser.getObjectId());
        startActivityForResult(intent, 1);
    }

    public void startViajeActivity(View view) {
        if (navigationExtended) {
            closeNavBar(getCurrentFocus());
        } else {
            Intent intent = new Intent(this, ViajeActivity.class);
            StaticThings.setCurrentUser(currentUser);
            startActivityForResult(intent, 1);
        }
    }

    public void startEstadisticasActivity(View view) {
        if (navigationExtended) {
            closeNavBar(getCurrentFocus());
        } else {
            Intent intent = new Intent(this, EstadisticasActivity.class);
            StaticThings.setCurrentUser(currentUser);
            startActivityForResult(intent, 1);
        }
    }

    public void startViajeListActivity(View view) {
        if (navigationExtended) {
            closeNavBar(getCurrentFocus());
        } else {
            Intent intent = new Intent(this, ViajeListActivity.class);
            StaticThings.setCurrentUser(currentUser);
            startActivity(intent);
        }
    }

    public void cerrarSesion() {
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(getString(R.string.progressSignOut));
        progressDialog.show();
        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                currentUser = null;
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivityForResult(intent, 1);
                progressDialog.dismiss();
            }
        });
    }

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
                            saveNewUser();
                        } catch (JSONException b) {
                            b.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
    }

    //----------------------------------------------------------------------------------------------
    //-------------------------------------PARSE METHODS--------------------------------------------
    //----------------------------------------------------------------------------------------------

    public void getDetailsFromParse() {
        fbGet = false;
        fbProfile = Profile.getCurrentProfile();
        currentUser = ParseUser.getCurrentUser();
        StaticThings.setCurrentUser(currentUser);
        firstName = currentUser.getString(ParseConstants.User.FIRST.val());
        lastName = currentUser.getString(ParseConstants.User.LAST.val());
        id = currentUser.getString(ParseConstants.User.FBID.val());
        picture = currentUser.getString(ParseConstants.User.PIC.val());
    }

    public void saveNewUser() {
        picture = fbProfile.getProfilePictureUri(500, 500).toString();
        currentUser = ParseUser.getCurrentUser();
        currentUser.put(ParseConstants.User.FBID.val(), id);
        currentUser.put(ParseConstants.User.FIRST.val(), firstName);
        currentUser.put(ParseConstants.User.LAST.val(), lastName);
        currentUser.put(ParseConstants.User.PIC.val(), picture);
        currentUser.put(ParseConstants.User.KG.val(), 60);
        currentUser.put(ParseConstants.User.EDAD.val(), 20);
        currentUser.put(ParseConstants.User.ALTURA.val(), 1.7);
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
            final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage(getString(R.string.progressChangingWorld));
            progressDialog.show();
            currentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    progressDialog.dismiss();
                }
            });
            currentUser.pin(currentUser.getObjectId());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        StaticThings.setCurrentUser(currentUser);
    }

    private class loadView extends AsyncTask {
        @Override
        protected void onPreExecute() {
            viewSwitcher = new ViewSwitcher(MainActivity.this);
            viewSwitcher.addView(ViewSwitcher.inflate(MainActivity.this, R.layout.layout_splash_screen, null));
            setContentView(viewSwitcher);
            final ImageView bici = (ImageView) viewSwitcher.findViewById(R.id.splash_imagen_bici);
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.nubes_traslacion);
            animation.setRepeatMode(Animation.INFINITE);
            animation.setDuration(4000);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    bici.startAnimation(animation);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            bici.startAnimation(animation);
        }

        @Override
        protected Object doInBackground(Object[] params) {
            currentUser = ParseUser.getCurrentUser();
            statistics = currentUser.getParseObject(ParseConstants.User.STATS.val());
            try {
                statistics.fetch();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                //Get the current thread's token
                synchronized (this) {
                    //Initialize an integer (that will act as a counter) to zero
                    int counter = 0;
                    //While the counter is smaller than four
                    while (counter <= 3) {
                        //Wait 850 milliseconds
                        this.wait(850);
                        //Increment the counter
                        counter++;
                        //Set the current progress.
                        //This value is going to be passed to the onProgressUpdate() method.
                        publishProgress(counter * 25);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return currentUser;
        }

        @Override
        protected void onPostExecute(Object o) {
            viewSwitcher.addView(ViewSwitcher.inflate(MainActivity.this, R.layout.activity_main, null));
            viewSwitcher.showNext();
            prepareLayout();
        }
    }
}
