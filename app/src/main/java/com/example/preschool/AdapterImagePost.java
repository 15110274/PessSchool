package com.example.preschool;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterImagePost extends PagerAdapter {
    private Context context;
    private ArrayList<String> imageUrls;

    AdapterImagePost(Context context, ArrayList<String> imageUrls) {
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
        final ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Picasso.get()
                .load(imageUrls.get(position))
                .resize(800, 0)
                .centerCrop()
                .into(imageView);


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialogImage = new Dialog(context, R.style.DialogViewImage);
                dialogImage.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogImage.setCancelable(true);
                dialogImage.setContentView(R.layout.dialog_show_image_post);
                ViewPager viewPager = dialogImage.findViewById(R.id.image_post_view);
                AdapterImagePostView adapterImagePostView = new AdapterImagePostView(context, imageUrls);
                viewPager.setAdapter(adapterImagePostView);
                dialogImage.show();
            }
        });
        container.addView(imageView);

        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    public class AdapterImagePostView extends PagerAdapter {

        private Context context;
        private ArrayList<String> imageUrls;

        AdapterImagePostView(Context context, ArrayList<String> imageUrls) {
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
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .resize(1440, 0)
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
}
