package com.example.preschool;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ChatFragment extends Fragment {
    private RecyclerView myMessagesList;
    private DatabaseReference MessagesRef,UsersRef;
    private FirebaseAuth mAuth;
    private String online_user_id;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_chat, container, false);

        mAuth=FirebaseAuth.getInstance();
        online_user_id=mAuth.getCurrentUser().getUid();
        MessagesRef= FirebaseDatabase.getInstance().getReference().child("Messages").child(online_user_id);
        UsersRef=FirebaseDatabase.getInstance().getReference().child("Users");

        myMessagesList=view.findViewById(R.id.messages_list);
        myMessagesList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myMessagesList.setLayoutManager(linearLayoutManager);
        DisplayAllUserMessages();

        return view;
    }

    private void DisplayAllUserMessages() {
    }
}
