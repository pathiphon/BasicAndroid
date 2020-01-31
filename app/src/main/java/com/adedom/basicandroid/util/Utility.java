package com.adedom.basicandroid.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Utility {

    public static String toPrice(double price) {
        String str = String.format("%,.2f", price);
        return str + " บาท";
    }

    public static String toQty(int qty) {
        String str = String.format("%,d", qty);
        return str + " หน่วย";
    }

    public static String getImageName() {
        return UUID.randomUUID().toString().replace("-", "") + ".jpg";
    }

    private static String imageToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imgByte = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgByte, Base64.DEFAULT);
    }

    public static void selectImage(Activity activity, int code) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(intent, code);
    }

    public static void uploadImage(String name, Bitmap bitmap) {
        String image = Utility.imageToString(bitmap);
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<ImageClass> call = apiInterface.uploadImage(name, image);
        call.enqueue(new Callback<ImageClass>() {
            @Override
            public void onResponse(Call<ImageClass> call, Response<ImageClass> response) {
                ImageClass imageClass = response.body();
            }

            @Override
            public void onFailure(Call<ImageClass> call, Throwable t) {
            }
        });
    }

}
