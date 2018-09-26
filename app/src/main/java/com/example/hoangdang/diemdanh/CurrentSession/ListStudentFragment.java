package com.example.hoangdang.diemdanh.CurrentSession;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.hoangdang.diemdanh.R;
import com.example.hoangdang.diemdanh.SupportClass.AppVariable;
import com.example.hoangdang.diemdanh.SupportClass.DatabaseHelper;
import com.example.hoangdang.diemdanh.SupportClass.Student;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListStudentFragment extends Fragment {
    @BindView(R.id.list_student_listView) ListView listView;
    ArrayList<Student> list;

    public ListStudentFragment(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_student, container,false);

        ButterKnife.bind(this, view);

        setListViewAdapter();

        return view;
    }

    public void setListViewAdapter(){
        DatabaseHelper db = new DatabaseHelper(getActivity());
        list = db.getStudentByAttendanceStatus(((CurrentSessionActivity)this.getActivity()).getAttendanceID(), AppVariable.UNPROCESSED_STATUS);

        listView.setAdapter(new ListStudentAdapter(list, getActivity()));
    }
}

