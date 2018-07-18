package com.organization.Giscle.giscle_app.User_Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.organization.Giscle.giscle_app.Adapter.ExpLVAdapter;
import com.organization.Giscle.giscle_app.R;
import com.organization.Giscle.giscle_app.User_Fragment.Home_frag.Home;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FAQ extends Fragment {
    ArrayList<String> questions = new ArrayList<>();
    Map<String, String> hashMap = new HashMap<>();
    ExpandableListView listView;
    ExpLVAdapter adapter;
    String[] answer = {
            "OfferCam is a Dashcam with reward app. Your uploaded video data will help us in making roadmap up to date.",
            "The OfferCam shop is located under My Profile tab.",
            "The phone should be mounted in such a way that the camera can see the road and you should be comfortable in driving the car just seeing the video in phone.",
            "The app is configured default to upload only on WiFi. However, if you want to upload video, you can choose the upload tab from profile.",
            "No, OfferCam don’t record sound.",
            "You help city roads up to date by tracking hazards, potholes etc."};

    private void init(View view) {
        listView = (ExpandableListView) view.findViewById(R.id.expandable_faq);
        initList();
    }

    private void initList() {
        questions.add("What is OfferCam?");
        questions.add("How do I redeem the points?");
        questions.add("Where should I mount the phone?");
        questions.add("How much data does the app use?");
        questions.add("Does the app record sound");
        questions.add("Why we record video");
        for (int i = 0; i < questions.size(); i++) {
            hashMap.put(questions.get(i),answer[i]);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_faq, container, false);
        init(view);
        adapter = new ExpLVAdapter(hashMap,view.getContext(),questions);


        listView.setAdapter(adapter);
        return view;
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
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_user,new Home()).commit();
                    return true;
                }
                return false;
            }
        });


    }
}
