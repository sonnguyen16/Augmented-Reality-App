package com.google.ar.sceneform.samples.gltf;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.ModelLoader;
import com.google.ar.sceneform.samples.gltf.adapter.FurnitureHomeAdapter;
import com.google.ar.sceneform.samples.gltf.model.Furniture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.ar.sceneform.samples.gltf.model.OnClickFurniture;

import java.util.ArrayList;
import java.util.List;

public class favorite extends AppCompatActivity implements OnClickFurniture {

    ImageView loading_favorite;
    RecyclerView favorite_recycler;
    TextView txt_favorite;
    List<Furniture> furnitureList;
    List<Furniture> furnitureList2;
    FurnitureHomeAdapter adapter;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favourite);
        getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        Init();
        Intent intent = getIntent();
        String category = intent.getStringExtra("category");
        Furniture furniture = (Furniture) intent.getSerializableExtra("furniture");
        if(!category.equals(null)){
            txt_favorite.setText(category);
            LoadData(category);
        }
        if (category.equals("favorite")){
            txt_favorite.setText("Favorite");
            LoadData("Table");
            LoadData("Chair");
            LoadData("Lamp");
            LoadData("Plant");
            LoadData("Other");
            LoadFavorite();
        }



    }

    private void LoadFavorite() {
        furnitureList2 = new ArrayList<Furniture>();
        adapter = new FurnitureHomeAdapter(furnitureList2,this);
        favorite_recycler.setAdapter(adapter);
        databaseReference = FirebaseDatabase.getInstance().getReference("Favorite").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    String name = (String) data.getValue();
                    for (Furniture f: furnitureList
                         ) {
                        if (f.getName().equals(name)){
                            furnitureList2.add(f);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                loading_favorite.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loading_favorite.setImageResource(R.drawable.error);
            }

        });
    }

    private void LoadData(String category) {
        furnitureList = new ArrayList<Furniture>();
        adapter = new FurnitureHomeAdapter(furnitureList,this);
        favorite_recycler.setAdapter(adapter);
        databaseReference = FirebaseDatabase.getInstance().getReference("Furniture").child(category);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    Furniture furniture = data.getValue(Furniture.class);
                    furnitureList.add(furniture);
                }
                adapter.notifyDataSetChanged();
                loading_favorite.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loading_favorite.setImageResource(R.drawable.error);
            }

        });
    }

    private void Init() {
        loading_favorite = findViewById(R.id.loading_favorite);
        favorite_recycler = findViewById(R.id.rv_favorite);
        txt_favorite = findViewById(R.id.txt_favorite);
        Glide.with(this).load("https://upload.wikimedia.org/wikipedia/commons/c/c7/Loading_2.gif").into(loading_favorite);
    }

    @Override
    public void onFurnitureClicked(Furniture furniture) {
        Intent intent = new Intent(getApplicationContext(), product.class);
        intent.putExtra("Furniture", furniture);
        startActivity(intent);
    }
}
