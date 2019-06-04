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
import androidx.viewpager.widget.PagerAdapter;

//public class AdapterImageView extends RecyclerView.Adapter<AdapterImageView.ViewHolder> {
//
//    ArrayList<String> urls;
//
//    public AdapterImageView(ArrayList<String> ImgUrl) {
//        this.urls = ImgUrl;
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        private ImageView image;
//
//        public ViewHolder(View v) {
//            super(v);
//            image = v.findViewById(R.id.photo_item);
//        }
//
//        public ImageView getImage() {
//            return this.image;
//        }
//    }
//
//    @NonNull
//    @Override
//    public AdapterImageView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item, parent, false);
////        v.setLayoutParams(new RecyclerView.LayoutParams(0,800));
//        return new ViewHolder(v);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull AdapterImageView.ViewHolder holder, int position) {
//        Picasso.get().load(urls.get(position)).resize(2000,0).onlyScaleDown().centerCrop().into(holder.getImage());
//    }
//
//    @Override
//    public int getItemCount() {
//        return urls.size();
//    }
//}
public class AdapterImageView extends PagerAdapter {

    private Context context;
    private ArrayList<String> imageUrls;

    AdapterImageView(Context context, ArrayList<String> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }
    @Override
    public int getCount() {
        return imageUrls.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView = new ImageView(context);
        Picasso.get()
                .load(imageUrls.get(position))
                .resize(2000,0)
                .onlyScaleDown()
                .centerCrop()
                .into(imageView);
        container.addView(imageView);

        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}