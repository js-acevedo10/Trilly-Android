package com.tresastronautas.trilly;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    public static final int NUEVO_USUARIO = 1801;
    public static final int VIEJO_USUARIO = 1802;
    public static final int CERRAR_EJECUCION = 1803;
    public List<String> fbPermissions;
    public ImageView login_imagen_nube_1, login_imagen_nube_2;
    public AppCompatButton login_boton_facebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login_imagen_nube_1 = (ImageView) findViewById(R.id.login_imagen_nube_1);
        login_imagen_nube_2 = (ImageView) findViewById(R.id.login_imagen_nube_2);
        login_boton_facebook = (AppCompatButton) findViewById(R.id.login_boton_facebook);
        login_boton_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginWithFB(v);
            }
        });
        Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.nubes_traslacion);
        Animation animation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.nubes_traslacion);
        animation1.setRepeatCount(Animation.INFINITE);
        animation2.setRepeatCount(Animation.INFINITE);
        animation2.setStartOffset(900);
        animation1.setDuration(8000);
        animation2.setDuration(4000);
        login_imagen_nube_1.startAnimation(animation1);
        login_imagen_nube_2.startAnimation(animation2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        setResult(CERRAR_EJECUCION);
        finish();
    }

    public void loginWithFB(View view) {

        fbPermissions = Arrays.asList(getResources().getStringArray(R.array.my_facebook_permissions));
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(getString(R.string.progressLogin));
        progressDialog.show();
        ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginActivity.this,
                fbPermissions, new LogInCallback() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        progressDialog.dismiss();
                        if (e != null) {
                            e.printStackTrace();
                        }
                        if (parseUser == null) {
                            Toast.makeText(LoginActivity.this, getString(R.string.login_error_red), Toast.LENGTH_LONG).show();
                        } else if (parseUser.isNew()) {
                            setResult(NUEVO_USUARIO);
                            finish();
                        } else {
                            setResult(VIEJO_USUARIO);
                            finish();
                        }
                    }
                });
    }
}
