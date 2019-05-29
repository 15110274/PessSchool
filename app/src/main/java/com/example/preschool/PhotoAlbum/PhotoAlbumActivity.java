package com.example.preschool.PhotoAlbum;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.preschool.PhotoAlbum.Adapter.Album;
import com.example.preschool.R;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;


public class PhotoAlbumActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private RecyclerView myRecycleView;
    private DatabaseReference mPhotosRef;
    private FirebaseRecyclerAdapter<Album, PhotoAlbumActivity.ItemViewHolder> myRecycleViewAdpter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_album);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.photo_album);

        fab=findViewById(R.id.add_new_album);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NewAlbumActivity.class);
                startActivity(intent);
            }
        });

        mPhotosRef = FirebaseDatabase.getInstance().getReference().child("Albums");
        mPhotosRef.keepSynced(true);

        myRecycleView = findViewById(R.id.albumRecyclerView);

        DatabaseReference personsRef = FirebaseDatabase.getInstance().getReference().child("Albums");
        Query personsQuery = personsRef.orderByKey();

        myRecycleView.hasFixedSize();
        myRecycleView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions AlbumOptions = new FirebaseRecyclerOptions.Builder<Album>().setQuery(personsQuery, Album.class).build();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        myRecycleView.setLayoutManager(gridLayoutManager);
        myRecycleViewAdpter = new FirebaseRecyclerAdapter<Album, PhotoAlbumActivity.ItemViewHolder>(AlbumOptions) {
            @Override
            protected void onBindViewHolder(PhotoAlbumActivity.ItemViewHolder holder, final int position, final Album album) {
                holder.setTitle(album.getName());
                holder.setImage(album.getImageUrlList().get(0));
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        final String url = album.();
                        Intent intent = new Intent(getApplicationContext(), UploadMultiImageActivity.class);
//                        intent.putExtra("id", url);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public PhotoAlbumActivity.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.album_grid_view, parent, false);

                return new PhotoAlbumActivity.ItemViewHolder(view);
            }
        };

        myRecycleView.setAdapter(myRecycleViewAdpter);
    }

    @Override
    public void onStart() {
        super.onStart();
        myRecycleViewAdpter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        myRecycleViewAdpter.stopListening();


    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public ItemViewHolder(View itemView){
            super(itemView);
            mView = itemView.findViewById(R.id.card);
        }
        public void setTitle(String title){
            TextView albumTile = mView.findViewById(R.id.title_album);
            albumTile.setText(title);
        }
        public void setImage(String image){
            ImageView imgAlbum = mView.findViewById(R.id.img_thumbnail);
            Picasso.get().load(image).resize(100,100).into(imgAlbum);
        }

    }
}
