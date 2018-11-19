package com.organization.Giscle.giscle_app.User_Fragment.Trip;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.organization.Giscle.giscle_app.MainActivity;
import com.organization.Giscle.giscle_app.R;
import com.organization.Giscle.giscle_app.Variable.CONSTANT;
import com.organization.Giscle.giscle_app.Variable.Trip_record_variable;
import com.organization.Giscle.giscle_app.local_db.Tables;
import com.organization.Giscle.giscle_app.local_db.db_helper;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

public class SingleVideo extends Fragment
        implements
        OnMapReadyCallback,
        ResultCallback<Status> {
    private TextView totlaTime, path, point, upload;
    private Button btnUpload;
    private db_helper db_helper;
    //    private VideoView videoView;
    Trip_record_variable recordVariable;
    private GoogleMap googleMap;
    //    private MapFragment mapFragment;
    private SupportMapFragment mapFragment;
    //    private GoogleApi googleApiClient;
    private int backStatus = 0;
//    private ProgressBar progressBar;

    // Initialize GoogleMaps
    Bundle bundle;

    private void initGMaps() throws Exception {
        if (mapFragment == null) {
//            mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map);

            mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                    .findFragmentById(R.id.map);

//            SupportMapFragment mapFragment = SupportMapFragment.newInstance();
//            FragmentTransaction fragmentTransaction =
//                    getChildFragmentManager().beginTransaction();
//            fragmentTransaction.replace(R.id.map, mapFragment);
//            fragmentTransaction.commit();
            mapFragment.getMapAsync(this);
        }
    }

    private void init(View view) {
        totlaTime = (TextView) view.findViewById(R.id.total_time_video_indivual);
        path = (TextView) view.findViewById(R.id.path_time_video_indivual);
        point = (TextView) view.findViewById(R.id.point_time_video_indivual);
        upload = (TextView) view.findViewById(R.id.upload_status_video_indivual);
        btnUpload = (Button) view.findViewById(R.id.btn_upload_video_indivual);
//        progressBar = (ProgressBar) view.findViewById(R.id.progress_singleVideo);
        db_helper = new db_helper(view.getContext());
//        videoView = (VideoView) view.findViewById(R.id.video_view_single);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        FragmentManager fm = getActivity().getSupportFragmentManager();
//        Fragment fragment = (fm.findFragmentById(R.id.map));
//        FragmentTransaction ft = fm.beginTransaction();
//        ft.remove(fragment);
//        ft.commit();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_single_video, null, false);
        View view = null;
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
        }
        try {
            view = inflater.inflate(R.layout.fragment_single_video, container, false);
            init(view);
        } catch (InflateException e) {
            e.getMessage();
        }
        Bundle bundle = getArguments();
        try {
//            initGMaps(view);
            initGMaps();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bundle != null) {
            if (bundle.containsKey("specificVideo")) {
                recordVariable = (Trip_record_variable) bundle.getSerializable("specificVideo");
                backStatus = bundle.getInt("abcd");
//                Uri uri = Uri.parse(recordVariable.getFileName());
//                videoView.setVideoURI(new Uri.parse(recordVariable.getFileName()));
//                File myDir = new File(Environment.getExternalStorageDirectory() + "/Giscle Video/"+recordVariable.getFileName());
//                if (myDir.exists()){
//                    videoView.start();


//ok comment..
//                   try{
//                       android.widget.MediaController mediacontroller = new android.widget.MediaController(
//                               getActivity());
//                       mediacontroller.setAnchorView(videoView);
//                       // Get the URL from String VideoURL
//                       Uri video = Uri.parse(Environment.getExternalStorageDirectory() + "/Giscle Video/"+recordVariable.getFileName());
//                       videoView.setMediaController(mediacontroller);
//                       videoView.setVideoURI(video);
//
////                       videoView.setVideoURI(Uri.parse(Environment.getExternalStorageDirectory() + "/Giscle Video/"+recordVariable.getFileName()));
//                    videoView.start();
//                   }
//                   catch (Exception e){
//                       e.getMessage();
//                       Toast.makeText(getApplicationContext(), "Video not found..!!", Toast.LENGTH_SHORT).show();
//                   }
//yeha tk ka ok wala comment hai. videoView ka..

//                }
//                else{
//                    Toast.makeText(getActivity(), "FIle not found in Local Directory..!!", Toast.LENGTH_SHORT).show();
//                }
//                videoView.setVideoURI(uri);
                path.setText("File Name: " + recordVariable.getFileName());
//                upload.setText();
                totlaTime.setText(recordVariable.getTime());
                if (recordVariable.getUploading().equalsIgnoreCase("true")) {
                    uploadStatus(true);
                    point.setText("Points:: " + recordVariable.getPoints());
                    upload.setText("Upload Status:: Uploaded");
                } else {
                    uploadStatus(false);
                    point.setText("Points:: " + recordVariable.getPoints() + " , Gain after uploading");
                    upload.setText("Upload Status:: Not Uploaded");
                }

            } else {
                Toast.makeText(getActivity(), "Video not found..!!", Toast.LENGTH_SHORT).show();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.frame_main_user, new TripLog()).commit();

            }
        }
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload_video_firebase(recordVariable.getFilePath(), recordVariable.getFileName());

            }
        });


        return view;
    }


    private void showLine(String lat, String lon) {

        List<LatLng> points = extractCoordinate(lat, lon);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        MarkerOptions startMarker = new MarkerOptions()
                .position(points.get(0))
                .title("Starting Point");
//        LAT = Double.parseDouble(recordVariable.getFinal_lat());
//        LON = Double.parseDouble(recordVariable.getFinal_long());
        MarkerOptions endMarker = new MarkerOptions()
                .position(points.get(points.size() - 1))
                .title("Destination point");
        if (points.size() > 0) {
            googleMap.addMarker(startMarker);
            googleMap.addMarker(endMarker);
            PolygonOptions polygonOptions = new PolygonOptions();
            polygonOptions.addAll(points);
            polygonOptions
                    .strokeWidth(20)
                    .clickable(true)
                    .strokeColor(Color.BLACK);
            googleMap.addPolygon(polygonOptions);
            builder.include(points.get(points.size() - 1));
//            LatLngBounds bounds = builder.build();
            float padding = 25f;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(points.get(0), padding);
            googleMap.moveCamera(cameraUpdate);
            googleMap.animateCamera(cameraUpdate);
        }
    }

    private List<LatLng> extractCoordinate(String lat, String lon) {
        List<LatLng> coordinates = new ArrayList<>();
        if (!lat.equalsIgnoreCase("not found") && !lon.equalsIgnoreCase("not found")) {
            String[] arraylat = lat.split(",");
            String[] arrayLon = lon.split(",");

            if (arraylat.length == arrayLon.length) {

                for (int i = 0; i < arraylat.length; i++) {
                    double lati = Double.parseDouble(arraylat[i]);
                    double longi = Double.parseDouble(arrayLon[i]);
                    coordinates.add(new LatLng(lati, longi));
                }
            }
            return coordinates;
        }

        coordinates.add(new LatLng(24.8994892, 67.0467055));
        return coordinates;

    }

    private void uploadVideo() {
        SQLiteDatabase database = db_helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Tables.videoDetails.COLUMN_UPLOAD_STATUS, "true");
        recordVariable.setUploading("true");
        String selections = Tables.videoDetails._ID + "=" + recordVariable.get_id();
        String[] videoData = recordVariable.getFileName().split("0");
        database.update(Tables.videoDetails.TABLE_NAME, values, selections, null);
        upload.setText("Upload Status:: Uploaded");
        uploadStatus(true);
        int previousPoint = getPreviousPoints();
        previousPoint += Integer.valueOf(recordVariable.getPoints());
        recordVariable.setPoints("" + previousPoint);
        ContentValues values1 = new ContentValues();
        values1.put(Tables.userTable.COLUMN_POINTS, previousPoint);
        database.update(Tables.userTable.TABLE_NAME, values1, null, null);
        videoDetailSaveInFirebase(recordVariable);
        database.close();
    }

    private void videoDetailSaveInFirebase(Trip_record_variable var) {
        FirebaseAuth currentUser = FirebaseAuth.getInstance();
        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid());
        userDb.child("points").setValue(var.getPoints());
        DatabaseReference videoDb = FirebaseDatabase.getInstance().getReference().child("Videos");
        videoDb.child(currentUser.getUid()).child("" + getCurrentTime() + " " + recordVariable.getFileName()).setValue(var);
    }

    DecimalFormat df = new DecimalFormat("##");

    public void upload_video_firebase(String filename, String filename1) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        StorageReference storageref = storage.getReferenceFromUrl("gs://giscle-facial-app.appspot.com").child(auth.getUid()).child(filename1 + ".mp4");
//        progressBar.setVisibility(View.VISIBLE);
        btnUpload.setEnabled(false);
//        System.out.println(filename);

        //Upload input stream to Firebase
        FileInputStream fileinputstream = null;
        try {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Uploading..!!");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            fileinputstream = new FileInputStream(filename + ".mp4");
//            Log.e("fileSize", "fileSize:: " + fileSize(recordVariable.getFilePath() + ".mp4"));
            final int bytes = getByte(fileSize(recordVariable.getFilePath() + ".mp4"));
//            Log.e("fileSize", "fileSize:: " + bytes);

            UploadTask uploadTask = storageref.putStream(fileinputstream);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    if (taskSnapshot != null) {
                        Toast.makeText(getActivity(), "Video uploaded successfully!!", Toast.LENGTH_SHORT).show();
                        recordVariable.setFirebaseVideoUrl(taskSnapshot.getDownloadUrl().toString());
//                        progressBar.setVisibility(View.GONE);
                        uploadVideo();
                    } else {
                        Toast.makeText(getApplicationContext(), "Did not upload successfully.", Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * (taskSnapshot.getBytesTransferred() / bytes));
//                            String data = "%2",progess;
//                    Log.e("logs", taskSnapshot.getBytesTransferred() + ":" + taskSnapshot.getTotalByteCount() + " and overall:: " + (100 * (taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount())));
                    progressDialog.setTitle("Please Wait while uploading.");
                    progressDialog.setMessage(" Now Uploading.....");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
//                    progressBar.setVisibility(View.GONE);
                    btnUpload.setEnabled(true);
                    Toast.makeText(getActivity(), "Video uploaded failed!!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
//            progressDialog.dismiss();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Kindly check your internet connection", Toast.LENGTH_SHORT).show();
//            progressBar.setVisibility(View.GONE);
        }
    }

    private int getByte(double mb) {
//        return (int) (1048576*mb);
        return (int) (1000000 * mb);
    }

    private int fileSize(String url) {
        File file = new File(url);
        int file_size = Integer.parseInt(String.valueOf(file.length() / 1024));
        return file_size;
    }

    private int getPreviousPoints() {
        int get = 0;
        SQLiteDatabase database = new db_helper(getApplicationContext()).getReadableDatabase();
        String[] projection = {Tables.userTable.COLUMN_POINTS};
        Cursor cursor = database.query(Tables.userTable.TABLE_NAME, projection, null, null, null, null, null);
        if (cursor != null && cursor.getCount() == 1) {
            while (cursor.moveToNext()) {

                get = cursor.getInt(cursor.getColumnIndex(Tables.userTable.COLUMN_POINTS));
                return get;
            }
//            Toast.makeText(getActivity(), "Points " + get, Toast.LENGTH_SHORT).show();

        }
        return 0;
    }

    private String getCurrentTime() {
        Calendar c = Calendar.getInstance();
//        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getView() == null) {
            return;
        }

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    // handle back button's click listener
                    TripLog tripLog = new TripLog();
                    if (backStatus == 1) {
                        Bundle bundle = new Bundle();
                        tripLog = new TripLog();
                        bundle.putString(CONSTANT.NOT_UPLOADED_VIDEO, "notUplaod");
                        tripLog.setArguments(bundle);
                    } else {
                        tripLog = new TripLog();
                    }
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.frame_main_user, new TripLog()).commit();
//                    getActivity().finish();
                    googleMap.clear();
                    mapFragment.onStop();
                    return true;
                }
                return false;
            }
        });
    }

    private void uploadStatus(boolean status) {
        if (status) {
            btnUpload.setEnabled(false);
        } else {
            btnUpload.setEnabled(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getArguments().remove("specificVideo");

    }

    @Override
    public void onResult(@NonNull Status status) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (!recordVariable.getAll_lat().equalsIgnoreCase("not found") && !recordVariable.getAll_long().equalsIgnoreCase("not found"))
            showLine(recordVariable.getAll_lat(), recordVariable.getAll_long());
    }

}
