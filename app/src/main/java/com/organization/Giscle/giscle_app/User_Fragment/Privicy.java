package com.organization.Giscle.giscle_app.User_Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.organization.Giscle.giscle_app.R;


public class Privicy extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_privicy, container, false);
        getActivity().setTitle("Privicy poll");
        return view;
    }

}
