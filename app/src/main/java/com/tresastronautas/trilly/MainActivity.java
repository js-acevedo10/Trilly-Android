package com.tresastronautas.trilly;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    public String email, firstName, lastName, id, picture;
    public Profile fbProfile;
    public ImageView mProfileImage;
    public ParseUser currentUser;
    public TextView nomBienvenida;
    public boolean fbGet;

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
        if(ParseUser.getCurrentUser() != null) {
            prepareLayout();
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

    public void prepareLayout() {
        setContentView(R.layout.activity_main);
        mProfileImage = (ImageView) findViewById(R.id.profileTest);
        nomBienvenida = (TextView) findViewById(R.id.label_nombre_main);
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
                            nomBienvenida.setText(R.string.saludo_main + " " + firstName + "!");
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
        nomBienvenida.setText("Hola " + firstName + "!");
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
            this.profile = profile;
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
            mProfileImage = (ImageView) findViewById(R.id.profileTest);
            mProfileImage.setImageBitmap(bitmap);
            if (fbGet) saveNewUser(pic);
            fbGet = false;
        }
    }
}
