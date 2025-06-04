package com.yamatoapps.coffeeorderingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AdminPanel extends AppCompatActivity {
    Uri fileUri;
    String action = "";
    Intent intent;
    TextView tvName,tvPrice,tvDetails,tvSize;
    ImageView iv;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);
        tvName = findViewById(R.id.textInputName);
        tvPrice = findViewById(R.id.textInputPrice);
        tvSize = findViewById(R.id.textInputSize);
        tvDetails = findViewById(R.id.textInputDescription);
        Button btnPost = findViewById(R.id.btnPost) ;
        Button btnMenu = findViewById(R.id.btnMenu) ;
        intent = getIntent();

        iv = findViewById(R.id.ivListingPhoto);
         action = intent.getStringExtra("action");
         if (intent.hasExtra("action") && action.contains("edit")){
             db.collection("coffee").document(intent.getStringExtra("id")).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                 @Override
                 public void onSuccess(DocumentSnapshot documentSnapshot) {
                     tvName.setText(documentSnapshot.getString("name"));
                     tvSize.setText(documentSnapshot.getString("size"));
                     tvDetails.setText(documentSnapshot.getString("description"));
                     tvPrice.setText(String.valueOf(documentSnapshot.getDouble("price")));
                     Picasso.get().load(documentSnapshot.getString("image_url")).into(iv);
                 }
             });
         }
        btnMenu.setOnClickListener(view -> {
            startActivity(new Intent(AdminPanel.this, Admin.class));
        });
        Button btnUploadImage = findViewById(R.id.btnUploadImage);
        Intent imageIntent = new Intent();
        findViewById(R.id.btnBack).setOnClickListener(view ->  {
                finish();
        });
        btnUploadImage.setOnClickListener(view ->{
            imageIntent.setType("image/*");
            imageIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(imageIntent,"Pick image to upload"),22);
        });
        btnPost.setOnClickListener(view ->{
            uploadImage();
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
       iv = findViewById(R.id.ivListingPhoto);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 22 && resultCode == RESULT_OK && data != null && data != null) {
            fileUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),fileUri);
                iv.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public  void uploadImage(){
        if (fileUri != null){
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            if (action.contains("edit")){
            progressDialog.setMessage("Updating your product...");
            }
           else{
            progressDialog.setMessage("Adding your product...");
            }

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
                        listing.put("description",  tvDetails.getText().toString());
                        listing.put("size",  tvSize.getText().toString());
                        listing.put("price",  Double.parseDouble(tvPrice.getText().toString()));
                        listing.put("image_url",  task.getResult());
                        if (action.contains("edit")){
                            DocumentReference newListingRef = db.collection("coffee").document(intent.getStringExtra("id"));
                            newListingRef.update(listing);
                        }else{
                            DocumentReference newListingRef = db.collection("coffee").document();
                            newListingRef.set(listing);
                        }
                        progressDialog.dismiss();
                        finish();
                        Toast.makeText(getApplicationContext(), "Congratulations! your Product is added..", Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });

        }
    }
}