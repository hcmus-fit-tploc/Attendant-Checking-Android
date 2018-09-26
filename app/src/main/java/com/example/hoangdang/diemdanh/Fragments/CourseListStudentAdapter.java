package com.example.hoangdang.diemdanh.Fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.hoangdang.diemdanh.R;
import com.example.hoangdang.diemdanh.SupportClass.Course;
import com.example.hoangdang.diemdanh.SupportClass.CourseStatistic;

import java.util.ArrayList;

public class CourseListStudentAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<CourseStatistic> list = new ArrayList<>();
    private Context context;

    public View view;

    public CourseListStudentAdapter(ArrayList<CourseStatistic> list, Context context){
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
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        view = convertView;
        if (view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.custom_course_list_student, null);
        }

        CourseStatistic c = list.get(position);

        final TextView _courseCode = (TextView)view.findViewById(R.id.choose_course_code_textView);
        _courseCode.setText(c.code);

        final TextView _courseName = (TextView)view.findViewById(R.id.choose_course_name_textView);
        _courseName.setText(c.name);

        Double cCom = ((double)c.absenceCounts/c.attendanceCounts) * 100;

        String text = "Total: " + String.valueOf(c.attendanceCounts) + " Absences: " + String.valueOf(c.absenceCounts) + " A-Percent: " + String.format("%.2f", cCom) + "%";

        final TextView _stast = (TextView)view.findViewById(R.id.stast);
        _stast.setText(text);

        return view;
    }
}
