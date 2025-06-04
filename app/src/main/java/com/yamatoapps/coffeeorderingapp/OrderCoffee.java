package com.yamatoapps.coffeeorderingapp;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class OrderCoffee extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_coffee);
        ArrayList<Coffee> products = new ArrayList<>();
        CoffeeAdapter adapter = new CoffeeAdapter(this, products);
        //adapter.add();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        ImageView ivProfilePicture = findViewById(R.id.ivProfilePicture);
        ivProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent changePasswordIntent = new Intent(OrderCoffee.this, ChangePassword.class);
                changePasswordIntent.putExtra("id",intent.getStringExtra("id"));
                changePasswordIntent.putExtra("old_password",intent.getStringExtra("old_password"));
                startActivity(changePasswordIntent);
            }
        });
        Picasso.get().load(intent.getStringExtra("profile_picture")).into(ivProfilePicture);
        GridView gridView = findViewById(R.id.gvCoffeeCatalogue);
        TextView tvCaption = (TextView)findViewById(R.id.tvCaption);
        tvCaption.setText("Welcome, "+intent.getStringExtra("name"));
        db.collection("coffee").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    adapter.add(new Coffee(document.getString("name"),document.getString("description"),document.getString("size"),Double.parseDouble(document.get("price").toString()),document.getString("image_url")));
                }
                gridView.setAdapter(adapter);
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
        findViewById(R.id.btnBack).setOnClickListener(view ->{
            finish();
        });
    }
}