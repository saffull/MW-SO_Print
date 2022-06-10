package com.techsmith.mw_so.collection_utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.techsmith.mw_so.Collections;
import com.techsmith.mw_so.R;
import com.techsmith.mw_so.utils.KArrayAdapter;

public class AutoCompleteCustomerAdapter  extends KArrayAdapter<String> {
    Context mContext;
    int layoutResourceId;
    public String[]  items;

    public AutoCompleteCustomerAdapter(Context context, int layoutResourceId, String[] objects) {
        super(context, layoutResourceId, objects);
        this.layoutResourceId = layoutResourceId;
        this.mContext = context;
        this.items = objects;
    }
    @Override
    public int getCount() {
        return items.length;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        try {

            if(convertView == null) {
                // inflate the layout
                LayoutInflater inflater = ((Collections) mContext).getLayoutInflater();
                convertView = inflater.inflate(layoutResourceId, parent, false);
            }
            String objectItem = items[position];

            TextView textViewItem = convertView.findViewById(R.id.textViewItem);
            textViewItem.setText(objectItem);


        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;

    }
}
