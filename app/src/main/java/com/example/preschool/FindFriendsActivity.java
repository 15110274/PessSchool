package com.example.preschool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class FindFriendsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageButton SearchButton;
    private EditText SearchInputText;
    private RecyclerView SearchResultList;

    private DatabaseReference allUserDatabaseRef;
    //////////////////////////////////////////
    private String idClass, idTeacher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);
        allUserDatabaseRef= FirebaseDatabase.getInstance().getReference().child("Users");
        SearchResultList = findViewById(R.id.search_result_list);
        SearchResultList.setHasFixedSize(true);
        SearchResultList.setLayoutManager(new LinearLayoutManager(this));

        SearchButton = findViewById(R.id.search_people_friends_button);
        SearchInputText = findViewById(R.id.search_box_input);
        ////////////////////////////////////////////////
        idClass=getIntent().getExtras().get("idClass").toString();

        idTeacher=getIntent().getExtras().get("idTeacher").toString();

        SearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchBoxInput = SearchInputText.getText().toString();
                SearchPeopleAndFriends(searchBoxInput);
            }
        });

    }
    private void SearchPeopleAndFriends(String searchBoxInput)
    {
        Query searchPeopleAndFriendsQuery=allUserDatabaseRef.orderByChild("fullname")
                .startAt(searchBoxInput).endAt(searchBoxInput+"\uf8ff");
        FirebaseRecyclerOptions<FindFriends> options=new FirebaseRecyclerOptions.Builder<FindFriends>().
                setQuery(searchPeopleAndFriendsQuery, FindFriends.class).build();
        FirebaseRecyclerAdapter<FindFriends, FindFriendsViewHolder> adapter=new FirebaseRecyclerAdapter<FindFriends, FindFriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendsViewHolder findFriendsViewHolder, final int position, @NonNull FindFriends findFriends) {
                if(findFriends.getIdclass().equals(idClass)&&!getRef(position).getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    findFriendsViewHolder.setFullname(findFriends.getFullname());
                    findFriendsViewHolder.setAddress(findFriends.getAddress());
                    findFriendsViewHolder.setProfileImage(getApplicationContext(), findFriends.getProfileimage());

                    findFriendsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String visit_user_id=getRef(position).getKey();
                            Intent profileIntent=new Intent(FindFriendsActivity.this,PersonProfileActivity.class);
                            profileIntent.putExtra("visit_user_id",visit_user_id);
                            profileIntent.putExtra("idClass",idClass);
                            profileIntent.putExtra("idTeacher",idTeacher);
                            startActivity(profileIntent);
                        }
                    });
                }
                else{
                   findFriendsViewHolder.Layout_hide();
                }
            }
            @NonNull
            @Override
            public FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.all_friend_display_layout,parent,false);

                FindFriendsViewHolder viewHolder=new FindFriendsViewHolder(view);
                return viewHolder;
            }
        };
        SearchResultList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder {
        //View mView;
        private final LinearLayout layout;
        final LinearLayout.LayoutParams params;

        public FindFriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            //mView = itemView;
            layout =(LinearLayout)itemView.findViewById(R.id.all_user_find_friend);
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        public void setProfileImage(Context ctx, String profileimage) {
            CircleImageView myImage = layout.findViewById(R.id.all_users_profile_image);
            Picasso.get().load(profileimage).placeholder(R.drawable.ic_person_black_50dp).resize(200,0).into(myImage);

        }

        public void setFullname(String fullname) {
            TextView myName=layout.findViewById(R.id.all_users_profile_full_name);
            myName.setText(fullname);
        }
        public void setAddress(String address){
            TextView myAddress=layout.findViewById(R.id.all_users_address);
            myAddress.setText(address);
        }
        private void Layout_hide() {
            params.height = 0;
            //itemView.setLayoutParams(params); //This One.
            layout.setLayoutParams(params);   //Or This one.

        }
    }
}
