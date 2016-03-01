package com.tresastronautas.trilly;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.facebook.appevents.AppEventsLogger;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class GroupListActivity extends AppCompatActivity {

    private ParseUser currentUser;
    private FloatingActionButton groupList_fab;
    private TextView grouplist_text_nombre_grupo1, grouplist_text_nombre_grupo2, grouplist_text_nombre_grupo3,
            grouplist_text_stats_grupo1, grouplist_text_stats_grupo2, grouplist_text_stats_grupo3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/lato-regular-webfont.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
        setContentView(R.layout.activity_group_list);
        String userID = getIntent().getStringExtra("user_id");
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.fromLocalDatastore();
        query.getInBackground(userID, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {
                    currentUser = object;
                    prepareLayout();
                }
            }
        });
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

    public void prepareLayout() {
        groupList_fab = (FloatingActionButton) findViewById(R.id.grouplist_fab);
        groupList_fab.setImageDrawable(new IconDrawable(this, Iconify.IconValue.zmdi_chevron_left)
                .colorRes(android.R.color.white));

        grouplist_text_nombre_grupo1 = (TextView) findViewById(R.id.grouplist_text_nombre_grupo1);
        grouplist_text_nombre_grupo2 = (TextView) findViewById(R.id.grouplist_text_nombre_grupo2);
        grouplist_text_nombre_grupo3 = (TextView) findViewById(R.id.grouplist_text_nombre_grupo3);

        grouplist_text_stats_grupo1 = (TextView) findViewById(R.id.grouplist_text_stats_grupo1);
        grouplist_text_stats_grupo1.setText(getString(R.string.grouplist_stats, 0.0, 0.0));
        grouplist_text_stats_grupo2 = (TextView) findViewById(R.id.grouplist_text_stats_grupo2);
        grouplist_text_stats_grupo2.setText(getString(R.string.grouplist_stats, 0.0, 0.0));
        grouplist_text_stats_grupo3 = (TextView) findViewById(R.id.grouplist_text_stats_grupo3);
        grouplist_text_stats_grupo3.setText(getString(R.string.grouplist_stats, 0.0, 0.0));


    }

    public void startGroupActivity(View view) {
        Intent intent = new Intent(getApplicationContext(), GroupActivity.class);
        intent.putExtra("user_id", currentUser.getObjectId());
        startActivity(intent);
    }

    public void groupListBack(View view) {
        finish();
    }
}
