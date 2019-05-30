package com.example.preschool;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatFragment extends Fragment {
    private RecyclerView myMessagesList;
    private DatabaseReference MessagesRef, UsersRef;
    private FirebaseAuth mAuth;
    private String online_user_id;

    @Nullable

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        mAuth = FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();

        MessagesRef=FirebaseDatabase.getInstance().getReference().child("Messages").child(online_user_id);

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        myMessagesList = view.findViewById(R.id.messages_list);
        myMessagesList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myMessagesList.setLayoutManager(linearLayoutManager);
        DisplayAllUserMessages();

        return view;
    }

    private void DisplayAllUserMessages() {
        FirebaseRecyclerOptions<Messages> optionsMessages=new FirebaseRecyclerOptions.Builder<Messages>().
                setQuery(MessagesRef, Messages.class).build();
        FirebaseRecyclerAdapter<Messages, MessagesViewHolder> adapterMessages=new FirebaseRecyclerAdapter<Messages, MessagesViewHolder>(optionsMessages) {
            @Override
            protected void onBindViewHolder(@NonNull final MessagesViewHolder messagesViewHolder, final int position, @NonNull Messages messages) {
                final String chatWithIDs=getRef(position).getKey();
                final Intent chatIntent=new Intent(getActivity(),ChatActivity.class);
                UsersRef.child(chatWithIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){

                            final String userName=dataSnapshot.child("fullname").getValue().toString();
                            final String profileImage=dataSnapshot.child("profileimage").getValue().toString();
                            messagesViewHolder.setFullname(userName);
                            messagesViewHolder.setProfileImage(profileImage);
                            //gửi đến chatActivity
                            chatIntent.putExtra("userName",userName);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                MessagesRef=FirebaseDatabase.getInstance().getReference().child("Messages").child(online_user_id).child(chatWithIDs);
                Query query=MessagesRef.orderByKey().limitToLast(1);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()) {
                            for (DataSnapshot child: dataSnapshot.getChildren()) {

                                final String contentMessage = child.child("message").getValue().toString();
                                final String timeMessage = child.child("time").getValue().toString();
                                final String senderMessage = child.child("from").getValue().toString();
                                if(!senderMessage.equals(online_user_id)){
                                    //ẩn chữ bạn đi
                                    messagesViewHolder.senderIsMe.setVisibility(View.GONE);
                                }
                                else{

                                    messagesViewHolder.senderIsMe.setVisibility(View.VISIBLE);
                                }
                                messagesViewHolder.setMessages(contentMessage);
                                messagesViewHolder.setTime(timeMessage);

                            }
                        }
                        else{
                            Toast.makeText(getActivity(),"Error",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                messagesViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //gửi đến chat activity
                        String visit_user_id=getRef(position).getKey();
                        chatIntent.putExtra("visit_user_id",visit_user_id);
                        startActivity(chatIntent);

                        //đổi màu là chưa đọc
//                        messagesViewHolder.myMessage.setTextColor(Color.BLACK);
//                        messagesViewHolder.myName.setTextSize(20);
//                        messagesViewHolder.myMessage.setTypeface(null, Typeface.BOLD);
//                        messagesViewHolder.itemView.setBackgroundColor(getResources().getColor(R.color.colorNotSeenMes));
                        //đổi màu là đọc rồi
//                        messagesViewHolder.myMessage.setTextColor(getResources().getColor(R.color.colorMes));
//                        messagesViewHolder.myName.setTextSize(16);
//                        messagesViewHolder.myMessage.setTypeface(null, Typeface.NORMAL);
//                        messagesViewHolder.itemView.setBackgroundColor(getResources().getColor(R.color.background));

                    }
                });
            }

            @NonNull
            @Override
            public MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View messagesView= LayoutInflater.from(parent.getContext()).inflate(R.layout.all_messages_display_layout,parent,false);

                MessagesViewHolder viewHolder=new MessagesViewHolder(messagesView);
                return viewHolder;
            }
        };
        /////////////////////////////////////////////////////////////////////////////////////////
        myMessagesList.setAdapter(adapterMessages);
        adapterMessages.startListening();
    }

    public static class MessagesViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView senderIsMe;
        TextView myMessage;
        TextView myName;

        public MessagesViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            senderIsMe=mView.findViewById(R.id.all_messages_me);
            myMessage = mView.findViewById(R.id.all_messages_final_chat);
            myName = mView.findViewById(R.id.all_messages_full_name);
        }


        public void setProfileImage(String profileimage) {
            CircleImageView myImage = mView.findViewById(R.id.all_messages_profile_image);
            Picasso.get().load(profileimage).placeholder(R.drawable.ic_person_black_50dp).resize(200,0).into(myImage);

        }

        public void setFullname(String fullname) {
//            TextView myName = mView.findViewById(R.id.all_messages_full_name);
            myName.setText(fullname);
        }
        public void setMessages(String messages) {
//            TextView myMessage = mView.findViewById(R.id.all_messages_final_chat);
            myMessage.setText(messages);
        }

        public void setTime(String time) {
            TextView myTime = mView.findViewById(R.id.all_messages_time);
            myTime.setText(time);
        }
    }

}
