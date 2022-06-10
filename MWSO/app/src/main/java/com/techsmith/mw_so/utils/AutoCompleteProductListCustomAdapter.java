package com.techsmith.mw_so.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.techsmith.mw_so.R;
import com.techsmith.mw_so.SOActivity;

public class AutoCompleteProductListCustomAdapter extends KArrayAdapter<String> {
    final String TAG = "AutocompleteCustomArrayAdapter.java";

    Context mContext;
    int layoutResourceId;
    //    MyObject data[] = null;
    public String[] items;
    public String[] Mrp;
    public String[] Soh;

    public AutoCompleteProductListCustomAdapter(Context mContext, int layoutResourceId, String[] objects, String[] mrp, String[] soh) {

        super(mContext, layoutResourceId, objects);

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.items = objects;
        this.Mrp = mrp;
        this.Soh = soh;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        try {

            /*
             * The convertView argument is essentially a "ScrapView" as described is Lucas post
             * http://lucasr.org/2012/04/05/performance-tips-for-androids-listview/
             * It will have a non-null value when ListView is asking you recycle the row layout.
             * So, when convertView is not null, you should simply update its contents instead of inflating a new row layout.
             */

            if (convertView == null) {
                // inflate the layout
                LayoutInflater inflater = ((SOActivity) mContext).getLayoutInflater();
                convertView = inflater.inflate(layoutResourceId, parent, false);
            }

            // object item based on the position
            String objectItem = items[position];

            // get the TextView and then set the text (item name) and tag (item ID) values
            TextView textViewItem = convertView.findViewById(R.id.textViewItem);
            TextView txtMrp = convertView.findViewById(R.id.txtmrp);
            TextView txtSoh = convertView.findViewById(R.id.txtsoh);

            try {
                textViewItem.setText(objectItem);
                txtMrp.setText("MRP: "+Mrp[position]);
                txtSoh.setText("SOH: "+Soh[position]);
            } catch (Exception e) {
                e.printStackTrace();
            }


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
