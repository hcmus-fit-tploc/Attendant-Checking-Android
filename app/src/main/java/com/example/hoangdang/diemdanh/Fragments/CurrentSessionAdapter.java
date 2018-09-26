package com.example.hoangdang.diemdanh.Fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.hoangdang.diemdanh.R;

public class CurrentSessionAdapter extends BaseAdapter {
    private Context context;
    private final int[] list;
    private final String[] listTittle = {"Total", "Present", "Absent"};

    private View gridView;

    public CurrentSessionAdapter(int[] list, Context context) {
        this.context = context;
        this.list = list;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            gridView = new View(context);

            // get layout from mobile.xml
            gridView = inflater.inflate(R.layout.custom_status_session, null);

            // set test based on selected text
            TextView _stud_quantity_textView = (TextView)gridView.findViewById(R.id.stud_quantity_textView);
            _stud_quantity_textView.setText(String.valueOf(list[position]));

            TextView _stud_type_textView = (TextView)gridView.findViewById(R.id.type_textView);
            _stud_type_textView.setText(listTittle[position]);

        } else {
            gridView = (View) convertView;
        }

        return gridView;
    }

    @Override
    public int getCount() {
        return list.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}