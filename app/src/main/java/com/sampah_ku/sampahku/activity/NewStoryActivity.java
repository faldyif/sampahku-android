package com.sampah_ku.sampahku.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.sampah_ku.sampahku.R;
import com.sampah_ku.sampahku.function.SQLiteHandler;
import com.sampah_ku.sampahku.function.SampahkuRestClient;
import com.sampah_ku.sampahku.function.SessionManager;
import com.sampah_ku.sampahku.service.GPSTracker;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

public class NewStoryActivity extends AppCompatActivity {

    private static final String TAG = NewStoryActivity.class.getSimpleName();
    private ImageView imagePreview;
    private Button buttonSubmit;
    private ProgressDialog pDialog;
    private GPSTracker gps;
    private File image;

    private SQLiteHandler db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_story);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        gps = new GPSTracker(this);
        if(gps.canGetLocation()){
            Toast.makeText(NewStoryActivity.this, "Please grant GPS permission", Toast.LENGTH_SHORT).show();
        } // return boolean true/false

        // Progress dialog
        pDialog = new ProgressDialog(NewStoryActivity.this);
        pDialog.setCancelable(false);

        buttonSubmit = (Button) findViewById(R.id.button_submit);
        imagePreview = (ImageView) findViewById(R.id.image_preview);

        Log.d(TAG, "onCreate: " + getIntent().getStringExtra("pathImage"));
        image = new File(getIntent().getStringExtra("pathImage"));
        Picasso.with(NewStoryActivity.this).load(image).into(imagePreview);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sumbitStory();
            }
        });
    }

    private void sumbitStory() {
        HashMap<String, String> user = db.getUserDetails();

        pDialog.setMessage("Mengirim story...");
        showDialog();

        RequestParams params = new RequestParams();
        try {
            params.put("photo", image);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        params.put("latitude", gps.getLatitude());
        params.put("longitude", gps.getLongitude());
        params.put("accuracy", gps.getAccuracy());
        params.put("user_id", user.get("id"));

        SampahkuRestClient.post("story/new", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                Log.d(TAG, "Response: " + responseString.substring(3000));
                Log.d(TAG, "Response error: " + throwable.toString());
                hideDialog();
                Toast.makeText(NewStoryActivity.this, "Unexpected error!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString) {
                Log.d(TAG, "Response: " + responseString);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(responseString);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        Toast.makeText(NewStoryActivity.this, "Sukses menambahkan tempat sampah baru", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(NewStoryActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(NewStoryActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
