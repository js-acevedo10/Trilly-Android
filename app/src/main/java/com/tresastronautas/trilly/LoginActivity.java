package com.tresastronautas.trilly;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    public List<String> fbPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void loginWithFB(View view) {

        fbPermissions = Arrays.asList(getResources().getStringArray(R.array.my_facebook_permissions));
        ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginActivity.this,
                fbPermissions, new LogInCallback() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        if (e != null) {
                            e.printStackTrace();
                        }
                        if (parseUser == null) {
                            Toast.makeText(LoginActivity.this, "Error al iniciar Sesion", Toast.LENGTH_LONG).show();
                        } else if (parseUser.isNew()) {
                            setResult(2);
                            finish();
                        } else {
                            setResult(1);
                            finish();
                        }
                    }
                });
    }
}
