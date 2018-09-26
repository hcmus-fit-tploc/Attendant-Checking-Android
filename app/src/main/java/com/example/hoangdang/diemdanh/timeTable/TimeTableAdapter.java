package com.example.hoangdang.diemdanh.timeTable;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.hoangdang.diemdanh.R;

public class TimeTableAdapter extends BaseAdapter {
    private Context context;
    private final TimeTable tt;

    public View view;

    public TimeTableAdapter(TimeTable tt, Context context) {
        this.context = context;
        this.tt = tt;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        view = convertView;
        if (view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.custom_timetable, null);
        }
        String code = "";
        for (LessonInfo l: tt.lessonInfos[position]) {
            if (l.underline == 1){
                code = code + " <u>" + l.code + "</u>";
            }
            else{
                code = code + l.code + " ";
            }
        }

        TextView _pos = (TextView) view.findViewById(R.id.fake_textView);
        if (tt.index[position] == 1) {
            _pos.setText(Html.fromHtml(code));
        }
        else if (tt.index[position] == 2){
            _pos.setBackgroundColor(ContextCompat.getColor(context, R.color.green));
            _pos.setText(code);
        }

        return view;
    }

    @Override
    public int getCount() {
        return tt.index.length;
    }

    @Override
    public Object getItem(int position) {
        return tt.lessonInfos[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}