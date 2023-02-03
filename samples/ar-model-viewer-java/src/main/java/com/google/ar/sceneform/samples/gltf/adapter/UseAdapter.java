package com.google.ar.sceneform.samples.gltf.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.ar.sceneform.samples.gltf.R;
import com.google.ar.sceneform.samples.gltf.model.Furniture;
import com.google.ar.sceneform.samples.gltf.model.OnClickFurniture;

import java.util.List;

public class UseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<Furniture> furnitureList;
    public final OnClickFurniture onClickFurniture;

    public UseAdapter(List<Furniture> furnitureList,OnClickFurniture onClickFurniture) {
        this.furnitureList = furnitureList;
        this.onClickFurniture = onClickFurniture;
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UseAdapter.UseViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.use_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((UseAdapter.UseViewHolder) holder).setData(furnitureList.get(position));
    }

    @Override
    public int getItemCount() {
        return furnitureList.size();
    }

    public class UseViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFurniture;

        public UseViewHolder(View itemView) {
            super(itemView);
            imgFurniture = itemView.findViewById(R.id.imageProductHome);
        }

        public void setData(Furniture furniture) {
            Glide.with(itemView.getContext()).load(furniture.getImage()).into(imgFurniture);
            itemView.setOnClickListener(v -> onClickFurniture.onFurnitureClicked(furniture));
        }
    }
}
