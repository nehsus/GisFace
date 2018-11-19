package com.organization.Giscle.giscle_app.Unuseable;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.organization.Giscle.giscle_app.R;
import com.organization.Giscle.giscle_app.Variable.CONSTANT;

/**
 * Created by asher.ansari on 9/24/2017.
 */

public class Dialog_detail_video extends Dialog {
    Bundle bundle;

    public Dialog_detail_video(Context context, Bundle bundle) {
        super(context);
        this.bundle = bundle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setCanceledOnTouchOutside(false);
        setContentView(R.layout.video_detail_dialog);
        TextView time, filename, distance, points;
        Button btn;
        time = (TextView) findViewById(R.id.time_video);
        points = (TextView) findViewById(R.id.points_video);
        distance = (TextView) findViewById(R.id.distance_video);
        filename = (TextView) findViewById(R.id.fileName_video);
        btn = (Button) findViewById(R.id.ok_btn_video);

        if (bundle.containsKey(CONSTANT.TOTAL_TIME)) {
            time.setText(bundle.getString(CONSTANT.TOTAL_TIME));
        }
        if (bundle.containsKey(CONSTANT.DISTANCE)) {
            distance.setText(bundle.getString(CONSTANT.DISTANCE));
        }
        if (bundle.containsKey(CONSTANT.VIDEO_NAME)) {
            filename.setText(bundle.getString(CONSTANT.VIDEO_NAME));
        }
        if (bundle.containsKey(CONSTANT.POINTS)) {
            points.setText(bundle.getString(CONSTANT.POINTS));
        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
