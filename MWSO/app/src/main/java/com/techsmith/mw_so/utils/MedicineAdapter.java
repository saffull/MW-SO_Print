package com.techsmith.mw_so.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.techsmith.mw_so.R;

import java.util.List;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.MyViewHolder> {

    private final List<Medicine> medicineList;


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvSlNo, tvmrp, tvProductName, tvItem, tvMrp, tvRate, tvqty, tvSOH, tvQty, tvFreeQty, tvTotal;

        public MyViewHolder(View rowView) {
            super(rowView);
            tvSlNo = rowView.findViewById(R.id.tvSlNo);
            tvmrp = rowView.findViewById(R.id.tvmrp);
            tvProductName = rowView.findViewById(R.id.tvProductName);
            tvMrp = rowView.findViewById(R.id.tvMRP);
            tvRate = rowView.findViewById(R.id.tvRate);
            tvqty = rowView.findViewById(R.id.tvqty);
            tvSOH = rowView.findViewById(R.id.tvSOHInQtySelection);
            tvQty = rowView.findViewById(R.id.tvQty);
            tvFreeQty = rowView.findViewById(R.id.tvFreeQty);
            tvTotal = rowView.findViewById(R.id.tvTotal);
            final ImageButton imgBtnRemarksItem = rowView.findViewById(R.id.imgBtnRemarksItem);
            ImageButton btnDelete = rowView.findViewById(R.id.btnDeleteItem);

        }
    }

    public MedicineAdapter(List<Medicine> medicineList) {
        this.medicineList = medicineList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lv_saleorder, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Medicine medicine = medicineList.get(position);
        System.out.println("Medicine names are: "+medicine.getItemName());
        holder.tvProductName.setText(String.valueOf(medicine.getItemName()));
    }

    @Override
    public int getItemCount() {
        return medicineList.size();
    }
}
