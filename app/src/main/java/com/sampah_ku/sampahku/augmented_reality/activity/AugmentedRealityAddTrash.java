package com.sampah_ku.sampahku.augmented_reality.activity;

import java.text.DecimalFormat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import com.frosquivel.magicalcamera.MagicalCamera;
import com.frosquivel.magicalcamera.MagicalPermissions;
import com.sampah_ku.sampahku.R;
import com.sampah_ku.sampahku.activity.NewTrashActivity;
import com.sampah_ku.sampahku.augmented_reality.camera.CameraSurface;
import com.sampah_ku.sampahku.augmented_reality.data.ARData;
import com.sampah_ku.sampahku.augmented_reality.ui.Marker;
import com.sampah_ku.sampahku.augmented_reality.widget.VerticalSeekBar;
import com.sampah_ku.sampahku.augmented_reality.widget.VerticalTextView;

/**
 * This class extends the SensorsActivity and is designed tie the AugmentedView
 * and zoom bar together.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class AugmentedRealityAddTrash extends SensorsActivity implements OnTouchListener {

    private static final String TAG = "AugmentedRealityAddTrash";
    private static final DecimalFormat FORMAT = new DecimalFormat("#.##");
    private static final int ZOOMBAR_BACKGROUND_COLOR = Color.argb(125, 55, 55, 55);
    private static final String END_TEXT = FORMAT.format(AugmentedRealityAddTrash.MAX_ZOOM) + " km";
    private static final int END_TEXT_COLOR = Color.WHITE;

    protected static WakeLock wakeLock = null;
    protected static CameraSurface camScreen = null;
    protected static VerticalSeekBar myZoomBar = null;
    protected static VerticalTextView endLabel = null;
    protected static LinearLayout zoomLayout = null;
    protected static AugmentedView augmentedView = null;

    public static final float MAX_ZOOM = 100; // in KM
    public static final float ONE_PERCENT = MAX_ZOOM / 100f;
    public static final float TEN_PERCENT = 10f * ONE_PERCENT;
    public static final float TWENTY_PERCENT = 2f * TEN_PERCENT;
    public static final float EIGHTY_PERCENTY = 4f * TWENTY_PERCENT;

    public static boolean ui_portrait = false;  // Defaulted to LANDSCAPE use
    public static boolean showRadar = true;
    public static boolean showZoomBar = false;
    public static boolean useRadarAutoOrientate = true;
    public static boolean useMarkerAutoRotate = true;
    public static boolean useDataSmoothing = true;
    public static boolean useCollisionDetection = false; // defaulted OFF

    private MagicalPermissions magicalPermissions;
    private MagicalCamera magicalCamera;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String[] permissions = new String[] {
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        };
        magicalPermissions = new MagicalPermissions(this, permissions);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        camScreen = new CameraSurface(this);
        setContentView(camScreen);

        //The pixel percentage is declare like an percentage of 100, if your value is 50, the photo will have the middle quality of your camera.
        // this value could be only 1 to 100.
        int RESIZE_PHOTO_PIXELS_PERCENTAGE = 80;
        magicalCamera = new MagicalCamera(this,RESIZE_PHOTO_PIXELS_PERCENTAGE, magicalPermissions);

        augmentedView = new AugmentedView(this);
        augmentedView.setOnTouchListener(this);
        LayoutParams augLayout = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        addContentView(augmentedView, augLayout);

        FloatingActionButton fab = new FloatingActionButton(this);

        FrameLayout.LayoutParams frameLayoutParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        addContentView(fab, frameLayoutParams);

        zoomLayout = new LinearLayout(this);
        zoomLayout.setVisibility((showZoomBar) ? LinearLayout.VISIBLE : LinearLayout.GONE);
        zoomLayout.setOrientation(LinearLayout.VERTICAL);
        zoomLayout.setPadding(5, 5, 5, 5);
        zoomLayout.setBackgroundColor(ZOOMBAR_BACKGROUND_COLOR);

        endLabel = new VerticalTextView(this);
        endLabel.setText(END_TEXT);
        endLabel.setTextColor(END_TEXT_COLOR);
        LinearLayout.LayoutParams zoomTextParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        zoomTextParams.gravity = Gravity.CENTER;
        zoomLayout.addView(endLabel, zoomTextParams);

        myZoomBar = new VerticalSeekBar(this);
        myZoomBar.setMax(100);
        myZoomBar.setProgress(50);
        myZoomBar.setOnSeekBarChangeListener(myZoomBarOnSeekBarChangeListener);
        LinearLayout.LayoutParams zoomBarParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
        zoomBarParams.gravity = Gravity.CENTER_HORIZONTAL;
        zoomLayout.addView(myZoomBar, zoomBarParams);

//        FrameLayout.LayoutParams frameLayoutParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT, Gravity.RIGHT);
        addContentView(zoomLayout, frameLayoutParams);

        updateDataOnZoom();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");

        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_photo_camera_black_24dp);

        fab.setImageDrawable(drawable);
        // Gets the layout params that will allow you to resize the layout
        LayoutParams params = fab.getLayoutParams();
        // Changes the height and width to the specified *pixels*
        params.height = 250;
        params.width = 250;
        fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary)));
        fab.setLayoutParams(params);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //take photo
                magicalCamera.takePhoto();
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //CALL THIS METHOD EVER
        magicalCamera.resultPhoto(requestCode, resultCode, data);

        //this is for rotate picture in this method
        //magicalCamera.resultPhoto(requestCode, resultCode, data, MagicalCamera.ORIENTATION_ROTATE_180);

        //if you need save your bitmap in device use this method and return the path if you need this
        //You need to send, the bitmap picture, the photo name, the directory name, the picture type, and autoincrement photo name if           //you need this send true, else you have the posibility or realize your standard name for your pictures.
        String path = magicalCamera.savePhotoInMemoryDevice(magicalCamera.getPhoto(),"SampahKuNewTrash", "sampahku", MagicalCamera.JPEG, true);

        if(path != null){
            Toast.makeText(AugmentedRealityAddTrash.this, "The photo is save in device, please check this path: " + path, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, NewTrashActivity.class);
            intent.putExtra("pathImage", path);
            startActivity(intent);
        }else{
            Toast.makeText(AugmentedRealityAddTrash.this, "Sorry your photo dont write in device, maybe your permission is still not allowing you to write on external storage", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onResume() {
        super.onResume();

        wakeLock.acquire();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPause() {
        super.onPause();

        wakeLock.release();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSensorChanged(SensorEvent evt) {
        super.onSensorChanged(evt);

        if (evt.sensor.getType() == Sensor.TYPE_ACCELEROMETER || evt.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            augmentedView.postInvalidate();
        }
    }

    private OnSeekBarChangeListener myZoomBarOnSeekBarChangeListener = new OnSeekBarChangeListener() {

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            updateDataOnZoom();
            camScreen.invalidate();
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            // Ignore
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            updateDataOnZoom();
            camScreen.invalidate();
        }
    };

    private static float calcZoomLevel() {
        int myZoomLevel = myZoomBar.getProgress();
        float myout = 0;

        float percent = 0;
        if (myZoomLevel <= 25) {
            percent = myZoomLevel / 25f;
            myout = ONE_PERCENT * percent;
        } else if (myZoomLevel > 25 && myZoomLevel <= 50) {
            percent = (myZoomLevel - 25f) / 25f;
            myout = ONE_PERCENT + (TEN_PERCENT * percent);
        } else if (myZoomLevel > 50 && myZoomLevel <= 75) {
            percent = (myZoomLevel - 50f) / 25f;
            myout = TEN_PERCENT + (TWENTY_PERCENT * percent);
        } else {
            percent = (myZoomLevel - 75f) / 25f;
            myout = TWENTY_PERCENT + (EIGHTY_PERCENTY * percent);
        }

        return myout;
    }

    /**
     * Called when the zoom bar has changed.
     */
    protected void updateDataOnZoom() {
        float zoomLevel = calcZoomLevel();
        ARData.setRadius(zoomLevel);
        ARData.setZoomLevel(FORMAT.format(zoomLevel));
        ARData.setZoomProgress(myZoomBar.getProgress());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onTouch(View view, MotionEvent me) {
        if (me.getAction() != MotionEvent.ACTION_DOWN) return false;

        // See if the motion event is on a Marker
        for (Marker marker : ARData.getMarkers()) {
            if (marker.handleClick(me.getX(), me.getY())) {
                markerTouched(marker);
                return true;
            }
        }

        return super.onTouchEvent(me);
    }

    protected void markerTouched(Marker marker) {
        Log.w(TAG, "markerTouched() not implemented. marker="+marker.getName());
    }
}
