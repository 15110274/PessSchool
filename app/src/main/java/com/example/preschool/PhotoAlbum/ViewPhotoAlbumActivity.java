package com.example.preschool.PhotoAlbum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
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
    private ViewPager viewPager;
    private DatabaseReference mPhotosRef;
    private String positionAlbum;
    private TextView nameAlbum;
    private Album mAlbum=new Album();
    private AdapterImageView adapterImageView;
    private String idClass,idTeacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photo_album);
        final ActionBar actionBar = getSupportActionBar();

        positionAlbum=getIntent().getExtras().get("POSITION_ALBUM").toString();
        /**
         * quăng id class vô chổ này classtest1
         *
         */
        /////////////////////////////////////////////////
        idClass=getIntent().getExtras().get("idClass").toString();
        idTeacher=getIntent().getExtras().get("idTeacher").toString();

        mPhotosRef = FirebaseDatabase.getInstance().getReference().child("Class").child(idClass).child("Albums").child(positionAlbum);
        mPhotosRef.keepSynced(true);

        //myRecycleView = findViewById(R.id.recycler_view_show_photo);
        viewPager=findViewById(R.id.view_pager);

//        myRecycleView.hasFixedSize();
//        myRecycleView.setLayoutManager(new LinearLayoutManager(this));

        mPhotosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mAlbum= dataSnapshot.getValue(Album.class);
                actionBar.setTitle(mAlbum.getName());

//                adapterImageView=new AdapterImageView(mAlbum.getImageUrlList());
//                myRecycleView.setAdapter(adapterImageView);
                AdapterImageView adapter = new AdapterImageView(ViewPhotoAlbumActivity.this, mAlbum.getImageUrlList());
                viewPager.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

}
