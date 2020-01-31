package com.adedom.basicandroid;

public class Utility {

    public static String toPrice(double price) {
        String str = String.format("%,.2f", price);
        return str + " บาท";
    }

    public static String toQty(int qty) {
        String str = String.format("%,d", qty);
        return str + " หน่วย";
    }

}
