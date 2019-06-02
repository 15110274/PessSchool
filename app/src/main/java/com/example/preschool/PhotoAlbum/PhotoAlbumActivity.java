package com.example.preschool.PhotoAlbum;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.preschool.MainActivity;
import com.example.preschool.R;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class PhotoAlbumActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private RecyclerView myRecycleView;
    private DatabaseReference mPhotosRef,UsersRef;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private FirebaseRecyclerAdapter<Album, PhotoAlbumActivity.ItemViewHolder> myRecycleViewAdpter;

    ////////////////////////////////
    private String idClass,idTeacher;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_album);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.photo_album);

        fab=findViewById(R.id.add_new_album);
        fab.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();
        currentUserID=mAuth.getCurrentUser().getUid();
        //////////////////////////////////////////////
        idTeacher=getIntent().getExtras().get("idTeacher").toString();
        idClass=getIntent().getExtras().get("idClass").toString();
        if (idTeacher.equals(currentUserID)) {
            fab.setVisibility(View.VISIBLE);
            Toast.makeText(PhotoAlbumActivity.this,"trung",Toast.LENGTH_LONG).show();
        }
        else{
            fab.setVisibility(View.INVISIBLE);
            Toast.makeText(PhotoAlbumActivity.this,"ko trung",Toast.LENGTH_LONG).show();
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NewAlbumActivity.class);
                intent.putExtra("idClass",idClass);
                startActivity(intent);
            }
        });

        /**
         * quăng id class vô chổ này classtest1
         *
         */
        mPhotosRef = FirebaseDatabase.getInstance().getReference().child("Class").child(idClass).child("Albums");
        mPhotosRef.keepSynced(true);

        myRecycleView = findViewById(R.id.albumRecyclerView);

        DatabaseReference personsRef = FirebaseDatabase.getInstance().getReference().child("Class").child(idClass).child("Albums");
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
                holder.setSoLuongAnh(String.valueOf(album.getImageUrlList().size()));
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), ViewPhotoAlbumActivity.class);
                        //////////////////////////////////////////////
                        intent.putExtra("idClass",idClass);
                        intent.putExtra("POSITION_ALBUM", getRef(position).getKey());
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
            mView = itemView.findViewById(R.id.view_album);
        }
        public void setTitle(String title){
            TextView albumTile = mView.findViewById(R.id.title_album);
            albumTile.setText(title);
        }
        public void setImage(String image){
            ImageView imgAlbum = mView.findViewById(R.id.img_thumbnail);
            Picasso.get().load(image).resize(500,0 ).into(imgAlbum);
        }
        public void setSoLuongAnh(String soLuongAnh){
            TextView soAnh = mView.findViewById(R.id.so_anh);
            soAnh.setText(soLuongAnh+" ảnh");
        }

    }
}
