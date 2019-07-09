package com.example.preschool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class StudentActivity extends AppCompatActivity {
    private String idClass,idTeacher;
    private ProgressDialog loadingBar;
    private Bundle bundle;
    private ImageView imageView;
    private FloatingActionButton floatingActionButton;
    private static final int Gallery_Pick = 1,GALLERY = 1;
    private Uri ImageUri;
    private StorageReference ImageRef;
    private DatabaseReference ClassRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        imageView=findViewById(R.id.btn_select_image);
        floatingActionButton=findViewById(R.id.floating_add_image);
        bundle=getIntent().getExtras();
        idClass=bundle.getString("ID_CLASS");
        idTeacher=bundle.getString("ID_TEACHER");
        loadingBar = new ProgressDialog(this);
        ImageRef = FirebaseStorage.getInstance().getReference().child(idClass);
        ClassRef= FirebaseDatabase.getInstance().getReference().child("Class").child(idClass).child("Children");

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidatePostInfo();
            }
        });
    }
    private void ValidatePostInfo() {
        if (ImageUri == null) {
            Toast.makeText(StudentActivity.this, "Please select post image...", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.setTitle("Add Image");
            loadingBar.setMessage("Please wait, while we are updating your new image...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(false);

            StorageReference filePath = ImageRef.child(idClass+ ".jpg");

            if(ImageUri!=null){
                filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {

                            Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();

                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String downloadUrl = uri.toString();

                                    ClassRef.child("image").setValue(downloadUrl)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        loadingBar.dismiss();
                                                        Toast.makeText(StudentActivity.this, "Children Image stored to Firebase Database Successfully...", Toast.LENGTH_SHORT).show();

                                                    } else {
                                                        String message = task.getException().getMessage();
                                                        Toast.makeText(StudentActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            });
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY && resultCode == RESULT_OK && data != null) {
            ImageUri = data.getData();
            Picasso.get().load(ImageUri).resize(2000, 0).into(imageView);
        }
    }
    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, Gallery_Pick);
    }

}
