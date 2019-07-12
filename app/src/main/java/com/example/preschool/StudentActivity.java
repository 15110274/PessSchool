package com.example.preschool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.preschool.Event.Event;
import com.example.preschool.Event.EventsActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class StudentActivity extends AppCompatActivity {
    private String idClass, idTeacher;
    private Bundle bundle;

    private DatabaseReference ClassRef;
    private DatabaseReference UsersRef,ChildrenRef;
    private FirebaseAuth mAuth;
    private String current_user_id;
    private RecyclerView childrenList;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Danh sách trẻ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        bundle = getIntent().getExtras();
        if(bundle!=null){
            idClass=bundle.getString("ID_CLASS");
            idTeacher=bundle.getString("ID_TEACHER");
        }
        ClassRef = FirebaseDatabase.getInstance().getReference().child("Class");
        ChildrenRef=ClassRef.child(idClass).child("Children");
        mAuth = FirebaseAuth.getInstance();
        childrenList=findViewById(R.id.childrenRecycleView);
        childrenList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(StudentActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        childrenList.setLayoutManager(linearLayoutManager);
        current_user_id = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        showAllChildren();
    }
    private void showAllChildren(){
        FirebaseRecyclerOptions<Children> options = new FirebaseRecyclerOptions.Builder<Children>().
                setQuery(ChildrenRef, Children.class).build();
        FirebaseRecyclerAdapter<Children,ChildrensViewHolder> adapter =new FirebaseRecyclerAdapter<Children, ChildrensViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ChildrensViewHolder childrensViewHolder, int i, @NonNull Children children) {
                childrensViewHolder.txtFullName.setText(children.getParentof());
                childrensViewHolder.txtBirthday.setText(children.getBirthday());
                final String visit_children_id = getRef(i).getKey();

            }

            @NonNull
            @Override
            public ChildrensViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_children_layout, parent, false);

                ChildrensViewHolder viewHolder = new ChildrensViewHolder(view);
                return viewHolder;
            }
        };

        childrenList.setAdapter(adapter);
        adapter.startListening();

    }
    public static class ChildrensViewHolder extends RecyclerView.ViewHolder {
        private TextView txtFullName;
        private TextView txtBirthday;

        public ChildrensViewHolder(View itemView) {
            super(itemView);
            txtFullName = itemView.findViewById(R.id.txtChildrenFullname);
            txtBirthday = itemView.findViewById(R.id.txtChildrenBirth);
        }
    }

}
