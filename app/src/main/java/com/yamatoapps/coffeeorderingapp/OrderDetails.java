package com.yamatoapps.coffeeorderingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class OrderDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
             String name = getIntent().getStringExtra("name");
             String image_url = getIntent().getStringExtra("image_url");
             String size = getIntent().getStringExtra("size");
             String description = getIntent().getStringExtra("description");
             Double price = getIntent().getDoubleExtra("price",0);
        TextView tvName,tvSize,tvDescription,tvPrice;
        tvName = findViewById(R.id.tvCoffeeName);
        tvSize = findViewById(R.id.tvCoffeeSize);
        tvDescription = findViewById(R.id.tvCoffeeDetails);
        tvPrice = findViewById(R.id.tvCoffeePrice);
        ImageView ivImage = findViewById(R.id.ivCoffeePhoto);
        Picasso.get().load(image_url).into(ivImage);
        tvName.setText(name);
            tvSize.setText(size);
            tvDescription.setText(description);
            tvPrice.setText(String.valueOf(price));
    }
}