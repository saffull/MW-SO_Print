package com.techsmith.mw_so.Expandable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.techsmith.mw_so.Model.CardModel;
import com.techsmith.mw_so.R;

import java.util.ArrayList;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.Viewholder> {
    private final Context context;
    private final ArrayList<CardModel> courseModelArrayList;

    // Constructor
    public CourseAdapter(Context context, ArrayList<CardModel> courseModelArrayList) {
        this.context = context;
        this.courseModelArrayList = courseModelArrayList;
    }

    @NonNull
    @Override
    public CourseAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cards_layout, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseAdapter.Viewholder holder, int position) {
        CardModel model = courseModelArrayList.get(position);
        holder.tvProfileName.setText(model.getName());
        holder.ivProfilePic.setImageResource(model.getImageName());

    }

    @Override
    public int getItemCount() {
        return courseModelArrayList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        private final ImageView ivProfilePic;
        private final TextView tvProfileName;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            ivProfilePic = itemView.findViewById(R.id.ivProfilePic);
            tvProfileName = itemView.findViewById(R.id.tvProfileName);
        }
    }
}
