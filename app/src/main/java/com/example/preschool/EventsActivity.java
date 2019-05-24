package com.example.preschool;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.renderscript.Sampler;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;

public class EventsActivity extends AppCompatActivity {

    private DatePicker datePicker;
    private TextView showEvent;

    private DatabaseReference UsersRef, EventsRef;
    private FirebaseAuth mAuth;
    private String current_user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        EventsRef= FirebaseDatabase.getInstance().getReference().child("Events");

//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setTitle(R.string.event);

        datePicker=findViewById(R.id.date_picker);
        showEvent=findViewById(R.id.show_event);

        setupDatePicker();

//        datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
//            @Override
//            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//
//            }
//        });


    }

    public void setupDatePicker(){
        Calendar calendar = Calendar.getInstance();
        // Lấy ra năm - tháng - ngày hiện tại
        int year = calendar.get(calendar.YEAR);
        final int month = calendar.get(calendar.MONTH);
        int day = calendar.get(calendar.DAY_OF_MONTH);
        showEvent.setText(String.valueOf(day));

        // Khởi tạo sự kiện lắng nghe khi DatePicker thay đổi
        datePicker.init(year,month,day,new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Toast.makeText(EventsActivity.this, dayOfMonth+"-"+monthOfYear+"-"+year, Toast.LENGTH_LONG).show();
                Toast.makeText(EventsActivity.this,year,Toast.LENGTH_LONG).show();
            }
        });
    }
}
