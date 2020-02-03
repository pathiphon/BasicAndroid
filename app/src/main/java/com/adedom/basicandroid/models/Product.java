package com.adedom.basicandroid.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {

    private String productId;
    private String name;
    private String price;
    private String qty;
    private String image;
    private String productTypeId;

    public Product() {
    }

    public Product(String productId, String name, String price, String qty, String image, String productTypeId) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.qty = qty;
        this.image = image;
        this.productTypeId = productTypeId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getProductTypeId() {
        return productTypeId;
    }

    public void setProductTypeId(String productTypeId) {
        this.productTypeId = productTypeId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.productId);
        dest.writeString(this.name);
        dest.writeString(this.price);
        dest.writeString(this.qty);
        dest.writeString(this.image);
        dest.writeString(this.productTypeId);
    }

    protected Product(Parcel in) {
        this.productId = in.readString();
        this.name = in.readString();
        this.price = in.readString();
        this.qty = in.readString();
        this.image = in.readString();
        this.productTypeId = in.readString();
    }

    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel source) {
            return new Product(source);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}
