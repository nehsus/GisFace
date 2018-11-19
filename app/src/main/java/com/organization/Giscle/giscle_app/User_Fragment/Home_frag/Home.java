package com.organization.Giscle.giscle_app.User_Fragment.Home_frag;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.organization.Giscle.giscle_app.R;
import com.organization.Giscle.giscle_app.User_Fragment.Camera_frag.Camera_act;
import com.organization.Giscle.giscle_app.User_Fragment.Trip.TripLog;

public class Home extends Fragment implements View.OnClickListener {

    KenBurnsView view1, view2;
    ImageView imageView;
    Button tripLog, startRecord;


    int[] imagesArray = {R.drawable.image_one, R.drawable.image_two, R.drawable.image_three};
    int picArrayLength = imagesArray.length;

    private int getDrawable() {
        int index = (int) (Math.random() * picArrayLength);
        Log.e("INDEX", "" + index);
        if (index < picArrayLength && index > -1) {
            return imagesArray[index];
        }
        return imagesArray[index];
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        getActivity().setTitle("Home");
        view1 = (KenBurnsView) view.findViewById(R.id.ken_burns_images1);
        view2 = (KenBurnsView) view.findViewById(R.id.ken_burns_images2);
        view2.setVisibility(View.GONE);
        startRecord = (Button) view.findViewById(R.id.start_trip_btn);
        tripLog = (Button) view.findViewById(R.id.record_trip_btn);
        tripLog.setOnClickListener(this);
        startRecord.setOnClickListener(this);
        imageView = (ImageView) view.findViewById(R.id.image_user_home);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Glide.with(getActivity()).load(getDrawable()).thumbnail(0.5f).into(view1);
            try {
                fadeIn(view1, view2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            imageView.setVisibility(View.VISIBLE);
            view1.setVisibility(View.GONE);
            view2.setVisibility(View.GONE);
            imageView.setImageDrawable(getResources().getDrawable(getDrawable()));
        }

        return view;
    }

    public void fadeIn(final ImageView img1, final ImageView img2) throws Exception {
        float a = 0f;
        float b = 1;
        Animation fadeIn = new AlphaAnimation(a, b);
        fadeIn.setInterpolator(new AccelerateInterpolator());
        fadeIn.setDuration(3000);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                img2.setVisibility(View.VISIBLE);
                Glide.with(getActivity()).load(getDrawable()).thumbnail(0.5f)
                        .into(img2);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                try {
                    fadeOut(img1, img2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        img2.startAnimation(fadeIn);
        img1.setVisibility(View.GONE);
    }

    public void fadeOut(final ImageView img1, final ImageView img2) throws Exception {
        float a = 0f;
        Animation fadeOut = new AlphaAnimation(1, a);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(3000);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                img2.setVisibility(View.GONE);
                Glide.with(getActivity()).load(getDrawable()).thumbnail(0.5f)
                        .into(img1);
                try {
                    fadeIn(img2, img1);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        img2.startAnimation(fadeOut);
        img2.setVisibility(View.VISIBLE);
    }

    private void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Want to exit?");
        builder.setCancelable(true);
        builder.setMessage("Do you want to close Offercam ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getActivity().finish();
                System.exit(0);
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
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
//                    getActivity().finish();
                    showExitDialog();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.start_trip_btn:
                Intent i = new Intent(getActivity(), Camera_act.class);
                getActivity().startActivity(i);
                getActivity().finish();
                break;
            case R.id.record_trip_btn:
                openFragment(new TripLog());
                break;
        }
    }

    private void openFragment(Fragment fragment) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_main_user, fragment).commit();
    }
}
