package com.example.preschool;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import com.example.preschool.NghiPhep.DonNghiPhepActivity;
import com.example.preschool.Notification.NotificationFragment;
import com.example.preschool.Notification.TestNotifyActivity;
import com.example.preschool.PhotoAlbum.PhotoAlbumActivity;
import com.example.preschool.Setting.SettingActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef,ClassRef;
    private TextView findButton;
    String currentUserID;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private User user;
    private Class userClass;
    private Intent NewFeedIntent;
    private String idTeacher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseApp.initializeApp(this);
        mAuth=FirebaseAuth.getInstance();

        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ClassRef=FirebaseDatabase.getInstance().getReference().child("Class");


        updateUserStatus("online");
        AppCenter.start(getApplication(), "74bc89c2-9212-4cc3-9b55-6fc10baf76bb", Analytics.class, Crashes.class);

        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findButton=findViewById(R.id.find_button);
        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToFindFriendActivity();
            }
        });

        mViewPager = findViewById(R.id.viewPager);
        final TabLayout tabLayout = findViewById(R.id.tablayout);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);



        // Set icon for tabItem
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_tab_contact);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_tab_timeline);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_tab_message);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_tab_notification);

        /**
         * đổi màu icon tab_selected
         *
         */
        tabLayout.setOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(Color.parseColor("#a8a8a8"), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(Gravity.RIGHT)) {
                    drawer.closeDrawer(Gravity.RIGHT);
                } else {
                    drawer.openDrawer(Gravity.RIGHT);
                }
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }
    private void guiIdTeacher(final Fragment fragment){
        UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String idclass=dataSnapshot.child("idclass").getValue().toString();
                ClassRef.child(idclass).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String idTeacher=dataSnapshot.child("teacher").getValue().toString();
                        Bundle bundle = new Bundle();
                        bundle.putString("teacher",idTeacher);
                        fragment.setArguments(bundle);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void SendUserToFindFriendActivity() {
        Intent friendsIntent=new Intent(MainActivity.this,FindFriendsActivity.class);
        startActivity(friendsIntent);

    }

    //
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if(currentUser==null){
            SendUserToLoginActivity();
        }
        else
        {
            CheckUserExistence();
        }
    }


    /**
     * Check xem user đã có full name hay chưa, nếu chưa gửi sang trang SetupActivity
     */
    private void CheckUserExistence() {

        final String current_user_id = mAuth.getCurrentUser().getUid();

        UsersRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild("fullname"))
                {
                    SendUserToSetupActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void SendUserToSetupActivity() {
        Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }


    private void SendUserToLoginActivity() {
        Intent loginIntent=new Intent(MainActivity.this,LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();

    }

    @Override
    public void onBackPressed() {
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(Gravity.RIGHT)) {
                    drawer.closeDrawer(Gravity.RIGHT);
                } else {
                    drawer.openDrawer(Gravity.RIGHT);
                }
            }
        });
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;

        switch (id){
            case R.id.photoalbum:
                intent=new Intent(MainActivity.this, PhotoAlbumActivity.class);
                startActivity(intent);
                break;
            case R.id.parent:
                break;
            case R.id.curriculum:
                //Nếu là phụ huynh thì chuyển sang DonNghiPhepActivity
                //Nếu là giáo viên thì chuyển sang trang DonNghiPhepFullViewActivity
                intent=new Intent(MainActivity.this, DonNghiPhepActivity.class);
                startActivity(intent);
                break;
//            case R.id.menu:
//                break;
            case R.id.camera:
                intent=new Intent(MainActivity.this, CameraActivity.class);
                startActivity(intent);
                break;
            case R.id.homework:
                intent=new Intent(MainActivity.this, TestNotifyActivity.class);
                startActivity(intent);
                break;
            case R.id.profile:
                intent=new Intent(MainActivity.this, PersonProfileActivity.class);
                intent.putExtra("visit_user_id",currentUserID);
                startActivity(intent);
                break;
            case R.id.event:
                intent= new Intent(MainActivity.this, EventsActivity.class);
                startActivity(intent);
                break;
//            case R.id.knowledge:
//                break;
            case R.id.changeclass:
                intent= new Intent(MainActivity.this, ChangeClassActivity.class);
                startActivity(intent);
                break;
            case R.id.help:
                intent=new Intent(MainActivity.this, HelpActivity.class);
                startActivity(intent);
                break;
            case R.id.setting:
                intent=new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.logout:
                updateUserStatus("offline");

                intent=new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                mAuth.signOut();
                finish();
                break;

        }


        return true;
    }
    public void updateUserStatus(String state){
        String currentUserID=mAuth.getCurrentUser().getUid();
        String saveCurrentDate,saveCurrentTime;
        Calendar calForDate=Calendar.getInstance();
        SimpleDateFormat currentDate= new SimpleDateFormat("MM dd,yyyy");
        saveCurrentDate=currentDate.format(calForDate.getTime());

        Calendar calForTime=Calendar.getInstance();
        SimpleDateFormat currentTime=new SimpleDateFormat("hh:mm a");
        saveCurrentTime=currentTime.format(calForTime.getTime());

        Map currentStateMap=new HashMap();
        currentStateMap.put("time",saveCurrentTime);
        currentStateMap.put("date",saveCurrentDate);
        currentStateMap.put("type",state);

        UsersRef.child(currentUserID).child("userState")
                .updateChildren(currentStateMap);
    }
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment=null;
            switch (position){
                case 0:
                    fragment=new FriendsFragment();
                    guiIdTeacher(fragment);
                    break;
                case 1:
                    fragment=new NewsFeedFragment();
                    guiIdTeacher(fragment);
                    break;
                case 2:
                    fragment=new ChatFragment();
                    guiIdTeacher(fragment);
                    break;
                case 3:
                    fragment=new NotificationFragment();
                    guiIdTeacher(fragment);
                    break;
            }
            //guiIdTeacher(fragment);
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }
    }
}
