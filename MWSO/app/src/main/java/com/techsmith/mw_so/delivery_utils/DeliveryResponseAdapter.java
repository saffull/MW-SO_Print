package com.techsmith.mw_so.delivery_utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.techsmith.mw_so.Collections;
import com.techsmith.mw_so.Delivery;
import com.techsmith.mw_so.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DeliveryResponseAdapter extends RecyclerView.Adapter<DeliveryResponseAdapter.MyViewHolder> {

    private final List<DeliveryResponse> deliveryList;
    onClickInterface onClickInterface;
    Context context;
    List<String> posList;
    List testList;


    public DeliveryResponseAdapter(Context context, List<DeliveryResponse> deliveryList, onClickInterface onClickInterface) {
        this.deliveryList = deliveryList;
        this.context = context;
        this.onClickInterface = onClickInterface;
        posList=new ArrayList<>(deliveryList.size());
        testList=new ArrayList(deliveryList.size());
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView amount, items, invoice, billno;
        public CheckBox selectBox;

        public MyViewHolder(View view) {
            super(view);
            amount = view.findViewById(R.id.amount);
            invoice = view.findViewById(R.id.invoice);
            items = view.findViewById(R.id.items);
            billno = view.findViewById(R.id.billno);
            selectBox = view.findViewById(R.id.selectBox);
        }


    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.collection_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        DeliveryResponse dResponse = deliveryList.get(position);
        final int pos = position;
        holder.amount.setText(dResponse.getAmount());
        holder.billno.setText(dResponse.getBillno());
        holder.items.setText(dResponse.getItems());
        holder.invoice.setText(dResponse.getInvoice());

        holder.selectBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
//                    onClickInterface.setClick(2);
                }catch (Exception e){e.printStackTrace();}

                System.out.println("Clicked position is" + pos);

                if (isChecked){
                    Toast.makeText(context, "Checked Position is " + pos, Toast.LENGTH_SHORT).show();
                    posList.add(String.valueOf(pos));
                    ((Delivery) context).posList=posList;
                }else {
                    Iterator<String> itr = posList.iterator();
                    while (itr.hasNext()) {
                        String si = itr.next();
                        if (si.equals(String.valueOf(pos))) {
                            itr.remove();
                            System.out.println("New List is "+posList);
                            ((Delivery) context).posList=posList;
                        }else {
                            System.out.println("Nothing to remove");
                        }
                    }
                }
                System.out.println("New List is "+posList);

            }
        });
    }

    @Override
    public int getItemCount() {
        return deliveryList.size();
    }


}
