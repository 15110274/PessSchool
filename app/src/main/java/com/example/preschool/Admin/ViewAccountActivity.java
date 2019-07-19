package com.example.preschool.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.preschool.Children.Children;
import com.example.preschool.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewAccountActivity extends AppCompatActivity {
    private TextView userPhone, userName, userProfName, userRole, userEmail;
    private TextView userClass;
    private RecyclerView recyclerView;
    private CircleImageView userProfileImage;

    private Button EditButton;

    private DatabaseReference UsersRef, ClassRef;
    private FirebaseAuth mAuth;
    private String userid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_account);

        userid = getIntent().getStringExtra("USER_ID");
        mAuth = FirebaseAuth.getInstance();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
        ClassRef = FirebaseDatabase.getInstance().getReference("Class");

        userProfileImage = findViewById(R.id.person_profile_pic);
        userName = findViewById(R.id.person_username);
        userEmail = findViewById(R.id.email);
        userProfName = findViewById(R.id.person_full_name);
//        userParentof = findViewById(R.id.relationship_with_children);
//        userBirthDay = findViewById(R.id.person_birthday);
        userClass = findViewById(R.id.person_class1);
        userRole = findViewById(R.id.role);
        userPhone = findViewById(R.id.phonenumber);
        EditButton = findViewById(R.id.edit_account);
        recyclerView=findViewById(R.id.recycler_info_kid);
        recyclerView.setHasFixedSize(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        //show info user

        UsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("role")) {
                    //show on recyclerview
                    String myRole = dataSnapshot.child("role").getValue().toString();
                    if (myRole.equals("Teacher")) {
                        userClass.setVisibility(View.VISIBLE);
                        String myClass = dataSnapshot.child("classname").getValue().toString();
                        userClass.setText("Lớp: " + myClass);
                    } else if (myRole.equals("Parent")) {

                        Query mychildrenQuery = UsersRef.child("mychildren");
                        final FirebaseRecyclerOptions<Children> options = new FirebaseRecyclerOptions.Builder<Children>().setQuery(mychildrenQuery, Children.class).build();
                        FirebaseRecyclerAdapter adapter;
                        adapter = new FirebaseRecyclerAdapter<Children, ViewAccountActivity.ChildrenViewHolder>(options){

                            @NonNull
                            @Override
                            public ChildrenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                View view;
                                // create a new view

                                view = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.info_kid_items, parent, false);
                                return new ViewAccountActivity.ChildrenViewHolder (view);
                            }

                            @Override
                            protected void onBindViewHolder(@NonNull final ChildrenViewHolder childrenViewHolder, int i, @NonNull Children children) {
                                String classKey = getRef(i).getKey();
                                final String[] className = new String[1];
                                ClassRef.child(classKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        childrenViewHolder.txtClass.setText("Lớp: "+dataSnapshot.child("classname").getValue(String.class));
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                childrenViewHolder.txtName.setText("Bé: "+ children.getName());
                                childrenViewHolder.txtBirthday.setText("Sinh nhật: "+children.getBirthday());
                            }
                        };
                        adapter.startListening();
                        adapter.notifyDataSetChanged();
                        recyclerView.setAdapter(adapter);
                    }
//
                    userRole.setText("Role: " + myRole);
                }


                String myEmail = dataSnapshot.child("email").getValue().toString();
                userEmail.setText("Email: " + myEmail);
                if (dataSnapshot.hasChild("profileimage")) {
                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    Picasso.get().load(myProfileImage).placeholder(R.drawable.ic_person_black_50dp).into(userProfileImage);
                }
                if (dataSnapshot.hasChild("phonenumber")) {
                    String phone = dataSnapshot.child("phonenumber").getValue().toString();
                    userPhone.setText("Sdt: " + phone);
                }
                if (dataSnapshot.hasChild("fullname")) {
                    String myProfileName = dataSnapshot.child("fullname").getValue().toString();
                    userProfName.setText(myProfileName);
                }
                if (dataSnapshot.hasChild("username")) {
                    String myUserName = dataSnapshot.child("username").getValue().toString();
                    userName.setText(myUserName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        UsersRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.hasChild("role")) {
//                    //show on recyclerview
//                    String myRole = dataSnapshot.child("role").getValue().toString();
////                    if (myRole.equals("Admin")) {
////                        userClass.setVisibility(View.GONE);
////                        userParentof.setVisibility(View.GONE);
////                        userBirthDay.setVisibility(View.GONE);
////                    } else {
////                        String myClass = dataSnapshot.child("classname").getValue().toString();
////                        userClass.setText("Lớp: " + myClass);
////                    }
//                    if (myRole.equals("Teacher")) {
//                        userClass.setVisibility(View.VISIBLE);
//                        String myClass = dataSnapshot.child("classname").getValue().toString();
//                        userClass.setText("Lớp: " + myClass);
////                        userClass.setText("Lớp: " + myClass);
////                        userParentof.setVisibility(View.GONE);
////                        userBirthDay.setVisibility(View.GONE);
//                    }else if(myRole.equals("Parent")){
//
//                        Query SortPostsInDecendingOrder = UsersRef.child("");
//                        final FirebaseRecyclerOptions<Posts> options = new FirebaseRecyclerOptions.Builder<Posts>().setQuery(SortPostsInDecendingOrder, Posts.class).build();
//                        adapter = new FirebaseRecyclerAdapter<Posts, NewsFeedFragment.PostsViewHolder>(options)
//                    }
////                    if (myRole.equals("Parent")) {
////                        userClass.setVisibility(View.VISIBLE);
////                        userParentof.setVisibility(View.VISIBLE);
////                        userBirthDay.setVisibility(View.VISIBLE);
////                    }
////                    userRole.setText("Role: " + myRole);
//                }
//
//
//
//                String myEmail = dataSnapshot.child("email").getValue().toString();
//                userEmail.setText("Email: " + myEmail);
//                if (dataSnapshot.hasChild("profileimage")) {
//                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
//                    Picasso.get().load(myProfileImage).placeholder(R.drawable.ic_person_black_50dp).into(userProfileImage);
//                }
//                if (dataSnapshot.hasChild("phonenumber")) {
//                    String phone = dataSnapshot.child("phonenumber").getValue().toString();
//                    userPhone.setText("Sdt: " + phone);
//                }
//                if (dataSnapshot.hasChild("fullname")) {
//                    String myProfileName = dataSnapshot.child("fullname").getValue().toString();
//                    userProfName.setText(myProfileName);
//                }
//                if (dataSnapshot.hasChild("username")) {
//                    String myUserName = dataSnapshot.child("username").getValue().toString();
//                    userName.setText(myUserName);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
        EditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewAccountActivity.this, EditAccountActivity.class);
                intent.putExtra("USER_ID", getIntent().getStringExtra("USER_ID"));
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
        Intent intent = new Intent(ViewAccountActivity.this, ManageUserActivity.class);
        startActivity(intent);
    }


    public class ChildrenViewHolder extends RecyclerView.ViewHolder {

        TextView txtClass, txtName, txtBirthday;

        public ChildrenViewHolder(@NonNull View itemView) {
            super(itemView);
            txtClass=itemView.findViewById(R.id.person_class);
            txtName=itemView.findViewById(R.id.relationship_with_children);
            txtBirthday=itemView.findViewById(R.id.person_birthday);
        }
    }
}
