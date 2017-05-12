package com.sampah_ku.sampahku.augmented_reality.data;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.TextHttpResponseHandler;
import com.sampah_ku.sampahku.R;
import com.sampah_ku.sampahku.activity.NewTrashActivity;
import com.sampah_ku.sampahku.augmented_reality.ui.IconMarker;
import com.sampah_ku.sampahku.augmented_reality.ui.Marker;
import com.sampah_ku.sampahku.function.SampahkuRestClient;
import com.sampah_ku.sampahku.model.ResponseTrash;
import com.sampah_ku.sampahku.model.Trash;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;


/**
 * This abstract class should be extended for new data sources. It has many
 * methods to get and parse data from numerous web sources.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class SampahkuApiDataSource extends DataSource {

    private static final String TAG = SampahkuApiDataSource.class.getSimpleName();
    protected static final int READ_TIMEOUT = 10000;
    protected static final int CONNECT_TIMEOUT = 10000;
    public List<Marker> markersCache = new ArrayList<Marker>();
    public List<Trash> trashes = new ArrayList<Trash>();

    private static Bitmap ICON_LARGE = null;
    private static Bitmap ICON_SMALL = null;
    private static Bitmap ICON_SMALL_UNV = null;
    private static Bitmap ICON_LARGE_UNV = null;

    public SampahkuApiDataSource(Resources res) {
        if (res == null) throw new NullPointerException();

        createIcon(res);
    }

    protected void createIcon(Resources res) {
        if (res == null) throw new NullPointerException();

        ICON_LARGE = BitmapFactory.decodeResource(res, R.drawable.trash_large_ver);
        ICON_SMALL = BitmapFactory.decodeResource(res, R.drawable.trash_small_ver);
        ICON_LARGE_UNV = BitmapFactory.decodeResource(res, R.drawable.trash_large_unver);
        ICON_SMALL_UNV = BitmapFactory.decodeResource(res, R.drawable.trash_small_unver);
    }

    /**
     * This method get the Markers if they have already been downloaded once.
     *
     * @return List of Marker objects or NULL if not downloaded yet.
     */
    public List<Marker> getMarkers() {
        return markersCache;
    }

    public void loadMarkers() {
        SampahkuRestClient.post("trash/all", null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d(TAG, "Response: " + responseString.substring(3000));
                Log.d(TAG, "Response error: " + throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d(TAG, "Response: " + responseString);

                try {
                    JSONObject jObj = new JSONObject(responseString);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        Gson gson = new Gson();
                        String test = gson.toJson(gson.fromJson(responseString, ResponseTrash.class));
                        Log.d(TAG, "onSuccess: " + test);
                        trashes = gson.fromJson(responseString, ResponseTrash.class).getTrash();
                        for(Trash trash : trashes) {
                            final List<Marker> markerList = new ArrayList<Marker>();
                            Bitmap pinImage = null;
                            if(trash.getTrash_type_id() == "1" && trash.getVerified() == 1) {
                                pinImage = ICON_LARGE;
                            } else if(trash.getTrash_type_id() == "1" && trash.getVerified() == 0) {
                                pinImage = ICON_LARGE_UNV;
                            } else if(trash.getTrash_type_id() == "2" && trash.getVerified() == 1) {
                                pinImage = ICON_SMALL;
                            } else if(trash.getTrash_type_id() == "2" && trash.getVerified() == 0) {
                                pinImage = ICON_SMALL_UNV;
                            } else {
                                pinImage = ICON_LARGE;
                            }
                            Marker tempMarker = new IconMarker(trash.getDescription(), trash.getLatitude(), trash.getLongitude(), 0, Color.RED, pinImage);

                            markerList.add(tempMarker);
                            markersCache.add(tempMarker);

                            ARData.addMarkers(markerList);
                        }
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Log.d(TAG, "onSuccess: " + errorMsg);
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Log.d(TAG, "onSuccess: " + e.getMessage());
                }
            }
        });
    }
}
