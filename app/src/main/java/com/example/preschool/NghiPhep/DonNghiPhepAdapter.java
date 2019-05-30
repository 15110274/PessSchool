package com.example.preschool.NghiPhep;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.preschool.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DonNghiPhepAdapter extends RecyclerView.Adapter<DonNghiPhepAdapter.ViewHolder>{

    private ArrayList<DonNghiPhep> list;

    public DonNghiPhepAdapter(ArrayList<DonNghiPhep> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public DonNghiPhepAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.don_nghi_phep_item, parent, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        holder.ngayNghi.setText("Ngày nghỉ: "+list.get(i).getNgayNghi());
        holder.soNgayNghi.setText("Số ngày: "+list.get(i).getSoNgay());
        holder.lyDo.setText("Lý do: "+list.get(i).getLyDo());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView ngayNghi,soNgayNghi,lyDo;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ngayNghi=itemView.findViewById(R.id.view_ngay_nghi);
            soNgayNghi=itemView.findViewById(R.id.view_so_ngay_nghi);
            lyDo=itemView.findViewById(R.id.view_ly_do);
        }
    }
}
