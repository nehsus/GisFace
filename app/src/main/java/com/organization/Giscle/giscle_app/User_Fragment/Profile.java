package com.organization.Giscle.giscle_app.User_Fragment;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.organization.Giscle.giscle_app.R;
import com.organization.Giscle.giscle_app.Shopping;
import com.organization.Giscle.giscle_app.User_Fragment.Home_frag.Home;
import com.organization.Giscle.giscle_app.Variable.user_var;
import com.organization.Giscle.giscle_app.local_db.Tables;
import com.organization.Giscle.giscle_app.local_db.db_helper;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class Profile extends Fragment {


    private TextView points;
    private CircleImageView imageView;
    private EditText name, email, number;
    private user_var user_var;
    private Button radeemBtn;
    DatabaseReference mUserDb;

    private int REQUEST_CODE_GALLLERY = 111;
    Uri path = Uri.parse("android.resource://com.organization.giscle/" + R.drawable.user_profile);


    private void variableInit(View view) {
        points = (TextView) view.findViewById(R.id.tv_earn_point_profile);
        imageView = (CircleImageView) view.findViewById(R.id.imageView_user_dash_profile);
        name = (EditText) view.findViewById(R.id.et_name_profile);
        email = (EditText) view.findViewById(R.id.et_email_profile);
//        name = (TextView) view.findViewById(R.id.et_name_profile);
//        email = (TextView) view.findViewById(R.id.et_email_profile);
        radeemBtn = (Button) view.findViewById(R.id.btn_radeem_profile);
        number = (EditText) view.findViewById(R.id.et_number_profile);


        getUserDetails();

//        convertByteIntoBitmap();
    }

    private void setDataInFields() {
        name.setText(user_var.getName());
        email.setText(user_var.getEmail());
        points.setText("" + user_var.getPoints());

        number.setText(user_var.getNumber());
    }

    private void getUserDetails() {
        SQLiteDatabase database = new db_helper(getContext()).getReadableDatabase();
        Cursor cursor = database.query(Tables.userTable.TABLE_NAME, null, null, null, null, null, null, null);
//        Log.e("count", "" + cursor.getCount());
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                user_var = new user_var(
//                        cursor.getInt(cursor.getColumnIndex(Tables.userTable._ID)),
                        cursor.getString(cursor.getColumnIndex(Tables.userTable.COLUMN_NAME)),
//                        cursor.getBlob(cursor.getColumnIndex(Tables.userTable.COLUMN_AVTAR)),
                        cursor.getString(cursor.getColumnIndex(Tables.userTable.COLUMN_EMAIL)),
                        cursor.getString(cursor.getColumnIndex(Tables.userTable.COLUMN_TYPE)),
                        cursor.getString(cursor.getColumnIndex(Tables.userTable.COLUMN_POINTS)),
                        cursor.getString(cursor.getColumnIndex(Tables.userTable.COLUMN_NUMBER)));
            }
            setDataInFields();
        }
    }

//    private void convertByteIntoBitmap() {
//        if (user_var.getAvtar() != null) {
//            Bitmap bitmap = BitmapFactory.decodeByteArray(user_var.getAvtar(), 0, user_var.getAvtar().length);
//            imageView.setImageBitmap(bitmap);
//        }
//    }

    private void updateDetails() {
        if (!TextUtils.isEmpty(name.getText().toString()) && !TextUtils.isEmpty(email.getText().toString()) && !TextUtils.isEmpty(number.getText().toString())) {
            if (number.getText().toString().length() >= 10 && number.getText().toString().length() < 12) {
                String emailAddress = email.getText().toString().trim();
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                if (emailAddress.matches(emailPattern) && emailAddress.length() > 0) {
                    SQLiteDatabase database = new db_helper(getActivity()).getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put(Tables.userTable.COLUMN_NAME, name.getText().toString());
                    values.put(Tables.userTable.COLUMN_EMAIL, emailAddress);
                    values.put(Tables.userTable.COLUMN_NUMBER, (number.getText().toString() == null ? "" : number.getText().toString()));
                    database.update(Tables.userTable.TABLE_NAME, values, null, null);
                    Toast.makeText(getActivity(), "Data has been updated.", Toast.LENGTH_SHORT).show();
                    database.close();
                } else {
                    Toast.makeText(getActivity(), "Email not valid..", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Invalid mobile number.\nTry another number", Toast.LENGTH_SHORT).show();
            }

            //User instance

            user_var user = new user_var(mAuth.getCurrentUser().getUid().toString(),name.getText().toString(), email.getText().toString(), "none", points.getText().toString(), number.getText().toString());
            uplaodFileAndData(user);

        } else {
            Toast.makeText(getActivity(), "Enter details first.", Toast.LENGTH_SHORT).show();
        }

    }
    FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Userdatabase
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        setHasOptionsMenu(true);
        mAuth = FirebaseAuth.getInstance();
        getActivity().setTitle("User Profile");
        variableInit(view);

        radeemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.frame_main_user, new Shopping()).commit();

            }
        });

//        radeemBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getActivity(), "Working onit..!!", Toast.LENGTH_SHORT).show();
//            }
//        });

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
//            case R.id.nav_update_image:
//                if (checkPermissioin()) {
//                    showGallery();
//                } else {
//                    askForPermission();
//                }
//                return true;
            case R.id.nav_update_details:
                updateDetails();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    private void showGallery() {
//        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(i, REQUEST_CODE_GALLLERY);
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GALLLERY && resultCode == RESULT_OK && data != null) {
//            try {
//                Uri uri = data.getData();
//                InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
//                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//                imageView.setImageBitmap(bitmap);
//                makeByteImage(imageView);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
        }
    }


//    private void makeByteImage(CircleImageView imageView) {
//        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
//        byte[] bytes = outputStream.toByteArray();
////        updateImageInDatabase(bytes);
//        updateNavHeaderView(bitmap);
//    }

//    private void updateImageInDatabase(byte[] bytes) {
//        SQLiteDatabase database = new db_helper(getActivity()).getWritableDatabase();
//        ContentValues values = new ContentValues();
////        values.put(Tables.userTable.COLUMN_AVTAR, bytes);
//        database.update(Tables.userTable.TABLE_NAME, values, null, null);
//        database.close();
//    }

//    private void updateNavHeaderView(Bitmap bitmap) {
//        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
//        View hView = navigationView.getHeaderView(0);
//        CircleImageView circleImageView = (CircleImageView) hView.findViewById(R.id.imageView_user_dash);
//        circleImageView.setImageBitmap(bitmap);
//    }

    private void askForPermission() {
        ActivityCompat.requestPermissions(
                getActivity(),
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_CODE_GALLLERY
        );

    }


    private boolean checkPermissioin() {
        return (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_profile, menu);
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
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_user, new Home()).commit();
                    return true;
                }
                return false;
            }
        });
    }

    public void uplaodFileAndData(final user_var user) {
//        String uid = user.getuId();

        FirebaseAuth maAuth = FirebaseAuth.getInstance();
        if (maAuth.getCurrentUser() != null) {
            mUserDb = FirebaseDatabase.getInstance().getReference().child("Users");
            mUserDb.child(maAuth.getCurrentUser().getUid()).setValue(user);
//            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
//
//            StorageReference file = storageReference.child("Users_profliePics").child(uid);
//            UploadTask uploadTask = file.putFile(path);
//            uploadTask.addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
////                    ToDO: shery bhai yeha masla a rha hai ke wo en lines me nhe a rha jab ke image upar ja rhi hai us UID ke name se..
//
////                    user.setImageAvtar(taskSnapshot.getDownloadUrl().toString());
////                    startActivity(new Intent(Login.this, UserDashboard.class));
////                    finish();
//                }
//            }).addOnFailureListener(getActivity(), new OnFailureListener() {
//                @Override
//                public void onFailure(Exception e) {
//                    e.getMessage();
//                }
//            });
        } else {
            Toast.makeText(getActivity(), "user authentication carring failed", Toast.LENGTH_SHORT).show();
        }
    }

}