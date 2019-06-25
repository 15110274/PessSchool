//package com.example.preschool.Event;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.ActionBar;
//import androidx.appcompat.app.AppCompatActivity;
//import sun.bob.mcalendarview.MCalendarView;
//import sun.bob.mcalendarview.vo.DateData;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.renderscript.Sampler;
//import android.view.View;
//import android.widget.CalendarView;
//import android.widget.DatePicker;
//import android.widget.QuickContactBadge;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.example.preschool.R;
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.storage.StorageReference;
//
//import java.util.ArrayList;
//import java.util.Calendar;
//
//public class EventsActivity extends AppCompatActivity {
//
//    private CalendarView calendarView;
//    private TextView showEvent;
//    private FloatingActionButton addEvent;
//
//    private DatabaseReference UsersRef, EventsRef;
//    private FirebaseAuth mAuth;
//    private String current_user_id;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_events);
//
//        mAuth = FirebaseAuth.getInstance();
//        current_user_id = mAuth.getCurrentUser().getUid();
//        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
//        EventsRef = FirebaseDatabase.getInstance().getReference().child("Events");
//
//        showEvent = findViewById(R.id.show_event);
//        addEvent=findViewById(R.id.add_event);
//        calendarView = findViewById(R.id.calendarView);
//        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
//            @Override
//            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
//                Toast.makeText(EventsActivity.this, dayOfMonth + "-" + month + "-" + year, Toast.LENGTH_LONG).show();
//            }
//        });
//
//        addEvent.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent=new Intent(EventsActivity.this,AddEventActivity.class);
//                startActivity(intent);
//            }
//        });
//
////        ArrayList<DateData> dates=new ArrayList<>();
////        dates.add(new DateData(2019,05,26));
////        dates.add(new DateData(2018,06,2));
////
////        for(int i=0;i<dates.size();i++) {
////            mCalendarView.markDate(dates.get(i).getYear(),dates.get(i).getMonth(),dates.get(i).getDay());//mark multiple dates with this code.
////        }
////        mCalendarView.markDate(2019,05,30);//mark multiple dates with this code.
////        addEvent.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                Toast.makeText(EventsActivity.this,"test",Toast.LENGTH_LONG).show();
////            }
////        });
//    }
//}
package com.example.preschool.Event;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import sun.bob.mcalendarview.MCalendarView;
import sun.bob.mcalendarview.vo.DateData;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.text.TextUtils;
import android.view.View;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import android.widget.Toast;

import com.example.preschool.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;

public class EventsActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private TextView showEvent;
    private FloatingActionButton addEvent;
    private DatabaseReference UsersRef, EventsRef;
    private FirebaseAuth mAuth;
    private String current_user_id;
    private String idClass, idTeacher;

    private Bundle bundle;
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Events");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // and this
//                startActivity(new Intent(MessageActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }
        });
        // Get Bundle
        bundle=getIntent().getExtras();
        if(bundle!=null){
            idClass=bundle.getString("ID_CLASS");
            idTeacher=bundle.getString("ID_TEACHER");
        }
        // Set name ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.event);
        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();

        EventsRef = FirebaseDatabase.getInstance().getReference("Class").child(idClass).child("Events");

        showEvent = findViewById(R.id.show_event);
        addEvent=findViewById(R.id.add_event);
        addEvent.setVisibility(View.GONE);
        if(current_user_id.equals(idTeacher)){
            addEvent.setVisibility(View.VISIBLE);
        }
        calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String x;
                if((month+1)<10){
                    x= dayOfMonth + "-0" + (month +1) + "-" + year; }
                else {
                    x= dayOfMonth + "-" + (month +1) + "-" + year;
                }
                try {
                    EventsRef.child(x).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Event event = dataSnapshot.getValue(Event.class);
                            if(event!=null){
                                if(event.getTimeStart().equals("Cả ngày")==false){
                                    showEvent.setText("Sự kiện:"+event.getNameEvent() +"\n" +"Thời gian từ: " +event.getTimeStart()+
                                            " đên "+ event.getTimeEnd()+"\n"+"Mô tả: "+event.getDescription()+"\n"+"Địa điểm: " +event.getPosition());
                                }else {
                                    showEvent.setText("Sự kiện:"+event.getNameEvent() +"\n" +"Thời gian: " +event.getTimeStart()+
                                            "\n"+"Mô tả: "+event.getDescription()+"\n"+"Địa điểm: " +event.getPosition());
                                }

                            }
                            else {
                                showEvent.setText("Không có sự kiện");
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } catch (Exception e) {
                    showEvent.setText("hehe");
                }


            }
        });

        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(EventsActivity.this,AddEventActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
