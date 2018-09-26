package com.example.hoangdang.diemdanh.studentQuiz;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.hoangdang.diemdanh.R;
import com.example.hoangdang.diemdanh.SupportClass.AppVariable;
import com.example.hoangdang.diemdanh.SupportClass.SecurePreferences;
import com.trncic.library.DottedProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WaitingActivity extends AppCompatActivity {

    @BindView(R.id.tv1)
    TextView tv1;

    @BindView(R.id.progress_dot)
    DottedProgressBar dottedProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);

        ButterKnife.bind(this);

        SharedPreferences pres = new SecurePreferences(this);
        tv1.setText(pres.getString(AppVariable.QUIZ_MESSAGE, "Waiting for the quiz to start"));

        dottedProgressBar.startProgress();
        //dottedProgressBar.stopProgress();
    }
}
