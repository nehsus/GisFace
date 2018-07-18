package com.organization.Giscle.giscle_app.Intro;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.crashlytics.android.Crashlytics;
import com.organization.Giscle.giscle_app.Authentication.Login;
import com.organization.Giscle.giscle_app.R;
import com.organization.Giscle.giscle_app.UserDashboard;
import com.organization.Giscle.giscle_app.local_db.Tables;
import com.organization.Giscle.giscle_app.local_db.db_helper;

import io.fabric.sdk.android.Fabric;

public class Splash extends AppCompatActivity {
    db_helper db_helper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        db_helper = new db_helper(this);
        final Intent i = getIntentActivity();
        final Thread timer = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(3600);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    startActivity(i);
                    finish();
                }
            }
        };
        timer.start();
    }

    private Intent getIntentActivity() {
        SQLiteDatabase database = db_helper.getReadableDatabase();
        Cursor cursor = database.query(Tables.userTable.TABLE_NAME, null, null, null, null, null, null);

        if (cursor != null && cursor.getCount() == 1) {
            return new Intent(Splash.this, UserDashboard.class);
        }
        deleteUser(database);
        database.close();
        return new Intent(Splash.this, Login.class);
    }

    private void deleteUser(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.delete(Tables.userTable.TABLE_NAME, null, null);
    }
}
