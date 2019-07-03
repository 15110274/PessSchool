package com.example.preschool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.preschool.Event.AddEventActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class EditPostActivity extends AppCompatActivity {

    private TextView userName;
    private EditText description;
    private ImageView postImages,avatar;
    private String currentUserId;
    private DatabaseReference PostRef,UserRef;
    private ValueEventListener postEventListener,userEventListener;
    private String postKey,idClass;
    private Button btnSave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        userName=findViewById(R.id.post_user_name);
        description=findViewById(R.id.post_description);
        postImages=findViewById(R.id.post_image);
        avatar=findViewById(R.id.post_profile_image);
        btnSave=findViewById(R.id.btn_save_post);

        currentUserId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        postKey=getIntent().getStringExtra("POST_KEY");
        idClass=getIntent().getStringExtra("ID_CLASS");


        UserRef=FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        PostRef= FirebaseDatabase.getInstance().getReference().child("Class").child(idClass).child("Posts").child(postKey);

        loadData();


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveEditPost();

            }
        });

    }

    private void saveEditPost() {
        PostRef.child("description").setValue(description.getText().toString());
        finish();
    }

    private void loadData() {
        // Load avatar and username
        userEventListener=UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userName.setText(dataSnapshot.child("username").getValue().toString());
                Picasso.get()
                        .load(dataSnapshot.child("profileimage").getValue().toString())
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .resize(70,0)
                        .into(avatar);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Load decription and image
        postEventListener=PostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                description.setText(dataSnapshot.child("description").getValue().toString());
                Picasso.get()
                        .load(dataSnapshot.child("postimage").getValue().toString())
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .resize(600,0)
                        .into(postImages);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        UserRef.removeEventListener(userEventListener);
        PostRef.removeEventListener(postEventListener);
    }
}
