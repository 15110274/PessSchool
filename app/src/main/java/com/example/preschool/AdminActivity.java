package com.example.preschool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class AdminActivity extends AppCompatActivity {
    private Button Logout;
    private FirebaseAuth mAuth;
    private EditText UserEmail;
    private Button CreateAccountButton;
    private ProgressDialog loadingBar;
    private Spinner classNameSpinner;
    private DatabaseReference UsersRef;
    private int positionChoose=0;
    private String userIdJustAdd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Logout=findViewById(R.id.logout_button);
        mAuth=FirebaseAuth.getInstance();
        UserEmail = findViewById(R.id.setup_email);
        CreateAccountButton = (Button) findViewById(R.id.create_button);
        classNameSpinner=findViewById(R.id.classNameSniper);
        loadingBar = new ProgressDialog(this);
        UsersRef=FirebaseDatabase.getInstance().getReference().child("Users");

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
        final ArrayAdapter<String> autoComplete = new ArrayAdapter<String>(AdminActivity.this,android.R.layout.simple_spinner_item,className){
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
                CreateNewAccount();
                String email = UserEmail.getText().toString();
                String password= "123456";
                if(TextUtils.isEmpty(email))
                {
                    Toast.makeText(AdminActivity.this, "Please write your email...", Toast.LENGTH_SHORT).show();
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
                                                    Toast.makeText(AdminActivity.this, "your Account is created Successfully.", Toast.LENGTH_LONG).show();
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
                                                    Toast.makeText(AdminActivity.this, "Error Occured: " + message, Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();
                                                }
                                            }
                                        });
                                    }
                                    else
                                    {
                                        String message = task.getException().getMessage();
                                        Toast.makeText(AdminActivity.this, "Error Occured: " + message, Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }
                                }

                            });
                }
            }

        });
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminActivity.this, LoginActivity.class);
                startActivity(intent);
                mAuth.signOut();
                finish();
            }
        });
    }



    private void CreateNewAccount() {
    }
}
