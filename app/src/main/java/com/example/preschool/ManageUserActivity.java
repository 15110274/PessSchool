package com.example.preschool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.preschool.Chats.MessageActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class ManageUserActivity extends AppCompatActivity {
    private RecyclerView myAccountList;
    private FirebaseAuth mAuth;
    private EditText UserEmail;
    private FloatingActionButton CreateAccountButton;
    private ProgressDialog loadingBar;
    private Spinner classNameSpinner;
    private DatabaseReference UsersRef;
    private int positionChoose=0;
    private String userIdJustAdd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user);
        mAuth=FirebaseAuth.getInstance();
        UserEmail = findViewById(R.id.setup_email);
        CreateAccountButton = findViewById(R.id.create_button);
        classNameSpinner=findViewById(R.id.classNameSniper);
        loadingBar = new ProgressDialog(this);
        UsersRef=FirebaseDatabase.getInstance().getReference().child("Users");
        myAccountList=findViewById(R.id.list_account);
        myAccountList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ManageUserActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myAccountList.setLayoutManager(linearLayoutManager);
        LoadAccountClass();
        final ArrayList<String> className=new ArrayList<>();
        final ArrayList<String> classId=new ArrayList<>();
        className.add("Choose Class...");
        classId.add("");
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("Class").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot suggestionSnapshot : dataSnapshot.getChildren()){
                    String classname = suggestionSnapshot.child("classname").getValue(String.class);
                    String classid = suggestionSnapshot.getKey();
                    className.add(classname);
                    classId.add(classid);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        final ArrayAdapter<String> autoComplete = new ArrayAdapter<String>(ManageUserActivity.this,android.R.layout.simple_spinner_item,className){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    tv.setTextColor(getResources().getColor(R.color.hintcolor));
                }
                else {
                    tv.setTextColor(Color.WHITE);
                }
                view.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                return view;
            }
        };
        autoComplete.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classNameSpinner.setAdapter(autoComplete);
        classNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                classNameSpinner.setOnItemSelectedListener(this);
                positionChoose=position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String email = UserEmail.getText().toString();
                String password= "123456";
                if(TextUtils.isEmpty(email))
                {
                    Toast.makeText(ManageUserActivity.this, "Please write your email...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    loadingBar.setTitle("Creating New Account");
                    loadingBar.setMessage("Please wait, while we are creating your new Account...");
                    loadingBar.show();
                    loadingBar.setCanceledOnTouchOutside(true);
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        UserEmail.setText("");
                                        userIdJustAdd=task.getResult().getUser().getUid();
                                        final HashMap userMap = new HashMap();
                                        userMap.put("idclass", classId.get(positionChoose));
                                        userMap.put("classname",className.get(positionChoose));
                                        UsersRef.child(userIdJustAdd).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(ManageUserActivity.this, "your Account is created Successfully.", Toast.LENGTH_LONG).show();
                                                    mAuth.signOut();
                                                    mAuth.signInWithEmailAndPassword("khoandv@gmail.com", "123456")
                                                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<AuthResult> task)
                                                                {
                                                                    recreate();
                                                                }
                                                            });
                                                    loadingBar.dismiss();
                                                } else {
                                                    String message = task.getException().getMessage();
                                                    Toast.makeText(ManageUserActivity.this, "Error Occured: " + message, Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();
                                                }
                                            }
                                        });
                                    }
                                    else
                                    {
                                        String message = task.getException().getMessage();
                                        Toast.makeText(ManageUserActivity.this, "Error Occured: " + message, Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }
                                }

                            });
                }
            }

        });

    }

    private void LoadAccountClass() {
        FirebaseRecyclerOptions<User> options=new FirebaseRecyclerOptions.Builder<User>().
                setQuery(UsersRef, User.class).build();
        FirebaseRecyclerAdapter<User, UsersViewHolder> adapter=new FirebaseRecyclerAdapter<User, UsersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final UsersViewHolder usersViewHolder, int position, @NonNull final User model) {
//                if(model.getIdclass().equals(idClassChoose)){
                final String usersIDs = getRef(position).getKey();
                if(model.getFullname()!=null){
                    usersViewHolder.setFullname(model.getFullname());
                    usersViewHolder.setProfileImage(model.getProfileimage());
                    usersViewHolder.setStudent(model.getParentof());
                }
                usersViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence options[] = new CharSequence[]{
                                "Detail User",
                                "Edit User",
                                "Delete User"
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(ManageUserActivity.this);
                        builder.setTitle("Select Option");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                if (which == 0) {
//                                    Intent profileintent=new Intent(getActivity(),PersonProfileActivity.class);
//                                    profileintent.putExtra("visit_user_id",usersIDs);
//                                    profileintent.putExtra("idTeacher",idTeacher);
//                                    profileintent.putExtra("idClass",idClass);
//                                    startActivity(profileintent);
//                                }
//                                if (which == 1) {
//                                    Intent chatintent = new Intent(ManageUserActivity.this, MessageActivity.class);
//                                    chatintent.putExtra("userid", usersIDs);
//                                    startActivity(chatintent);
//                                }
                            }
                        });
                        builder.show();
                    }
                });
//                }
//                else{
//                    usersViewHolder.Layout_hide();
//                }
            }
            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.all_account_display_layout,parent,false);

                UsersViewHolder viewHolder=new UsersViewHolder(view);
                return viewHolder;
            }
        };
        myAccountList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder{
        private ImageView user_image;
        private TextView user_name;
        private TextView user_student;
        private final androidx.constraintlayout.widget.ConstraintLayout layout;
        final LinearLayout.LayoutParams params;
        public UsersViewHolder(View itemView){
            super(itemView);
            layout =itemView.findViewById(R.id.all_account_class_layout);

            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            user_image=layout.findViewById(R.id.all_account_profile_image);
            user_name=layout.findViewById(R.id.all_account_full_name);
            user_student=layout.findViewById(R.id.all_account_student);

        }
        public void setProfileImage(String profileimage) {
            Picasso.get().load(profileimage).placeholder(R.drawable.ic_person_black_50dp).into(user_image);

        }

        public void setFullname(String fullname) {
            user_name.setText(fullname);
        }

        public void setStudent(String student) {
            user_student.setText(student);
        }
        private void Layout_hide() {
            params.height = 0;
            layout.setLayoutParams(params);
        }
    }

}