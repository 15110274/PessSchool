package com.example.preschool.PhotoAlbum;

import android.app.Dialog;
import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.DialogTitle;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.preschool.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ViewAllPhotoActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private DatabaseReference albumRef;
    private StorageReference albumStorageReference;
    private ValueEventListener albumEventListener;
    private Bundle bundle;
    private Album album;
    private boolean isTeacher;

    private String idClass, current_user_id, idTeacher, positionAlbum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_photo);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // get bundle
        bundle=getIntent().getExtras();
        idClass=bundle.getString("ID_CLASS");
        idTeacher=bundle.getString("ID_TEACHER");
        positionAlbum=bundle.getString("POSITION_ALBUM");

        current_user_id= FirebaseAuth.getInstance().getCurrentUser().getUid();
        if(current_user_id.equals(idTeacher))
            isTeacher=true;
        else isTeacher=false;

        albumStorageReference = FirebaseStorage.getInstance().getReference().child(idClass).child("Albums");
        albumRef = FirebaseDatabase.getInstance().getReference().child("Class").child(idClass).child("Albums").child(positionAlbum);
        albumRef.keepSynced(true);

        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gridLayoutManager);

        albumEventListener= albumRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                album= dataSnapshot.getValue(Album.class);
                getSupportActionBar().setTitle(album.getName());
                AdapterGripViewPhoto adapter = new AdapterGripViewPhoto(getApplicationContext(),album.getImageUrlList());
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Add new photo
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        albumRef.removeEventListener(albumEventListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_option_album, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.action_rename_album:
                if(isTeacher){
                    renameAlbum();
                }else Toast.makeText(ViewAllPhotoActivity.this,"Bạn không phải là giảo viên",Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_save_on_device:
//                saveOnDevice();
                break;
            case R.id.action_delete_album:
                if(isTeacher){
//                    deleteAlbum();
                }else Toast.makeText(ViewAllPhotoActivity.this,"Bạn không phải là giảo viên",Toast.LENGTH_SHORT).show();
                break;


        }


        return true;
    }

    private void renameAlbum() {
        final Dialog dialogRenameAlbum=new Dialog(ViewAllPhotoActivity.this);
        dialogRenameAlbum.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogRenameAlbum.setContentView(R.layout.dialog_rename_album);

        dialogRenameAlbum.setCancelable(true);

        final EditText txtNewName=dialogRenameAlbum.findViewById(R.id.txt_rename_album);
        Button btnRename=dialogRenameAlbum.findViewById(R.id.btn_rename_album);

        btnRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!txtNewName.getText().toString().equals("")){
                    albumRef.child("name").setValue(txtNewName.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            dialogRenameAlbum.dismiss();
                        }
                    });
                }
                else Toast.makeText(ViewAllPhotoActivity.this,"Nhập gì đi chứ man!!!",Toast.LENGTH_SHORT).show();

//                albumRef.child("name").setValue(txtNewName.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        dialogRenameAlbum.cancel();
//                    }
//                });
            }
        });

        dialogRenameAlbum.show();

    }
}
