package com.example.preschool.NghiPhep;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.preschool.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class DonNghiPhepActivity extends AppCompatActivity {

    /**
     * DonXinPhep này đặt trong Class->idClass
     * Sau khi tạo đơn mới, setValue lên Firebase ok
     */

    private EditText ngayNghi;
    private EditText soNgayNghi;
    private EditText lyDo;
    private Button btnGui;
    private DatabaseReference DonXinPhepRef, UserRef;
    private DatePickerDialog.OnDateSetListener mDatePickerDialog;
    private String userId;
    private String mParentName, classId;
    private String mKidName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_don_xin_phep);

        ngayNghi = findViewById(R.id.ngay_nghi);
        soNgayNghi = findViewById(R.id.so_ngay_nghi);
        lyDo = findViewById(R.id.ly_do);
        btnGui = findViewById(R.id.gui_don);

        // Lấy User ID hiện tại đang đăng nhập
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        UserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mParentName = dataSnapshot.child("fullname").getValue(String.class);
                mKidName = dataSnapshot.child("relationshipstatus").getValue(String.class);
                classId = dataSnapshot.child("idClass").getValue(String.class);

                DonXinPhepRef = FirebaseDatabase.getInstance().getReference().child("Class").child(classId).child("DonNghiPhep");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        mDatePickerDialog = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = dayOfMonth + "/" + month + "/" + year;
                ngayNghi.setText(date);
            }
        };
    }

    public void ShowDanhSach(View view) {
        Intent intent = new Intent(DonNghiPhepActivity.this, DonNghiPhepViewActivity.class);
        intent.putExtra("USER_ID", userId);
        startActivity(intent);

    }

    public void guiDonXinPhep(View view) {
        String mNgayNghi = ngayNghi.getText().toString();
        String mSoNgay = soNgayNghi.getText().toString();
        String mLyDo = lyDo.getText().toString();

        btnGui.setEnabled(false);


        if (!TextUtils.isEmpty(mNgayNghi) && !TextUtils.isEmpty(mSoNgay) && !TextUtils.isEmpty(mLyDo)) {
            String id = DonXinPhepRef.push().getKey();

            DonNghiPhep donNghiPhep = new DonNghiPhep(userId, mParentName, mKidName, mNgayNghi, mSoNgay, mLyDo);
            DonXinPhepRef.child(id).setValue(donNghiPhep, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    Toast.makeText(DonNghiPhepActivity.this, "Đã nộp đơn", Toast.LENGTH_LONG).show();
                    ngayNghi.setText("");
                    soNgayNghi.setText("");
                    lyDo.setText("");
                    btnGui.setEnabled(true);
                }
            });

        } else {
            Toast.makeText(this, "Bạn phải điền đủ nội dung", Toast.LENGTH_SHORT).show();
            btnGui.setEnabled(true);
        }

    }

    public void pickNgayNghi(View view) {
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        DatePickerDialog dialog = new DatePickerDialog(
                DonNghiPhepActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDatePickerDialog,
                year, month, day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}
