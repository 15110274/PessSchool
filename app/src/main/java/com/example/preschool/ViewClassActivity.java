package com.example.preschool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewClassActivity extends AppCompatActivity {
    private TextView classname,teacher;

    private Button EditButton;

    private DatabaseReference ClassRef;
    private DatabaseReference UsersRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_class);
        mAuth=FirebaseAuth.getInstance();
        UsersRef= FirebaseDatabase.getInstance().getReference().child("Users");
        ClassRef=FirebaseDatabase.getInstance().getReference().child("Class");
        classname=findViewById(R.id.class_name);
        teacher=findViewById(R.id.teacher);
        EditButton=findViewById(R.id.edit_class);
        ClassRef.child(getIntent().getStringExtra("CLASS_ID")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("classname")){
                    String name=dataSnapshot.child("classname").getValue().toString();
                    classname.setText("Tên lớp: "+name);
                }
                if(dataSnapshot.hasChild("teacher")){
                    final String _teacher=dataSnapshot.child("teacher").getValue().toString();
                   UsersRef.addValueEventListener(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                           for(DataSnapshot children:dataSnapshot.getChildren()){
                               if(children.getKey().equals(_teacher)){
                                   String email=children.child("email").getValue().toString();
                                   String fullname="";
                                   if(children.hasChild("fullname")){
                                       fullname=children.child("fullname").getValue().toString();
                                   }
                                   teacher.setText("Giáo viên: "+fullname+"("+email+")");
                               }
                           }
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
        EditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ViewClassActivity.this, EditClassActivity.class);
                intent.putExtra("CLASS_ID",getIntent().getStringExtra("CLASS_ID"));
                startActivity(intent);
            }
        });
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        Intent intent=new Intent(ViewClassActivity.this, ManageClassActivity.class);
        startActivity(intent);
    }
}
