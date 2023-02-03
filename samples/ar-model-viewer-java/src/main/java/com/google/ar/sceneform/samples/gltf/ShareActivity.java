package com.google.ar.sceneform.samples.gltf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareMediaContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class ShareActivity extends AppCompatActivity {

    ImageView imageView;
    Button buttonSave1,buttonSave2,buttonShare;
    Bitmap bmp;
    DatabaseReference databaseReference;
    FirebaseStorage storage;
    StorageReference storageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        imageView = findViewById(R.id.imageView);
        buttonSave1 = findViewById(R.id.buttonSave1);
        buttonSave2 = findViewById(R.id.buttonSave2);
        buttonShare = findViewById(R.id.buttonShare);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        byte[] byteArray = getIntent().getByteArrayExtra("image");
        bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        imageView.setImageBitmap(bmp);

        buttonSave1.setOnClickListener(v->{
            getImageFromBitmapAndSave();
        });

        buttonSave2.setOnClickListener(v->{
            saveToCollection();
        });

        buttonShare.setOnClickListener(v->{
            shareWithFB(bmp);
        });

    }

    private void saveToCollection() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        storageRef = storageRef.child("collection/"+System.currentTimeMillis()+".jpg");

        UploadTask uploadTask = storageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
             storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        databaseReference = FirebaseDatabase.getInstance().getReference("collection");
                                        String key = databaseReference.push().getKey();
                                        databaseReference.child(key).setValue(uri.toString());
                                        Toast.makeText(ShareActivity.this, "Saved to collection", Toast.LENGTH_SHORT).show();
                                    }
                                });
            }
        });
    }

    void getImageFromBitmapAndSave(){
        try {
            File file = new File(Environment.getExternalStorageDirectory() + "/Pictures/" + System.currentTimeMillis() + ".jpg");
            FileOutputStream fOut = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
            Toast.makeText(ShareActivity.this, "Screenshot saved", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void shareWithFB(Bitmap bitmap){
        SharePhoto sharePhoto1 = new SharePhoto.Builder()
                .setBitmap(bitmap)
                .build();


        ShareContent shareContent = new ShareMediaContent.Builder()
                .addMedium(sharePhoto1)
                .build();

        ShareDialog shareDialog = new ShareDialog(this);
        shareDialog.show(shareContent, ShareDialog.Mode.AUTOMATIC);
    }
}