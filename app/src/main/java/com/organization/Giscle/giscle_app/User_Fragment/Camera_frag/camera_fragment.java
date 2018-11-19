package com.organization.Giscle.giscle_app.User_Fragment.Camera_frag;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import java.lang.Runnable;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v13.app.FragmentCompat;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.organization.Giscle.giscle_app.CustomView.AutofitTextureView;
import com.organization.Giscle.giscle_app.R;
import com.organization.Giscle.giscle_app.UserDashboard;
import com.organization.Giscle.giscle_app.Variable.CONSTANT;
import com.organization.Giscle.giscle_app.giscle_socket.SocketClass;
import com.organization.Giscle.giscle_app.local_db.Tables;
import com.organization.Giscle.giscle_app.local_db.db_helper;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static android.content.Context.BIND_AUTO_CREATE;
import static android.content.Context.LOCATION_SERVICE;
import static com.organization.Giscle.giscle_app.User_Fragment.Camera_frag.LocationService_21.ALL_LATITUDE;
import static com.organization.Giscle.giscle_app.User_Fragment.Camera_frag.LocationService_21.ALL_LONGITUDE;
import static com.organization.Giscle.giscle_app.User_Fragment.Camera_frag.LocationService_21.MAIN_DISTANCE;
import static com.organization.Giscle.giscle_app.User_Fragment.Camera_frag.LocationService_21.MAIN_POINTS;
import static com.organization.Giscle.giscle_app.User_Fragment.Camera_frag.LocationService_21.STARAT_TIME;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by sushen.kumar on 9/19/2017.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class camera_fragment extends Fragment implements View.OnClickListener, FragmentCompat.OnRequestPermissionsResultCallback {
    private static final int SENSOR_ORIENTATION_DEFAULT_DEGREES = 90;
    private static final int SENSOR_ORIENTATION_INVERSE_DEGREES = 270;
    private static final SparseIntArray DEFAULT_ORIENTATIONS = new SparseIntArray();
    private static final SparseIntArray INVERSE_ORIENTATIONS = new SparseIntArray();


    private static final String TAG = "Camera2VideoFragment";
    private static final int REQUEST_VIDEO_PERMISSIONS = 1;
    private static final String FRAGMENT_DIALOG = "dialog";

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private static final String[] VIDEO_PERMISSIONS = {
            android.Manifest.permission.CAMERA,
//            Manifest.permission.RECORD_AUDIO,
    };

    static {
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_0, 90);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_90, 0);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_180, 270);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    static {
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_0, 270);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_90, 180);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_180, 90);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_270, 0);
    }

    private String mfile_name;

    private AutofitTextureView mTextureView;

    /**
     * Button to record video
     */
    private Button mButtonVideo;

    /**
     * A reference to the opened {@link android.hardware.camera2.CameraDevice}.
     */
    private CameraDevice mCameraDevice;

    /**
     * A reference to the current {@link android.hardware.camera2.CameraCaptureSession} for
     * preview.
     */
    private CameraCaptureSession mPreviewSession;

    /**
     * {@link TextureView.SurfaceTextureListener} handles several lifecycle events on a
     * {@link TextureView}.
     */
    private TextureView.SurfaceTextureListener mSurfaceTextureListener
            = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture,
                                              int width, int height) {
            openCamera(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
            try {
                configureTransform(width, height);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }

    };

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    /**
     * The {@link android.util.Size} of camera preview.
     */
    private Size mPreviewSize;


    /**
     * The {@link android.util.Size} of video recording.
     */
    private Size mVideoSize;
    private TextView mEchoText;
    private CameraDevice mCamera = mCameraDevice;
    private Camera_act mPreview;
    private FrameLayout mLayout;
    Handler mHandler = new Handler();

    private String mString;

    private com.organization.Giscle.giscle_app.giscle_socket.SocketClass mSockMan;

    volatile Thread mRunner;

    /**
     * MediaRecorder
     */
    private MediaRecorder mMediaRecorder;

    /**
     * Whether the app is recording video now
     */
    private boolean mIsRecordingVideo;

    /**
     * An additional thread for running tasks that shouldn't block the UI.
     */
    private HandlerThread mBackgroundThread;

    /**
     * A {@link Handler} for running tasks in the background.
     */
    private Handler mBackgroundHandler;

    private TextView tv_timer;

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

    /**
     * A {@link Semaphore} to prevent the app from exiting before closing the camera.
     */
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);

    /**
     * {@link CameraDevice.StateCallback} is called when {@link CameraDevice} changes its status.
     */
    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            mCameraDevice = cameraDevice;
            try {
                startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mCameraOpenCloseLock.release();
            if (null != mTextureView) {
                try {
                    configureTransform(mTextureView.getWidth(), mTextureView.getHeight());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
            Activity activity = getActivity();
            if (null != activity) {
                activity.finish();
            }
        }

    };
    private Integer mSensorOrientation;
    private String mNextVideoAbsolutePath;
    private CaptureRequest.Builder mPreviewBuilder;

    public static camera_fragment newInstance() {
        return new camera_fragment();
    }

    /**
     * In this sample, we choose a video size with 3x4 aspect ratio. Also, we don't use sizes
     * larger than 1080p, since MediaRecorder cannot handle such a high-resolution video.
     *
     * @param choices The list of available sizes
     * @return The video size
     */
    private static Size chooseVideoSize(Size[] choices) {
        for (Size size : choices) {
            if (size.getWidth() == size.getHeight() * 4 / 3 && size.getWidth() <= 1080) {
                return size;
            }
        }
        Log.e(TAG, "Couldn't find any suitable video size");
        return choices[choices.length - 1];
    }

    /**
     * Given {@code choices} of {@code Size}s supported by a camera, chooses the smallest one whose
     * width and height are at least as large as the respective requested values, and whose aspect
     * ratio matches with the specified value.
     *
     * @param choices     The list of sizes that the camera supports for the intended output class
     *                    //     * @param width       The minimum desired width
     *                    //     * @param height      The minimum desired height
     * @param aspectRatio The aspect ratio
     * @return The optimal {@code Size}, or an arbitrary one if none were big enough
     */
    private static Size chooseOptimalSize(Size[] choices, int width, int height, Size aspectRatio) {
        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getHeight() == option.getWidth() * h / w &&
                    option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }
        }

        // Pick the smallest of those, assuming we found any
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }


    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mlocationManager == null) {
            mlocationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(LOCATION_SERVICE);
        }
    }


    LocationManager mlocationManager;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 0;
    static boolean status;
    static double distance_v21 = 0.0;
    static TextView points, speed, camVariable;
    static long startTime, endTime;
    static int p = 0;

    //    LocationService_service myService;
    LocationService_21 myService;


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mlocationManager != null) {
//            for (int i = 0; i < locationListenObject.length; i++) {
//                try {
//                    mlocationManager.removeUpdates(locationListenObject[i]);
//                } catch (Exception ex) {
//                    Log.i(TAG, "fail to remove location listners, ignore", ex);
//                }
//            }

        }
    }

    public void bindService() {
        if (status)
            return;
//        Intent i = new Intent(getApplicationContext(), LocationService_service.class);
        Intent i = new Intent(getApplicationContext(), LocationService_21.class);
//        getActivity().bindService(i, sc, BIND_AUTO_CREATE);
        getActivity().startService(i);
        status = true;
        startTime = System.currentTimeMillis();
    }

    void unbindService() {
        if (!status)
            return;
//        Intent i = new Intent(getApplicationContext(), LocationService_service.class);
        Intent i = new Intent(getApplicationContext(), LocationService_21.class);
        getActivity().stopService(i);
//        if (sc != null) {
//            try {
//                getActivity().unbindService(sc);
//
//            } catch (Exception e) {
//                e.getMessage();
//            }
//        }

        status = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (status) {
            unbindService();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera_video, container, false);
        initializeLocationManager();
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mEditor = mPreferences.edit();
        speed = (TextView) view.findViewById(R.id.speed_video);
        points = (TextView) view.findViewById(R.id.points_video);
        camVariable = (TextView) view.findViewById(R.id.descy);
//        speedTExt.setText("Speed: 0 m/sec");
        checkGps();
        tv_timer = (TextView) view.findViewById(R.id.tv_timeVideo);
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

        mEchoText = view.findViewById(R.id.echoText);
        mLayout = view.findViewById(R.id.texture_v19);


        mSockMan = new SocketClass(this);

        if (mRunner == null) {
            mRunner = new Thread(mSockMan);
            mRunner.start();
        }
        //Toast.makeText(this, "Thread Start", Toast.LENGTH_SHORT).show
        delaySome();

        return view;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        mTextureView = (AutofitTextureView) view.findViewById(R.id.texture);
        mButtonVideo = (Button) view.findViewById(R.id.video);
        mButtonVideo.setOnClickListener(this);
        view.findViewById(R.id.info).setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        startBackgroundThread();
        if (mTextureView.isAvailable()) {
            openCamera(mTextureView.getWidth(), mTextureView.getHeight());
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }

    void checkGps() {
        mlocationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        if (!mlocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showGPSDisabledAlertToUser();
        }
    }


    //This method configures the Alert Dialog box.
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

    @Override
    public void onPause() {
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    private void delaySome() {
        final Thread timer = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    startRecordingVideo();
                }
            }
        };
        timer.start();

    }

    final Context context = this.getActivity();
    private Button button;
    private EditText descText;
    Handler stopDelay = new Handler();
    String lol = "";

    public static void setDescription(Context context, String desc) {
        SharedPreferences prefs = context.getSharedPreferences("giscle", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("TAG", desc);
        editor.commit();
    }

    public static String getDescription(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("giscle", 0);
        return prefs.getString("TAG", "");

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.video: {
                String content = descText.getText().toString();
                long t1 = System.currentTimeMillis();
                long end = t1 + 10000;
                if (mIsRecordingVideo) {
                    stopRecordingVideo();
                } else {
                    if (content == null) {
                        Log.w("myApp", "NO TAG ENTERED!\n");
                        setDescription(this.getContext(), content);

                    } else {

                        try {
                            lol = content;
                            stopDelay.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    stopRecordingVideo();
                                }
                            }, 10000);
                            startRecordingVideo();
                        } catch (Exception unname) {
                            Log.w("penis", "ayyoWhynomName");
                        }
                    }

                    // set prompts.xml to alertdialog builder

                    //gets you the contents of edit text
                    //tvTextView.setText(content);
                    // set dialog message

                    // add button listener


                }


                break;
            }
            case R.id.info: {
                Activity activity = getActivity();
                if (null != activity) {
                    new AlertDialog.Builder(activity)
                            .setMessage("This is the temp message")
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                }
                break;
            }
        }
    }

    /**
     * Starts a background thread and its {@link Handler}.
     */
    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    /**
     * Stops the background thread and its {@link Handler}.
     */
    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets whether you should show UI with rationale for requesting permissions.
     *
     * @param permissions The permissions your app wants to request.
     * @param permissions The permissions your app wants to request.
     * @return Whether you can show permission rationale UI.
     */
    private boolean shouldShowRequestPermissionRationale(String[] permissions) {
        if (permissions != null) {
            for (String permission : permissions) {
                if (FragmentCompat.shouldShowRequestPermissionRationale(getParentFragment(), permission)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    /**
     * Requests permissions needed for recording video.
     */

    ///TODO check it yeha tak..
    private void requestVideoPermissions() {
        if (shouldShowRequestPermissionRationale(VIDEO_PERMISSIONS)) {
            new ConfirmationDialog().show(getActivity().getFragmentManager(), FRAGMENT_DIALOG);
        } else {
            FragmentCompat.requestPermissions(this, VIDEO_PERMISSIONS, REQUEST_VIDEO_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult");

        switch (requestCode) {
            case REQUEST_VIDEO_PERMISSIONS:
                if (grantResults.length == VIDEO_PERMISSIONS.length) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            ErrorDialog.newInstance(getString(R.string.permission_request))
                                    .show(getActivity().getFragmentManager(), FRAGMENT_DIALOG);
                            break;
                        }
                    }
                } else {
                    ErrorDialog.newInstance(getString(R.string.permission_request))
                            .show(getActivity().getFragmentManager(), FRAGMENT_DIALOG);
                }
                break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
            }
        }
    }

    private boolean hasPermissionsGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(getActivity(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * Tries to open a {@link CameraDevice}. The result is listened by `mStateCallback`.
     */
    @SuppressWarnings("MissingPermission")
    private void openCamera(int width, int height) {
        if (!hasPermissionsGranted(VIDEO_PERMISSIONS)) {
            requestVideoPermissions();
            return;
        }
        final Activity activity = getActivity();
        if (null == activity || activity.isFinishing()) {
            return;
        }
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            Log.d(TAG, "tryAcquire");
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            String cameraId = null /*manager.getCameraIdList()[0]*/;
            try {
                cameraId = manager.getCameraIdList()[0];
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
            // Choose the sizes for camera preview and video recording
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics
                    .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
            if (map == null) {
                throw new RuntimeException("Cannot get available preview/video sizes");
            }
            mVideoSize = chooseVideoSize(map.getOutputSizes(MediaRecorder.class));
            mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                    width, height, mVideoSize);

            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mTextureView.setAspectRatio(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            } else {
                mTextureView.setAspectRatio(mPreviewSize.getHeight(), mPreviewSize.getWidth());
            }

            configureTransform(width, height);
            mMediaRecorder = new MediaRecorder();
            manager.openCamera(cameraId, mStateCallback, null);
        } catch (CameraAccessException e) {
            Toast.makeText(activity, "Cannot access the camera.", Toast.LENGTH_SHORT).show();
            activity.finish();
        } catch (NullPointerException e) {
            // Currently an NPE is thrown when the Camera2API is used but not supported on the
            // device this code runs.
            ErrorDialog.newInstance(getString(R.string.camera_error))
                    .show(getActivity().getFragmentManager(), FRAGMENT_DIALOG);
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera opening.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeCamera() {
        try {
            mCameraOpenCloseLock.acquire();
            closePreviewSession();
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            if (null != mMediaRecorder) {
                mMediaRecorder.release();
                mMediaRecorder = null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.");
        } finally {
            mCameraOpenCloseLock.release();
        }
    }

    /**
     * Start the camera preview.
     */
    private void startPreview() throws Exception {
//        mCameraDevice.close();
//        mCameraDevice = null;
        if (null == mCameraDevice || !mTextureView.isAvailable() || null == mPreviewSize) {
            return;
        }
        try {
            closePreviewSession();
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

            Surface previewSurface = new Surface(texture);
            mPreviewBuilder.addTarget(previewSurface);

            mCameraDevice.createCaptureSession(Collections.singletonList(previewSurface),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            mPreviewSession = session;
                            updatePreview();
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                            Activity activity = getActivity();
                            if (null != activity) {
//                                Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show();
                                try {
                                    session.stopRepeating();
                                    session.abortCaptures();
                                } catch (CameraAccessException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update the camera preview. {@link #startPreview()} needs to be called in advance.
     */
    private void updatePreview() {
        if (null == mCameraDevice) {
            return;
        }
        try {
            setUpCaptureRequestBuilder(mPreviewBuilder);
            HandlerThread thread = new HandlerThread("CameraPreview");
            thread.start();
            mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void setUpCaptureRequestBuilder(CaptureRequest.Builder builder) {
        builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
    }

    /**
     * Configures the necessary {@link android.graphics.Matrix} transformation to `mTextureView`.
     * This method should not to be called until the camera preview size is determined in
     * openCamera, or until the size of `mTextureView` is fixed.
     *
     * @param viewWidth  The width of `mTextureView`
     * @param viewHeight The height of `mTextureView`
     */
    private void configureTransform(int viewWidth, int viewHeight) throws Exception {
        Activity activity = getActivity();
        if (null == mTextureView || null == mPreviewSize || null == activity) {
            return;
        }
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / mPreviewSize.getHeight(),
                    (float) viewWidth / mPreviewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        }
        mTextureView.setTransform(matrix);
    }

    private void setUpMediaRecorder() throws IOException {
        final Activity activity = getActivity();
        if (null == activity) {
            return;
        }
//        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        if (mNextVideoAbsolutePath == null || mNextVideoAbsolutePath.isEmpty()) {
            mNextVideoAbsolutePath = getVideoFilePath(getActivity());
        }
        mMediaRecorder.setOutputFile(mNextVideoAbsolutePath);
        mMediaRecorder.setVideoEncodingBitRate(10000000);
        mMediaRecorder.setVideoFrameRate(30);
        mMediaRecorder.setVideoSize(mVideoSize.getWidth(), mVideoSize.getHeight());
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
//        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        switch (mSensorOrientation) {
            case SENSOR_ORIENTATION_DEFAULT_DEGREES:
                mMediaRecorder.setOrientationHint(DEFAULT_ORIENTATIONS.get(rotation));
                break;
            case SENSOR_ORIENTATION_INVERSE_DEGREES:
                mMediaRecorder.setOrientationHint(INVERSE_ORIENTATIONS.get(rotation));
                break;
        }
        mMediaRecorder.prepare();
    }

    private String filePath = null;
    private Context bun = this.getContext();

    private String getVideoFilePath(Context context) {
//        final File dir = context.getExternalFilesDir(null);
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
       /* Log.e("FIle", myDir.getAbsolutePath());
        String fname = "";
        File file = new File(myDir, fname);

        Log.w("BUNBUNBUNBUN\n\n\n\n\n", "what the fuck are you doing\n\n\n");

        mfile_name = fname;
        filePath = file.getAbsolutePath();
//                + System.currentTimeMillis() + ".mp4";
        return (myDir == null ? "" : (myDir.getAbsolutePath() + "/"))
                + fname + ".mp4";*/
    }

    /*public void howText() {
        final String fname = "";
        //ides view
        final Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {

                final String lel = "";
                camVariable.setText(getDescription(bun));
                final String lel2 = lel + camVariable.getText().toString();// Shows view
                final String please = fname + lel2;
            }

        }, 3000);

        // After 3 seconds
    }*/

    Handler timeHandler = new Handler();
    private void startRecordingVideo() {
        timeHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopRecordingVideo();
            }
        }, 10000);

                        if (null == mCameraDevice || !mTextureView.isAvailable() || null == mPreviewSize) {
                            return;
                        }
                        try {
                            closePreviewSession();
                            setUpMediaRecorder();
                            SurfaceTexture texture = mTextureView.getSurfaceTexture();
                            assert texture != null;
                            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
                            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
                            List<Surface> surfaces = new ArrayList<>();

                            // Set up Surface for the camera preview
                            Surface previewSurface = new Surface(texture);
                            surfaces.add(previewSurface);
                            mPreviewBuilder.addTarget(previewSurface);

                            // Set up Surface for the MediaRecorder
                            Surface recorderSurface = mMediaRecorder.getSurface();
                            surfaces.add(recorderSurface);
                            mPreviewBuilder.addTarget(recorderSurface);

                            // Start a capture session
                            // Once the session starts, we can update the UI and start recording
                            mCameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {

                                @Override
                                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                                    mPreviewSession = cameraCaptureSession;
                                    timerStart();
                                    updatePreview();
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // UI
                                            mButtonVideo.setText(R.string.stop);
                                            mIsRecordingVideo = true;
                                            mEditor.putString(STARAT_TIME, getCurrentTime()).commit();
                                            // Start recording
                                            mMediaRecorder.start();
                                        }
                                    });
                                }

                                @Override
                                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                                    Activity activity = getActivity();
                                    if (null != activity) {
                                        Toast.makeText(activity, "Failed to record", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }, mBackgroundHandler);
                        } catch (CameraAccessException | IOException e) {
                            e.printStackTrace();
                        }

                    //Hopefully this stops shit at ten seconds




        }

    private void closePreviewSession() {
        if (mPreviewSession != null) {
            mPreviewSession.close();
            mPreviewSession = null;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void writeToEchoText(String str){
        mString = str;
        mHandler.post(new Runnable() {
            public void run() {
                mEchoText.setText(mString);
            }
        });
    }
    private void stopRecordingVideo() {
        // UI
        if (status)
            unbindService();
        p = 0;
        mIsRecordingVideo = false;
        mButtonVideo.setText(R.string.record);
        speed.setText("0 km/sec");

        // Stop recording
        try {
            mPreviewSession.stopRepeating();
            mPreviewSession.abortCaptures();
            mMediaRecorder.stop();
            mMediaRecorder.reset();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Activity activity = getActivity();
        if (null != activity) {
           Toast.makeText(activity, "Video Uploaded: " + mNextVideoAbsolutePath, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Video Uploaded to firebase!");
        }
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
        /*long abc = */
        database.insert(Tables.videoDetails.TABLE_NAME, null, values);
//        Log.e("abc",""+abc);
        database.close();
    }

    private String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        return dateFormat.format(calendar.getTime());
    }


    private void removePreferences() {
        mEditor.remove(MAIN_POINTS);
        mEditor.remove(MAIN_DISTANCE);
        mEditor.remove(STARAT_TIME);
        mEditor.remove(ALL_LATITUDE);
        mEditor.remove(ALL_LONGITUDE);

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

    /**
     * Compares two {@code Size}s based on their areas.
     */
    static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }

    }

    public static class ErrorDialog extends DialogFragment {

        private static final String ARG_MESSAGE = "message";

        public static ErrorDialog newInstance(String message) {
            ErrorDialog dialog = new ErrorDialog();
            Bundle args = new Bundle();
            args.putString(ARG_MESSAGE, message);
            dialog.setArguments(args);
            return dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Activity activity = getActivity();
            return new AlertDialog.Builder(activity)
                    .setMessage(getArguments().getString(ARG_MESSAGE))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            activity.finish();
                        }
                    })
                    .create();
        }

    }

    public static class ConfirmationDialog extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final android.app.Fragment parent = getParentFragment();
            return new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.permission_request)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FragmentCompat.requestPermissions(parent, VIDEO_PERMISSIONS,
                                    REQUEST_VIDEO_PERMISSIONS);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    parent.getActivity().finish();
                                }
                            })
                    .create();
        }
    }
}



