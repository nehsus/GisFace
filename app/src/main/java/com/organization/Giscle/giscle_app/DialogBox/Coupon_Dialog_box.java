package com.organization.Giscle.giscle_app.DialogBox;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.organization.Giscle.giscle_app.R;

import static com.organization.Giscle.giscle_app.Variable.CONSTANT.COMPANY_NAME;
import static com.organization.Giscle.giscle_app.Variable.CONSTANT.COUPON_NUMBER;
import static com.organization.Giscle.giscle_app.Variable.CONSTANT.INR_COUPON;

/**
 * Created by asher.ansari on 10/5/2017.
 */

public class Coupon_Dialog_box extends Dialog {

    Bundle bundle;
    Context context;

    public Coupon_Dialog_box(@NonNull Context context, Bundle bundle) {
        super(context);
        this.context = context;
        this.bundle = bundle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setCanceledOnTouchOutside(false);
        setContentView(R.layout.coupon_dialog_box);

        TextView name = (TextView) findViewById(R.id.coupon_org_name);
        TextView coupon = (TextView) findViewById(R.id.CouponFlip_textView);
        TextView inrText = (TextView)findViewById(R.id.inr_coupon_rupee);
        Button button = (Button)findViewById(R.id.copy_btn_coupon);
        String compName = bundle.getString(COMPANY_NAME);
       final String coup = bundle.getString(COUPON_NUMBER);
        inrText.setText("Added:: "+bundle.getInt(INR_COUPON)+"/= INR in your Giscle Pocket");
        name.setText(compName);
        coupon.setText("Coupon Code:: " + coup);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager manager = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("coupon",coup);
                manager.setPrimaryClip(clipData);
                Toast.makeText(context, "Coupon has been copied.", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }
}
