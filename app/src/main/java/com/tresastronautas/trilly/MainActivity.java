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
        mProfileImage = (ImageView) findViewById(R.id.profileTest);
        Log.d("RESULTADO", resultCode + "");
        if(resultCode == 0) {
            finish();
        } else if(resultCode == 1) {
            getDetailsFromFacebook();
        } else if(resultCode == 2) {
//            getDetailsFromParse();
        }
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

    public void saveNewUser() {

        currentUser = ParseUser.getCurrentUser();
        currentUser.setUsername(name);
        if(email != null) {
            currentUser.setEmail(email);
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap bitmap = ((BitmapDrawable) mProfileImage.getDrawable()).getBitmap();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        String thumbName = currentUser.getUsername().replaceAll("\\s+", "")+ "_thumb.jpg";
        ParseFile parseFile = new ParseFile(thumbName, stream.toByteArray());

        try {
            parseFile.save();
            currentUser.put("profileThumb", parseFile);
            currentUser.save();
        } catch (ParseException e) {
            Log.e("ERRORRR", e.getMessage());
        }
        /**parseFile.saveInBackground(new SaveCallback() {
        @Override
        public void done(ParseException e) {
        if (e != null) {
        e.printStackTrace();
        }
        currentUser.put("profileThumb", parseFile);
        currentUser.saveInBackground();
        }
        });**/
    }

    public class ProfilePhotoAsync extends AsyncTask<String, String, String> {

        Profile profile;
        public Bitmap bitmap;

        ProfilePhotoAsync(Profile profile) {
            this.profile = profile;
        }

        @Override
        protected String doInBackground(String... strings) {
            bitmap = downloadImageBitmap(profile.getProfilePictureUri(200, 200).toString());
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
