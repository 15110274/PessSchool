package com.example.preschool;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class EventsActivity extends AppCompatActivity {

    private DatePicker datePicker;
    private TextView showEvent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);


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


        // Khởi tạo sự kiện lắng nghe khi DatePicker thay đổi
        datePicker.init(year,month,day,new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Toast.makeText(EventsActivity.this, dayOfMonth+"-"+monthOfYear+"-"+year, Toast.LENGTH_LONG).show();
                showEvent.setText(dayOfMonth);
                Toast.makeText(EventsActivity.this,year,Toast.LENGTH_LONG).show();
            }
        });
    }
}
