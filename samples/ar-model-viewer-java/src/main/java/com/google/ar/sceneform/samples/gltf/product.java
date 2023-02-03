package com.google.ar.sceneform.samples.gltf;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.SceneView;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.samples.gltf.model.Furniture;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.ref.WeakReference;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class product extends AppCompatActivity {

    ImageView image_product;
    TextView name_product,detail_product,price_product;
    Button btnViewAR;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product);
        getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        Init();
        Intent intent = getIntent();
        Furniture furniture = (Furniture) intent.getSerializableExtra("Furniture");
        Glide.with(this).load(furniture.getImage()).into(image_product);
        name_product.setText(furniture.getName());
        detail_product.setText(furniture.getDetail());
        price_product.setText("$"+furniture.getPrice());
        btnViewAR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(product.this,MainActivity.class);
                intent.putExtra("Furniture",furniture);
                startActivity(intent);
            }
        });
    }

    private void Init() {
        image_product = findViewById(R.id.image_product);
        name_product = findViewById(R.id.name_product);
        detail_product = findViewById(R.id.detail_product);
        price_product = findViewById(R.id.price_product);
        btnViewAR = findViewById(R.id.btnViewAR);
        back = findViewById(R.id.back1);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(product.this, "wi", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }


}
