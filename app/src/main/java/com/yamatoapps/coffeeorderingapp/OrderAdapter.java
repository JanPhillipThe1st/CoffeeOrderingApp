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

import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class OrderAdapter extends ArrayAdapter<Order> {
    public OrderAdapter(@NonNull Context context, @NonNull ArrayList<Order> objects) {
        super(context,0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Order item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.order_item, parent, false);
        }
        ImageView image = (ImageView)convertView.findViewById(R.id.ivPhoto);
        TextView tvName = (TextView)convertView.findViewById(R.id.tvName);
        TextView tvTotal = (TextView)convertView.findViewById(R.id.tvTotal);
        TextView tvDate = (TextView)convertView.findViewById(R.id.tvDate);
        Picasso.get().load(item.image_url).into(image);
        Log.e("Image","Successfully loaded image!" + image.getResources());
        tvName.setText("Name: "+ item.name);
        tvTotal.setText("Total: "+ item.price);
        tvDate.setText("Date ordered: "+ item.date_ordered);
        return convertView;

    }
}
