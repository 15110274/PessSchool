package com.example.preschool.NghiPhep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.example.preschool.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DonNghiPhepViewActivity extends AppCompatActivity {

    private DatabaseReference DonNghiPhepRef;
    private RecyclerView recyclerView;
    private ArrayList<DonNghiPhep> donNghiPhepArrayList = new ArrayList<DonNghiPhep>();
    private DonNghiPhepAdapter adapter;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_don_nghi_phep_view);
        userId = getIntent().getExtras().get("USER_ID").toString();

        recyclerView = findViewById(R.id.recycler_view_donnghiphep);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DonNghiPhepRef = FirebaseDatabase.getInstance().getReference().child("Class").child("Class01").child("DonNghiPhep");
        DonNghiPhepRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getValue(DonNghiPhep.class).getUserId().equals(userId)) {
                        donNghiPhepArrayList.add(ds.getValue(DonNghiPhep.class));
                    }
                }
                adapter = new DonNghiPhepAdapter(donNghiPhepArrayList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
