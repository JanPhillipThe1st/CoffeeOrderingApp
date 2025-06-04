package com.yamatoapps.coffeeorderingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextInputEditText tvUsername = findViewById(R.id.tiUsername),tvPassword = findViewById(R.id.tiPassword);
         Button btnLogin =findViewById(R.id.btnLoginAdmin);
        btnLogin .setOnClickListener(view -> {
            ProgressDialog progressDialog= new ProgressDialog(MainActivity.this);
            progressDialog.setTitle("Logging in");
            progressDialog.setMessage("Logging in...");
                        progressDialog.show();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("coffee_ordering_users").where(Filter.and(
                    Filter.equalTo("username",tvUsername.getText().toString()),
                    Filter.equalTo("password",tvPassword.getText().toString()),Filter.equalTo("type","admin"))).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if ( queryDocumentSnapshots.size() > 0){
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this,"Logged in as Admin",Toast.LENGTH_SHORT).show();
                        Intent adminIntent =  new Intent(MainActivity.this, AdminPanel.class);
                        adminIntent.putExtra("action","add");
                        startActivity(adminIntent);
                    }
                    else{
                        db.collection("coffee_ordering_users").where(Filter.and(
                                Filter.equalTo("username",tvUsername.getText().toString()),
                                Filter.equalTo("password",tvPassword.getText().toString()),Filter.equalTo("type","customer")
                        )).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshotsCustomer) {
                                if ( queryDocumentSnapshotsCustomer.getDocuments().size()>0){
                                    progressDialog.dismiss();
                                    Toast.makeText(MainActivity.this,"Logged in as Customer",Toast.LENGTH_SHORT).show();
                                    Intent customerIntent = new Intent(MainActivity.this, OrderCoffee.class);
                                    customerIntent.putExtra("profile_picture",queryDocumentSnapshotsCustomer.getDocuments().get(0).getString("profile_picture"));
                                    customerIntent.putExtra("id",queryDocumentSnapshotsCustomer.getDocuments().get(0).getId());
                                    customerIntent.putExtra("name",queryDocumentSnapshotsCustomer.getDocuments().get(0).getString("name"));
                                    customerIntent.putExtra("old_password",queryDocumentSnapshotsCustomer.getDocuments().get(0).getString("password"));
                                    startActivity( customerIntent);
                                }
                                else {
                                    AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
                                    progressDialog.dismiss();
                                    ab.setTitle("Invalid Login");
                                    ab.setMessage("No user could be found");
                                    ab.setNegativeButton("OK", (DialogInterface.OnClickListener) (dialog, which) -> {

                                    });
                                    AlertDialog alertDialog = ab.create();
                                    alertDialog.show();
                                }
                            }

                        });
                    }
                }
            });
        });
        Button btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Register.class));
            }
        });
    }
}