package com.example.preschool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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
    private Intent intent;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Toast.makeText(this,"Start Activity",Toast.LENGTH_LONG).show();

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUserID = mAuth.getCurrentUser().getUid();
            valueEventListener=UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (currentUserID.equals("Z85jCL2QLARLYoQGPjltOB5kCOE2")) {
                        intent = new Intent(StartActivity.this, AdminActivity.class);
                        startActivity(intent);
                    } else if (!dataSnapshot.hasChild("fullname")) {
                        intent = new Intent(StartActivity.this, SetupActivity.class);
                        startActivity(intent);
                    } else {
                        final String idClass;

                        idClass = dataSnapshot.child("idclass").getValue().toString();
                        DatabaseReference ClassRef = FirebaseDatabase.getInstance().getReference().child("Class").child(idClass);
                        ClassRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String idTeacher = dataSnapshot.child("teacher").getValue().toString();
                                String className = dataSnapshot.child("classname").getValue().toString();
                                Bundle bundleStart = new Bundle();
                                intent = new Intent(StartActivity.this, MainActivity.class);
                                // Đóng gói dữ liệu vào bundle
                                bundleStart.putString("ID_CLASS", idClass);
                                bundleStart.putString("CLASS_NAME", className);
                                bundleStart.putString("ID_TEACHER", idTeacher);
                                intent.putExtras(bundleStart);

                                startActivity(intent);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            intent = new Intent(StartActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }


//    private void SendUserToMainActivity() {
//        String currentUserID = mAuth.getCurrentUser().getUid();
//        final DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
//        final DatabaseReference ClassRef = FirebaseDatabase.getInstance().getReference().child("Class");
//        UsersRef.child(currentUserID).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                final String idClass;
//                idClass = dataSnapshot.child("idclass").getValue().toString();
//                ClassRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        String idTeacher = dataSnapshot.child(idClass).child("teacher").getValue().toString();
//                        String className = dataSnapshot.child(idClass).child("classname").getValue().toString();
//                        Bundle bundleStart = new Bundle();
//
//                        // Đóng gói dữ liệu vào bundle
//                        bundleStart.putString("ID_CLASS", idClass);
//                        bundleStart.putString("CLASS_NAME", className);
//                        bundleStart.putString("ID_TEACHER", idTeacher);
//                        intentMain.putExtras(bundleStart);
//                        startActivity(intentMain);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });
//
//
//    }

    @Override
    protected void onPause() {
        super.onPause();
        if(valueEventListener!=null){

            UsersRef.removeEventListener(valueEventListener);
        }
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        finish();
    }
}
