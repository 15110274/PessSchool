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
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        // Get Bundle
        bundle=getIntent().getExtras();
        idClass=bundle.getString("ID_CLASS");
        idTeacher=bundle.getString("ID_TEACHER");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.help);

        expandableTextView = findViewById(R.id.eTv);
        FragHelp adapter = new FragHelp(HelpActivity.this);
        expandableTextView.setAdapter(adapter);
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
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
