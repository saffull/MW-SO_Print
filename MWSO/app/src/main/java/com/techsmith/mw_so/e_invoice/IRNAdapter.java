package com.techsmith.mw_so.e_invoice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.techsmith.mw_so.R;

import java.util.List;

public class IRNAdapter extends RecyclerView.Adapter<IRNAdapter.MyViewHolder> {

    private final LayoutInflater inflater;
    private final String[] myImageNameList;
    Gson gson;
    private final String response;
    InvResponse invResponse;

    public IRNAdapter(Context ctx, String[] myImageNameList, String strfromweb) {
        inflater = LayoutInflater.from(ctx);
        this.myImageNameList = myImageNameList;
        this.response=strfromweb;
        gson = new Gson();
        System.out.println("Response coming is "+response);
        invResponse= gson.fromJson(response, InvResponse.class);
    }

    @NonNull
    @Override
    public IRNAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.invoice_list_item, parent, false);
        IRNAdapter.MyViewHolder holder = new IRNAdapter.MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull IRNAdapter.MyViewHolder holder, int position) {
        try{
            gson = new Gson();
            invResponse= gson.fromJson(response, InvResponse.class);
            for (int i = 0; i < invResponse.list.size(); i++) {
                holder.tvEmailSender.setText(String.valueOf(invResponse.list.get(i).storeId));
                holder.tvEmailTitle.setText(String.valueOf(invResponse.list.get(i).billId));
                holder.tvEmailDetails.setText(invResponse.list.get(i).irnNo);
                holder.tvEmaidesc.setText(String.valueOf(invResponse.list.get(i).billNo));
            }


        }catch (Exception e){e.printStackTrace();}
    }

    @Override
    public int getItemCount() {
        return invResponse.list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvEmailSender, tvEmailTitle, tvEmailDetails,tvEmaidesc;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvEmailSender = itemView.findViewById(R.id.tvEmailSend);
            tvEmailTitle = itemView.findViewById(R.id.tvEmailTitl);
            tvEmailDetails = itemView.findViewById(R.id.tvEmailDetail);
            tvEmaidesc=itemView.findViewById(R.id.tvEmaidesc);
        }

    }
}
