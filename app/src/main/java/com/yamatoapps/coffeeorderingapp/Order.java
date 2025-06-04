package com.yamatoapps.coffeeorderingapp;

import java.io.Serializable;
import java.util.Date;

public class Order implements Serializable {
    public String name = "";
    public  String image_url = "";
    public double price = 39;
    public Date date_ordered;
    public Order(String name,Date date_ordered, double price, String image_url) {
        this.name = name;
        this.image_url = image_url;
        this.price = price;
        this.date_ordered = date_ordered;
    }
}
