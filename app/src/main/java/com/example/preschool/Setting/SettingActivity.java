package com.example.preschool.Setting;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.preschool.ChangeClassActivity;
import com.example.preschool.MainActivity;
import com.example.preschool.R;

public class SettingActivity extends AppCompatActivity {


    private Switch myswitch;
    private String idClass,idTeacher;
    SharedPref sharedPref;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // Get Bundle
        bundle=getIntent().getExtras();
        idClass=bundle.getString("ID_CLASS");
        idTeacher=bundle.getString("ID_TEACHER");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.action_settings);

        // Change dark mode for app
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()) {
            setTheme(R.style.DarkTheme);
        } else setTheme(R.style.AppTheme);

        myswitch = (Switch) findViewById(R.id.myswitch);
        if (sharedPref.loadNightModeState()) {
            myswitch.setChecked(true);
        }
        myswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sharedPref.setNightModeState(true);
                    recreate();
                } else {
                    sharedPref.setNightModeState(false);
                    recreate();
                }
            }
        });
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
        Intent intent=new Intent(SettingActivity.this, MainActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void restarApp() {
        Intent i = new Intent(getApplicationContext(), SettingActivity.class);
        startActivity(i);
        finish();
    }
}
