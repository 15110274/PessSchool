package com.example.preschool.NghiPhep;
/**
 * Activity này dành cho giáo viên của lớp, có thể xem toàn bộ
 * đơn xin nghỉ phép của phụ huynh trong lớp
 */

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.preschool.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DonNghiPhepFullViewActivity extends AppCompatActivity {

    private DatabaseReference DonNghiPhepRef;
    private RecyclerView recyclerView;
    private ArrayList<DonNghiPhep> donNghiPhepArrayList = new ArrayList<DonNghiPhep>();
    private DonNghiPhepFullViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_don_nghi_phep_full_view);
        recyclerView = findViewById(R.id.recycler_view_donnghiphep_full);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DonNghiPhepRef = FirebaseDatabase.getInstance().getReference().child("Class").child("Class01").child("DonNghiPhep");
        DonNghiPhepRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    donNghiPhepArrayList.add(ds.getValue(DonNghiPhep.class));
                }
                adapter = new DonNghiPhepFullViewAdapter(donNghiPhepArrayList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
