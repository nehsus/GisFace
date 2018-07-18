package com.organization.Giscle.giscle_app;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.organization.Giscle.giscle_app.Authentication.Login;
import com.organization.Giscle.giscle_app.User_Fragment.FAQ;
import com.organization.Giscle.giscle_app.User_Fragment.Home_frag.Home;
import com.organization.Giscle.giscle_app.User_Fragment.Privicy;
import com.organization.Giscle.giscle_app.User_Fragment.Profile;
import com.organization.Giscle.giscle_app.User_Fragment.Trip.TripLog;
import com.organization.Giscle.giscle_app.Variable.CONSTANT;
import com.organization.Giscle.giscle_app.Variable.user_var;
import com.organization.Giscle.giscle_app.local_db.Tables;
import com.organization.Giscle.giscle_app.local_db.db_helper;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserDashboard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    db_helper db_helper;
    private int REQUEST_CODE_GALLLERY = 111;
    //    private CircleImageView imageView;
    private user_var userVar;
//    private db_helper helper;

    private void deleteColumns() {
        SQLiteDatabase database = db_helper.getWritableDatabase();
        database.delete(Tables.userTable.TABLE_NAME, null, null);
        database.close();
    }

//    class DownloadImage extends AsyncTask<user_var, Void, user_var> {
//
//        ProgressDialog pd;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            pd = new ProgressDialog(UserDashboard.this);
//            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            pd.setTitle("Please Wait");
//            pd.setMessage("getting data from server..");
//            pd.setCancelable(false);
//            pd.show();
//        }
//
//        @Override
//        protected user_var doInBackground(user_var... params) {
////            String imageUr = params[0].getImageAvtar();
////            try {
////                InputStream id = new URL(imageUr).openStream();
////                mainBitmap = BitmapFactory.decodeStream(id);
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
//
//            return params[0];
//        }
//
//        @Override
//        protected void onPostExecute(user_var var) {
//            super.onPostExecute(var);
//            SQLiteDatabase database = db_helper.getWritableDatabase();
//            ContentValues values = new ContentValues();
//            values.put(Tables.userTable.COLUMN_EMAIL, var.getEmail());
//            values.put(Tables.userTable.COLUMN_NAME, var.getName());
//            values.put(Tables.userTable.COLUMN_TYPE, var.getType());
//            values.put(Tables.userTable.COLUMN_NUMBER, var.getNumber());
//            values.put(Tables.userTable.COLUMN_POINTS, var.getPoints());
//
////            if (mainBitmap != null) {
//////                Glide.with(getApplicationContext()).load(mainBitmap).into(imageView);
//////                Glide.with(getApplicationContext()).load(var.getImageAvtar()).into(imageView);
////                imageView.setImageBitmap(mainBitmap);
//////                convertBitmapToByte(mainBitmap);
////            } else {
////                Glide.with(getApplicationContext()).load(getResources().getDrawable(R.drawable.user_profile)).into(imageView);
//////                convertImageIntoByte();
////            }
//
////            values.put(Tables.userTable.COLUMN_AVTAR, makeByteImageFromImageView(imageView));
//
//            database.insert(Tables.userTable.TABLE_NAME, null, values);
//            pd.dismiss();
//            database.close();
//
//
//        }
//
//
//    }


    private void saveDataInDatabase(user_var var) {
        SQLiteDatabase database = db_helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Tables.userTable.COLUMN_EMAIL, var.getEmail());
        values.put(Tables.userTable.COLUMN_NAME, var.getName());
        values.put(Tables.userTable.COLUMN_TYPE, var.getType());
        values.put(Tables.userTable.COLUMN_NUMBER, var.getNumber());
        values.put(Tables.userTable.COLUMN_POINTS, var.getPoints());
        database.insert(Tables.userTable.TABLE_NAME, null, values);
        database.close();
    }

    private void initialAskForPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                1
        );

    }

    private boolean getUserDetails() {
        SQLiteDatabase database = new db_helper(this).getReadableDatabase();
        Cursor cursor = database.query(Tables.userTable.TABLE_NAME, null, null, null, null, null, null);
        if (cursor.getCount() == 1) {
            database.close();
            return true;
        }

        database.close();
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void setDataFromSQLite() {
        SQLiteDatabase database = db_helper.getReadableDatabase();
        Cursor cursor = database.query(Tables.userTable.TABLE_NAME, null, null, null, null, null, null);
        if (cursor != null && cursor.getCount() == 1) {
            user_var var = null;
            while (cursor.moveToNext()) {
                var = new user_var(
//                        cursor.getInt(cursor.getColumnIndex(Tables.userTable._ID)),
                        cursor.getString(cursor.getColumnIndex(Tables.userTable.COLUMN_NAME)),

//                        cursor.getBlob(cursor.getColumnIndex(Tables.userTable.COLUMN_AVTAR)),
                        cursor.getString(cursor.getColumnIndex(Tables.userTable.COLUMN_EMAIL)),
                        cursor.getString(cursor.getColumnIndex(Tables.userTable.COLUMN_TYPE)),
                        cursor.getString(cursor.getColumnIndex(Tables.userTable.COLUMN_POINTS)),
                        cursor.getString(cursor.getColumnIndex(Tables.userTable.COLUMN_NUMBER)));
//                updateUi(var.getAvtar());
                return;
            }
//            Toast.makeText(this, "Welcome:: " + (var.getName().length() == 0 ? "User" : var.getName().toString()), Toast.LENGTH_SHORT).show();

        }
    }

//    private void updateUi(byte[] image) {
//        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
//        imageView.setImageBitmap(bitmap);
//    }

    ///for testing
    private void getUserProfileFirebase() {
        if (getUserDetails()) {
            setDataFromSQLite();
        } else {
            deleteColumns();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users");
                userDb.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String uid = (String) dataSnapshot.child("uId").getValue();
                        String name = (String) dataSnapshot.child("name").getValue();
                        String email = (String) dataSnapshot.child("email").getValue();
                        String type = (String) dataSnapshot.child("type").getValue();
                        Log.e("int", "" + dataSnapshot.child("points").getValue());
                        Object o = dataSnapshot.child("points").getValue();
                        String number = (String) dataSnapshot.child("number").getValue();
                        user_var var = new user_var(uid, name, email, type,  dataSnapshot.child("points").getValue().toString(), number);
//                        user_var var = new user_var(uid, name, imageUrl, email, type, (Long) dataSnapshot.child("points").getValue(), number);
                        saveDataInDatabase(var);

//                        Toast.makeText(UserDashboard.this, "name" + name, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } else {
                Toast.makeText(this, "User details not found..!!", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private byte[] makeByteImageFromImageView(CircleImageView imageView) {

        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db_helper = new db_helper(this);
//        Intent i = getIntent();
        initialAskForPermission();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);
        View hView = navigationView.getHeaderView(0);
//        imageView = (CircleImageView) hView.findViewById(R.id.imageView_user_dash);
        //getting data from firebase..
        getUserProfileFirebase();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        TextView uploadData = (TextView) hView.findViewById(R.id.upload_btn_user);
        uploadData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissioin()) {
                    showGallery();
                } else {
                    askForPermission();
                }
            }
        });


        //abhi esko aise rehne dete hai. hosakte hai bad me need hoto,, abhi esko aise hi rehene dete hai..
//        if (bundle != null) {
//            new Dialog_detail_video(this,bundle).show();
        changeFragment(new Home());
//        } else {
//            changeFragment(new Home());
//        }

    }

    private void askForPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                REQUEST_CODE_GALLLERY
        );

    }

    private Bitmap getBitmapFromByte(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    private boolean checkPermissioin() {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_GALLLERY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showGallery();
            } else {
                Toast.makeText(this, "You  don't have permission to Acc+ess the Storage", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == 1) {
            boolean check = true;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    check = false;
                }
            }
            if (!check) {
                Toast.makeText(this, "Permission not granted..!!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void showGallery() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, REQUEST_CODE_GALLLERY);
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
                matrix, false);

        return resizedBitmap;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GALLLERY && resultCode == RESULT_OK && data != null) {
//            try {
//                Uri path = data.getData();
//                File file = new File(path.getPath());
//                long size = file.length();
//                Log.i("size=", size + "");
//                if (size > 1600000) { // 0.2MB
//                    Toast.makeText(this, "Image must less than 1MB.", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                try {
//                    InputStream inputStream = this.getContentResolver().openInputStream(path);
//                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
////                    bitmap = getResizedBitmap(bitmap,40,40);
////                    imageView.setImageBitmap(bitmap);
////                    makeByteImage(imageView);
//                    udpateImageInFirebase(path);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
////                makeByteImage(imageView);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }

    }

//    private void makeByteImage(CircleImageView imageView) {
//        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
//        byte[] bytes = outputStream.toByteArray();
//        updateImageInDatabase(bytes);
//    }

    private void updateImageInDatabase(byte[] bytes) {
        SQLiteDatabase database = db_helper.getWritableDatabase();
        ContentValues values = new ContentValues();
//        values.put(Tables.userTable.COLUMN_AVTAR, bytes);
        database.update(Tables.userTable.TABLE_NAME, values, null, null);
        database.close();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            changeFragment(new Home());
        } else if (id == R.id.nav_trip) {
            changeFragment(new TripLog());
        } else if (id == R.id.nav_upload) {
            TripLog tripLog = new TripLog();
            Bundle bundle = new Bundle();
            bundle.putString(CONSTANT.NOT_UPLOADED_VIDEO, "notUplaod");
            tripLog.setArguments(bundle);
            changeFragment(tripLog);
        } else if (id == R.id.nav_privcy) {
            changeFragment(new Privicy());
        } else if (id == R.id.nav_faq) {
            changeFragment(new FAQ());
        } else if (id == R.id.nav_profile) {
            changeFragment(new Profile());
        } else if (id == R.id.nav_logout) {
            Logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


//    private void udpateImageInFirebase(Uri uri) {
//        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//        if (mAuth != null) {
//            StorageReference storageReference = firebaseStorage.getReference().child("Users_profliePics").child(mAuth.getCurrentUser().getUid());
//            UploadTask task = storageReference.putFile(uri);
//            task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    Toast.makeText(UserDashboard.this, "Profile Image updated successfully", Toast.LENGTH_SHORT).show();
////                    makeByteImage(imageView);
//                }
//            })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(UserDashboard.this, "Kindly check your internet connectivity and retry again", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//
//        } else {
//            Toast.makeText(this, "System error: kindly login again.", Toast.LENGTH_SHORT).show();
//            Logout();
//        }
//    }

    private void Logout() {
        FirebaseAuth.getInstance().signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    deleteData();
                }
            }
        });
        LoginManager.getInstance().logOut();
        deleteData();
    }

    private void deleteData() {
        SQLiteDatabase database = db_helper.getWritableDatabase();
        database.delete(Tables.userTable.TABLE_NAME, null, null);
        database.delete(Tables.videoDetails.TABLE_NAME,null,null);
        database.close();
        Intent i = new Intent(UserDashboard.this, Login.class);
        startActivity(i);
        finish();
    }

    private void changeFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_main_user, fragment).commit();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
