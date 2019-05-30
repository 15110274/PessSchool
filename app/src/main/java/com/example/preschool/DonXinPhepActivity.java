package com.example.preschool;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class DonXinPhepActivity extends AppCompatActivity {

    /**
     * DonXinPhep này đặt trong Class->idClass
     * Sao khi tạo đơn mới, setValue lên Firebase ok
     */

    private EditText ngayNghi;
    private EditText soNgayNghi;
    private EditText lyDo;
    private Button btnGui;
    private Button btnDaGui;
    private FirebaseAuth mAuth;
    private DatabaseReference DonXinPhepRef,UserRef;
    private DatePickerDialog.OnDateSetListener mDatePickerDialog;
    private DonXinNghi donXinNghi;
    private String userId;
    private User user;
    String mParentName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_don_xin_phep);

        ngayNghi=findViewById(R.id.ngay_nghi);
        soNgayNghi=findViewById(R.id.so_ngay_nghi);
        lyDo=findViewById(R.id.ly_do);
        btnGui=findViewById(R.id.gui_don);

        // Lấy User ID hiện tại đang đăng nhập
        userId=FirebaseAuth.getInstance().getCurrentUser().getUid();

        DonXinPhepRef=FirebaseDatabase.getInstance().getReference().child("DonXinNghi");
        UserRef=FirebaseDatabase.getInstance().getReference().child("Users").child(userId);


        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user=dataSnapshot.getValue(User.class);
                mParentName=user.getFullname();
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

    }

    public void guiDonXinPhep(View view) {
        String mKidName="";
        String mNgayNghi=ngayNghi.getText().toString();
        String mSoNgay=soNgayNghi.getText().toString();
        String mLyDo=lyDo.getText().toString();

        btnGui.setEnabled(false);



        if (!TextUtils.isEmpty(mNgayNghi)&&!TextUtils.isEmpty(mSoNgay)&& !TextUtils.isEmpty(mLyDo)){
            String id=DonXinPhepRef.push().getKey();
            DonXinNghi donXinNghi = new DonXinNghi(userId,mParentName,mKidName,mNgayNghi,mSoNgay,mLyDo);
            DonXinPhepRef.child(id).setValue(donXinNghi, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    Toast.makeText(DonXinPhepActivity.this,"Đã nộp đơn",Toast.LENGTH_LONG).show();
                    ngayNghi.setText("");
                    soNgayNghi.setText("");
                    lyDo.setText("");
                    btnGui.setEnabled(true);
                }
            });

        }else {
            Toast.makeText(this,"Bạn phải điền đủ nội dung",Toast.LENGTH_LONG).show();
        }

    }

    public void pickNgayNghi(View view) {
        Calendar cal =Calendar.getInstance();
        int day= cal.get(Calendar.DAY_OF_MONTH);
        int month=cal.get(Calendar.MONTH);
        int year=cal.get(Calendar.YEAR);
        DatePickerDialog dialog = new DatePickerDialog(
                DonXinPhepActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDatePickerDialog,
                year,month,day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}
