package com.example.preschool;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.preschool.Chats.MessageActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsFragment extends Fragment {
    private RecyclerView myFriendList;
    private DatabaseReference FriendsRef, UsersRef;
    private FirebaseAuth mAuth;
    private String online_user_id, idClass, idTeacher;
    private Bundle bundle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        mAuth = FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();

        // Nhận idClass từ Main
        bundle = new Bundle();
        bundle = getArguments();
        if (bundle != null) {
            idClass = bundle.getString("ID_CLASS");
            idTeacher = bundle.getString("ID_TEACHER");
        }
        FriendsRef = FirebaseDatabase.getInstance().getReference().child("Class").child(idClass).child("Friends").child(online_user_id);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        myFriendList = view.findViewById(R.id.friend_list);
        myFriendList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myFriendList.setLayoutManager(linearLayoutManager);
        DisplayAllFriends();
        return view;
    }

    // tao user state tren firebase
    public void updateUserStatus(String state) {
        String saveCurrentDate, saveCurrentTime;
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd,yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calForTime.getTime());

        Map currentStateMap = new HashMap();
        currentStateMap.put("time", saveCurrentTime);
        currentStateMap.put("date", saveCurrentDate);
        currentStateMap.put("type", state);

        UsersRef.child(online_user_id).child("userState")
                .updateChildren(currentStateMap);
    }

    @Override
    public void onStart() {
        super.onStart();
        //updateUserStatus("online");
    }

    @Override
    public void onStop() {
        super.onStop();
        //updateUserStatus("offline");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //updateUserStatus("offline");

    }

    private void DisplayAllFriends() {
        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>().
                setQuery(FriendsRef, User.class).build();
        FirebaseRecyclerAdapter<User, FriendsViewHolder> adapter = new FirebaseRecyclerAdapter<User, FriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FriendsViewHolder friendsViewHolder, int position, @NonNull User model) {
                final String usersIDs = getRef(position).getKey();
                UsersRef.child(usersIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final String userName = dataSnapshot.child("fullname").getValue().toString();
                            final String profileImage = dataSnapshot.child("profileimage").getValue().toString();
                            final String type;

                            if (dataSnapshot.hasChild("userState")) {
                                type = dataSnapshot.child("userState").child("type").getValue().toString();

                                if (type.equals("online")) {
                                    friendsViewHolder.online.setVisibility(View.VISIBLE);
                                } else {
                                    friendsViewHolder.online.setVisibility(View.GONE);
                                }
                            }

                            friendsViewHolder.setFullname(userName);
                            friendsViewHolder.setProfileImage(profileImage);
                            friendsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    CharSequence options[] = new CharSequence[]{
                                            userName + "'s Profile",
                                            "Send Message"
                                    };
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setTitle("Select Option");

                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (which == 0) {
//                                                Intent profileintent=new Intent(getActivity(),PersonProfileActivity.class);
//                                                profileintent.putExtra("visit_user_id",usersIDs);
//                                                profileintent.putExtra("idTeacher",idTeacher);
//                                                profileintent.putExtra("idClass",idClass);
//                                                startActivity(profileintent);
                                            }
                                            if (which == 1) {
                                                Intent chatintent = new Intent(getActivity(), MessageActivity.class);
                                                bundle.putString("ID_RECIVER",usersIDs);
                                                chatintent.putExtras(bundle);
                                                startActivity(chatintent);
                                            }
                                        }
                                    });
                                    builder.show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_friend_display_layout, parent, false);

                FriendsViewHolder viewHolder = new FriendsViewHolder(view);
                return viewHolder;
            }
        };
        myFriendList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {
        private TextView hey_chat;
        private ImageView user_image;
        private TextView user_name;
        private ImageView online;

        public FriendsViewHolder(View itemView) {
            super(itemView);
            hey_chat = itemView.findViewById(R.id.hey_chat);
            user_image = itemView.findViewById(R.id.all_users_profile_image);
            user_name = itemView.findViewById(R.id.all_users_profile_full_name);
            hey_chat.setText("✌");
            online = itemView.findViewById(R.id.online);

        }

        public void setProfileImage(String profileimage) {
            Picasso.get().load(profileimage).placeholder(R.drawable.ic_person_black_50dp).into(user_image);

        }

        public void setFullname(String fullname) {
            user_name.setText(fullname);
        }

    }
}
