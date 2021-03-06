package com.adedom.basicandroid.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ProductIn implements Parcelable {
    private String name;
    private String image;
    private String productIdNo;
    private String productId;
    private String dateIn;
    private String quantity;
    private String price;

    public ProductIn() {
    }

    public ProductIn(String name, String image, String productIdNo, String productId, String dateIn, String quantity, String price) {
        this.name = name;
        this.image = image;
        this.productIdNo = productIdNo;
        this.productId = productId;
        this.dateIn = dateIn;
        this.quantity = quantity;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getProductIdNo() {
        return productIdNo;
    }

    public void setProductIdNo(String productIdNo) {
        this.productIdNo = productIdNo;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getDateIn() {
        return dateIn;
    }

    public void setDateIn(String dateIn) {
        this.dateIn = dateIn;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.image);
        dest.writeString(this.productIdNo);
        dest.writeString(this.productId);
        dest.writeString(this.dateIn);
        dest.writeString(this.quantity);
        dest.writeString(this.price);
    }

    protected ProductIn(Parcel in) {
        this.name = in.readString();
        this.image = in.readString();
        this.productIdNo = in.readString();
        this.productId = in.readString();
        this.dateIn = in.readString();
        this.quantity = in.readString();
        this.price = in.readString();
    }

    public static final Parcelable.Creator<ProductIn> CREATOR = new Parcelable.Creator<ProductIn>() {
        @Override
        public ProductIn createFromParcel(Parcel source) {
            return new ProductIn(source);
        }

        @Override
        public ProductIn[] newArray(int size) {
            return new ProductIn[size];
        }
    };
}
