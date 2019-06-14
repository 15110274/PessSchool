package com.example.preschool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StartActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null) {
            SendUserToMainActivity();
        }
        else {
            Intent intent=new Intent(StartActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void SendUserToMainActivity()
    {
        //////////////////////////////////////////////////////
        String currentUserID = mAuth.getCurrentUser().getUid();
        final DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        final DatabaseReference ClassRef=FirebaseDatabase.getInstance().getReference().child("Class");
        UsersRef.child(currentUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String idClass=dataSnapshot.child("idclass").getValue().toString();
                ClassRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String idTeacher=dataSnapshot.child(idClass).child("teacher").getValue().toString();
                        Intent mainIntent = new Intent(StartActivity.this, MainActivity.class);
                        mainIntent.putExtra("idClass",idClass);
                        mainIntent.putExtra("idTeacher",idTeacher);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mainIntent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


    }
}
