package com.adedom.basicandroid.models;

public class ProductIn {
    private String name;
    private String image;
    private String productIdNo;
    private String productId;
    private String dateIn;
    private int quantity;
    private double price;

    public ProductIn() {
    }

    public ProductIn(String name, String image, String productIdNo, String productId, String dateIn, int quantity, double price) {
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
