package com.organization.Giscle.giscle_app.Variable;

import java.io.Serializable;

/**
 * Created by sushen.kumaron 9/28/2017.
 */

public class Trip_record_variable implements Serializable {




//    public Trip_record_variable(int _id, String fileName, String filePath, String intial_long, String initail_lat, String final_long, String final_lat, String points, String distance, String time, String start_time, String end_time, String uploading) {
//        this._id = _id;
//        this.fileName = fileName;
//        this.filePath = filePath;
//        this.intial_long = intial_long;
//        this.initail_lat = initail_lat;
//        this.final_long = final_long;
//        this.final_lat = final_lat;
//        this.points = points;
//        this.distance = distance;
//        this.time = time;
//        this.start_time = start_time;
//        this.end_time = end_time;
//        this.uploading = uploading;
//    }

    private int _id;
    //    private String intial_long;
//    private String initail_lat;
//    private String final_long;
//    private String final_lat;
    private String points;
    private String distance;
    private String time;
    private String start_time;
    private String end_time;
    private String uploading;
    private String fileName;
    private String filePath;
    private String all_lat;
    private String all_long;
    private String firebaseVideoUrl;

    public Trip_record_variable(int _id, String points, String distance, String time, String start_time, String end_time, String uploading, String fileName, String filePath, String all_lat, String all_long) {
        this._id = _id;
//        this.intial_long = intial_long;
//        this.initail_lat = initail_lat;
//        this.final_long = final_long;
//        this.final_lat = final_lat;
        this.points = points;
        this.distance = distance;
        this.time = time;
        this.start_time = start_time;
        this.end_time = end_time;
        this.uploading = uploading;
        this.fileName = fileName;
        this.filePath = filePath;
        this.all_lat = all_lat;
        this.all_long = all_long;
    }

    public void setUploading(String uploading) {
        this.uploading = uploading;
    }

    public String getFirebaseVideoUrl() {

        return firebaseVideoUrl;
    }

    public void setFirebaseVideoUrl(String firebaseVideoUrl) {
        this.firebaseVideoUrl = firebaseVideoUrl;
    }
    //    public Trip_record_variable(String fileName, String intial_long, String initail_lat, String final_long, String final_lat, String points, String distance, String time, String start_time, String end_time, String uploading) {
//        this.fileName = fileName;
//        this.intial_long = intial_long;
//        this.initail_lat = initail_lat;
//        this.final_long = final_long;
//        this.final_lat = final_lat;
//        this.points = points;
//        this.distance = distance;
//        this.time = time;
//        this.start_time = start_time;
//        this.end_time = end_time;
//        this.uploading = uploading;
//    }

    public int get_id() {
        return _id;
    }

    public String getFileName() {
        return fileName;
    }

//    public String getIntial_long() {
//        return intial_long;
//    }
//
//    public String getInitail_lat() {
//        return initail_lat;
//    }
//
//    public String getFinal_long() {
//        return final_long;
//    }
//
//    public String getFinal_lat() {
//        return final_lat;
//    }

    public String getPoints() {
        return points;
    }

    public String getDistance() {
        return distance;
    }

    public String getTime() {
        return time;
    }

    public String getStart_time() {
        return start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public String getUploading() {
        return uploading;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getAll_lat() {
        return all_lat;
    }

    public String getAll_long() {
        return all_long;
    }

    public void setPoints(String points) {
        this.points = points;
    }
}
