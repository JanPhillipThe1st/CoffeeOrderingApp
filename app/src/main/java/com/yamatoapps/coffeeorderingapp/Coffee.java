package com.yamatoapps.coffeeorderingapp;

import java.io.Serializable;

public class Coffee implements Serializable {
    public String name = "";
    public String id = "";
    public  String description = "";
    public  String size = "";
    public  String image_url = "";
    public double price = 39;

    public Coffee(String name, String description, String size, double price,String image_url) {
        this.name = name;
        this.description = description;
        this.size = size;
        this.image_url = image_url;
        this.price = price;
    }
}
