package com.yamatoapps.coffeeorderingapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ChangePassword extends AppCompatActivity {
    TextInputEditText tvOldPassword,tvPassword,tvConfirmPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        Button btnBack = findViewById(R.id.btnBack);
        Button btnSave = findViewById(R.id.btnSave);
        Intent intent = getIntent();
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tvOldPassword = findViewById(R.id.tvOldPassword);
        tvPassword = findViewById(R.id.tvPassword);
        tvConfirmPassword = findViewById(R.id.tvConfirmPassword);
        AlertDialog.Builder builder = new AlertDialog.Builder(ChangePassword.this);
        tvConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!tvPassword.getText().toString().equals( tvConfirmPassword.getText().toString())){
                    tvPassword.setTextColor(Color.RED);
                    tvConfirmPassword.setTextColor(Color.RED);
                }
                else{
                    tvPassword.setTextColor(Color.WHITE);
                    tvConfirmPassword.setTextColor(Color.WHITE);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        btnSave.setOnClickListener(view ->{
            if(!tvOldPassword.getText().toString().equals( intent.getStringExtra("old_password"))){
                builder.setTitle("Invalid");
                builder.setMessage("Invalid Current Password Value." );
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
            }
                else{

                if (tvPassword.getText().toString().equals( tvConfirmPassword.getText().toString())) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    Map<String, Object> map = new HashMap<>();
                    map.put("password", tvPassword.getText().toString());
                    db.collection("coffee_ordering_users").document(intent.getStringExtra("id").toString()).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(ChangePassword.this, "Password Changed Successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
                else {

                }
                }
        });
    }
}