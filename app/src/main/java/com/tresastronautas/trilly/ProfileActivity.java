package com.tresastronautas.trilly;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private ParseUser currentUser;
    private TextView perfil_label_nombre;
    private CircleImageView perfil_circle_profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        String userID = getIntent().getStringExtra("user_id");
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.getInBackground(userID, new GetCallback<ParseUser>() {
            public void done(ParseUser item, ParseException e) {
                if (e == null) {
                    currentUser = item;
                    prepareLayout();
                } else {
                    finish();
                }
            }
        });
    }

    public void prepareLayout() {
        perfil_label_nombre = (TextView) findViewById(R.id.perfil_label_nombre);
        perfil_label_nombre.setText(currentUser.getString(ParseConstants.User.FIRST.val()));
    }

    public void profileBackToMain(View view) {
        finish();
    }
}
