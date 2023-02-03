package com.google.ar.sceneform.samples.gltf.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.ar.sceneform.samples.gltf.R;
import com.google.ar.sceneform.samples.gltf.model.Cover;
import com.google.ar.sceneform.samples.gltf.model.Furniture;
import com.google.ar.sceneform.samples.gltf.model.OnClickCover;
import com.google.ar.sceneform.samples.gltf.model.OnClickFurniture;

import java.util.List;

public class CoverAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<Cover> coverList;
    public final OnClickCover onClickCover;


    public CoverAdapter(List<Cover> coverList,OnClickCover onClickCover) {
        this.coverList = coverList;
        this.onClickCover = onClickCover;
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CoverAdapter.CoverViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((CoverAdapter.CoverViewHolder) holder).setData(coverList.get(position));
    }

    @Override
    public int getItemCount() {
        return coverList.size();
    }

    public void filterList(List<Cover> filteredList) {
        coverList = filteredList;
        notifyDataSetChanged();
    }

    public class CoverViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFurniture;

        public CoverViewHolder(View itemView) {
            super(itemView);
            imgFurniture = itemView.findViewById(R.id.imageProductHome);
        }

        public void setData(Cover cover) {
            Glide.with(itemView.getContext()).load(cover.getLink()).into(imgFurniture);
            itemView.setOnClickListener(v -> onClickCover.onClickCover(cover, itemView));
            itemView.setOnLongClickListener(v -> {onClickCover.onLongClickCover(cover, itemView); return true;});
        }
    }

}
