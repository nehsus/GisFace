package com.organization.Giscle.giscle_app.User_Fragment.Trip;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.organization.Giscle.giscle_app.R;
import com.organization.Giscle.giscle_app.Adapter.Trip_adapter;
import com.organization.Giscle.giscle_app.User_Fragment.Home_frag.Home;
import com.organization.Giscle.giscle_app.Variable.CONSTANT;
import com.organization.Giscle.giscle_app.Variable.Trip_record_variable;
import com.organization.Giscle.giscle_app.local_db.Tables;
import com.organization.Giscle.giscle_app.local_db.db_helper;

import java.util.ArrayList;
import java.util.List;

public class TripLog extends Fragment {
    LinearLayout notFound, found;
    ListView listView;
    db_helper db_helper;
    List<Trip_record_variable> list;

    private void init(View view) {
        notFound = (LinearLayout) view.findViewById(R.id.list_trip_notFound);
        found = (LinearLayout) view.findViewById(R.id.list_trip_layout);
        listView = (ListView) view.findViewById(R.id.trip_log_list);
        db_helper = new db_helper(view.getContext());
        list = new ArrayList<>();
    }

    Trip_adapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trip_log, container, false);
        init(view);
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(CONSTANT.NOT_UPLOADED_VIDEO)){
            if (bundle.getString(CONSTANT.NOT_UPLOADED_VIDEO) != null){
                getNotUploadedVideos();
//                Toast.makeText(getActivity(), "Not uploaded videos if condition", Toast.LENGTH_SHORT).show();
                getActivity().setTitle("Want Uploads Trips??");
            }
//            Toast.makeText(getActivity(), "no Video found..!!\ntry again later", Toast.LENGTH_SHORT).show();
        }
        else{
            getActivity().setTitle("Trip logs");
            getDataFromDatabase();

        }
        adapter = new Trip_adapter(list, getContext());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getActivity(), "" + position, Toast.LENGTH_SHORT).show();
                Bundle bundle = new Bundle();
                bundle.putSerializable("specificVideo", list.get(position));
                SingleVideo singleVideo = new SingleVideo();
                bundle.putInt("abcd",1);
                singleVideo.setArguments(bundle);
                changeFragment(singleVideo);
            }
        });

        return view;
    }

    private void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.frame_main_user, fragment).commit();
    }

    private void getNotUploadedVideos(){
        SQLiteDatabase database = db_helper.getReadableDatabase();
        String selection = Tables.videoDetails.COLUMN_UPLOAD_STATUS +"='false'";

        Cursor cursor = database.query(Tables.videoDetails.TABLE_NAME, null, selection, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            visibileLayout(true);
            while (cursor.moveToNext()) {
                list.add(new Trip_record_variable(
                        cursor.getInt(cursor.getColumnIndex(Tables.videoDetails._ID)),
                        "" + cursor.getInt(cursor.getColumnIndex(Tables.videoDetails.COLUMN_POINTS)),
                        cursor.getString(cursor.getColumnIndex(Tables.videoDetails.COLUMN_DISTANCE)),
                        cursor.getString(cursor.getColumnIndex(Tables.videoDetails.COLUMN_TIME)),
                        cursor.getString(cursor.getColumnIndex(Tables.videoDetails.COLUMN_START_TIME)),
                        cursor.getString(cursor.getColumnIndex(Tables.videoDetails.COLUMN_END_TIME)),
                        cursor.getString(cursor.getColumnIndex(Tables.videoDetails.COLUMN_UPLOAD_STATUS)),
                        cursor.getString(cursor.getColumnIndex(Tables.videoDetails.COLUMN_FILE_NAME)),
                        cursor.getString(cursor.getColumnIndex(Tables.videoDetails.COLUMN_FILE_path)),
                        cursor.getString(cursor.getColumnIndex(Tables.videoDetails.COLUMN_ALL_LATITUDE)),
                        cursor.getString(cursor.getColumnIndex(Tables.videoDetails.COLUMN_ALL_LONGITUDE))

                ));

            }
        } else {
            Toast.makeText(getActivity(), "No Data found..!!", Toast.LENGTH_SHORT).show();
            visibileLayout(false);
        }

    }
    private void getDataFromDatabase() {
        SQLiteDatabase database = db_helper.getReadableDatabase();

        Cursor cursor = database.query(Tables.videoDetails.TABLE_NAME, null, null, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            visibileLayout(true);
            while (cursor.moveToNext()) {
                list.add(new Trip_record_variable(
                        cursor.getInt(cursor.getColumnIndex(Tables.videoDetails._ID)),
                        "" + cursor.getInt(cursor.getColumnIndex(Tables.videoDetails.COLUMN_POINTS)),
                        cursor.getString(cursor.getColumnIndex(Tables.videoDetails.COLUMN_DISTANCE)),
                        cursor.getString(cursor.getColumnIndex(Tables.videoDetails.COLUMN_TIME)),
                        cursor.getString(cursor.getColumnIndex(Tables.videoDetails.COLUMN_START_TIME)),
                        cursor.getString(cursor.getColumnIndex(Tables.videoDetails.COLUMN_END_TIME)),
                        cursor.getString(cursor.getColumnIndex(Tables.videoDetails.COLUMN_UPLOAD_STATUS)),
                        cursor.getString(cursor.getColumnIndex(Tables.videoDetails.COLUMN_FILE_NAME)),
                        cursor.getString(cursor.getColumnIndex(Tables.videoDetails.COLUMN_FILE_path)),
                        cursor.getString(cursor.getColumnIndex(Tables.videoDetails.COLUMN_ALL_LATITUDE)),
                        cursor.getString(cursor.getColumnIndex(Tables.videoDetails.COLUMN_ALL_LONGITUDE))

                ));

            }
        } else {
            Toast.makeText(getActivity(), "No Data found..!!", Toast.LENGTH_SHORT).show();
            visibileLayout(false);
        }
    }


    private void visibileLayout(boolean foundData) {

        if (foundData) {
            found.setVisibility(View.VISIBLE);
            notFound.setVisibility(View.GONE);
        } else {
            found.setVisibility(View.GONE);
            notFound.setVisibility(View.VISIBLE);
        }
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
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_user,new Home()).commit();
//                    getActivity().finish();
                    return true;
                }
                return false;
            }
        });
    }

}
