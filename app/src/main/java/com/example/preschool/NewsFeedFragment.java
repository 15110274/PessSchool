package com.example.preschool;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.preschool.TimeLine.Posts;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Comment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import de.hdodenhof.circleimageview.CircleImageView;

public class NewsFeedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private DatabaseReference UsersRef, PostsRef, LikesRef;
    private RecyclerView postList;
    String currentUserID;
    private FirebaseAuth mAuth;
    Boolean LikeChecker = false;

    FloatingActionButton addPost;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_news_feed, container, false);

        addPost=view.findViewById(R.id.floating_add_post);

        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToPostActivity();
            }
        });


        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        LikesRef = FirebaseDatabase.getInstance().getReference().child("Likes");


        postList = view.findViewById(R.id.all_users_post_list);
        postList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);

        DisplayAllUsersPosts();
        return view;

    }

    //hiển thị bảng tin
    private void DisplayAllUsersPosts() {
        Query SortPostsInDecendingOrder=PostsRef.orderByChild("counter");
        FirebaseRecyclerOptions<Posts> options = new FirebaseRecyclerOptions.Builder<Posts>().setQuery(SortPostsInDecendingOrder, Posts.class).build();
        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Posts, PostsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PostsViewHolder postsViewHolder, int position, @NonNull Posts posts) {


                if (position == 0) {
                    postsViewHolder.WhatOnYourMind.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SendUserToPostActivity();
                        }
                    });
                } else if (position > 0) {

                    final String PostKey = getRef(position).getKey();
                    postsViewHolder.setFullname(posts.getFullname());
                    postsViewHolder.setDescription(posts.getDescription());
                    postsViewHolder.setProfileImage(posts.getProfileimage());
                    postsViewHolder.setPostImage(posts.getPostimage());

                    Calendar calFordTime = Calendar.getInstance();
                    int hours = calFordTime.get(Calendar.HOUR_OF_DAY);
                    int minutes = calFordTime.get(Calendar.MINUTE);
                    int seconds = calFordTime.get(Calendar.SECOND);

                    SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
                    String saveCurrentTime = currentTime.format(calFordTime.getTime());

                    postsViewHolder.setMinute(posts.getTime());
//                postsViewHolder.setDate(posts.getDate());
//                postsViewHolder.SetTime(posts.getTime());

                    postsViewHolder.setLikeButtonStatus(PostKey);
                    postsViewHolder.setCommentPostButtonStatus(PostKey);

                    //click post activity chua lm

                    //cmt
                    postsViewHolder.CommentPostButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent commentsIntent=new Intent(getActivity(),CommentsActivity.class);
                            commentsIntent.putExtra("PostKey",PostKey);
                            startActivity(commentsIntent);
                        }
                    });
                    //like
                    postsViewHolder.LikePostButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LikeChecker=true;
                            LikesRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(LikeChecker.equals(true)){
                                        if(dataSnapshot.child(PostKey).hasChild(currentUserID)){
                                            LikesRef.child(PostKey).child(currentUserID).removeValue();
                                            LikeChecker=false;
                                        }
                                        else{
                                            LikesRef.child(PostKey).child(currentUserID).setValue(true);
                                            LikeChecker=false;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    });


                }
            }


            @NonNull
            @Override
            public PostsViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
                View view;
                // create a new view
                switch (viewType) {
                    case 0: //This would be the header view in my Recycler
                        view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.what_on_your_mind, parent, false);

                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SendUserToPostActivity();
                            }
                        });
                        return new PostsViewHolder(view, viewType);
                    default: //This would be the normal list with the pictures of the places in the world
                        view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.post_layout, parent, false);
                        return new PostsViewHolder(view, viewType);
                }
            }

            @Override
            public int getItemViewType(int position) {
                int viewType = 1; //Default is 1
                if (position == 0) viewType = 0; //if zero, it will be a header view
                return viewType;
            }

        };
        adapter.startListening();
        postList.setAdapter(adapter);

    }

    public static class PostsViewHolder extends RecyclerView.ViewHolder {

        Button WhatOnYourMind;
        TextView LikePostButton, CommentPostButton;
        TextView DisplayNoOfLikes, DisplayNoOfComments;
        int countLikes;
        int countComments;

        String currentUserId;
        DatabaseReference LikesRef;
        DatabaseReference CommentsRef;

        public PostsViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            if (viewType == 1) {
                LikePostButton = itemView.findViewById(R.id.like_button);
                CommentPostButton = itemView.findViewById(R.id.comment_button);
                DisplayNoOfLikes = itemView.findViewById(R.id.display_no_of_likes);
                DisplayNoOfComments = itemView.findViewById(R.id.display_no_of_comments);

                LikesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
                CommentsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
                currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            } else if (viewType == 0) {
                WhatOnYourMind = itemView.findViewById(R.id.button_what_on_your_mind);
            }
        }

        public void setCommentPostButtonStatus(final String PostKey){
            CommentsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(PostKey).child("Comments").hasChild(currentUserId)){
                        countComments=(int)dataSnapshot.child(PostKey).child("Comments").getChildrenCount();
                        DisplayNoOfComments.setText((Integer.toString(countComments)+" Comments"));
                    }
                    else{
                        countComments=(int)dataSnapshot.child(PostKey).child("Comments").getChildrenCount();
                        DisplayNoOfComments.setText((Integer.toString(countComments)+" Comments"));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        //set like button status
        public void setLikeButtonStatus(final String PostKey){
            LikesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(PostKey).hasChild(currentUserId)){
                        countLikes=(int)dataSnapshot.child(PostKey).getChildrenCount();
                        LikePostButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_black_25dp,0,0,0);
                        DisplayNoOfLikes.setText((Integer.toString(countLikes)+" Likes"));
                        LikePostButton.setTextColor(Color.parseColor("#FF5722"));
                    }
                    else{
                        countLikes=(int)dataSnapshot.child(PostKey).getChildrenCount();
                        LikePostButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_border_black_25dp,0,0,0);
                        DisplayNoOfLikes.setText((Integer.toString(countLikes)+" Likes"));
                        LikePostButton.setTextColor(Color.parseColor("#959292"));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


        public void setFullname (String fullname){
            TextView username = (TextView) itemView.findViewById(R.id.post_user_name);
            username.setText(fullname);
        }

        public void setProfileImage (String profileimage){
            CircleImageView image = (CircleImageView) itemView.findViewById(R.id.post_profile_image);
            Picasso.get().load(profileimage).into(image);
        }
        public void setMinute(String minute){
            TextView PostMinute=itemView.findViewById(R.id.post_minute);
            PostMinute.setText(minute+" min ago");
        }

        public void setDescription (String description){
            TextView postDescription = (TextView) itemView.findViewById(R.id.post_description);
            postDescription.setText(description);
        }

        public void setPostImage (String postImage){
            ImageView postImages = (ImageView) itemView.findViewById(R.id.post_image);
            Picasso.get().load(postImage).resize(400, 300)
                    .centerInside().into(postImages);
        }

    }

    private void SendUserToPostActivity() {
        Intent addNewPostIntent = new Intent(getActivity(), PostActivity.class);
        startActivity(addNewPostIntent);
    }


    @Override
    public void onRefresh() {

    }
}
