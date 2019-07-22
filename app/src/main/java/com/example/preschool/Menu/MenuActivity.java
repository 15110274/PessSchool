package com.example.preschool.Menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.preschool.R;
import com.github.jhonnyx2012.horizontalpicker.DatePickerListener;
import com.github.jhonnyx2012.horizontalpicker.HorizontalPicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.joda.time.DateTime;


public class MenuActivity extends AppCompatActivity implements DatePickerListener {

    private TextView txtViewDay;
    private EditText edtSang;
    private EditText edtTrua;
    private EditText edtXe;
    private Button btnSave;
    private DatabaseReference MenuRef;
    private String daysele;
    private Button btnEdit;
    private String idClass;
    private Bundle bundle;
    private Button btnDelete;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Get Bundle
        bundle = getIntent().getExtras();
        if (bundle != null) {
            idClass = bundle.getString("ID_CLASS");
//            idTeacher=bundle.getString("ID_TEACHER");
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //Khai báo đối tượng
        txtViewDay = findViewById(R.id.showdate);
        edtSang = findViewById(R.id.isang);
        edtTrua = findViewById(R.id.itrua);
        edtXe = findViewById(R.id.ixe);
        btnSave = findViewById(R.id.save);
        btnDelete = findViewById(R.id.delete);
        btnEdit = findViewById(R.id.edit);
        MenuRef = FirebaseDatabase.getInstance().getReference("Class").child(idClass).child("Menu");
        //Calendar
        HorizontalPicker picker = (HorizontalPicker) findViewById(R.id.datePicker);
        picker.setListener(this)
                .setDays(365)
                .setOffset(3)
                .setDateSelectedColor(Color.DKGRAY)
                .setDateSelectedTextColor(Color.WHITE)
                .setMonthAndYearTextColor(Color.DKGRAY)
                .setTodayButtonTextColor(getResources().getColor(R.color.colorPrimary))
                .setTodayDateTextColor(getResources().getColor(R.color.colorPrimary))
                .setTodayDateBackgroundColor(Color.GRAY)
                .setUnselectedDayTextColor(Color.DKGRAY)
                .setDayOfWeekTextColor(Color.DKGRAY)
                .setUnselectedDayTextColor(getResources().getColor(R.color.colorPrimary))
                .showTodayButton(false)
                .init();
        picker.setBackgroundColor(getResources().getColor(R.color.bgr));
        picker.setDate(new DateTime().plusDays(0));
        //btnSave
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateMenu();

            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtSang.setEnabled(true);
                edtTrua.setEnabled(true);
                edtXe.setEnabled(true);

                btnSave.setVisibility(View.VISIBLE);
                btnEdit.setVisibility(View.INVISIBLE);
                btnDelete.setVisibility(View.INVISIBLE);
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteMenu();
            }
        });
    }

    //DateSelected
    @Override
    public void onDateSelected(DateTime dateSelected) {
        if (dateSelected != null) {
            daysele = dateSelected.toString("ddMMyyyy");
            String x = "Bạn hãy tạo thực đơn cho ngày " + dateSelected.toString("dd/MM/yyyy");
            txtViewDay.setText(x);
            try {
                MenuRef.child(daysele).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Menu menu = dataSnapshot.getValue(Menu.class);
                        if (menu != null) {
                            btnEdit.setVisibility(View.VISIBLE);
                            btnSave.setVisibility(View.INVISIBLE);
                            btnDelete.setVisibility(View.VISIBLE);
                            edtSang.setText(menu.getiSang());
                            edtTrua.setText(menu.getiTrua());
                            edtXe.setText(menu.getiChieu());
                            edtSang.setEnabled(false);
                            edtTrua.setEnabled(false);
                            edtXe.setEnabled(false);
                        } else {
                            btnSave.setVisibility(View.VISIBLE);
                            btnEdit.setVisibility(View.INVISIBLE);
                            btnDelete.setVisibility(View.INVISIBLE);
                            edtSang.setText("");
                            edtTrua.setText("");
                            edtXe.setText("");
                            edtSang.setEnabled(true);
                            edtTrua.setEnabled(true);
                            edtXe.setEnabled(true);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } catch (Exception e) {
                edtSang.setText("");
                edtTrua.setText("");
                edtXe.setText("");
            }

        }
    }

    //Delete Menu to firebase
    public void DeleteMenu() {

        final AlertDialog.Builder dialogDeleteMenu = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        dialogDeleteMenu.setMessage("Bạn có chắc muốn thực đơn này?");
        dialogDeleteMenu.setCancelable(false);
        dialogDeleteMenu.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                // Delete Album on CloudStorage
                MenuRef.child(daysele).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        dialogInterface.dismiss();
                        btnSave.setVisibility(View.VISIBLE);
                        btnEdit.setVisibility(View.INVISIBLE);
                        edtSang.setEnabled(true);
                        edtTrua.setEnabled(true);
                        edtXe.setEnabled(true);
                    }
                });

            }
        }).setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialogDeleteMenu.show();


    }

    // Create Menu to firebase
    public void CreateMenu() {
        String iSang = edtSang.getText().toString();
        String iTrua = edtTrua.getText().toString();
        String iXe = edtXe.getText().toString();
        if (iSang.equals("") && iTrua.equals("") && iXe.equals("")) {
            Toast.makeText(this, "Bạn cần nhập ít nhất một buổi", Toast.LENGTH_SHORT).show();
        } else {
            if (iSang.equals("")) {
                iSang = "Không có món";
            }
            if (iTrua.equals("")) {
                iTrua = "Không có món";
            }
            if (iXe.equals("")) {
                iXe = "Không có món";
            }
            Menu menu = new Menu(iSang, iTrua, iXe);
            MenuRef.child(daysele).setValue(menu).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    btnDelete.setVisibility(View.VISIBLE);
                    btnEdit.setVisibility(View.VISIBLE);
                    btnSave.setVisibility(View.INVISIBLE);
                    edtSang.setEnabled(false);
                    edtTrua.setEnabled(false);
                    edtXe.setEnabled(false);
                }
            });
            Toast.makeText(this, "Đã lưu", Toast.LENGTH_SHORT).show();
        }
    }

}
