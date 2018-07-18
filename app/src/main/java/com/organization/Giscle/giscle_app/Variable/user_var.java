package com.organization.Giscle.giscle_app.Variable;

import android.net.Uri;

import java.net.URI;

/**
 * Created by sushen.kumaron 9/18/2017.
 */

public class user_var {
//    public void setImageAvtar(String imageAvtar) {
//        this.imageAvtar = imageAvtar;
//    }

    //creat uid var
    private String uId;
    //also create avtar in String for firebase
//    private String imageAvtar;
    private int _id;
    private String name;
    //    private byte[] avtar;
    private String email;
    private String type;
    private String points;
    private String number;
//    private Uri iamgeUriMain;

    public String getPoints() {
        return points;
    }

    public user_var(String name, String email, String type, String points, String number) {
        this.name = name;
        this.email = email;
        this.type = type;
        this.points = points;
        this.number = number;
    }

//    public user_var(/*int _id,*/ String name, /*byte[] avtar,*/ String email, String type, int points, String number) {
////        this._id = _id;
//        this.name = name;
////        this.avtar = avtar;
//        this.email = email;
//        this.type = type;
//        this.points = points;
//        this.number = number;
//    }

    public user_var(int _id, String name,/* byte[] avtar,*/ String email, String type, String points, String number, String imagePath) {
        this._id = _id;
        this.name = name;
//        this.avtar = avtar;
        this.email = email;
        this.type = type;
        this.points = points;
        this.number = number;
//        this.imageAvtar = imagePath;
    }

    public String getNumber() {
        return number;
    }

    public int get_id() {
        return _id;
    }

    public String getName() {
        return name;
    }

//    public byte[] getAvtar() {
//        return avtar;
//    }

    public String getEmail() {
        return email;
    }

    public String getType() {
        return type;
    }

    public String getuId() {
        return uId;
    }

//    public String getImageAvtar() {
//        return imageAvtar;
//    }

    public user_var(int _id, String name/*, byte[] avtar*/, String email, String type) {

        this._id = _id;
        this.name = name;
//        this.avtar = avtar;
        this.email = email;
        this.type = type;
    }

    //for firebase
    public user_var(String uid, String name, String email, String type, String points, String number) {
        this.uId = uid;
        this.name = name;
//        this.imageAvtar = avtar;
        this.email = email;
        this.type = type;
        this.points =points;
        this.number = number;
    }

    //this is fro userDashboard me save into Firebase wala ke liye constructor banaya hai..
    public user_var(String uid, String name, String avtar, String email, String type, String points, String number) {
        this.uId = uid;
        this.name = name;
//        this.imageAvtar = avtar;
        this.email = email;
        this.type = type;
        this.points =  points;
        this.number = number;
    }

//    public Uri getIamgeUriMain() {
//        return iamgeUriMain;
//    }

//    //for firebase
//    public user_var(String uid, String name, Uri avtar, String email, String type, int points, String number) {
//        this.uId = uid;
//        this.name = name;
////        this.iamgeUriMain = avtar;
////        this.imageAvtar = avtar;
//        this.email = email;
//        this.type = type;
//        this.points = points;
//        this.number = number;
//    }
}
