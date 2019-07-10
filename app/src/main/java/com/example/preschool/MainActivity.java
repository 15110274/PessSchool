package com.example.preschool;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.example.preschool.Chats.ChatsFragment;
import com.example.preschool.Event.EventsActivity;
import com.example.preschool.Menu.MenuActivity;
import com.example.preschool.Menu.ViewMenuActivity;
import com.example.preschool.NghiPhep.DonNghiPhepActivity;
import com.example.preschool.NghiPhep.DonNghiPhepFullViewActivity;
import com.example.preschool.Notification.NotificationFragment;
import com.example.preschool.Notifications.Token;
import com.example.preschool.PhotoAlbum.ViewAllAlbumActivity;
import com.example.preschool.Setting.SettingActivity;
import com.example.preschool.TimeTable.TimeTableActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

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
    private DatabaseReference UsersRef,ClassRef,UserStateRef;
    private TextView txtclassName;
    private String currentUserID;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private Bundle bundle;
    private int countExit=0;
    private Boolean isTeacher=false;

    private String idClass, idTeacher, className;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Nhận bundle
        bundle = getIntent().getExtras();
        if(bundle!=null){
            idClass= bundle.getString("ID_CLASS");
            className=bundle.getString("CLASS_NAME");
            idTeacher=bundle.getString("ID_TEACHER");
        }

        FirebaseApp.initializeApp(this);
        mAuth=FirebaseAuth.getInstance();

        currentUserID = mAuth.getCurrentUser().getUid();
        if(currentUserID.equals(idTeacher)){
            isTeacher=true;
        }
        // Update token
        updateToken(FirebaseInstanceId.getInstance().getToken());

        UserStateRef=FirebaseDatabase.getInstance().getReference("UserState");
//        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
//        ClassRef=FirebaseDatabase.getInstance().getReference().child("Class").child(idClass).child("classmane").toString();


//        updateUserStatus("online");
        AppCenter.start(getApplication(), "74bc89c2-9212-4cc3-9b55-6fc10baf76bb", Analytics.class, Crashes.class);

        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txtclassName=findViewById(R.id.class_name);
        txtclassName.setText(className);

        mViewPager = findViewById(R.id.viewPager);
        final TabLayout tabLayout = findViewById(R.id.tablayout);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),bundle);

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


    /**
     * Check xem user đã có full name hay chưa, nếu chưa gửi sang trang SetupActivity
     */

//    private void SendUserToSetupActivity() {
//        Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
//        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(setupIntent);
//        finish();
//    }


//    private void SendUserToLoginActivity() {
//        Intent loginIntent=new Intent(MainActivity.this,LoginActivity.class);
//        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        finish();
//        startActivity(loginIntent);
//
//    }

    @Override
    public void onBackPressed() {
        countExit++;
        if(countExit==1){
            Toast.makeText(MainActivity.this,"Nhấn lần nữa để thoát",Toast.LENGTH_SHORT).show();
        }
        else finish();

//        drawer = findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (drawer.isDrawerOpen(Gravity.RIGHT)) {
//                    drawer.closeDrawer(Gravity.RIGHT);
//                } else {
//                    drawer.openDrawer(Gravity.RIGHT);
//                }
//            }
//        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUserStatus("online");
//        Toast.makeText(this,"Main Start",Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        countExit=0;
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
//        updateUserStatus("offline");
//        Toast.makeText(this,"Main Stop",Toast.LENGTH_LONG).show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        final Intent intent;

        switch (id){
            case R.id.photoalbum:
                intent=new Intent(MainActivity.this, ViewAllAlbumActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.permissonform:
                if(isTeacher){
                    intent=new Intent(MainActivity.this, DonNghiPhepFullViewActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                }
                else {
                    intent=new Intent(MainActivity.this, DonNghiPhepActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                }
            case R.id.timetable:
                intent=new Intent(MainActivity.this, TimeTableActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.student:
                intent=new Intent(MainActivity.this, StudentActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.menu:
                if(isTeacher){
                    intent=new Intent(MainActivity.this, MenuActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                else {
                    intent=new Intent(MainActivity.this, ViewMenuActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
            case R.id.profile:
                intent=new Intent(MainActivity.this, MyProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.event:
                intent= new Intent(MainActivity.this, EventsActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
//            case R.id.knowledge:
//                break;
            case R.id.changeclass:
                if(!isTeacher){
                    intent= new Intent(MainActivity.this, ChangeClassActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else {
                    Toast.makeText(MainActivity.this,"Giáo viên không thể đổi lớp",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.help:
                intent=new Intent(MainActivity.this, HelpActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.setting:
                intent=new Intent(MainActivity.this, SettingActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.logout:
                intent=new Intent(MainActivity.this, StartActivity.class);
                mAuth.signOut();
                startActivity(intent);
                break;
        }
        return true;
    }

    // Update token to send Notifications
    private void updateToken(String token) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(currentUserID).setValue(token1);
    }
    public void updateUserStatus(String state){
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

        UserStateRef.child(currentUserID)
                .setValue(currentStateMap);
    }
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        ////////////////////////////////////////////
        Bundle bundle;
        public SectionsPagerAdapter(FragmentManager fm,Bundle bundle) {
            super(fm);
            this.bundle=bundle;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment=null;
            switch (position){
                case 0:
                    fragment=new FriendsFragment();
                    break;
                case 1:
                    fragment=new NewsFeedFragment();
                    break;
                case 2:
                    fragment=new ChatsFragment();
                    break;
                case 3:
                    fragment=new NotificationFragment();
                    break;
            }
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }
    }

}
