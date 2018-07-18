package com.organization.Giscle.giscle_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.organization.Giscle.giscle_app.giscle_socket.*;
import com.organization.Giscle.giscle_app.Authentication.Login;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Intent i = new Intent(MainActivity.this, UserDashboard.class);
        //startActivity(i);

    }
}
