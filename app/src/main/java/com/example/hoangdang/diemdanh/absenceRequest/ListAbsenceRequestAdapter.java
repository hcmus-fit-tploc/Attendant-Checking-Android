package com.example.hoangdang.diemdanh.absenceRequest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.hoangdang.diemdanh.R;
import com.example.hoangdang.diemdanh.SupportClass.AbsenceRequest;

import java.util.ArrayList;

public class ListAbsenceRequestAdapter extends BaseAdapter implements ListAdapter{
    private ArrayList<AbsenceRequest> list = new ArrayList<>();
    private Context context;

    public View view;

    public ListAbsenceRequestAdapter(ArrayList<AbsenceRequest> list, Context context){
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        view = convertView;
        if (view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.custom_absence_request_list, null);
        }

        TextView _accepted = (TextView)view.findViewById(R.id.absence_request_accepted);
        TextView _new = (TextView)view.findViewById(R.id.absence_request_new);
        TextView _rejected = (TextView)view.findViewById(R.id.absence_request_deny);

        AbsenceRequest ar = list.get(position);
        if (ar.isAccepted == 1){
            _accepted.setVisibility(View.VISIBLE);
            _new.setVisibility(View.GONE);
            _rejected.setVisibility(View.GONE);
        }
        else if(ar.isAccepted == 0) {
            _new.setVisibility(View.VISIBLE);
            _accepted.setVisibility(View.GONE);
            _rejected.setVisibility(View.GONE);
        }
        else if(ar.isAccepted == 2){
            _rejected.setVisibility(View.VISIBLE);
            _accepted.setVisibility(View.GONE);
            _new.setVisibility(View.GONE);
        }

        TextView _title = (TextView)view.findViewById(R.id.absence_request_date_textView);
        _title.setText(ar.fromDate + " to " + ar.toDate);

        TextView _content = (TextView)view.findViewById(R.id.absence_request_content_textView);
        _content.setText(ar.reason);

        TextView _time = (TextView)view.findViewById(R.id.absence_request_time_textView);
        _time.setText(ar.time);

        return view;
    }
}
