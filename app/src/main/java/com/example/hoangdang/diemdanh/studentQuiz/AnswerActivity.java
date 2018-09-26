package com.example.hoangdang.diemdanh.studentQuiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.hoangdang.diemdanh.R;
import com.example.hoangdang.diemdanh.SupportClass.AppVariable;
import com.example.hoangdang.diemdanh.SupportClass.Network;
import com.example.hoangdang.diemdanh.SupportClass.SecurePreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class AnswerActivity extends AppCompatActivity {

    @BindView(R.id.answer_1)
    Button buttonA;
    @BindView(R.id.answer_2)
    Button buttonB;
    @BindView(R.id.answer_3)
    Button buttonC;
    @BindView(R.id.answer_4)
    Button buttonD;
    @BindView(R.id.quiz_name)
    TextView quizName;
    @BindView(R.id.quiz_noti)
    TextView quizNoti;
    @BindView(R.id.toolbar_quiz)
    Toolbar toolbar;

    Socket socket;
    SharedPreferences prefs;
    String selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        ButterKnife.bind(this);

        prefs = new SecurePreferences(this);

        quizName.setText(prefs.getString(AppVariable.QUIZ_TITLE, "Quiz title"));

        toolbar.setTitle("QUIZ ATTENDANCE");

        int currentQuestionIndexForShowing = prefs.getInt(AppVariable.QUIZ_INDEX, -1);

        String text = "Question " +
                String.valueOf(currentQuestionIndexForShowing + 1) + "/"
                + prefs.getString(AppVariable.QUIZ_TOTAL, "0");

        quizNoti.setText(text);

        setButtonListener();

        setSocket();
    }

    private void setButtonListener(){
        buttonA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonA.setClickable(false);
                buttonB.setEnabled(false);
                buttonC.setEnabled(false);
                buttonD.setEnabled(false);

                emitAnswer("a");
            }
        });

        buttonB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonB.setClickable(false);
                buttonA.setEnabled(false);
                buttonC.setEnabled(false);
                buttonD.setEnabled(false);

                emitAnswer("b");
            }
        });

        buttonC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonC.setClickable(false);
                buttonB.setEnabled(false);
                buttonA.setEnabled(false);
                buttonD.setEnabled(false);

                emitAnswer("c");
            }
        });

        buttonD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonD.setClickable(false);
                buttonA.setEnabled(false);
                buttonC.setEnabled(false);
                buttonB.setEnabled(false);

                emitAnswer("d");
            }
        });
    }

    private void endActivity(){
        unsetSocket();
        Intent returnIntent = new Intent();
        returnIntent.putExtra("option", selected);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    private void endQuizByBoss(){
        unsetSocket();
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    //Socket
    private void unsetSocket(){
        if (socket != null){
            socket.disconnect();
        }
    }

    private void setSocket(){
        if (Network.isOnline(this)){
            try {
                socket = IO.socket(Network.HOST);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    Log.d("socket_log", "connected");
                }

            }).on("quizQuestionEnded", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    endActivity();
                }

            }).on("quizEnded", new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    endQuizByBoss();
                }

            }).on("quizStopped", new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    JSONObject obj = (JSONObject)args[0];
                    String quiz_code;
                    try {
                        quiz_code = obj.getString("quiz_code");

                        String quiz_code_c = prefs.getString(AppVariable.QUIZ_CODE, null);

                        if (quiz_code.equals(quiz_code_c)){

                            endQuizByBoss();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {}

            });
            socket.connect();
        }
    }

    public void emitAnswer(String option){
        JSONObject payload = new JSONObject();

        int currentQuestionIndexForShowing = prefs.getInt(AppVariable.QUIZ_INDEX, -1);

        try {
            payload.put("quiz_code", prefs.getString(AppVariable.QUIZ_CODE, null));
            payload.put("question_index", currentQuestionIndexForShowing);
            payload.put("option", option);
            payload.put("student_id", prefs.getInt(AppVariable.USER_ID, 0));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        prefs.edit()
                .putInt(AppVariable.QUIZ_BLANK, 1)
                .apply();

        selected = option;

        socket.emit("answeredQuiz", payload);
    }
}
