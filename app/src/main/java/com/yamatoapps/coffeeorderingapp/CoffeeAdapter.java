package com.yamatoapps.coffeeorderingapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoffeeAdapter extends ArrayAdapter<Coffee> {
    public CoffeeAdapter(@NonNull Context context, @NonNull ArrayList<Coffee> objects) {
        super(context,0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Coffee item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.product_item, parent, false);
        }
        ImageView image = (ImageView)convertView.findViewById(R.id.ivPhoto);
        TextView tvName = (TextView)convertView.findViewById(R.id.tvName);
        TextView tvPrice = (TextView)convertView.findViewById(R.id.tvPrice);
        TextView tvDetails = (TextView)convertView.findViewById(R.id.tvDetails);
        Button btnOrder = convertView.findViewById(R.id.btnPlaceOrder);
        Button btnViewDetails = convertView.findViewById(R.id.btnSeeDetails);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new  Intent(parent.getContext(), OrderDetails.class);
                intent.putExtra("name",item.name);
                intent.putExtra("image_url",item.image_url);
                intent.putExtra("size",item.size);
                intent.putExtra("description",item.description);
                intent.putExtra("price",item.price);
                Map<String,Object> map = new HashMap<>();
                map.put("name",item.name);
                map.put("price",item.price);
                map.put("image_url",item.image_url);
                map.put("date_ordered",new Date());
                db.collection("coffee_orders").add(map);
                parent.getContext().startActivity(intent);
            }
        });
        Picasso.get().load(item.image_url).into(image);
        Log.e("Image","Successfully loaded image!" + image.getResources());
        tvName.setText("Name: "+ item.name);
        tvPrice.setText("Price: "+ item.price);
        tvDetails.setText("Additional Details: "+ item.description);
        return convertView;

    }
}
