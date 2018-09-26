package com.example.hoangdang.diemdanh.feedback;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.hoangdang.diemdanh.R;
import com.example.hoangdang.diemdanh.SupportClass.Feedback;

import java.util.ArrayList;

public class ListFeedbackAdapter extends BaseAdapter implements ListAdapter{
    private ArrayList<Feedback> list = new ArrayList<>();
    private Context context;

    public View view;

    public ListFeedbackAdapter(ArrayList<Feedback> list, Context context){
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
            view = inflater.inflate(R.layout.custom_feedback_list, null);
        }

        TextView _seen = (TextView)view.findViewById(R.id.feedback_seen);
        TextView _delivered = (TextView)view.findViewById(R.id.feedback_delivered);
        Feedback fb = list.get(position);
        if (fb.isRead == 1){
            _seen.setVisibility(View.VISIBLE);
            _delivered.setVisibility(View.GONE);
        }
        else if(fb.isRead == 0) {
            _delivered.setVisibility(View.VISIBLE);
            _seen.setVisibility(View.GONE);
        }

        TextView _title = (TextView)view.findViewById(R.id.feedback_title_textView);
        _title.setText(fb.title);

        TextView _content = (TextView)view.findViewById(R.id.feedback_content_textView);
        _content.setText(fb.content);

        TextView _time = (TextView)view.findViewById(R.id.feedback_time_textView);
        _time.setText(fb.time);

        return view;
    }
}
