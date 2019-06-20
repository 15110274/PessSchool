package com.example.preschool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.microsoft.appcenter.ingestion.Ingestion;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.MissingFormatArgumentException;

public class PersonProfileActivity extends AppCompatActivity {

    private TextView userName, userProfName,userClass,userParentof,userBirthDay;
    private CircleImageView userProfileImage;

    private Button SendFriendReqButton,DeclineFriendReqButton;

    private DatabaseReference FriendRequestRef,UsersRef,FriendsRef;
    private FirebaseAuth mAuth;

    private String senderUserId,receiverUserId,CURRENT_STATE,saveCurrentDate,idClass,idTeacher, className;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);

        mAuth=FirebaseAuth.getInstance();
        senderUserId=mAuth.getCurrentUser().getUid();
        receiverUserId=getIntent().getExtras().get("VISIT_USER_ID").toString();
        Bundle bundle=getIntent().getExtras();
        idClass=bundle.getString("ID_CLASS");
        idTeacher=bundle.getString("ID_TEACHER");
        className=bundle.getString("CLASS_NAME");

        UsersRef= FirebaseDatabase.getInstance().getReference().child("Users");
        FriendRequestRef= FirebaseDatabase.getInstance().getReference().child("Class").child(idClass).child("FriendRequests");
        FriendsRef= FirebaseDatabase.getInstance().getReference().child("Class").child(idClass).child("Friends");

        IntializeFields();
        UsersRef.child(receiverUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    String myUserName = dataSnapshot.child("username").getValue().toString();
                    String myProfileName = dataSnapshot.child("fullname").getValue().toString();
                    String myBirthday = dataSnapshot.child("birthday").getValue().toString();
                    String myClass = dataSnapshot.child("classname").getValue().toString();
                    String myParentOf = dataSnapshot.child("parentof").getValue().toString();

                    Picasso.get().load(myProfileImage).placeholder(R.drawable.ic_person_black_50dp).into(userProfileImage);

                    userName.setText(myUserName);
                    userProfName.setText(myProfileName);
                    userBirthDay.setText("Sinh nhật: " + myBirthday);
                    userClass.setText("Lớp: " + myClass);
                    userParentof.setText("Phụ huynh của bé: " + myParentOf);
                    MaintananceofButton();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // xác nhận để enable button lời mời kết bạn
        DeclineFriendReqButton.setVisibility(View.INVISIBLE);
        DeclineFriendReqButton.setEnabled(false);

        if(!senderUserId.equals(receiverUserId)){
            SendFriendReqButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SendFriendReqButton.setEnabled(false);
                    if(CURRENT_STATE.equals("not_friends")){
                        SendFriendRequestToaPerson();
                    }
                    if(CURRENT_STATE.equals("request_sent")){
                        CancleFriendRequest();
                    }
                    if(CURRENT_STATE.equals("request_received")){
                        AcceptFriendRequest();
                    }
                    if(CURRENT_STATE.equals("friends")){
                        UnFriendAnExistingFriends();
                    }
                }
            });
        }
        else{
            //nếu là chính người đó thì hiển thị button sửa thông tin
            DeclineFriendReqButton.setVisibility(View.INVISIBLE);
            //SendFriendReqButton.setVisibility(View.INVISIBLE);
            SendFriendReqButton.setText("Sửa thông tin");
            SendFriendReqButton.setEnabled(true);
            SendFriendReqButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SendUserToEditProfileActivity();
                }
            });
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // chuyen ve trang trc ko bi mat du lieu
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        Intent intent=new Intent(PersonProfileActivity.this, MainActivity.class);
        intent.putExtra("ID_CLASS",idClass);
        intent.putExtra("ID_TEACHER",idTeacher);
        intent.putExtra("CLASS_NAME",className);
        startActivity(intent);
    }
    private void SendUserToEditProfileActivity() {
        Intent intent=new Intent(PersonProfileActivity.this, EditProfileActivity.class);
        intent.putExtra("idClass",idClass);
        intent.putExtra("idTeacher",idTeacher);
        intent.putExtra("CLASS_NAME",className);
        startActivity(intent);
    }

    private void UnFriendAnExistingFriends() {
        FriendsRef.child(senderUserId).child(receiverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FriendsRef.child(receiverUserId).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                SendFriendReqButton.setEnabled((true));
                                                CURRENT_STATE="not_friends";

                                                SendFriendReqButton.setText("Send Friend Request");

                                                DeclineFriendReqButton.setVisibility(View.INVISIBLE);
                                                DeclineFriendReqButton.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
    private void AcceptFriendRequest() {
        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calFordDate.getTime());

        FriendsRef.child(senderUserId).child(receiverUserId).child("date").setValue(saveCurrentDate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FriendsRef.child(receiverUserId).child(senderUserId).child("date").setValue(saveCurrentDate)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                FriendRequestRef.child(senderUserId).child(receiverUserId)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    FriendRequestRef.child(receiverUserId).child(senderUserId)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if(task.isSuccessful()){
                                                                                        SendFriendReqButton.setEnabled((true));
                                                                                        CURRENT_STATE="friends";

                                                                                        SendFriendReqButton.setText("Unfriend this Person");

                                                                                        DeclineFriendReqButton.setVisibility(View.INVISIBLE);
                                                                                        DeclineFriendReqButton.setEnabled(false);
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });

    }
    private void CancleFriendRequest() {
        FriendRequestRef.child(senderUserId).child(receiverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FriendRequestRef.child(receiverUserId).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                SendFriendReqButton.setEnabled((true));
                                                CURRENT_STATE="not_friends";

                                                SendFriendReqButton.setText("Send Friend Request");

                                                DeclineFriendReqButton.setVisibility(View.INVISIBLE);
                                                DeclineFriendReqButton.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
    private void SendFriendRequestToaPerson() {
        FriendRequestRef.child(senderUserId).child(receiverUserId)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FriendRequestRef.child(receiverUserId).child(senderUserId)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                SendFriendReqButton.setEnabled((true));
                                                CURRENT_STATE="request_sent";

                                                SendFriendReqButton.setText("Cancle friend Request");

                                                DeclineFriendReqButton.setVisibility(View.INVISIBLE);
                                                DeclineFriendReqButton.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
    private void MaintananceofButton() {
        FriendRequestRef.child(senderUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(receiverUserId))
                        {

                            String request_type=dataSnapshot.child(receiverUserId)
                                    .child("request_type").getValue().toString();

                            if(request_type.equals("sent"))
                            {
                                CURRENT_STATE="request_sent";
                                SendFriendReqButton.setText("Cancel Friend Request");
                                DeclineFriendReqButton.setVisibility(View.INVISIBLE);
                                DeclineFriendReqButton.setEnabled(false);
                            }
                            else if(request_type.equals("received")){
                                CURRENT_STATE="request_received";
                                SendFriendReqButton.setText("Accept Friend Request");
                                DeclineFriendReqButton.setVisibility(View.VISIBLE);
                                DeclineFriendReqButton.setEnabled(true);


                                DeclineFriendReqButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        CancleFriendRequest();
                                    }
                                });


                            }
                        }
                        else{
                            FriendsRef.child(senderUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(receiverUserId)){
                                        CURRENT_STATE="friends";
                                        SendFriendReqButton.setText("Unfriend this Person");
                                        DeclineFriendReqButton.setVisibility(View.INVISIBLE);
                                        DeclineFriendReqButton.setEnabled(false);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
    private void IntializeFields() {
        userProfileImage=findViewById(R.id.person_profile_pic);
        userName=findViewById(R.id.person_username);
        userProfName=findViewById(R.id.person_full_name);
        userParentof=findViewById(R.id.relationship_with_children);
        userBirthDay=findViewById(R.id.person_birthday);
        userClass=findViewById(R.id.person_class);


        SendFriendReqButton=findViewById(R.id.person_send_friend_request_btn);
        DeclineFriendReqButton=findViewById(R.id.person_decline_friend_request_btn);

        CURRENT_STATE="not_friends";
    }
}
