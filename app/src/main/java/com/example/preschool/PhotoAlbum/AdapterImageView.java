package com.example.preschool.PhotoAlbum;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.preschool.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterImageView extends RecyclerView.Adapter<AdapterImageView.ViewHolder> {

    ArrayList<String> urls;

    public AdapterImageView(ArrayList<String> ImgUrl) {
        this.urls = ImgUrl;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;

        public ViewHolder(View v) {
            super(v);
            image = v.findViewById(R.id.photo_item);
        }

        public ImageView getImage() {
            return this.image;
        }
    }

    @NonNull
    @Override
    public AdapterImageView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item, parent, false);
//        v.setLayoutParams(new RecyclerView.LayoutParams(0,800));
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterImageView.ViewHolder holder, int position) {
        Picasso.get().load(urls.get(position)).resize(2000,0).onlyScaleDown().centerCrop().into(holder.getImage());
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }
}
