package com.sampah_ku.sampahku.fragment;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.loopj.android.http.TextHttpResponseHandler;
import com.sampah_ku.sampahku.R;
import com.sampah_ku.sampahku.activity.AddStoryActivity;
import com.sampah_ku.sampahku.activity.AddTrashActivity;
import com.sampah_ku.sampahku.activity.NewStoryActivity;
import com.sampah_ku.sampahku.function.SampahkuRestClient;
import com.sampah_ku.sampahku.model.ResponseTrash;
import com.sampah_ku.sampahku.model.Trash;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = MapsFragment.class.getSimpleName();

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final Integer RC_LOCATION_PERM = 1832;

    private Double centerLatitude;
    private Double centerLongitude;

    private MapView mapView;
    private GoogleMap googleMap;
    private CameraPosition cameraPosition;

    public MapsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param centerLatitude Parameter 1.
     * @param centerLongitude Parameter 2.
     * @return A new instance of fragment MapsFragment.
     */
    public static MapsFragment newInstance(Double centerLatitude, Double centerLongitude) {
        MapsFragment fragment = new MapsFragment();
        Bundle args = new Bundle();
        args.putDouble(ARG_PARAM1, centerLatitude);
        args.putDouble(ARG_PARAM2, centerLongitude);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            centerLatitude = getArguments().getDouble(ARG_PARAM1);
            centerLongitude = getArguments().getDouble(ARG_PARAM2);
        }

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_CONTACTS)) {
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.CAMERA},
                        112);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_maps, container, false);
        FloatingActionButton tambahFab = (FloatingActionButton) rootView.findViewById(R.id.tambah_fab);
        tambahFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddTrashActivity.class);
                startActivity(intent);
            }
        });
        FloatingActionButton storyFab = (FloatingActionButton) rootView.findViewById(R.id.story_fab);
        storyFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddStoryActivity.class);
                startActivity(intent);
            }
        });
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);//when you already implement OnMapReadyCallback in your fragment
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        mapInit();
    }

    public void mapInit() {
        // Set the center of the map
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(centerLatitude, centerLongitude))      // Sets the center of the map to location user
                .zoom(17)                   // Sets the zoom
                .build();                   // Creates a CameraPosition from the builder
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setTrafficEnabled(true);
        googleMap.setIndoorEnabled(true);
        googleMap.setBuildingsEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, RC_LOCATION_PERM);
        } else {
            googleMap.setMyLocationEnabled(true);
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();

            Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
            if (location != null)
            {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(location.getLatitude(), location.getLongitude()), 13));

                cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                        .zoom(17)                   // Sets the zoom
                        .build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            }
        }

        downloadMarkers();
    }

    public void downloadMarkers() {
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
                        List<Trash> trashes = gson.fromJson(responseString, ResponseTrash.class).getTrash();

                        for(Trash trash : trashes) {
                            int pinImage;
                            String snippet;

                            Log.d(TAG, "onSuccess: " + trash.getTrash_type_id() + " " + trash.getVerified());
                            if(trash.getTrash_type_id().equals("1") && trash.getVerified().equals(1)) {
                                pinImage = R.drawable.trash_large_ver;
                                snippet = "TPA Terverifikasi";
                            } else if(trash.getTrash_type_id().equals("1") && trash.getVerified().equals(0)) {
                                pinImage = R.drawable.trash_large_unver;
                                snippet = "TPA Belum Terverifikasi";
                            } else if(trash.getTrash_type_id().equals("2") && trash.getVerified().equals(1)) {
                                pinImage = R.drawable.trash_small_ver;
                                snippet = "Tempat Sampah Portable Terverifikasi";
                            } else if(trash.getTrash_type_id().equals("2") && trash.getVerified().equals(0)) {
                                pinImage = R.drawable.trash_small_unver;
                                snippet = "Tempat Sampah Portable Belum Terverifikasi";
                            } else {
                                pinImage = R.drawable.trash_large_ver;
                                snippet = "Unknown";
                            }

                            googleMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(trash.getLatitude(), trash.getLongitude()))
                                    .title(trash.getDescription()).snippet(snippet)
                                    .icon(BitmapDescriptorFactory.fromResource(pinImage)));
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_LOCATION_PERM) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, RC_LOCATION_PERM);
                } else {
                    googleMap.setMyLocationEnabled(true);
                }
            } else {
                Toast.makeText(getActivity(), "Lokasi perangkat dibutuhkan untuk fitur lokasi", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
