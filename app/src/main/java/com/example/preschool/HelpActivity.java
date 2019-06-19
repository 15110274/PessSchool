package com.example.preschool;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.Toast;

public class HelpActivity extends AppCompatActivity {

    private ExpandableListView expandableTextView;
    private  String idClass,idTeacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.help);

        expandableTextView = findViewById(R.id.eTv);
        FragHelp adapter = new FragHelp(HelpActivity.this);
        expandableTextView.setAdapter(adapter);
        idClass=getIntent().getExtras().get("idClass").toString();
        idTeacher=getIntent().getExtras().get("idTeacher").toString();
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        // chuyen ve trang trc ko bi mat du lieu
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        Intent intent=new Intent(HelpActivity.this, MainActivity.class);
        intent.putExtra("idClass",idClass);
        intent.putExtra("idTeacher",idTeacher);
        startActivity(intent);
    }
}
