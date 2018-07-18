package com.organization.Giscle.giscle_app.User_Fragment.Camera_frag;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.organization.Giscle.giscle_app.R;
import com.organization.Giscle.giscle_app.UserDashboard;
import com.organization.Giscle.giscle_app.Variable.CONSTANT;
import com.organization.Giscle.giscle_app.local_db.Tables;
import com.organization.Giscle.giscle_app.local_db.db_helper;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import static android.content.Context.LOCATION_SERVICE;
import static com.organization.Giscle.giscle_app.User_Fragment.Camera_frag.LocationService_service_19.ALL_LATITUDE;
import static com.organization.Giscle.giscle_app.User_Fragment.Camera_frag.LocationService_service_19.ALL_LONGITUDE;

import static com.organization.Giscle.giscle_app.User_Fragment.Camera_frag.LocationService_service_19.MAIN_DISTANCE;
import static com.organization.Giscle.giscle_app.User_Fragment.Camera_frag.LocationService_service_19.MAIN_POINTS;
import static com.organization.Giscle.giscle_app.User_Fragment.Camera_frag.LocationService_service_19.STARAT_TIME;
import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * Created by sushen.kumaron 10/8/2017.
 */

public class camera_fragment_19 extends Fragment implements View.OnClickListener {

    private Camera mCamera;
    Button stopButton;
    FrameLayout cameraPreviewFrame;
    CameraPreview cameraPreview;
    MediaRecorder mediaRecorder;
    File file;
    String mfile_name;
    private TextView tv_timer;
    static boolean status;
    static TextView points, speed;
    static long startTime, endTime;
    //    static int p = 0;
    static double distance_v19 = 0.0;
    LocationService_service_19 myService;
    LocationManager mlocationManager;
    private boolean mIsRecordingVideo;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;


    private void init(View view) {
        this.cameraPreviewFrame = (FrameLayout) view.findViewById(R.id.texture_v19);
        this.stopButton = (Button) view.findViewById(R.id.video_v19);
        this.tv_timer = (TextView) view.findViewById(R.id.tv_timeVideo_v19);
        speed = (TextView) view.findViewById(R.id.speed_video_v19);
        points = (TextView) view.findViewById(R.id.points_video_v19);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mEditor = mPreferences.edit();

    }

    //this is for timer..
    private Handler handler = new Handler();

    private Runnable runnable;
    private long millSecond = 0;

    private void timerStart() {
        runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                millSecond = millSecond + 1000;
                int hours = (int) ((millSecond / (1000 * 60 * 60)) % 24);
                int minutes = (int) ((millSecond / (1000 * 60)) % 60);
                int seconds = (int) (millSecond / 1000) % 60;
                tv_timer.setText(String.format("%02d", hours)
                        + ":" + String.format("%02d", minutes)
                        + ":" + String.format("%02d", seconds));
            }
        };
        handler.postDelayed(runnable, 0);
    }
//timer working has been done..

    //this is for for location..


    private void initializeLocationManager() {
//        Log.e(TAG, "initializeLocationManager");
        if (mlocationManager == null) {
            mlocationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(LOCATION_SERVICE);
        }
    }

    private static String TAG = camera_fragment_19.class.getSimpleName();

    private class locationList implements LocationListener {

        Location mLastLocation;

        public locationList(String provider, Context context) {
//            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            if (location.hasSpeed()) {
                String value = String.format("%.2f", location.getSpeed());
//                speed.setText(value + " m/sec");
                speed.setText(value + " m/sec");
            } else {
//                Toast.makeText(getActivity(), "DId not get Speed", Toast.LENGTH_SHORT).show();
            }


        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    camera_fragment_19.locationList[] locationListenObject = new camera_fragment_19.locationList[]{
            new camera_fragment_19.locationList(LocationManager.GPS_PROVIDER, getActivity()),
            new camera_fragment_19.locationList(LocationManager.NETWORK_PROVIDER, getActivity())
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera_v19, container, false);
        init(view);
        checkGps();
        initializeLocationManager();
        mlocationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        if (!mlocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(getActivity(), "GPS Turn off Detect", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(getActivity(), UserDashboard.class);
            getActivity().startActivity(i);
            getActivity().finish();
            return null;
        }
        if (!status) {
            //Here, the Location Service gets bound and the GPS Speedometer gets Active.
            bindService();
        }


//        stopButton.setOnClickListener(this);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecording();
            }
        });
        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mlocationManager != null) {
            for (int i = 0; i < locationListenObject.length; i++) {
                try {
                    mlocationManager.removeUpdates(locationListenObject[i]);
                } catch (Exception e) {
                    e.getMessage();
                }
            }
        }
    }

    //this is for our service;;

//    private ServiceConnection sc = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            LocationService_service_19.LocalBinder localBinder = (LocationService_service_19.LocalBinder) service;
//
//            myService = localBinder.getService();
//            status = true;
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            status = false;
//        }
//    };

    public void bindService() {
        if (status)
            return;
        Intent i = new Intent(getActivity().getApplicationContext(), LocationService_service_19.class);
//        getActivity().bindService(i, sc, Context.BIND_AUTO_CREATE);
        getContext().startService(i);
        status = true;
        startTime = System.currentTimeMillis();
    }

    public void unBindService() {
        if (!status)
            return;
        Intent i = new Intent(getActivity().getApplicationContext(), LocationService_service_19.class);
//        if (sc != null) {
//            try {
//                getActivity().unbindService(sc);
//            } catch (Exception e) {
//                e.getMessage();
//            }
//        }
        getContext().stopService(i);
        status = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (status) {
            unBindService();
        }
    }

    public void startRecording() {
//        Log.e("VideoCaptureActivity", "startRecording");
//        Log.d("abcd", "startRecording()");
        // we need to unlock the camera so that mediaRecorder can use it
        mCamera.unlock(); // unnecessary in API >= 14
        // now we can initialize the media recorder and set it up with our
        // camera
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setCamera(this.mCamera);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
        mediaRecorder.setVideoEncodingBitRate(512 * 1000);
        mediaRecorder.setVideoFrameRate(15);
//        mediaRecorder.setOutputFile(initFile().getAbsolutePath());
        if (mNextVideoAbsolutePath == null || mNextVideoAbsolutePath.isEmpty()) {
            mNextVideoAbsolutePath = getVideoFilePath();
        }
//        mediaRecorder.setOutputFile(mNextVideoAbsolutePath);
        mediaRecorder.setOutputFile(mNextVideoAbsolutePath);

//        mediaRecorder.setOrientationHint(90);

        mediaRecorder.setPreviewDisplay(this.cameraPreview.getHolder().getSurface());
        try {
            mediaRecorder.prepare();
            // start the actual recording
            // throws IllegalStateException if not prepared
            mediaRecorder.start();
            setCamFocusMode();
            mEditor.putString(LocationService_service_19.STARAT_TIME, getCurrentTime()).commit();
            timerStart();
            Toast.makeText(getActivity(), "Start Recording", Toast.LENGTH_SHORT).show();
            // enable the stop button by indicating that we are recording
//            this.toggleButtons(true);
        } catch (Exception e) {
//            Log.wtf("mCamera", "Failed to prepare MediaRecorder", e);
            Toast.makeText(getActivity(), "Cannot Start Video recording", Toast.LENGTH_SHORT).show();
            this.releaseMediaRecorder();
        }
    }


    private void setCamFocusMode() {
//        Log.e("VideoCaptureActivity", "setCamFocusMode");
        if (null == mCamera) {
            return;
        }

    /* Set Auto focus */
        Camera.Parameters parameters = mCamera.getParameters();
        List<String> focusModes = parameters.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        } else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }

        mCamera.setParameters(parameters);
    }

    private void addDataInDatabase(int points, String distance, String videoPath, String time) {
        SQLiteDatabase database = new db_helper(getApplicationContext()).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Tables.videoDetails.COLUMN_DISTANCE, distance);
        values.put(Tables.videoDetails.COLUMN_FILE_path, videoPath);
        values.put(Tables.videoDetails.COLUMN_POINTS, points);
        values.put(Tables.videoDetails.COLUMN_TIME, time);
        values.put(Tables.videoDetails.COLUMN_FILE_NAME, mfile_name);
        values.put(Tables.videoDetails.COLUMN_START_TIME, mPreferences.getString(STARAT_TIME, "00:00:00"));
        values.put(Tables.videoDetails.COLUMN_END_TIME, getCurrentTime());
        values.put(Tables.videoDetails.COLUMN_UPLOAD_STATUS, "false");
        values.put(Tables.videoDetails.COLUMN_ALL_LATITUDE, mPreferences.getString(ALL_LATITUDE, "not found"));
        values.put(Tables.videoDetails.COLUMN_ALL_LONGITUDE, mPreferences.getString(ALL_LONGITUDE, "not found"));
//        Log.e("All Latlng", mPreferences.getString(ALL_LATITUDE, "not found") + "\n" + mPreferences.getString(ALL_LONGITUDE, "not found"));
        database.insert(Tables.videoDetails.TABLE_NAME, null, values);
        database.close();
    }


    private String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        return dateFormat.format(calendar.getTime());
    }

    private String getTime(String hour, String minute, String second) {
        String time = "Consumed time :: ";
        if (!hour.equalsIgnoreCase("00")) {
            time += " Hour: " + hour;
        }
        if (!minute.equalsIgnoreCase("00")) {
            time += " Minute: " + minute;
        }
        if (!second.equalsIgnoreCase("00")) {
            time += " Seconds: " + second;
        }

        return time;

    }

    private void removePreferences() {
        mEditor.remove(MAIN_POINTS);
        mEditor.remove(MAIN_DISTANCE);
        mEditor.remove(STARAT_TIME);
        mEditor.remove(ALL_LATITUDE);
        mEditor.remove(ALL_LONGITUDE);

    }


    // gets called by the button press
    public void stopRecording() {

//        Log.e("VideoCaptureActivity", "stopRecording");
        if (status)
            unBindService();
//        p = 0;
        mIsRecordingVideo = false;
        speed.setText("0 km/hr");
        assert this.mediaRecorder != null;
        try {
            mediaRecorder.stop();
            mNextVideoAbsolutePath = null;
            Intent i = new Intent(getApplicationContext(), UserDashboard.class);
            Bundle bundle = new Bundle();
            String[] data = tv_timer.getText().toString().split(":");
            bundle.putString(CONSTANT.TOTAL_TIME, getTime(data[0], data[1], data[2]));
            addDataInDatabase(
                    mPreferences.getInt(MAIN_POINTS, 0),
                    mPreferences.getString(MAIN_DISTANCE, ""),
                    filePath,
                    tv_timer.getText().toString());
            removePreferences();
            i.putExtra(CONSTANT.BUNDLE_VIDEO_CAMERA, bundle);
            startActivity(i);
            getActivity().finish();

            Toast.makeText(getActivity(), "File Saved", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getActivity(), "File not Saved", Toast.LENGTH_SHORT).show();
            return;
        } finally {
            this.releaseMediaRecorder();
        }
        Intent intent = new Intent(getActivity(), UserDashboard.class);
        super.startActivity(intent);
        getActivity().finish();
    }


    private String filePath = null;
    private String mNextVideoAbsolutePath;

    private String getVideoFilePath() {
        File file = new File(Environment.getExternalStorageDirectory() + "/Giscle Video");

        if (!file.exists()) {
            file.mkdir();
        }
        Random generator = new Random();
        int n = 9000000;
        n = generator.nextInt(n) + 1000000;
        String fname = "video-" + n + ".mp4";
        File file1 = new File(file, fname);
        mfile_name = fname;
        filePath = file1.getAbsolutePath();

        return (file1 == null ? "" : file1.getAbsolutePath());
    }

    void checkGps() {
        mlocationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        if (!mlocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showGPSDisabledAlertToUser();
        }
    }

    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext());
        alertDialogBuilder.setMessage("Enable GPS to use application")
                .setCancelable(false)
                .setPositiveButton("Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        try {
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static camera_fragment_19 newInstance() {
        return new camera_fragment_19();
    }

    @Override
    public void onResume() {
        super.onResume();
        new AsyncTask<Void, Void, Camera>() {
            @Override
            protected Camera doInBackground(Void... voids) {
                try {
                    Camera camera = Camera.open();
                    return camera == null ? Camera.open(0) : camera;
                } catch (RuntimeException e) {
//                    Log.wtf("abcd", "Failed to get camera", e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Camera camera) {
                if (camera == null) {
                    Toast.makeText(getActivity(), "Cannot recording",
                            Toast.LENGTH_SHORT);
                } else {
                    initCamera(camera);
                }
            }
        }.execute();


    }

    @Override
    public void onPause() {
        super.onPause();
//        Log.e("VideoCaptureActivity", "onPause");
        this.releaseResources();
    }

    void releaseResources() {
//        Log.e("VideoCaptureActivity", "releaseResources");
        this.releaseMediaRecorder();
        this.releaseCamera();
    }

    void releaseMediaRecorder() {
//        Log.e("VideoCaptureActivity", "releaseMediaRecorder");
        if (this.mediaRecorder != null) {
            this.mediaRecorder.reset(); // clear configuration (optional here)
            this.mediaRecorder.release();
            this.mediaRecorder = null;
        }
    }

    void releaseCamera() {
//        Log.e("VideoCaptureActivity", "releaseCamera");
        if (this.mCamera != null) {
            this.mCamera.lock(); // unnecessary in API >= 14
            this.mCamera.stopPreview();
            this.mCamera.release();
            this.mCamera = null;
            this.cameraPreviewFrame.removeView(this.cameraPreview);
        }
    }

    void initCamera(Camera camera) {
//        Log.e("VideoCaptureActivity", "initCamera");
        // we now have the camera
        this.mCamera = camera;
        // create a preview for our camera
        this.cameraPreview = new CameraPreview(getActivity());
        // add the preview to our preview frame
        this.cameraPreviewFrame.addView(this.cameraPreview, 0);

        // enable just the record button
//        this.recordButton.setEnabled(true);
//        Log.e("tata", "abcd");
//        startRecording();
    }

    public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
        public CameraPreview(Context context) {
            super(context);
            super.getHolder().addCallback(this);
            super.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        }

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            try {
                mCamera.setPreviewDisplay(surfaceHolder);
                mCamera.startPreview();
                startRecording();
            } catch (Exception e) {
//                Log.e("VideoCaptureActivity", "Failed to start camera preview", e);
            }

        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
//            Log.e("VideoCaptureActivity", "surfaceChanged()");

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
//            Log.e("VideoCaptureActivity", "surfaceDestroyed()");
        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.video:
                stopRecording();
                break;
        }
    }
}

//package com.example.asheransari.giscle_app.User_Fragment.Camera_frag;
//
//import android.content.Context;
//import android.content.Intent;
//import android.hardware.Camera;
//import android.media.MediaRecorder;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.os.Environment;
//import android.support.v4.app.Fragment;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.SurfaceHolder;
//import android.view.SurfaceView;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.FrameLayout;
//import android.widget.Toast;
//
//import com.example.asheransari.giscle_app.R;
//import com.example.asheransari.giscle_app.UserDashboard;
//
//import java.io.File;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.List;
//import java.util.Random;
//
//
///**
// * Created by sushen.kumaron 10/8/2017.
// */
//
//public class camera_fragment_19 extends Fragment implements View.OnClickListener {
//
//    private Camera mCamera;
//    Button stopButton;
//    FrameLayout cameraPreviewFrame;
//    CameraPreview cameraPreview;
//    MediaRecorder mediaRecorder;
//    File file;
//    String mfile_name;
//
//    private void init(View view) {
//        this.cameraPreviewFrame = (FrameLayout) view.findViewById(R.id.texture);
//        this.stopButton = (Button) view.findViewById(R.id.video);
//
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_camera_video, container, false);
//        init(view);
////        stopButton.setOnClickListener(this);
//        stopButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                stopRecording();
//            }
//        });
//        return view;
//    }
//
//
//    public void startRecording() {
//        Log.e("VideoCaptureActivity", "startRecording");
//        Log.d("abcd", "startRecording()");
//        // we need to unlock the camera so that mediaRecorder can use it
//        mCamera.unlock(); // unnecessary in API >= 14
//        // now we can initialize the media recorder and set it up with our
//        // camera
//        mediaRecorder = new MediaRecorder();
//        mediaRecorder.setCamera(this.mCamera);
//        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
//        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
//        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
//        mediaRecorder.setVideoEncodingBitRate(512 * 1000);
//        mediaRecorder.setVideoFrameRate(15);
////        mediaRecorder.setOutputFile(initFile().getAbsolutePath());
//        if (mNextVideoAbsolutePath == null || mNextVideoAbsolutePath.isEmpty()) {
//            mNextVideoAbsolutePath = getVideoFilePath();
//        }
////        mediaRecorder.setOutputFile(mNextVideoAbsolutePath);
//        mediaRecorder.setOutputFile(mNextVideoAbsolutePath);
//
//        mediaRecorder.setOrientationHint(90);
//
//        mediaRecorder.setPreviewDisplay(this.cameraPreview.getHolder().getSurface());
//        try {
//            mediaRecorder.prepare();
//            // start the actual recording
//            // throws IllegalStateException if not prepared
//            mediaRecorder.start();
//            setCamFocusMode();
//            Toast.makeText(getActivity(), "Start Recording", Toast.LENGTH_SHORT).show();
//            // enable the stop button by indicating that we are recording
////            this.toggleButtons(true);
//        } catch (Exception e) {
//            Log.wtf("mCamera", "Failed to prepare MediaRecorder", e);
//            Toast.makeText(getActivity(), "Cannot reading", Toast.LENGTH_SHORT).show();
//            this.releaseMediaRecorder();
//        }
//    }
//
//
//
//    private void setCamFocusMode() {
//        Log.e("VideoCaptureActivity", "setCamFocusMode");
//        if (null == mCamera) {
//            return;
//        }
//
//    /* Set Auto focus */
//        Camera.Parameters parameters = mCamera.getParameters();
//        List<String> focusModes = parameters.getSupportedFocusModes();
//        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
//            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
//        } else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
//            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
//        }
//
//        mCamera.setParameters(parameters);
//    }
//
//    // gets called by the button press
//    public void stopRecording() {
//
//        Log.e("VideoCaptureActivity", "stopRecording");
//        assert this.mediaRecorder != null;
//        try {
//            mediaRecorder.stop();
//            Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();
//            // we are no longer recording
////            this.toggleButtons(false);
//        }
//        catch (Exception e) {
//            // the recording did not succeed
//            Log.w("abcd", "Failed to record", e);
//            if (this.file != null && this.file.exists() && this.file.delete()) {
//                Log.d("abcd", "Deleted " + this.file.getAbsolutePath());
//            }
//            return;
//        } finally {
//            this.releaseMediaRecorder();
//        }
//        if (mNextVideoAbsolutePath == null ) {
//            Toast.makeText(getActivity(), "File not saved..!!", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(getActivity(), "File saved..!!", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(getActivity(), UserDashboard.class);
//            super.startActivity(intent);
//            getActivity().finish();
//        }
//    }
//    private String filePath = null;
//    private String mNextVideoAbsolutePath;
//
//    private String getVideoFilePath() {
//        File file = new File(Environment.getExternalStorageDirectory() + "/Giscle Video");
//
//        if (!file.exists()) {
//            file.mkdir();
//        }
//        Random generator = new Random();
//        Log.e("FIle", file.getAbsolutePath());
//        int n = 9000000;
//        n = generator.nextInt(n) + 1000000;
//        String fname = "video-" + n+".mp4";
//        File file1 = new File(file, fname);
//        mfile_name = fname;
//        return (file1 == null ? "" : file1.getAbsolutePath());
//    }
//
//    //TODO:: yeha kam krna hai..
//    private String initFile() {
//        Log.e("VideoCaptureActivity", "initFile");
//        File dir = new File(Environment.getExternalStorageDirectory() + "/Giscle Video");
//        if (!file.exists()) {
//            file.mkdir();
//            Toast.makeText(getActivity(), "Cannot recording..!!", Toast.LENGTH_SHORT);
//        }
//        Random generator = new Random();
//        Log.e("FIle", file.getAbsolutePath());
//        int n = 9000000;
//        n = generator.nextInt(n) + 1000000;
//        String fname = "video-" + n;
//        mfile_name = fname;
//        filePath = file.getAbsolutePath();
//        return (dir == null ? "" : (dir.getAbsolutePath() + "/")) + fname + ".mp4";
//    }
//
//    public static camera_fragment_19 newInstance(){
//        return new camera_fragment_19();
//    }
//    @Override
//    public void onResume() {
//        super.onResume();
//        new AsyncTask<Void, Void, Camera>() {
//            @Override
//            protected Camera doInBackground(Void... voids) {
//                try {
//                    Camera camera = Camera.open();
//                    return camera == null ? Camera.open(0) : camera;
//                } catch (RuntimeException e) {
//                    Log.wtf("abcd", "Failed to get camera", e);
//                    return null;
//                }
//            }
//
//            @Override
//            protected void onPostExecute(Camera camera) {
//                if (camera == null) {
//                    Toast.makeText(getActivity(), "Cannot recording",
//                            Toast.LENGTH_SHORT);
//                } else {
//                    initCamera(camera);
//                }
//            }
//        }.execute();
//
//
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        Log.e("VideoCaptureActivity", "onPause");
//        this.releaseResources();
//    }
//
//    void releaseResources() {
//        Log.e("VideoCaptureActivity", "releaseResources");
//        this.releaseMediaRecorder();
//        this.releaseCamera();
//    }
//
//    void releaseMediaRecorder() {
//        Log.e("VideoCaptureActivity", "releaseMediaRecorder");
//        if (this.mediaRecorder != null) {
//            this.mediaRecorder.reset(); // clear configuration (optional here)
//            this.mediaRecorder.release();
//            this.mediaRecorder = null;
//        }
//    }
//
//    void releaseCamera() {
//        Log.e("VideoCaptureActivity", "releaseCamera");
//        if (this.mCamera != null) {
//            this.mCamera.lock(); // unnecessary in API >= 14
//            this.mCamera.stopPreview();
//            this.mCamera.release();
//            this.mCamera = null;
//            this.cameraPreviewFrame.removeView(this.cameraPreview);
//        }
//    }
//
//    void initCamera(Camera camera) {
//        Log.e("VideoCaptureActivity", "initCamera");
//        // we now have the camera
//        this.mCamera = camera;
//        // create a preview for our camera
//        this.cameraPreview = new CameraPreview(getActivity());
//        // add the preview to our preview frame
//        this.cameraPreviewFrame.addView(this.cameraPreview, 0);
//
//        // enable just the record button
////        this.recordButton.setEnabled(true);
//        Log.e("tata", "abcd");
////        startRecording();
//    }
//
//    public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
//        public CameraPreview(Context context) {
//            super(context);
//            super.getHolder().addCallback(this);
//            super.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//
//        }
//
//        @Override
//        public void surfaceCreated(SurfaceHolder surfaceHolder) {
//            try {
//                mCamera.setPreviewDisplay(surfaceHolder);
//                mCamera.startPreview();
//                startRecording();
//            } catch (Exception e) {
//                Log.e("VideoCaptureActivity", "Failed to start camera preview", e);
//            }
//
//        }
//
//        @Override
//        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
//            Log.e("VideoCaptureActivity", "surfaceChanged()");
//
//        }
//
//        @Override
//        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
//            Log.e("VideoCaptureActivity", "surfaceDestroyed()");
//        }
//    }
//
//
//    @Override
//    public void onClick(View v) {
//
//        switch (v.getId()){
////            case R.id.video:
////                stopRecording();
////                break;
//        }
//    }
//}