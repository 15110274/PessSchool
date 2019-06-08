package com.example.preschool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {
    private EditText userName,userFullName,userDOB,userParentOf;
    private Button UpdateAccountSettingButton;
    private CircleImageView userProfImage;
    private ProgressDialog loadingBar;

    private DatabaseReference EditUserRef;
    private FirebaseAuth mAuth;
    private String currentUserId;
    final static int Gallery_Pick = 1;
    private StorageReference UserProfileImageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();
        EditUserRef= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        userProfImage=findViewById(R.id.edit_profile_image);
        userName=findViewById(R.id.edit_username);
        userFullName=findViewById(R.id.edit_fullname);
        userDOB=findViewById(R.id.edit_birthday);
        userParentOf=findViewById(R.id.edit_parentof);

        UpdateAccountSettingButton=findViewById(R.id.update_account_settings_button);
        loadingBar=new ProgressDialog(this);

        EditUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String myProfileImage=dataSnapshot.child("profileimage").getValue().toString();
                String myUserName=dataSnapshot.child("username").getValue().toString();
                String myProfileName=dataSnapshot.child("fullname").getValue().toString();
                String myDOB=dataSnapshot.child("birthday").getValue().toString();
                String myParentOf=dataSnapshot.child("parentof").getValue().toString();

                Picasso.get().load(myProfileImage).into(userProfImage);
                userName.setText(myUserName);
                userFullName.setText(myProfileName);
                userDOB.setText(myDOB);
                userParentOf.setText(myParentOf);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        UpdateAccountSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateAccountInfo();
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
    }
    @Override
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
                loadingBar.setTitle("Profile Image");
                loadingBar.setMessage("Please wait, while we updating your profile image...");
                loadingBar.setCanceledOnTouchOutside(true);
                loadingBar.show();

                Uri resultUri = result.getUri();

                StorageReference filePath = UserProfileImageRef.child(currentUserId + ".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task)
                    {
                        if(task.isSuccessful()) {

                            Toast.makeText(EditProfileActivity.this, "Profile Image stored successfully to Firebase storage...", Toast.LENGTH_SHORT).show();

                            Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();

                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String downloadUrl = uri.toString();

                                    EditUserRef.child("profileimage").setValue(downloadUrl)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Intent selfIntent = new Intent(EditProfileActivity.this, EditProfileActivity.class);
                                                        startActivity(selfIntent);

                                                        Toast.makeText(EditProfileActivity.this, "Profile Image stored to Firebase Database Successfully...", Toast.LENGTH_SHORT).show();
                                                        loadingBar.dismiss();
                                                    } else {
                                                        String message = task.getException().getMessage();
                                                        Toast.makeText(EditProfileActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                                        loadingBar.dismiss();
                                                    }
                                                }
                                            });
                                }
                            });
                        }
                    }
                });
            }
            else {
                Toast.makeText(EditProfileActivity.this, "Error Occured: Image can not be cropped. Try Again.", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
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
        UpdateAccountInfo(username,userfullname,userdob,userparentof);
    }

    private void UpdateAccountInfo(String username, String userfullname, String userdob, String userparentof) {
        HashMap userMap=new HashMap();
        userMap.put("username",username);
        userMap.put("fullname",userfullname);
        userMap.put("birthday",userdob);
        userMap.put("parentof",userparentof);
        EditUserRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    SendUserToPersonProfileActicity();
                    Toast.makeText(EditProfileActivity.this,"Updated Successful",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(EditProfileActivity.this,"Error",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void SendUserToPersonProfileActicity() {
        Intent intent=new Intent(EditProfileActivity.this, PersonProfileActivity.class);
        intent.putExtra("visit_user_id",currentUserId);
        startActivity(intent);
    }
}