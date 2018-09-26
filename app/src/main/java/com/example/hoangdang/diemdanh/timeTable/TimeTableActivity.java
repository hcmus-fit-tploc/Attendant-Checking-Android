package com.example.hoangdang.diemdanh.timeTable;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hoangdang.diemdanh.R;
import com.example.hoangdang.diemdanh.SupportClass.Course;
import com.example.hoangdang.diemdanh.SupportClass.DatabaseHelper;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TimeTableActivity extends AppCompatActivity {
    @BindView(R.id.timetable_toolbar)
    Toolbar _toolBar;
    @BindView(R.id.timetable_gripView)
    GridView _gripView;

    static TimeTable table;
    ListView info_ll;
    TextView time;
    @BindView(R.id.free_tv)
    TextView free;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);

        ButterKnife.bind(this);

        info_ll = (ListView)findViewById(R.id.detail_ll);
        time = (TextView)findViewById(R.id.time_tv);

        _toolBar.setTitle("SCHEDULES");
        setSupportActionBar(_toolBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        progressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);

        new loadTimetable().execute();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void setAdapter(){
        _gripView.setAdapter(new TimeTableAdapter(table, this));
        _gripView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                time.setVisibility(View.VISIBLE);
                if(table.index[position] == 0) {
                    info_ll.setVisibility(View.INVISIBLE);
                    time.setText(table.getK(position));
                    free.setVisibility(View.VISIBLE);

                }
                else if(table.index[position] == 1){
                    info_ll.setAdapter(new LessonAdapter(table.lessonInfos[position], getBaseContext()));
                    time.setText(table.getK(position));
                    info_ll.setVisibility(View.VISIBLE);
                    free.setVisibility(View.GONE);
                }
            }
        });
    }

    private class loadTimetable extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            DatabaseHelper db = new DatabaseHelper(getBaseContext());
            table = db.getTimetable();
            return null;
        }

        @Override
        protected void onPostExecute(Void status) {
            setAdapter();
            progressDialog.dismiss();
        }
    }

}
