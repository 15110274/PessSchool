package com.example.preschool.Chats;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.preschool.Chats.Model.Chat;
import com.example.preschool.R;
import com.example.preschool.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private boolean ischat;

    private String theLastMessage;
    private Boolean seen = false;
    private Boolean you_send = false;

    private Bundle bundle;

    public ChatListAdapter(Context mContext, List<User> mUsers, boolean ischat) {
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.ischat = ischat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item_chat, parent, false);

        return new ChatListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final User user = mUsers.get(position);
        holder.username.setText(user.getUsername());
        if (user.getProfileimage().equals("default")) {
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        } else {
            Picasso.get().load(user.getProfileimage()).into(holder.profile_image);
        }

        if (ischat) {
            lastMessage(user.getUserid(), holder.last_msg, holder.sender);
        } else {
            holder.last_msg.setVisibility(View.GONE);
        }

        if (ischat) {
            if (user.getUserState().getType().equals("online")) {
                holder.is_online.setVisibility(View.VISIBLE);
            } else {
                holder.is_online.setVisibility(View.GONE);
            }
        } else {
            holder.is_online.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                bundle=new Bundle();
                bundle.putString("VISIT_USER_ID",user.getUserid());
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public ImageView profile_image;
        private ImageView is_online;
        private TextView last_msg;
        private TextView sender;

        public ViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            is_online = itemView.findViewById(R.id.online);
            last_msg = itemView.findViewById(R.id.last_msg);
            sender = itemView.findViewById(R.id.sender);
        }
    }

    //check for last message
    private void lastMessage(final String senderid, final TextView last_msg, final TextView sender) {
        theLastMessage = "default";
//        last_msg.setTextColor();
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = firebaseUser.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (firebaseUser != null && chat != null) {
                        if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(senderid) ||
                                chat.getReceiver().equals(senderid) && chat.getSender().equals(firebaseUser.getUid())) {
                            theLastMessage = chat.getMessage();
                            if (chat.getReceiver().equals(userId)) {
                                seen = chat.isIsseen();
                            }
                            if (chat.getSender().equals(firebaseUser.getUid())) {
                                you_send = true;
                            } else you_send = false;
                        }
                    }
                }

                setTextLastMess(theLastMessage, last_msg, seen);
                // Ẩn hiện là bạn gửi hay người khác gửi
                if (you_send) {
                    sender.setVisibility(View.VISIBLE);
                } else sender.setVisibility(View.GONE);
                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setTextLastMess(String theLastMessage, TextView last_msg, Boolean seen) {

        switch (theLastMessage) {
            case "default":
                last_msg.setText("No Message");
                last_msg.setTextColor(Color.parseColor("#03A9F4"));
                break;

            default:
                if (seen) {
                    last_msg.setTextColor(Color.parseColor("#03A9F4"));
                } else last_msg.setTextColor(Color.parseColor("#03A9F4"));
                try {// Có  dấu Enter
                    if (theLastMessage.indexOf("\n") < 20)
                        last_msg.setText(theLastMessage.substring(0, theLastMessage.indexOf("\n")) + "...");
                    else last_msg.setText(theLastMessage.substring(0, 20) + "...");
                } catch (Exception e) {// Không có dấu Enter
                    if (theLastMessage.length() > 20) {
                        last_msg.setText(theLastMessage.substring(0, 20) + "...");
                    } else last_msg.setText(theLastMessage);
                }
                break;
        }
    }
}
