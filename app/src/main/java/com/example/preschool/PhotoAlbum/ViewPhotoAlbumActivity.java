package com.example.preschool.PhotoAlbum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;


import com.example.preschool.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ViewPhotoAlbumActivity extends AppCompatActivity {

    private RecyclerView myRecycleView;
    private DatabaseReference mPhotosRef;
    private String positionAlbum;
    private TextView nameAlbum;
    private Album mAlbum=new Album();
    private AdapterImageView adapterImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photo_album);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.camera);

        positionAlbum=getIntent().getExtras().get("POSITION_ALBUM").toString();

        mPhotosRef = FirebaseDatabase.getInstance().getReference().child("Albums").child(positionAlbum);
        mPhotosRef.keepSynced(true);

        myRecycleView = findViewById(R.id.recycler_view_show_photo);


        myRecycleView.hasFixedSize();
        myRecycleView.setLayoutManager(new LinearLayoutManager(this));

        mPhotosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mAlbum= dataSnapshot.getValue(Album.class);
                actionBar.setTitle(mAlbum.getName());

                adapterImageView=new AdapterImageView(mAlbum.getImageUrlList());
                myRecycleView.setAdapter(adapterImageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });



    }


}
