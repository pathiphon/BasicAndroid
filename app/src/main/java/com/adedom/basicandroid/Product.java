package com.adedom.basicandroid;

import android.os.Parcel;
import android.os.Parcelable;

class Product implements Parcelable {
    private String productId;
    private String name;
    private double price;
    private int qty;
    private String image;

    public Product() {
    }

    public Product(String productId, String name, double price, int qty, String image) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.qty = qty;
        this.image = image;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.productId);
        dest.writeString(this.name);
        dest.writeDouble(this.price);
        dest.writeInt(this.qty);
        dest.writeString(this.image);
    }

    protected Product(Parcel in) {
        this.productId = in.readString();
        this.name = in.readString();
        this.price = in.readDouble();
        this.qty = in.readInt();
        this.image = in.readString();
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
