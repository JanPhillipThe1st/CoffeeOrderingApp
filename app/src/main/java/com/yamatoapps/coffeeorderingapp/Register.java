package com.yamatoapps.coffeeorderingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yamatoapps.coffeeorderingapp.R;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Register extends AppCompatActivity {
    Uri fileUri;
    TextInputEditText tvName,tvUsername,tvPassword,tvConfirmPassword;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ImageView imageView;
    Spinner s;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        imageView = findViewById(R.id.ivProfilePicture);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 22 && resultCode == RESULT_OK && data != null && data != null) {
            fileUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),fileUri);
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        tvName = findViewById(R.id.tvName);
        tvUsername = findViewById(R.id.tvUsername);
        tvPassword = findViewById(R.id.tvPassword);
        tvConfirmPassword = findViewById(R.id.tvConfirmPassword);
        imageView = findViewById(R.id.ivProfilePicture);
        AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);

        tvConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!tvConfirmPassword.getText().toString().equals( tvPassword.getText().toString())){
                tvPassword.setTextColor(Color.RED);
                tvConfirmPassword.setTextColor(Color.RED);
                }
                else{
                    tvPassword.setTextColor(Color.GREEN);
                    tvConfirmPassword.setTextColor(Color.GREEN);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        Button btnUploadImage = findViewById(R.id.btnUploadImage);
        Intent imageIntent = new Intent();

        Button btnPost = findViewById(R.id.btnRegister) ;
        String[] arraySpinner = new String[] {
                "admin","customer",
        };
         s = (Spinner) findViewById(R.id.spinnerUserType);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
        btnUploadImage.setOnClickListener(view ->{
            imageIntent.setType("image/*");
            imageIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(imageIntent,"Pick image to upload"),22);
        });
        btnPost.setOnClickListener(view ->{
            if (!tvConfirmPassword.getText().toString().equals( tvPassword.getText().toString())){
                builder.setTitle("Invalid Signup");
                builder.setMessage("Passwords do not match!");
                builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }else {
            uploadImage();

            }
        });

    }
    public  void uploadImage(){
        if (fileUri != null){
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");

            progressDialog.show();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                    .child(UUID.randomUUID().toString());
            UploadTask uploadTask = (UploadTask) storageReference.putFile(fileUri).addOnSuccessListener(taskSnapshot -> {
            }).addOnFailureListener(listener->{
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Fail to Upload Image..", Toast.LENGTH_SHORT)
                        .show();
            });
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Map<String, Object> listing = new HashMap<>();
                        listing.put("name", tvName.getText().toString());
                        listing.put("username",  tvUsername.getText().toString());
                        listing.put("password",  tvPassword.getText().toString());
                        listing.put("type",  s.getSelectedItem().toString());
                        listing.put("profile_picture",  task.getResult());
                            DocumentReference newListingRef = db.collection("coffee_ordering_users").document();
                            newListingRef.set(listing);

                        progressDialog.dismiss();
                        finish();
                        Toast.makeText(getApplicationContext(), "Congratulations! You have signed up successfully!..", Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });

        }
}
}