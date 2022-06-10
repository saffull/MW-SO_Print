package com.techsmith.mw_so.receivable_utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.techsmith.mw_so.R;

import java.util.List;

public class AdapterRe extends RecyclerView.Adapter<AdapterRe.MyViewHolder> {

    private final LayoutInflater inflater;
    private final List<String> docId;
    private final List<String> billAmount;
    private final List<String> balancelist;


    public AdapterRe(Context ctx, List docId, List billAmount, List balancelist) {

        inflater = LayoutInflater.from(ctx);
        this.docId = docId;
        this.billAmount = billAmount;
        this.balancelist = balancelist;
    }

    @Override
    public AdapterRe.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.rv_layout, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(AdapterRe.MyViewHolder holder, int position) {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.DocId.setText(docId.get(position));
        holder.txtAmount.setText(String.valueOf(billAmount.get(position)));
        holder.txtBalance.setText(String.valueOf(balancelist.get(position)));


    }

    @Override
    public int getItemCount() {
        return balancelist.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView DocId, txtBalance, txtAmount;


        public MyViewHolder(View itemView) {
            super(itemView);

            DocId = (TextView) itemView.findViewById(R.id.docno);
            txtBalance = itemView.findViewById(R.id.overdue);
            txtAmount = itemView.findViewById(R.id.Billamount);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //MainActivity.textView.setText("You have selected : "+myImageNameList[getAdapterPosition()]);
                    //MainActivity.dialog.dismiss();
                    System.out.println(docId.get(getAdapterPosition()));
                }
            });

        }

    }
}
