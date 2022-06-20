package com.techsmith.mw_so.e_invoice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.techsmith.mw_so.R;
import com.techsmith.mw_so.SOActivity;

import java.util.List;

public class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.MyViewHolder> {

    private final LayoutInflater inflater;
    private final String[] myImageNameList;

    List<String> billidList, billnoList, storeidList;

    public InvoiceAdapter(Context ctx, String[] myImageNameList, List<String> billidList, List<String> billnoList, List<String> storeidList) {
        inflater = LayoutInflater.from(ctx);
        this.myImageNameList = myImageNameList;
        this.billidList = billidList;
        this.billnoList = billnoList;
        this.storeidList = storeidList;
        System.out.println("new list is " + billidList);
    }

    @NonNull
    @Override
    public InvoiceAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.invoice_recycler, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull InvoiceAdapter.MyViewHolder holder, int position) {
        try {
            holder.name.setText(storeidList.get(position));
            holder.billid.setText(billidList.get(position));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return billidList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name, billid;
        Button invbtn;

        public MyViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            billid = itemView.findViewById(R.id.billid);
            invbtn = itemView.findViewById(R.id.invBtn);


        }

    }
}
