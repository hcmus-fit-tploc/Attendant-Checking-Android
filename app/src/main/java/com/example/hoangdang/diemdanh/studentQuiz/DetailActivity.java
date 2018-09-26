package com.example.hoangdang.diemdanh.studentQuiz;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hoangdang.diemdanh.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    public ArrayList<String> selecteds;
    public ArrayList<String> corrects;

    @BindView(R.id.list_detail)
    ListView listView;

    @BindView(R.id.quiz_toolbar_detail)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        selecteds = (ArrayList<String>)this.getIntent().getSerializableExtra("selecteds");
        corrects = (ArrayList<String>)this.getIntent().getSerializableExtra("corrects");


        toolbar.setTitle("QUIZ DETAIL");

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setButtonListener();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setButtonListener(){
        listView.setAdapter(new ListViewAdapter(selecteds,corrects,this));
    }

    public class ListViewAdapter extends BaseAdapter implements ListAdapter {
        ArrayList<String> answers;
        ArrayList<String> correct_answers;

        Context context;

        View view;

        public ListViewAdapter(ArrayList<String> answers,
                               ArrayList<String> correct_answers, Context context){
            this.answers = answers;
            this.correct_answers = correct_answers;
            this.context = context;
        }

        @Override
        public int getCount() {
            return corrects.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            view = convertView;
            if (view == null){
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.custom_question_result, null);
            }

            TextView textViewIndex = (TextView)view.findViewById(R.id.index);
            textViewIndex.setText("Q" + String.valueOf(position+1) + " ");

            TextView textView = (TextView)view.findViewById(R.id.your_a);
            textView.setText(answers.get(position).toUpperCase());

            TextView textView_r = (TextView)view.findViewById(R.id.correct_answer);
            textView_r.setText(correct_answers.get(position).toUpperCase());

            TextView csign = (TextView)view.findViewById(R.id.correct_sign);
            TextView icsign = (TextView)view.findViewById(R.id.incorrect_sign);
            if (answers.get(position).equals(correct_answers.get(position))){
                icsign.setVisibility(View.GONE);
                csign.setVisibility(View.VISIBLE);
            }
            else {
                csign.setVisibility(View.GONE);
                icsign.setVisibility(View.VISIBLE);
            }

            return view;
        }
    }

}
