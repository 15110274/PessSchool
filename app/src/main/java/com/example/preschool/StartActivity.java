package com.example.preschool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StartActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    private String currentUserID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            currentUserID=mAuth.getCurrentUser().getUid();
            UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
            UsersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (currentUserID.equals("Z85jCL2QLARLYoQGPjltOB5kCOE2")) {
                        Intent intent = new Intent(StartActivity.this, AdminActivity.class);
                        startActivity(intent);
                    }
                    else if (!dataSnapshot.hasChild("fullname")) {
                        SendUserToSetupActivity();
                    } else {
                        SendUserToMainActivity();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else {
//            Toast.makeText(StartActivity.this, "vao login1", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(StartActivity.this,LoginActivity.class);
            startActivity(intent);
        }
    }
    private void SendUserToSetupActivity() {
        Intent setupIntent = new Intent(StartActivity.this, SetupActivity.class);
//        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
    }
    private void SendUserToMainActivity()
    {
        String currentUserID = mAuth.getCurrentUser().getUid();
        final DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        final DatabaseReference ClassRef=FirebaseDatabase.getInstance().getReference().child("Class");
        UsersRef.child(currentUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String idClass;
                idClass=dataSnapshot.child("idclass").getValue().toString();
                ClassRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String idTeacher=dataSnapshot.child(idClass).child("teacher").getValue().toString();
                        Intent mainIntent = new Intent(StartActivity.this, MainActivity.class);
                        Bundle bundleStart=new Bundle();

                        // Đóng gói dữ liệu vào bundle
                        bundleStart.putString("ID_CLASS",idClass);
                        bundleStart.putString("ID_TEACHER",idTeacher);
                        mainIntent.putExtras(bundleStart);
//                        mainIntent.putExtra("idTeacher",idTeacher);
//                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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

    @Override
    protected void onPause() {
        super.onPause();
        this.finish();
    }
}
