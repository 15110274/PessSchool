package com.example.preschool;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class EditClassActivity extends AppCompatActivity {
    private EditText className;
    private Button UpdateClassButton;
    private ProgressDialog loadingBar;
    private DatabaseReference ClassRef,UserRef;
    private FirebaseAuth mAuth;
    private Spinner teacherSpinner;
    private final ArrayList<String> teacherid=new ArrayList<>();
    private final ArrayList<String> teachername=new ArrayList<>();
    private int teacherChoose=0;
    private int teacherOld=0;
    private ValueEventListener UsersEventListener, ClassEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_class);
        mAuth=FirebaseAuth.getInstance();
        ClassRef= FirebaseDatabase.getInstance().getReference().child("Class").child(getIntent().getStringExtra("CLASS_ID"));
        UserRef= FirebaseDatabase.getInstance().getReference().child("Users");
        className=findViewById(R.id.edit_classname);
        UpdateClassButton=findViewById(R.id.update_class_button);
        loadingBar=new ProgressDialog(this);
        teacherSpinner=findViewById(R.id.teacherSpinner);
        teachername.add("Choose Teacher...");
        teacherid.add("");
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
//        UsersEventListener=
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot suggestionSnapshot : dataSnapshot.getChildren()){
                    if(suggestionSnapshot.hasChild("role"))
                    {
                        if(suggestionSnapshot.child("role").getValue().toString().equals("Teacher")){
                            String name="";
                            if(suggestionSnapshot.hasChild("fullname")){
                                name = suggestionSnapshot.child("fullname").getValue(String.class);
                            }
                            String email=suggestionSnapshot.child("email").getValue(String.class);
                            String id = suggestionSnapshot.getKey();
                            teacherid.add(id);
                            teachername.add(name+"("+email+")");
                        }
                    }

                }
                ClassRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("classname")){
                            String name = dataSnapshot.child("classname").getValue().toString();
                            className.setText(name);
                        }
                        if(dataSnapshot.hasChild("teacher")){
                            String teacher=dataSnapshot.child("teacher").getValue().toString();
                            int vitri=0;
                            if(teacher=="")
                            {
                                vitri=0;
                            }
                            else{
                                for(int i=1;i<teacherid.size();i++){
                                    if(teacherid.get(i).equals(teacher)){
                                        vitri=i;
                                    }
                                }
                            }
                            teacherSpinner.setSelection(vitri);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                final int[] temp = {0};
                teacherSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        teacherChoose=position;
                        if(temp[0] ==0){
                            teacherOld=position;
                        }
                        temp[0]++;
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        final ArrayAdapter<String> autoComplete = new ArrayAdapter<String>(EditClassActivity.this,android.R.layout.simple_spinner_item,teachername){
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
            public View getView(int position, View convertView, ViewGroup parent) {
                // Cast the spinner collapsed item (non-popup item) as a text view
                TextView tv = (TextView) super.getView(position, convertView, parent);

                // Set the text color of spinner item
                tv.setTextColor(Color.GRAY);

                // Return the view
                return tv;
            }

        };
        autoComplete.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        teacherSpinner.setAdapter(autoComplete);
        UpdateClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateClassInfo();
            }
        });
    }
    private void ValidateClassInfo() {
        final String name=className.getText().toString();
        HashMap classMap=new HashMap();
        classMap.put("classname",name);
        if(teacherChoose!=0&&teacherChoose!=teacherOld){
            classMap.put("teacher",teacherid.get(teacherChoose));
        }

        ClassRef.updateChildren(classMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()) {
                    if (teacherChoose != 0&&teacherChoose!=teacherOld) {
                        HashMap userMap = new HashMap();
                        userMap.put("idclass", getIntent().getStringExtra("CLASS_ID"));
                        userMap.put("classname", name);
                        UserRef.child(teacherid.get(teacherChoose)).updateChildren(userMap);
                        HashMap userMap2 = new HashMap();
                        userMap2.put("idclass", "");
                        userMap2.put("classname", "");
                        UserRef.child(teacherid.get(teacherOld)).updateChildren(userMap2);
                    }
                    Toast.makeText(EditClassActivity.this,"Updated Successful",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(EditClassActivity.this,"Error",Toast.LENGTH_SHORT).show();

                }
            }
        });
        Intent intent=new Intent(EditClassActivity.this, ViewClassActivity.class);
        intent.putExtra("CLASS_ID",getIntent().getStringExtra("CLASS_ID"));
        intent.putExtra("test",teacherid);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(ClassEventListener!=null){
            ClassRef.removeEventListener(ClassEventListener);
        }
        if(UsersEventListener!=null){
            UserRef.removeEventListener(UsersEventListener);
        }
        finish();
    }
}
