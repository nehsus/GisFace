package com.organization.Giscle.giscle_app.Authentication;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.organization.Giscle.giscle_app.MainActivity;
import com.organization.Giscle.giscle_app.R;
import com.organization.Giscle.giscle_app.Terms;
import com.organization.Giscle.giscle_app.UserDashboard;
import com.organization.Giscle.giscle_app.Variable.CONSTANT;
import com.organization.Giscle.giscle_app.Variable.user_var;
import com.organization.Giscle.giscle_app.local_db.Tables;
import com.organization.Giscle.giscle_app.local_db.db_helper;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Login extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    SignInButton btnSignIn;
    private static final int REQ_CODE = 9001;
    LoginButton loginButton;
    private FirebaseAuth mAuth;
    private static byte[] avtar;
    private db_helper d_helper;
    private static final String TAG = Login.class.getSimpleName();
    private static final int RC_SIGN_IN = 007;
    private CallbackManager callbackManager;
    TextView terms;
    private GoogleApiClient mGoogleApiClient;
    Uri path = Uri.parse("android.resource://com.organization.giscle/" + R.drawable.user_profile);
    private ProgressBar mProgressBar;
    //firebase
    FirebaseDatabase mfirebaseDatabase;
    DatabaseReference mUserDb;
    StorageReference mUserProfile;
    Query mSearchingUser;


    private void askForPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.WAKE_LOCK},
                1
        );

    }

    private void getData() {
        SQLiteDatabase db = d_helper.getReadableDatabase();
        Cursor cursor = db.query(Tables.userTable.TABLE_NAME, null, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Log.e("data", "" + cursor.getString(cursor.getColumnIndex(Tables.userTable.COLUMN_EMAIL)));
            }
        } else {
//            Toast.makeText(this, "No Data found..!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void visibleProgress(boolean abc) {
        mProgressBar.setVisibility(abc ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        d_helper = new db_helper(this);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_login);
        askForPermission();
        firebaseInit();

        loginButton = (LoginButton) findViewById(R.id.login_button_fb);
        callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions("email");
        terms = (TextView) findViewById(R.id.tv_terms_condition);
        terms.setOnClickListener(this);

        btnSignIn = (SignInButton) findViewById(R.id.signIn_google_btn);

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            private ProfileTracker mProfileTracker;

            @Override
            public void onSuccess(final LoginResult loginResult) {
                mProgressBar.setVisibility(View.VISIBLE);
                final Profile[] profileMain = new Profile[1];
                if (Profile.getCurrentProfile() == null) {
                    mProfileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                            mProfileTracker.stopTracking();
                            profileMain[0] = currentProfile;
                            handleFacebookAccessToken(loginResult.getAccessToken(), profileMain[0]);
                        }
                    };
                } else {

                    profileMain[0] = Profile.getCurrentProfile();
                    handleFacebookAccessToken(loginResult.getAccessToken(), profileMain[0]);
                }
                mProgressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, error.getMessage());
                Logout();
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        btnSignIn.setOnClickListener(this);

        btnSignIn.setSize(SignInButton.SIZE_STANDARD);
        btnSignIn.setScopes(gso.getScopeArray());
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            Intent i = new Intent(Login.this, UserDashboard.class);
            startActivity(i);
            finish();
        } else {
        }
    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void getProfileData(Profile profile) {
        if (profile != null) {
            visibleProgress(true);
            user_var user = new user_var(mAuth.getCurrentUser().getUid(), profile.getName(), profile.getLinkUri().toString(), CONSTANT.FB, "0", "");
            saveDataInFirebase(user);

        } else {
            LoginManager.getInstance().logOut();
            Toast.makeText(this, "Profile details did not get.", Toast.LENGTH_SHORT).show();
        }

    }

    private void getProfileData(GoogleSignInResult result) {
//        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
//            Log.e(TAG, "display name: " + acct.getDisplayName());
            user_var user = new user_var(mAuth.getCurrentUser().getUid(), acct.getDisplayName()/*, path.getPath()*/, acct.getEmail().toString(), CONSTANT.GMAIL, "0", "");
            saveDataInFirebase(user);

        } else {
            Toast.makeText(this, "Login not sucessfull..!!", Toast.LENGTH_SHORT).show();
        }
        visibleProgress(false);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.signIn_google_btn:
                signIn();
                break;
            case R.id.tv_terms_condition:
                startActivity(new Intent(Login.this, Terms.class));
                break;
        }

    }

    private void handleFacebookAccessToken(AccessToken token, final Profile profile) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
//                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(Login.this, "Success",
                                    Toast.LENGTH_SHORT).show();
                            getProfileData(profile);
                        } else {
                            LoginManager.getInstance().logOut();
                            Toast.makeText(Login.this, "Authentication error",
                                    Toast.LENGTH_SHORT).show();
                            Logout();
                        }
                    }

                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            mProgressBar.setVisibility(View.VISIBLE);
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.e("abc", "token:: " + result.getSignInAccount().getIdToken());
            if (result.isSuccess() && result.getSignInAccount().getIdToken() != null) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account, result);
            } else {
                Logout();
                Toast.makeText(this, "Not Success login try again later", Toast.LENGTH_SHORT).show();
            }
            mProgressBar.setVisibility(View.GONE);
        } else {

            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct, final GoogleSignInResult result) {
//        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        visibleProgress(true);
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)

                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

//                            Toast.makeText(Login.this, "firebase user :" + user.toString(), Toast.LENGTH_SHORT).show();
                            getProfileData(result);
                        } else {
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Logout();
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                    }
                });
        visibleProgress(false);
    }

    private void saveDataInFirebase(user_var user) {
//        Log.e("data", "saveDataInFirebase");
        if (mAuth.getCurrentUser() != null) {
            checkUserExists(user);
        }
    }

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
        SQLiteDatabase database = new db_helper(this).getWritableDatabase();
        database.delete(Tables.userTable.TABLE_NAME, null, null);
        database.delete(Tables.videoDetails.TABLE_NAME, null, null);
        database.close();
        Intent i = new Intent(Login.this, Login.class);
        startActivity(i);
        finish();
    }

    private void firebaseInit() {
//        Log.e("data", "firebase init");
        mAuth = FirebaseAuth.getInstance();
        mfirebaseDatabase = FirebaseDatabase.getInstance();
        mUserDb = mfirebaseDatabase.getReference().child("Users");
        mUserProfile = FirebaseStorage.getInstance().getReference();

    }

    private void checkUserExists(final user_var user) {
        visibleProgress(true);
        mUserDb = FirebaseDatabase.getInstance().getReference().child("Users");
        mSearchingUser = mUserDb.orderByChild("uId").equalTo(mAuth.getCurrentUser().getUid().toString());
//        Log.e("data", "checkUserexists");

        mSearchingUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    startActivity(new Intent(Login.this, UserDashboard.class));
                    finish();
                } else {
//                    Toast.makeText(Login.this, "Nhi Bhai", Toast.LENGTH_SHORT).show();
                    uplaodFileAndData(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                /*ab[0] = true;*/
            }
        });
        visibleProgress(false);

    }

    public void uplaodFileAndData(final user_var user) {
        String uid = user.getuId();
        if (uid != null) {
            visibleProgress(true);
//            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
//
//            StorageReference file = storageReference.child("Users_profliePics").child(uid);
//            file.putFile(path).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            final Uri download = taskSnapshot.getDownloadUrl();
//                            user.setImageAvtar(download.toString());
            mUserDb.child(user.getuId()).setValue(user);
            visibleProgress(false);
//                        }
//                    })
//                    .addOnFailureListener(this, new OnFailureListener() {
//                        @Override
//                        public void onFailure(Exception e) {
//                            e.getMessage();
//                        }
//                    });
        } else {
//            Toast.makeText(this, "carring null", Toast.LENGTH_SHORT).show();
        }

    }

}
//    SignInButton btnSignIn;
//    private static final int REQ_CODE = 9001;
//    LoginButton loginButton;
//    private static byte[] avtar;
//    private db_helper db_helper;
//    private static final String TAG = MainActivity.class.getSimpleName();
//    private static final int RC_SIGN_IN = 007;
//    Bitmap mainBitmap = null;
//    private CallbackManager callbackManager;
//    TextView terms;
//    private GoogleApiClient mGoogleApiClient;
//
//    private void askForPermission() {
//        ActivityCompat.requestPermissions(
//                this,
//                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
//                1
//        );
//
//    }
//
//    private void getData() {
//        SQLiteDatabase db = db_helper.getReadableDatabase();
//        Cursor cursor = db.query(Tables.userTable.TABLE_NAME, null, null, null, null, null, null);
//        if (cursor != null) {
//            while (cursor.moveToNext()) {
//                Log.e("data", "" + cursor.getString(cursor.getColumnIndex(Tables.userTable.TABLE_NAME)));
//            }
//        } else {
//            Toast.makeText(this, "No Data found..!!", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        FacebookSdk.sdkInitialize(getApplicationContext());
//        setContentView(R.layout.activity_login);
//        db_helper = new db_helper(this);
//        getData();
//        askForPermission();
//        callbackManager = CallbackManager.Factory.create();
//        btnSignIn = (SignInButton) findViewById(R.id.signIn_google_btn);
//        loginButton = (LoginButton) findViewById(R.id.login_button_fb);
//        btnSignIn.setOnClickListener(this);
//        terms = (TextView)findViewById(R.id.tv_terms_condition);
//        terms.setOnClickListener(this);
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .build();
//
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this, this)
//                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//                .build();
//        btnSignIn.setSize(SignInButton.SIZE_STANDARD);
//        btnSignIn.setScopes(gso.getScopeArray());
//        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//
//            private ProfileTracker mProfileTracker;
//
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                //data found..!!
//                if (Profile.getCurrentProfile() == null) {
//                    mProfileTracker = new ProfileTracker() {
//                        @Override
//                        protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
//                            mProfileTracker.stopTracking();
//                            getProfileData(currentProfile);
//                        }
//                    };
//                } else {
//                    Profile profile = Profile.getCurrentProfile();
//                    getProfileData(profile);
//                }
//            }
//
//            @Override
//            public void onCancel() {
//
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//                error.getMessage();
//            }
//        });
//
//    }
//
//    private void signIn() {
//        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
//        startActivityForResult(signInIntent, RC_SIGN_IN);
//    }
//
//    private void getProfileData(Profile profile) {
//        if (profile != null) {
//            if (profile.getProfilePictureUri(200, 200) != null) {
//                String url = profile.getProfilePictureUri(200, 200).toString();
//                saveDataInDatabase(profile.getName(), profile.getLinkUri().toString(), url, CONSTANT.FB);
//            } else {
//                saveDataInDatabase(profile.getName(), profile.getLinkUri().toString(), "", CONSTANT.FB);
//            }
//        } else {
//            Toast.makeText(this, "Profile details did not get.", Toast.LENGTH_SHORT).show();
//        }
//
//    }
//
//    private void getProfileData(GoogleSignInResult result) {
//        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
//        if (result.isSuccess()) {
//            GoogleSignInAccount acct = result.getSignInAccount();
//            Log.e(TAG, "display name: " + acct.getDisplayName());
//            String personName = acct.getDisplayName();
//            String email = acct.getEmail();
//            if (acct.getPhotoUrl() != null) {
//                saveDataInDatabase(personName, email, acct.getPhotoUrl().toString(), CONSTANT.GMAIL);
//            } else {
//                saveDataInDatabase(personName, email, null, CONSTANT.GMAIL);
//            }
//        } else {
//            Toast.makeText(this, "Login not sucessfull..!!", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    @Override
//    public void onClick(View view) {
//        int id = view.getId();
//        switch (id) {
//            case R.id.signIn_google_btn:
//                signIn();
//                break;
//            case R.id.tv_terms_condition:
//                startActivity(new Intent(Login.this, Terms.class));
//                break;
//        }
//
//    }
//
//
//    private void convertImageIntoByte() {
//        Resources res = getResources();
//        Drawable drawable = res.getDrawable(R.drawable.user_profile);
//        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//        avtar = stream.toByteArray();
//    }
//
//    private void saveDataInDatabase(String name, String email, String image, String type) {
//        SQLiteDatabase database = db_helper.getWritableDatabase();
////        Log.e("url", image.getPath());
//        ContentValues values = new ContentValues();
//        if (image == null) {
//            convertImageIntoByte();
//        } else {
//            new DownloadImage().execute(image);
//        }
//        values.put(Tables.userTable.COLUMN_EMAIL, email);
//        values.put(Tables.userTable.COLUMN_NAME, name);
//        values.put(Tables.userTable.COLUMN_TYPE, type);
//        values.put(Tables.userTable.COLUMN_AVTAR, avtar);
//        database.insert(Tables.userTable.TABLE_NAME, null, values);
//        database.close();
//        Intent i = new Intent(Login.this, UserDashboard.class);
//        startActivity(i);
//        finish();
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        callbackManager.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == RC_SIGN_IN) {
//            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//            getProfileData(result);
//        }
//    }
//
////    private void signOut() {
////        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
////            @Override
////            public void onResult(@NonNull Status status) {
////                if (status.isSuccess()) {
////                    //user logout..!!
////                    Toast.makeText(Login.this, "User Logout Successfully..!!", Toast.LENGTH_SHORT).show();
////                }
////            }
////        });
////    }
//
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//
//    }
//
//    private class DownloadImage extends AsyncTask<String, Void, Void> {
//        @Override
//        protected Void doInBackground(String... url) {
//            String imageUr = url[0];
//            try {
//                InputStream id = new URL(imageUr).openStream();
//                mainBitmap = BitmapFactory.decodeStream(id);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            if (mainBitmap != null) {
//                convertBitmapToByte(mainBitmap);
//            } else {
//                convertImageIntoByte();
//            }
//
//        }
//    }
//
//    private void convertBitmapToByte(Bitmap bitmap) {
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        boolean check = bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
//        if (!check) {
//            Toast.makeText(this, "not successfull", Toast.LENGTH_SHORT).show();
//        }
//        if (avtar != null) {
//            avtar = null;
//        }
//        avtar = bos.toByteArray();
//    }
//}
