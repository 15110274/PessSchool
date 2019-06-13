package com.example.preschool.Event;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.example.preschool.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AddEventActivity extends AppCompatActivity {

    private Button btnOK, btnCancel;
    private EditText txtTitle, txtLocal, txtDecription;
    private Switch isAllDay;
    private TextView txtStart, txtEnd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        addControlls();
        addEvents();
    }

    private void addControlls() {
        btnCancel=findViewById(R.id.event_cancel);
        btnOK=findViewById(R.id.event_ok);
        txtTitle=findViewById(R.id.event_title);
        txtLocal=findViewById(R.id.event_location);
        txtDecription=findViewById(R.id.event_decription);
        isAllDay=findViewById(R.id.is_all_day);
        txtStart=findViewById(R.id.event_start);
        txtEnd=findViewById(R.id.event_end);
    }

    private void addEvents() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        txtStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAllDay.isChecked()){
//                    DatePicker.

                }

            }
        });

    }
}
