package com.yamatoapps.coffeeorderingapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CoffeeAdapterAdmin extends ArrayAdapter<Coffee> {
    FirebaseFirestore db  = FirebaseFirestore.getInstance();
    public CoffeeAdapterAdmin(@NonNull Context context, @NonNull ArrayList<Coffee> objects) {
        super(context,0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Coffee item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.product_item_for_admin, parent, false);
        }
        ImageView image = (ImageView)convertView.findViewById(R.id.ivPhoto);
        TextView tvName = (TextView)convertView.findViewById(R.id.tvName);
        TextView tvPrice = (TextView)convertView.findViewById(R.id.tvPrice);
        TextView tvDetails = (TextView)convertView.findViewById(R.id.tvDetails);
        Button btnEdit = convertView.findViewById(R.id.btnEdit);
        Button btnDelete = convertView.findViewById(R.id.btnDelete);
        btnEdit.setOnClickListener(view->{
            Intent editIntent  = new Intent(parent.getContext(), AdminPanel.class);
            editIntent.putExtra("action","edit");
            editIntent.putExtra("id",item.id);
            parent.getContext().startActivity(editIntent);
        });
        btnDelete.setOnClickListener(view ->{
            AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());
            builder.setTitle("Delete Product");
            builder.setMessage("You are deleting this product. Press OK to confirm");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    db.collection("coffee").document(item.id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(parent.getContext(),"Product successfully deleted!",Toast.LENGTH_SHORT ).show();
                            ((Activity)parent.getContext()).finish();
                        }
                    });
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });
        Picasso.get().load(item.image_url).into(image);
        Log.e("Image","Successfully loaded image!" + image.getResources());
        tvName.setText("Name: "+ item.name);
        tvPrice.setText("Price: "+ item.price);
        tvDetails.setText("Additional Details: "+ item.description);
        return convertView;

    }
}
