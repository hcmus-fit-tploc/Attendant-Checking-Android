package com.example.hoangdang.diemdanh.CurrentSession;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.hoangdang.diemdanh.R;
import com.example.hoangdang.diemdanh.SupportClass.AppVariable;
import com.example.hoangdang.diemdanh.SupportClass.DatabaseHelper;
import com.example.hoangdang.diemdanh.SupportClass.Student;

import java.util.ArrayList;

public class ListStudentAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<Student> list = new ArrayList<>();
    private Context context;

    public View view;

    public ListStudentAdapter(ArrayList<Student> list, Context context){
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        view = convertView;
        if( view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.custom_list_student, null);
        }

        TextView studName = (TextView)view.findViewById(R.id.stud_name_textView);
        studName.setText(list.get(position).strName);

        TextView studID = (TextView)view.findViewById(R.id.stud_id_textView);
        studID.setText(list.get(position).strCode);

        //Handle buttons and add onClickListeners
        Button _absenceButton = (Button) view.findViewById(R.id.absence_button);
        Button _checkButton = (Button) view.findViewById(R.id.check_button);

        final DatabaseHelper db = new DatabaseHelper(context);

        _absenceButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                db.changeAttendanceStatus(list.get(position).iID, ((CurrentSessionActivity)context).getAttendanceID(), AppVariable.ABSENCE_STATUS);
                list.remove(position);
                notifyDataSetChanged();
            }
        });
        _checkButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                db.changeAttendanceStatus(list.get(position).iID, ((CurrentSessionActivity)context).getAttendanceID(), AppVariable.ATTENDANCE_STATUS);
                list.remove(position);
                notifyDataSetChanged();
            }
        });

        return view;
    }
}
