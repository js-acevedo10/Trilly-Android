package com.tresastronautas.trilly;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.tresastronautas.trilly.Helpers.ParseConstants;
import com.tresastronautas.trilly.Helpers.StaticThings;

import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class GroupListActivity extends AppCompatActivity {

    private ParseObject selectedUser;
    private FloatingActionButton groupList_fab;
    private TextView grouplist_text_nombre_grupo1, grouplist_text_nombre_grupo2, grouplist_text_nombre_grupo3,
            grouplist_text_stats_grupo1, grouplist_text_stats_grupo2, grouplist_text_stats_grupo3,
            grouplist_text_nogroups, grouplist_text_stats_gru;
    private ImageButton grouplist_boton_grupo_1, grouplist_boton_grupo_2, grouplist_boton_grupo_3,
            grouplist_boton_nogroup1, grouplist_boton_nogroup2, grouplist_boton_redgroup, grouplist_boton_add;
    private boolean group1Active, group2Active, group3Active;
    private List<ParseObject> groupList;
    private boolean isCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/lato-regular-webfont.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
        setContentView(R.layout.activity_group_list);
        selectedUser = StaticThings.getSelectedUser();
        isCurrentUser = selectedUser.getObjectId().toString().equals(StaticThings.getCurrentUser().getObjectId().toString());
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
        groupList_fab = (FloatingActionButton) findViewById(R.id.grouplist_fab);
        groupList_fab.setImageDrawable(new IconDrawable(this, Iconify.IconValue.zmdi_chevron_left)
                .colorRes(android.R.color.white));

        grouplist_boton_grupo_1 = (ImageButton) findViewById(R.id.grouplist_boton_grupo_1);
        grouplist_boton_grupo_2 = (ImageButton) findViewById(R.id.grouplist_boton_grupo_2);
        grouplist_boton_grupo_3 = (ImageButton) findViewById(R.id.grouplist_boton_grupo_3);

        grouplist_boton_grupo_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(group1Active) {
                    startGroupActivity(groupList.get(0));
                }
            }
        });
        grouplist_boton_grupo_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(group2Active) {
                    startGroupActivity(groupList.get(1));
                }
            }
        });
        grouplist_boton_grupo_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(group3Active) {
                    startGroupActivity(groupList.get(2));
                }
            }
        });

        grouplist_boton_nogroup1 = (ImageButton) findViewById(R.id.grouplist_boton_nogroup1);
        grouplist_boton_nogroup2 = (ImageButton) findViewById(R.id.grouplist_boton_nogroup2);
        grouplist_boton_redgroup = (ImageButton) findViewById(R.id.grouplist_boton_redgroup);

        grouplist_text_nogroups = (TextView) findViewById(R.id.grouplist_text_nogroups);
        grouplist_text_stats_gru = (TextView) findViewById(R.id.grouplist_text_stats_gru);
        grouplist_boton_add = (ImageButton) findViewById(R.id.grouplist_boton_add);

        grouplist_boton_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAddNewGroupActivity();
            }
        });

        setGroups();
    }

    public void setGroups() {
        grouplist_text_nombre_grupo1 = (TextView) findViewById(R.id.grouplist_text_nombre_grupo1);
        grouplist_text_nombre_grupo2 = (TextView) findViewById(R.id.grouplist_text_nombre_grupo2);
        grouplist_text_nombre_grupo3 = (TextView) findViewById(R.id.grouplist_text_nombre_grupo3);
        grouplist_text_stats_grupo1 = (TextView) findViewById(R.id.grouplist_text_stats_grupo1);
        grouplist_text_stats_grupo2 = (TextView) findViewById(R.id.grouplist_text_stats_grupo2);
        grouplist_text_stats_grupo3 = (TextView) findViewById(R.id.grouplist_text_stats_grupo3);
        ParseRelation<ParseObject> relation = selectedUser.getRelation(ParseConstants.User.GROUPS.val());
        ParseQuery<ParseObject> query = relation.getQuery();
        final ProgressDialog progressDialog = new ProgressDialog(GroupListActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(getString(R.string.progressReloadingUserGroups));
        progressDialog.show();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                grouplist_boton_nogroup1.setVisibility(View.GONE);
                grouplist_boton_nogroup2.setVisibility(View.GONE);
                grouplist_boton_grupo_1.setVisibility(View.GONE);
                grouplist_boton_grupo_2.setVisibility(View.GONE);
                grouplist_boton_grupo_3.setVisibility(View.GONE);
                grouplist_boton_redgroup.setVisibility(View.GONE);
                grouplist_text_nogroups.setVisibility(View.GONE);
                grouplist_text_stats_gru.setVisibility(View.GONE);
                grouplist_boton_add.setVisibility(View.GONE);
                groupList = objects;
                if(groupList == null || groupList.size() == 0) {
                    grouplist_boton_redgroup.setVisibility(View.VISIBLE);
                    grouplist_text_nogroups.setVisibility(View.VISIBLE);
                    grouplist_text_stats_gru.setVisibility(View.VISIBLE);
                    if (isCurrentUser) {
                        grouplist_boton_add.setVisibility(View.VISIBLE);
                    }
                }
                for (int i = 0; i < objects.size(); i++) {
                    ParseObject group = objects.get(i);
                    if(i == 0) {
                        group1Active = true;
                        grouplist_boton_grupo_1.setVisibility(View.VISIBLE);
                        grouplist_text_nombre_grupo1.setText(group.getString(ParseConstants.Grupo.GROUP_NAME.val()));
                        grouplist_text_stats_grupo1.setText(getString(R.string.grouplist_stats,
                                group.getDouble(ParseConstants.Grupo.SAVED_TREES.val()),
                                group.getDouble(ParseConstants.Grupo.USER_COUNT.val())));
                        if (isCurrentUser) {
                            grouplist_boton_nogroup1.setVisibility(View.VISIBLE);
                        }
                    } else if(i == 1) {
                        group2Active = true;
                        grouplist_boton_nogroup1.setVisibility(View.GONE);
                        grouplist_text_nombre_grupo2.setText(group.getString(ParseConstants.Grupo.GROUP_NAME.val()));
                        grouplist_text_stats_grupo2.setText(getString(R.string.grouplist_stats,
                                group.getDouble(ParseConstants.Grupo.SAVED_TREES.val()),
                                group.getDouble(ParseConstants.Grupo.USER_COUNT.val())));
                        grouplist_boton_grupo_2.setVisibility(View.VISIBLE);
                        if (isCurrentUser) {
                            grouplist_boton_nogroup2.setVisibility(View.VISIBLE);
                        }
                    } else if(i == 2) {
                        group3Active = true;
                        grouplist_boton_nogroup2.setVisibility(View.GONE);
                        grouplist_boton_grupo_3.setVisibility(View.VISIBLE);
                        grouplist_text_nombre_grupo3.setText(group.getString(ParseConstants.Grupo.GROUP_NAME.val()));
                        grouplist_text_stats_grupo3.setText(getString(R.string.grouplist_stats,
                                group.getDouble(ParseConstants.Grupo.SAVED_TREES.val()),
                                group.getDouble(ParseConstants.Grupo.USER_COUNT.val())));
                    } else {
                        break;
                    }
                }
                progressDialog.dismiss();
            }
        });
    }

    public void startGroupActivity(ParseObject group) {
        StaticThings.setSelectedUser(selectedUser);
        StaticThings.setSelectedGroup(group);
        StaticThings.setUserGroups(groupList);
        Intent intent = new Intent(getApplicationContext(), GroupActivity.class);
        startActivityForResult(intent, 1);
    }

    public void startAddNewGroupActivity() {
        Intent intent = new Intent(getApplicationContext(), AddGrupoListActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1) {
            if(resultCode == 10) {
                setGroups();
            }
        }
    }

    public void groupListBack(View view) {
        finish();
    }
}
