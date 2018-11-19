package com.organization.Giscle.giscle_app.local_db;

import android.provider.BaseColumns;

/**
 * Created by asher.ansari on 9/17/2017.
 */

public class Tables implements BaseColumns {

    public class userTable {
        public static final String TABLE_NAME = "user";
        //        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_NAME = "name";
        //        public static final String COLUMN_AVTAR = "image";
        public static final String COLUMN_TYPE = "login_type";
        public static final String COLUMN_POINTS = "user_points";
        public static final String COLUMN_NUMBER = "user_number";
    }

    public class videoDetails {
        public static final String TABLE_NAME = "video_record";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_FILE_path = "file_path";
        public static final String COLUMN_FILE_NAME = "file_name";
        public static final String COLUMN_ALL_LONGITUDE = "all_long";
        public static final String COLUMN_ALL_LATITUDE = "all_lat";
        public static final String COLUMN_POINTS = "points";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_DISTANCE = "distance";
        public static final String COLUMN_START_TIME = "start_time";
        public static final String COLUMN_END_TIME = "end_time";
        public static final String COLUMN_UPLOAD_STATUS = "uploading";

    }
}
