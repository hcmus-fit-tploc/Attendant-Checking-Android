package com.example.hoangdang.diemdanh.timeTable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.hoangdang.diemdanh.R;

import java.util.ArrayList;

public class LessonAdapter extends BaseAdapter implements ListAdapter{
    private ArrayList<LessonInfo> list = new ArrayList<>();
    private Context context;
    public View view;

    public LessonAdapter(ArrayList<LessonInfo> list, Context context){
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
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        view = convertView;
        if (view == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.custom_list_lesson,null);
        }

        LessonInfo l = list.get(position);

        TextView course = (TextView)view.findViewById(R.id.course_code_tv);
        course.setText(l.code + " - " + l.name + " - " + l.classname);
        TextView room = (TextView)view.findViewById(R.id.more_content_tv);
        room.setText(l.content);
        TextView note = (TextView)view.findViewById(R.id.note_tv);
        note.setText(l.note);
        TextView office = (TextView)view.findViewById(R.id.office_hour_tv);
        office.setText(l.office_hour);

        return view;
    }
}
