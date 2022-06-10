package com.techsmith.mw_so.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.techsmith.mw_so.CustomerInformation;
import com.techsmith.mw_so.R;


public class AutocompleteCustomArrayAdapter  extends KArrayAdapter<String>{


    Context mContext;
    int layoutResourceId;
    public String[]  items;


    public AutocompleteCustomArrayAdapter(Context mContext, int layoutResourceId, String[] objects) {

        super(mContext, layoutResourceId, objects);

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.items = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        try {

            if(convertView == null) {
                // inflate the layout
                LayoutInflater inflater = ((CustomerInformation) mContext).getLayoutInflater();
                convertView = inflater.inflate(layoutResourceId, parent, false);
            }

            // object item based on the position
            String objectItem = items[position];

            // get the TextView and then set the text (item name) and tag (item ID) values
            TextView textViewItem = convertView.findViewById(R.id.textViewItem);
            textViewItem.setText(objectItem);

            // in case you want to add some style, you can do something like:
//            textViewItem.setBackgroundColor(Color.CYAN);

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;

    }

    @Override
    public int getCount() {
        return items.length;
    }

}
