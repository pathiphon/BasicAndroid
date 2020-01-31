package com.adedom.basicandroid;

import com.adedom.library.Dru;

import java.sql.Connection;

public class ConnectDB {

    public static String BASE_URL = "192.168.43.22";
    public static String BASE_IMAGE = "http://" + BASE_URL + "/basic-android/image/";

    public static Connection getConnection() {
        return Dru.connection(BASE_URL, "root", "abc456", "basic_android");
    }
}

