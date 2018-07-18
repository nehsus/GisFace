package com.organization.Giscle.giscle_app.User_Fragment.Camera_frag;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.organization.Giscle.giscle_app.*;
import com.organization.Giscle.giscle_app.R;

public class Camera_act extends AppCompatActivity {
    private boolean permission = false;
    private static final int REQUEST_PERMISSIONS_LOCATION = 100;

    private void checkPermission() {
        Log.e("Camera_act", "CheckPermission()");

        permission = (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.organization.Giscle.giscle_app.R.layout.activity_camera_act);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (null == savedInstanceState) {
            checkPermission();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (!permission) {
                    askPermission();
                }
                if (permission) {
                    getFragmentManager().beginTransaction()
                            .replace(R.id.camera_frame, camera_fragment.newInstance())
                            .commit();
                } else {
                    Intent i = new Intent(Camera_act.this, UserDashboard.class);
                    startActivity(i);
                    finish();
                    Toast.makeText(getApplicationContext(), "Please allow Location permission", Toast.LENGTH_LONG).show();
                }
            } else {

                getSupportFragmentManager().beginTransaction().replace(R.id.camera_frame, camera_fragment_19.newInstance()).commit();
//                Toast.makeText(this, "This android version did not allow to use camera\n try to upgrade your Android version.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                REQUEST_PERMISSIONS_LOCATION
        );

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permission = true;

                } else {
                    Toast.makeText(this, "Android version did not allow to use camera\nPlease update your version", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(Camera_act.this, UserDashboard.class);
                    startActivity(i);
                    finish();
                    Toast.makeText(getApplicationContext(), "Please allow permissions", Toast.LENGTH_LONG).show();
                }
            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(Camera_act.this, UserDashboard.class);
        startActivity(i);
        finish();
    }
}
