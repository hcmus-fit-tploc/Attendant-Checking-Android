package com.example.hoangdang.diemdanh.Fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.hoangdang.diemdanh.R;
import com.example.hoangdang.diemdanh.SupportClass.Course;

import java.util.ArrayList;

public class CourseListAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<Course> list = new ArrayList<>();
    private Context context;

    public View view;

    public CourseListAdapter(ArrayList<Course> list, Context context){
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
        return list.get(position).iID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        view = convertView;
        if (view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.custom_course_list, null);
        }

        Course c = list.get(position);

        TextView _openTag = (TextView)view.findViewById(R.id.open);

        if (c.open != 1){
            _openTag.setVisibility(View.INVISIBLE);
        }
        else{
            _openTag.setVisibility(View.VISIBLE);
        }

        String text = c.strCode + " " + c.classname;
        final TextView _courseCode = (TextView)view.findViewById(R.id.choose_course_code_textView);
        _courseCode.setText(text);

        final TextView _courseName = (TextView)view.findViewById(R.id.choose_course_name_textView);
        _courseName.setText(c.strName);

        final TextView _chcID = (TextView)view.findViewById(R.id.choose_class_has_course_id_textView);
        _chcID.setText(String.valueOf(c.chcid));

        final TextView _attendID = (TextView)view.findViewById(R.id.choose_attend_id_textView);
        _attendID.setText(String.valueOf(c.attendid));

        final TextView _courseID = (TextView)view.findViewById(R.id.choose_course_id_textView);
        _courseID.setText(String.valueOf(c.iID));

        final TextView _classID = (TextView)view.findViewById(R.id.choose_class_id_textView);
        _classID.setText(String.valueOf(c.classID));

        return view;
    }
}
