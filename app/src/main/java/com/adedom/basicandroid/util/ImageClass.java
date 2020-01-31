package com.adedom.basicandroid.util;

import com.google.gson.annotations.SerializedName;

public class ImageClass {

    @SerializedName("response")
    private String Response;

    public String getResponse(){
        return Response;
    }
}
