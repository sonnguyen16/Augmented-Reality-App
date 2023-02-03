package com.google.ar.sceneform.samples.gltf.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.ar.sceneform.samples.gltf.R;
import com.google.ar.sceneform.samples.gltf.model.Furniture;
import com.google.ar.sceneform.samples.gltf.home;
import com.google.ar.sceneform.samples.gltf.model.OnClickFurniture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FurnitureHomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<Furniture> furnitureList;
    public final OnClickFurniture onClickFurniture;

    public FurnitureHomeAdapter(List<Furniture> furnitureList,OnClickFurniture onClickFurniture) {
        this.furnitureList = furnitureList;
        this.onClickFurniture = onClickFurniture;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FurnitureViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.product_home,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((FurnitureViewHolder) holder).setData(furnitureList.get(position));
    }

    @Override
    public int getItemCount() {
        return furnitureList.size();
    }

    public class FurnitureViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFurniture,imageFavorite;
        TextView txtFurnitureName;
        TextView txtFurniturePrice,like;

        public FurnitureViewHolder(View itemView) {
            super(itemView);
            imgFurniture = itemView.findViewById(R.id.imageProductHome);
            txtFurnitureName = itemView.findViewById(R.id.nameProductHome);
            txtFurniturePrice = itemView.findViewById(R.id.priceProductHome);
            imageFavorite = itemView.findViewById(R.id.imageFavorite);
            like = itemView.findViewById(R.id.like);
            imageFavorite.setTag(true);
        }

        public void setData(Furniture furniture) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Favorite").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            Glide.with(itemView.getContext()).load(furniture.getImage()).into(imgFurniture);
            txtFurnitureName.setText(furniture.getName());
            txtFurniturePrice.setText("$"+furniture.getPrice());
            itemView.setOnClickListener(v -> onClickFurniture.onFurnitureClicked(furniture));
            like.setText(furniture.getLike() + "");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snapshot1:snapshot.getChildren()
                         ) {
                        String favor = (String) snapshot1.getValue();
                        if (favor.equals(furniture.getName())){
                            imageFavorite.setImageResource(R.drawable.ic_favorite_red_24dp);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            imageFavorite.setOnClickListener(v -> {
                imageFavorite.setTag(!(boolean) imageFavorite.getTag());
                if (!(Boolean) imageFavorite.getTag()){
                    imageFavorite.setImageResource(R.drawable.ic_favorite_red_24dp);
                    databaseReference.push().setValue(furniture.getName());
                }

                else{
                    imageFavorite.setImageResource(R.drawable.ic_favorite_white_24dp);

                }
            });
        }
    }
}
