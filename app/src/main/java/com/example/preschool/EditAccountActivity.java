package com.example.preschool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditAccountActivity extends AppCompatActivity {
    private EditText userName,userFullName,userDOB,userParentOf;
    private Button UpdateAccountSettingButton;
    private CircleImageView userProfImage;
    private ProgressDialog loadingBar;

    private DatabaseReference EditUserRef;
    private FirebaseAuth mAuth;
    private String currentUserId;
    final static int Gallery_Pick = 1;
    private StorageReference UserProfileImageRef;
    private Uri resultUri;
    private Spinner classNameSpinner,roleSpinner;
    private String editRole,editClass;
    private int classChoose=0;
    private int roleChoose=0;
    final ArrayList<String> role=new ArrayList<>();
    private final ArrayList<String> className=new ArrayList<>();
    private final ArrayList<String> classId=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);
        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();

        EditUserRef= FirebaseDatabase.getInstance().getReference().child("Users").child(getIntent().getStringExtra("USER_ID"));
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        userProfImage=findViewById(R.id.edit_profile_image);
        userName=findViewById(R.id.edit_username);
        userFullName=findViewById(R.id.edit_fullname);
        userDOB=findViewById(R.id.edit_birthday);
        userParentOf=findViewById(R.id.edit_parentof);
        UpdateAccountSettingButton=findViewById(R.id.update_account_settings_button);
        loadingBar=new ProgressDialog(this);

        //spinner
        roleSpinner=findViewById(R.id.roleSniper);
        classNameSpinner=findViewById(R.id.classNameSpinner);

        role.add("Choose Role...");
        role.add("Parent");
        role.add("Teacher");
        role.add("Admin");
        final ArrayAdapter<String> roleAdapter = new ArrayAdapter<String>(EditAccountActivity.this,android.R.layout.simple_spinner_item,role){
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
        final ArrayAdapter<String> autoComplete = new ArrayAdapter<String>(EditAccountActivity.this,android.R.layout.simple_spinner_item,className){
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
        userParentOf.setVisibility(View.GONE);

        roleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==1||position==2){
                    classNameSpinner.setVisibility(View.VISIBLE);
                    userParentOf.setVisibility(View.VISIBLE);
                }
                else if(position==3){
                    classNameSpinner.setVisibility(View.GONE);
                    userParentOf.setVisibility(View.GONE);
                }
                roleChoose=position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        classNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                classChoose=position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        EditUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("profileimage")){
                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    Picasso.get().load(myProfileImage).placeholder(R.drawable.ic_person_black_50dp).into(userProfImage);
                }
                if(dataSnapshot.hasChild("fullname")){
                    String myProfileName = dataSnapshot.child("fullname").getValue().toString();
                    userFullName.setText(myProfileName);
                }
                if(dataSnapshot.hasChild("username")){
                    String myUserName = dataSnapshot.child("username").getValue().toString();
                    userName.setText(myUserName);
                }
                if(dataSnapshot.hasChild("birthday")){
                    String myDOB = dataSnapshot.child("birthday").getValue().toString();
                    userDOB.setText(myDOB);
                }
                if(dataSnapshot.hasChild("parentof")){
                    String myParentOf = dataSnapshot.child("parentof").getValue().toString();
                    userParentOf.setText(myParentOf);
                }
                editRole=dataSnapshot.child("role").getValue().toString();
                int vitri=0;
                for(int i=1;i<role.size();i++){
                    if(role.get(i).equals(editRole)){
                        vitri=i;
                    }
                }
                roleSpinner.setSelection(vitri);
                if(dataSnapshot.hasChild("idclass")){
                    editClass=dataSnapshot.child("idclass").getValue().toString();
                    for(int i=1;i<classId.size();i++){
                        if(classId.get(i).equals(editClass)){
                            vitri=i;
                        }
                    }
                    classNameSpinner.setSelection(vitri);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        userProfImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick);
            }
        });
        UpdateAccountSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateAccountInfo();
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==Gallery_Pick && resultCode==RESULT_OK && data!=null)
        {
            Uri ImageUri = data.getData();

            CropImage.activity(ImageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK)
            {
                resultUri = result.getUri();
                userProfImage.setImageURI(resultUri);
            }
        }
    }
    private void ValidateAccountInfo() {
        String username=userName.getText().toString();
        String userfullname=userFullName.getText().toString();
        String userdob=userDOB.getText().toString();
        String userparentof=userParentOf.getText().toString();
        loadingBar.setTitle("Profile Update");
        loadingBar.setMessage("Please wait, while we updating your profile...");
        loadingBar.setCanceledOnTouchOutside(true);
        loadingBar.show();
        UpdateAccountInfo(username,userfullname,userdob,userparentof,role.get(roleChoose),classId.get(classChoose),className.get(classChoose));
    }
    private void UpdateAccountInfo(final String username, final String userfullname, final String userdob, final String userparentof,final String role,final String idclass, final String classname) {
        StorageReference filePath = UserProfileImageRef.child(getIntent().getStringExtra("USER_ID") + ".jpg");
        if(resultUri!=null){
            filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task)
                {
                    if(task.isSuccessful()) {
                        Toast.makeText(EditAccountActivity.this, "Profile Image stored successfully to Firebase storage...", Toast.LENGTH_SHORT).show();
                        Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();
                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final String downloadUrl = uri.toString();
                                //lưu hình ảnh lên
                                EditUserRef.child("profileimage").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            //Toast.makeText(EditAccountActivity.this, "Profile Image stored to Firebase Database Successfully...", Toast.LENGTH_SHORT).show();
                                            //finish();
                                        } else {
                                            //String message = task.getException().getMessage();
                                            //Toast.makeText(EditAccountActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });

                    }
                }
            });
        }

        HashMap userMap=new HashMap();
        userMap.put("username",username);
        userMap.put("fullname",userfullname);
        userMap.put("birthday",userdob);
        userMap.put("role",role);
        if(roleChoose==1||roleChoose==2){
            userMap.put("idclass", idclass);
            userMap.put("classname",classname);
            userMap.put("parentof",userparentof);
        }
        else {
            EditUserRef.child("idclass").removeValue();
            EditUserRef.child("classname").removeValue();
            EditUserRef.child("parentof").removeValue();
        }

        EditUserRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    Toast.makeText(EditAccountActivity.this,"Updated Successful",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(EditAccountActivity.this,"Error",Toast.LENGTH_SHORT).show();

                }
            }
        });
        Intent intent=new Intent(EditAccountActivity.this, ViewAccountActivity.class);
        intent.putExtra("USER_ID",getIntent().getStringExtra("USER_ID"));
        startActivity(intent);
    }
}
