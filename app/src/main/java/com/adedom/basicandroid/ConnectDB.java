package com.adedom.basicandroid;

import com.adedom.library.Dru;

import java.sql.Connection;

// TODO: 04/02/2563 4 connect db
public class ConnectDB {

    public static String BASE_URL = "192.168.43.22";
    public static String BASE_IMAGE = "http://" + BASE_URL + "/basic-android/images/";

    public static Connection getConnection() {
        return Dru.connection(BASE_URL, "root", "abc456", "basic_android");
    }
}

