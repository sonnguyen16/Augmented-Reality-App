package com.google.ar.sceneform.samples.gltf;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.ar.sceneform.samples.gltf.adapter.FurnitureHomeAdapter;
import com.google.ar.sceneform.samples.gltf.model.Furniture;
import com.google.ar.sceneform.samples.gltf.model.OnClickFurniture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements OnClickFurniture {

    FirebaseStorage storage;
    StorageReference storageRef;
    String mUri;
    RecyclerView hotProduct,newProduct;
    FurnitureHomeAdapter adapter,adapter2;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    List<Furniture> furnitureList;
    List<Furniture> furnitureList2;
    View view;
    ImageView loading1,loading2;
    CardView plant,lamp,chair,table,other;
    Furniture f;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        init();
        LoadData("Table");
        LoadData("Chair");
        LoadData("Lamp");
        LoadData("Plant");
        LoadData2("Other");
    }

    private void init() {
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        hotProduct = view.findViewById(R.id.HotProduct);
        newProduct = view.findViewById(R.id.NewProduct);
        loading1 = view.findViewById(R.id.loading1);
        loading2 = view.findViewById(R.id.loading2);
        plant = view.findViewById(R.id.plant_category);
        lamp = view.findViewById(R.id.lamp_category);
        chair = view.findViewById(R.id.chair_category);
        table = view.findViewById(R.id.table_category);
        other = view.findViewById(R.id.other_category);
        Glide.with(getContext()).load("https://upload.wikimedia.org/wikipedia/commons/c/c7/Loading_2.gif").into(loading1);
        Glide.with(getContext()).load("https://upload.wikimedia.org/wikipedia/commons/c/c7/Loading_2.gif").into(loading2);
        plant.setOnClickListener(v->{
            Intent intent = new Intent(getContext(),favorite.class);
            intent.putExtra("category","Plant");
            startActivity(intent);
        });
        lamp.setOnClickListener(v->{
            Intent intent = new Intent(getContext(),favorite.class);
            intent.putExtra("category","Lamp");
            startActivity(intent);
        });
        chair.setOnClickListener(v->{
            Intent intent = new Intent(getContext(),favorite.class);
            intent.putExtra("category","Chair");
            startActivity(intent);
        });
        table.setOnClickListener(v->{
            Intent intent = new Intent(getContext(),favorite.class);
            intent.putExtra("category","Table");
            startActivity(intent);
        });
        other.setOnClickListener(v->{
            Intent intent = new Intent(getContext(),favorite.class);
            intent.putExtra("category","Other");
            startActivity(intent);
        });
    }

    private void LoadData(String category) {
        int maxLike = 0;
        f = new Furniture();
        furnitureList = new ArrayList<Furniture>();
        adapter = new FurnitureHomeAdapter(furnitureList,this);
        hotProduct.setAdapter(adapter);
        databaseReference = FirebaseDatabase.getInstance().getReference("Furniture").child(category);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    Furniture furniture = data.getValue(Furniture.class);
                    if(furniture.getLike() > maxLike){
                        f = furniture;
                    }
                }
                furnitureList.add(f);
                adapter.notifyDataSetChanged();
                loading1.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loading1.setImageResource(R.drawable.error);

            }

        });
    }

    private void LoadData2(String category) {
        furnitureList2 = new ArrayList<Furniture>();
        adapter2 = new FurnitureHomeAdapter(furnitureList2,this);
        newProduct.setAdapter(adapter2);

        databaseReference = FirebaseDatabase.getInstance().getReference("Furniture").child(category);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    Furniture furniture = data.getValue(Furniture.class);
                    furnitureList2.add(furniture);
                }

                adapter2.notifyDataSetChanged();

                loading2.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                loading2.setImageResource(R.drawable.error);
            }

        });
    }


    @Override
    public void onFurnitureClicked(Furniture furniture) {
        Intent intent = new Intent(getContext(), product.class);
        intent.putExtra("Furniture", furniture);
        startActivity(intent);
    }
}
