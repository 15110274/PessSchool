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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
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
    private Spinner classNameSpinner,roleSpinner;
    private DatabaseReference UsersRef,ClassRef;
    private int classChoose=0;
    private int roleChoose=0;
    private String userIdJustAdd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user);
        mAuth=FirebaseAuth.getInstance();
        UserEmail = findViewById(R.id.setup_email);
        CreateAccountButton = findViewById(R.id.create_button);
        classNameSpinner=findViewById(R.id.classNameSpinner);
        ClassRef=FirebaseDatabase.getInstance().getReference().child("Class");

        roleSpinner=findViewById(R.id.roleSniper);
        final ArrayList<String> role=new ArrayList<>();
        role.add("Choose Role...");
        role.add("Parent");
        role.add("Teacher");
        role.add("Admin");
        final ArrayAdapter<String> roleAdapter = new ArrayAdapter<String>(ManageUserActivity.this,android.R.layout.simple_spinner_item,role){
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
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(roleAdapter );

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
        classNameSpinner.setVisibility(View.GONE);

        roleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==1||position==2){
                    classNameSpinner.setVisibility(View.VISIBLE);
                    if(classChoose!=0)
                        LoadAccountClass(role.get(position),classId.get(classChoose));
                    else LoadAccountClass(role.get(position),classId.get(0));
                }
                else if(position==3){
                    classNameSpinner.setVisibility(View.GONE);
                    LoadAccountClass(role.get(position),classId.get(0));
                }
                else  LoadAccountClass(role.get(position),classId.get(0));
                roleChoose=position;
                classNameSpinner.setSelection(0);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        loadingBar = new ProgressDialog(this);
        UsersRef=FirebaseDatabase.getInstance().getReference().child("Users");
        myAccountList=findViewById(R.id.list_account);
        myAccountList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ManageUserActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myAccountList.setLayoutManager(linearLayoutManager);

        classNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                classChoose=position;
                LoadAccountClass(role.get(roleChoose),classId.get(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                final String email = UserEmail.getText().toString();
                String password= "123456";
                if(TextUtils.isEmpty(email))
                {
                    Toast.makeText(ManageUserActivity.this, "Please write email...", Toast.LENGTH_SHORT).show();
                }
                else if(roleChoose==0){
                    Toast.makeText(ManageUserActivity.this, "Please choose role...", Toast.LENGTH_SHORT).show();
                }
                else if(roleChoose!=0 && classChoose==0){
                    Toast.makeText(ManageUserActivity.this, "Please choose class...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    loadingBar.setTitle("Creating New Account");
                    loadingBar.setMessage("Please wait, while we are creating your new Account...");
                    loadingBar.show();
                    loadingBar.setCanceledOnTouchOutside(true);
                    loadingBar.dismiss();

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
                                        userMap.put("email",email);
                                        userMap.put("role",role.get(roleChoose));
                                        if(roleChoose==1||roleChoose==2){
                                            userMap.put("idclass", classId.get(classChoose));
                                            userMap.put("classname",className.get(classChoose));
                                        }
                                        UsersRef.child(userIdJustAdd).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {
                                                if (task.isSuccessful()) {
                                                    if(roleChoose==2){
                                                        ClassRef.child(classId.get(classChoose)).child("teacher").setValue(userIdJustAdd);
                                                    }
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
                                        try
                                        {
                                            throw task.getException();
                                        }
                                        catch (FirebaseAuthUserCollisionException existEmail)
                                        {
                                            final DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Users");
                                            ref.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    final int[] success = {0};
                                                    if(dataSnapshot.exists()){
                                                        for (final DataSnapshot children: dataSnapshot.getChildren()) {
                                                            //xác định nếu ko có role thì đã xóa
                                                            if(!children.hasChild("role")){
                                                                //nếu email tạo trùng với email đã xóa thì chỉ cần thêm dữ liệu, ko cần tạo lại user
                                                                if(children.child("email").getValue().toString().equals(UserEmail.getText().toString())){
                                                                    UserEmail.setText("");
                                                                    final HashMap userMap = new HashMap();
                                                                    userMap.put("role",role.get(roleChoose));
                                                                    if(roleChoose==1||roleChoose==2){
                                                                        userMap.put("idclass", classId.get(classChoose));
                                                                        userMap.put("classname",className.get(classChoose));
                                                                    }
                                                                    ref.child(children.getKey()).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task task) {
                                                                            if (task.isSuccessful()) {
                                                                                success[0] =1;
                                                                                if(roleChoose==2){
                                                                                    ClassRef.child(classId.get(classChoose)).child("teacher").setValue(children.getKey());
                                                                                }
                                                                                //recreate();
                                                                                Toast.makeText(ManageUserActivity.this, "your Account is created Successfully.", Toast.LENGTH_LONG).show();
                                                                                loadingBar.dismiss();
                                                                            } else {
                                                                                String message = task.getException().getMessage();
                                                                                Toast.makeText(ManageUserActivity.this, "Error Occured: " + message, Toast.LENGTH_SHORT).show();
                                                                                loadingBar.dismiss();
                                                                            }
                                                                        }
                                                                    });

                                                                }
                                                            }
                                                        }
                                                    }
                                                    if(success[0]==0){
                                                        Toast.makeText(ManageUserActivity.this, "Email Existed", Toast.LENGTH_SHORT).show();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                        catch (Exception e) {
                                            Toast.makeText(ManageUserActivity.this, "Error Occured: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            loadingBar.dismiss();
                                        }
                                    }
                                }

                            });
                }
            }

        });
    }
    private void LoadAccountClass(final String roleChoose, final String idClassChoose) {
        FirebaseRecyclerOptions<User> options=new FirebaseRecyclerOptions.Builder<User>().
                setQuery(UsersRef, User.class).build();
        FirebaseRecyclerAdapter<User, UsersViewHolder> adapter=new FirebaseRecyclerAdapter<User, UsersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final UsersViewHolder usersViewHolder, final int position, @NonNull final User model) {
                //nếu chọn role thì sắp xếp theo role
                if(!roleChoose.equals("Choose Role...")){
                    //nếu ko chọn class thì sẽ hiển thị theo role
                    if(idClassChoose.equals("")){
                        if(model.getRole()!=null&&model.getRole().equals(roleChoose)){
                            if(model.getFullname()!=null)
                                usersViewHolder.setFullname(model.getFullname());
                            usersViewHolder.setProfileImage(model.getProfileimage());
                            usersViewHolder.setEmail(model.getEmail());
                        }
                        else usersViewHolder.Layout_hide();
                    }
                    //nếu vừa chọn role vừa chọn class thì hiển theo role rồi hiển thị theo class
                    else{
                        if(model.getRole()!=null&&model.getIdclass()!=null&&model.getRole().equals(roleChoose)&&model.getIdclass().equals(idClassChoose)){
                            if(model.getFullname()!=null)
                                usersViewHolder.setFullname(model.getFullname());
                            usersViewHolder.setProfileImage(model.getProfileimage());
                            usersViewHolder.setEmail(model.getEmail());
                        }
                        else usersViewHolder.Layout_hide();
                    }
                }
                //ngược lại hiển thị toàn bộ user
                else{
                    if(model.getRole()!=null){
                        if(model.getFullname()!=null)
                            usersViewHolder.setFullname(model.getFullname());
                        usersViewHolder.setProfileImage(model.getProfileimage());
                        usersViewHolder.setEmail(model.getEmail());
                    }
                    else usersViewHolder.Layout_hide();
                }
                final String usersIDs = getRef(position).getKey();
                usersViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence options[] = new CharSequence[]{
                                "Detail User",
                                "Edit User",
                                "Delete User"
                        };
                        final AlertDialog.Builder builder = new AlertDialog.Builder(ManageUserActivity.this);
                        builder.setTitle("Select Option");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which==2){
                                    final String userID= getRef(position).getKey();
                                    UsersRef.child(userID).removeValue();
                                    UsersRef.child(userID).child("email").setValue(model.getEmail());
                                    //xóa user
                                    ClassRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.exists()){
                                                for (DataSnapshot children: dataSnapshot.getChildren()) {
                                                    if(children.hasChild("teacher")&&children.child("teacher").getValue().toString().equals(userID)){
                                                        ClassRef.child(children.getKey()).child("teacher").setValue("");
                                                    }
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                                if(which==1){
                                    Intent intent=new Intent(ManageUserActivity.this,EditAccountActivity.class);
                                    intent.putExtra("USER_ID",usersIDs);
                                    startActivity(intent);
                                }
                                if(which==0){
                                    Intent intent=new Intent(ManageUserActivity.this,ViewAccountActivity.class);
                                    intent.putExtra("USER_ID",usersIDs);
                                    startActivity(intent);
                                }
                            }
                        });
                        builder.show();
                    }
                });

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
        private TextView user_email;
        private final androidx.constraintlayout.widget.ConstraintLayout layout;
        final LinearLayout.LayoutParams params;
        public UsersViewHolder(View itemView){
            super(itemView);
            layout =itemView.findViewById(R.id.all_account_class_layout);

            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            user_image=layout.findViewById(R.id.all_account_profile_image);
            user_name=layout.findViewById(R.id.all_account_full_name);
            user_email=layout.findViewById(R.id.all_account_email);

        }
        public void setProfileImage(String profileimage) {
            Picasso.get().load(profileimage).placeholder(R.drawable.ic_person_black_50dp).into(user_image);

        }

        public void setFullname(String fullname) {
            user_name.setText(fullname);
        }

        public void setEmail(String student) {
            user_email.setText(student);
        }
        private void Layout_hide() {
            params.height = 0;
            layout.setLayoutParams(params);
        }
    }
}
