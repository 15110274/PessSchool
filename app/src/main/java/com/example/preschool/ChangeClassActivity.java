package com.example.preschool;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

public class ChangeClassActivity extends AppCompatActivity {
    private String idClass,idTeacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_class);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.change_join_class);
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
        Intent intent=new Intent(ChangeClassActivity.this, MainActivity.class);
        intent.putExtra("idClass",idClass);
        intent.putExtra("idTeacher",idTeacher);
        startActivity(intent);
    }
}
