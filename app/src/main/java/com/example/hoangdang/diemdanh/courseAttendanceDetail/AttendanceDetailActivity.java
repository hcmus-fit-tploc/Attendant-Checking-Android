package com.example.hoangdang.diemdanh.courseAttendanceDetail;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hoangdang.diemdanh.R;
import com.example.hoangdang.diemdanh.SupportClass.AttendanceDetail;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AttendanceDetailActivity extends AppCompatActivity {

    @BindView(R.id.attendance_detail_listView)
    ListView listView;

    ArrayList<AttendanceDetail> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_detail);

        list = (ArrayList<AttendanceDetail>)this.getIntent().getSerializableExtra("DATA_AD");

        this.setTitle("ATTENDANCE HISTORY");

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ButterKnife.bind(this);

        listView.setAdapter(new ListViewAdapter(list, this));
    }

    private class ListViewAdapter extends BaseAdapter implements ListAdapter {
        ArrayList<AttendanceDetail> list;
        Context context;

        View view;

        public ListViewAdapter(ArrayList<AttendanceDetail> list, Context context){
            this.list = list;
            this.context = context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
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
                view = inflater.inflate(R.layout.custom_attendance_detail_list, null);
            }

            AttendanceDetail attendanceDetail = list.get(position);

            TextView time = (TextView)view.findViewById(R.id.attendance_time_textView);
            time.setText(attendanceDetail.created_at.substring(0,10));

            TextView absence = (TextView)view.findViewById(R.id.attendance_absence);
            TextView present = (TextView)view.findViewById(R.id.attendance_present);

            if (attendanceDetail.type == 0){
                absence.setVisibility(View.VISIBLE);
                present.setVisibility(View.GONE);
            }
            else {
                absence.setVisibility(View.GONE);
                present.setVisibility(View.VISIBLE);
            }

            if (!attendanceDetail.edit_reason.equals("null")){
                TextView reason = (TextView)view.findViewById(R.id.reason_edited_textView);
                reason.setVisibility(View.VISIBLE);
                reason.setText(attendanceDetail.edit_reason);
            }

            return view;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
