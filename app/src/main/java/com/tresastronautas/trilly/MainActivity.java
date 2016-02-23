package com.tresastronautas.trilly;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

    public String email, name, id;
    public Profile fbProfile;
    public ImageView mProfileImage;
    public ParseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(ParseUser.getCurrentUser() != null) {
            setContentView(R.layout.activity_main);
            mProfileImage = (ImageView) findViewById(R.id.profileTest);
            getDetailsFromParse();
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, 1);
            currentUser = ParseUser.getCurrentUser();
        }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setContentView(R.layout.activity_main);
        mProfileImage = (ImageView) findViewById(R.id.profileTest);
//        if(resultCode == 0) {
//            finish();
//        } else if(resultCode == 2) {
            getDetailsFromFacebook();
//        } else if(resultCode == 1) {
//            getDetailsFromParse();
//        }
    }

    public void getDetailsFromFacebook() {
        fbProfile = Profile.getCurrentProfile();
        Bundle permissions = new Bundle();
        permissions.putString("fields", "id, name, email");
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
                            name = graphResponse.getJSONObject().getString("name");
                            id = graphResponse.getJSONObject().getString("id");
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
        fbProfile = Profile.getCurrentProfile();
        currentUser = ParseUser.getCurrentUser();
        try {
            ParseFile parseFile = currentUser.getParseFile("pictureFile");
            byte[] data = parseFile.getData();
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            mProfileImage.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        name = currentUser.getString("name");
        email = currentUser.getEmail()!=null?currentUser.getEmail():"no@email.com";
        id = currentUser.getString("facebookID");
    }

    public void saveNewUser() {

        currentUser = ParseUser.getCurrentUser();
        currentUser.put("facebookID", id);
        currentUser.put("nombre", name);
        currentUser.put("picture", fbProfile.getProfilePictureUri(1000, 1000).toString());
        if(email != null) {
            currentUser.setEmail(email);
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap bitmap = ((BitmapDrawable) mProfileImage.getDrawable()).getBitmap();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        String thumbName = currentUser.getString("nombre").replaceAll("\\s+", "")+ "_thumb.jpg";
        ParseFile parseFile = new ParseFile(thumbName, stream.toByteArray());

        try {
            parseFile.save();
            currentUser.put("pictureFile", parseFile);
            currentUser.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public class ProfilePhotoAsync extends AsyncTask<String, String, String> {

        Profile profile;
        public Bitmap bitmap;

        ProfilePhotoAsync(Profile profile) {
            this.profile = profile;
        }

        @Override
        protected String doInBackground(String... strings) {
            bitmap = downloadImageBitmap(profile.getProfilePictureUri(500, 500).toString());
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mProfileImage = (ImageView) findViewById(R.id.profileTest);
            mProfileImage.setImageBitmap(bitmap);
            saveNewUser();
        }
    }

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
}
