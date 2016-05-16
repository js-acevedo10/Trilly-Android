package com.tresastronautas.trilly;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.tresastronautas.trilly.Helpers.ParseConstants;
import com.tresastronautas.trilly.Helpers.StaticThings;
import com.tresastronautas.trilly.ListAdapters.Member;
import com.tresastronautas.trilly.ListAdapters.MembersAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class MemberListActivity extends AppCompatActivity {

    private List<ParseObject> memberList;
    private List<Member> members;
    private RecyclerView recyclerView;
    private FloatingActionButton memberlist_fab;
    private MembersAdapter membersAdapter;
    private ParseObject selectedGroup;
    private TextView memberlist_text_groupname, memberlist_text_miembros_dynamic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/lato-regular-webfont.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        setContentView(R.layout.activity_group_member_list);
        selectedGroup = StaticThings.getSelectedGroup();
        members = new ArrayList();
        recyclerView = (RecyclerView) findViewById(R.id.members_recycler_view);
        membersAdapter = new MembersAdapter(members);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(membersAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Member selected = members.get(position);
                for (int i = 0; i < memberList.size(); i++) {
                    if (memberList.get(i).getObjectId().toString().equals(selected.getId())) {
                        startPerfilActivity(memberList.get(i), selected.getStatistics());
                        break;
                    }
                }
            }
        }));
        prepareLayout();
    }

    public void prepareLayout() {
        memberlist_text_groupname = (TextView) findViewById(R.id.memberlist_text_groupname);
        memberlist_text_groupname.setText(selectedGroup.getString(ParseConstants.Grupo.GROUP_NAME.val()));
        memberlist_text_miembros_dynamic = (TextView) findViewById(R.id.memberlist_text_miembros_dynamic);
        memberlist_text_miembros_dynamic.setText(getString(R.string.memberlist_miembros_dynamic, selectedGroup.getDouble(ParseConstants.Grupo.USER_COUNT.val())));

        ParseRelation<ParseObject> relation = selectedGroup.getRelation(ParseConstants.Grupo.USERS.val());
        ParseQuery<ParseObject> query = relation.getQuery();
        final ProgressDialog progressDialog = new ProgressDialog(MemberListActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(getString(R.string.progressFetchingMembers));
        progressDialog.show();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                memberList = objects;
                for (int i = 0; i < memberList.size(); i++) {
                    final ParseObject actual = memberList.get(i);
                    final ParseObject statistics = actual.getParseObject(ParseConstants.User.STATS.val());
                    statistics.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject object, ParseException e) {
                            Member m = new Member(
                                    actual.getString(ParseConstants.User.FIRST.val()) + " " + actual.getString(ParseConstants.User.LAST.val()),
                                    actual.getObjectId().toString(),
                                    actual.getString(ParseConstants.User.FBID.val()),
                                    actual.getString(ParseConstants.User.FBID.val()),
                                    actual.getString(ParseConstants.User.PIC.val()),
                                    object.getDouble(ParseConstants.Estadistica.SAVED_TREES.val()));
                            m.setStatistics(object);
                            members.add(m);
                            Collections.sort(members);
                            membersAdapter.notifyDataSetChanged();
                        }
                    });
                }
                progressDialog.dismiss();
            }
        });

        memberlist_fab = (FloatingActionButton) findViewById(R.id.memberlist_fab);
        memberlist_fab.setImageDrawable(new IconDrawable(this, Iconify.IconValue.zmdi_chevron_left)
                .colorRes(android.R.color.white));
        memberlist_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void startPerfilActivity(ParseObject selectedUser, ParseObject statistics) {
        StaticThings.setSelectedUser(selectedUser);
        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(intent);
    }
}

class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    GestureDetector mGestureDetector;
    private OnItemClickListener mListener;

    public RecyclerItemClickListener(Context context, OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
            return true;
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
