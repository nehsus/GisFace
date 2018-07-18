package com.organization.Giscle.giscle_app;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.organization.Giscle.giscle_app.DialogBox.Coupon_Dialog_box;
import com.organization.Giscle.giscle_app.User_Fragment.Profile;
import com.organization.Giscle.giscle_app.local_db.Tables;
import com.organization.Giscle.giscle_app.local_db.db_helper;

import static com.organization.Giscle.giscle_app.Variable.CONSTANT.COMPANY_NAME;
import static com.organization.Giscle.giscle_app.Variable.CONSTANT.COUPON_NUMBER;
import static com.organization.Giscle.giscle_app.Variable.CONSTANT.INR_COUPON;

public class Shopping extends Fragment implements View.OnClickListener {
    Button flipkart, ebay, amazon, paytm;


    private void init(View view) {
        flipkart = (Button) view.findViewById(R.id.flipCard_redeem);
        ebay = (Button) view.findViewById(R.id.ebay_redeem);
        amazon = (Button) view.findViewById(R.id.amazon_redeem);
        paytm = (Button) view.findViewById(R.id.paytm_redeem);
    }

    private int points;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shopping, container, false);
        getActivity().setTitle("Choose your shop");
        init(view);

        getpointsFromDb();
        flipkart.setOnClickListener(this);
        ebay.setOnClickListener(this);
        amazon.setOnClickListener(this);
        paytm.setOnClickListener(this);
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
                    // handle back button's click listener
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.frame_main_user, new Profile()).commit();
//                    getActivity().finish();
                    return true;
                }
                return false;
            }
        });
    }

    private void getpointsFromDb() {

        SQLiteDatabase database = new db_helper(getActivity()).getReadableDatabase();
        Cursor cursor = database.query(Tables.userTable.TABLE_NAME, null, null, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                points = cursor.getInt(cursor.getColumnIndex(Tables.userTable.COLUMN_POINTS));
            }
        } else {
            Toast.makeText(getActivity(), "Data not found..!!\ntry login again", Toast.LENGTH_SHORT).show();
        }
//        points = 210000;
        database.close();
    }

    private int getPoints() {
        if (points > 0 && points < 10000) {
//            Toast.makeText(getActivity(), "Kindly make minimum 10000 points for Coupon.", Toast.LENGTH_SHORT).show();
            return 0;
        } else if (points == 10000) {
            return 10000;
        } else if (points > 10000 && points < 19000) {
            return 10000;
        } else if (points == 19000) {
            return 190000;
        } else if (points > 19000 && points < 35000) {
            return 19000;
        } else if (points == 35000) {
            return 35000;
        } else if (points > 35000) {
            return 35000;
        }
        return 0;
    }

    int detectedPoints = 0;

    private int getRupees(int inr) {
//        inr = 16000;
        switch (inr) {
            case 0:
                return 0;
            case 10000:
                return 500;
            case 19000:
                return 1000;
            case 35000:
                return 2000;
        }
        return 0;
    }

    private void showAlertBox(String msg, String title, Bundle bundle) {
//        if (checkPointsStatus()) {

        int gettedPoints = getPoints();
        detectedPoints = gettedPoints;
        int actualInrs = getRupees(gettedPoints);

        if (actualInrs == 0) {
            Toast.makeText(getActivity(), "Kindly make minimum 10000 points for Coupon.", Toast.LENGTH_SHORT).show();
        } else {
            showAlery(msg, title, bundle, actualInrs);
        }


//        if () {
//            showAlery(msg, title, bundle, gettedPoints);
//        } else {
//            Toast.makeText(getActivity(), "gain minimum 10000 points for coupons,\ntry again later.", Toast.LENGTH_SHORT).show();
//        }
    }

    private void showAlery(String msg, String title, final Bundle bundle, int inr) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title);
        bundle.putInt(INR_COUPON, inr);
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Coupon_Dialog_box dialogBox = new Coupon_Dialog_box(getContext(), bundle);
                        dialogBox.show();
                        Window window = dialogBox.getWindow();
                        window.setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                        updatePointsDatabase();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void updatePointsDatabase() {
        SQLiteDatabase database = new db_helper(getActivity()).getWritableDatabase();
        ContentValues values = new ContentValues();
        int finalPoint = points - detectedPoints;
        points = points - detectedPoints;
//        Toast.makeText(getActivity(), "Remaining Points:: " + points, Toast.LENGTH_SHORT).show();
        values.put(Tables.userTable.COLUMN_POINTS, finalPoint);
        database.update(Tables.userTable.TABLE_NAME, values, null, null);
        database.close();
        backScreen();
    }

    private void backScreen() {
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_user, new Profile()).commit();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Bundle bundle = new Bundle();

        switch (id) {
            case R.id.paytm_redeem:
                bundle.putString(COMPANY_NAME, "Paytm");
                bundle.putString(COUPON_NUMBER, "abcd12365");
                showAlertBox("Kindly confirm to request for coupon from Paytm", "Paytm Coupon Request", bundle);
                break;
            case R.id.amazon_redeem:
                bundle.putString(COMPANY_NAME, "Amazon");
                bundle.putString(COUPON_NUMBER, "efgh67890");
                showAlertBox("Kindly confirm to request for coupon from Amazon", "Amazon Coupon Request", bundle);
                break;
            case R.id.flipCard_redeem:
                bundle.putString(COMPANY_NAME, "Flip Kart");
                bundle.putString(COUPON_NUMBER, "ijkl12345");
                showAlertBox("Kindly confirm to request for coupon from Flipkard", "Flipkard Coupon Request", bundle);
                break;
            case R.id.ebay_redeem:
                bundle.putString(COMPANY_NAME, "Ebay");
                bundle.putString(COUPON_NUMBER, "mnop6789");
                showAlertBox("Kindly confirm to request for coupon from Ebay", "Ebay Coupon Request", bundle);
                break;
        }
    }
}
