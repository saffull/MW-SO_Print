package com.techsmith.mw_so.utils;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

public class KArrayAdapter<T> extends ArrayAdapter<String> {

    private final Filter filter = new KNoFilter();

    public String[]  items;

    @Override
    public Filter getFilter() {
        return filter;
    }

    public KArrayAdapter(Context context, int textViewResourceId, String[] objects) {
        super(context, textViewResourceId, objects);
        items = objects;
    }

    private class KNoFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence arg0) {
            FilterResults result = new FilterResults();
            result.values = items;
            result.count = items.length;
            return result;
        }

        @Override
        protected void publishResults(CharSequence arg0, FilterResults arg1) {
            notifyDataSetChanged();
        }
    }
}
