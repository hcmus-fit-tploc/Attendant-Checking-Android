package com.example.hoangdang.diemdanh.studentQuiz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hoangdang.diemdanh.R;
import com.example.hoangdang.diemdanh.SupportClass.AppVariable;
import com.example.hoangdang.diemdanh.SupportClass.Network;
import com.example.hoangdang.diemdanh.SupportClass.SecurePreferences;
import com.trncic.library.DottedProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class StudentQuizActivity extends AppCompatActivity {
    @BindView(R.id.tv1)
    TextView tv1;

    @BindView(R.id.progress_dot)
    DottedProgressBar dottedProgressBar;

    @BindView(R.id.quiz_toolbar)
    Toolbar toolbar;


    public Socket socket;
    String quiz_code;
    public SharedPreferences prefs;
    ProgressDialog progressDialog;
    int user_id;
    String token;

    public JSONObject quizConfig;
    public JSONArray quizQuestions;
    public ArrayList<String> selecteds;
    public ArrayList<String> corrects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_quiz);

        prefs = new SecurePreferences(this);

        quiz_code = prefs.getString(AppVariable.QUIZ_CODE, null);
        user_id = prefs.getInt(AppVariable.USER_ID, 0);
        token = prefs.getString(AppVariable.USER_TOKEN, null);

        ButterKnife.bind(this);

        toolbar.setTitle("QUIZ ATTENDANCE");

        selecteds = new ArrayList<>();
        corrects = new ArrayList<>();

        // prepare spinner
        progressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);

        prefs.edit()
                .putInt(AppVariable.QUIZ_BLANK, 0)
                .putString(AppVariable.QUIZ_MESSAGE, "Waiting for the quiz to start").apply();

        tv1.setText("Waiting for the quiz to start");

        dottedProgressBar.startProgress();

        setSocket();

        new GetQuizTask().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        reconnectSocket();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    String option = data.getStringExtra("option");
                    saveAnswer(option);
                    int index = prefs.getInt(AppVariable.QUIZ_INDEX, 0) + 1;
                    int total = prefs.getInt(AppVariable.QUIZ_TOTAL, 0);

                    if (index == total){
                        showResultActivity();
                    }
                }
                if (resultCode == RESULT_CANCELED){
                    finish();
                    displayToast("Stopped by teacher");
                }
                break;
        }
    }

    @Override
    protected void onStop() {
        /*JSONObject payload = new JSONObject();
        try {
            payload.put("quiz_code", prefs.getString(AppVariable.QUIZ_CODE, null));
            payload.put("student_id", prefs.getString(AppVariable.USER_ID, null));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit("quittedQuiz", payload);*/
        super.onStop();
    }

    //UI

    private void changeMessage(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv1.setText("Ready for the next question");
            }
        });
    }

    private void displayToast(String toast) {
        if(toast != null) {
            Toast.makeText(this, toast, Toast.LENGTH_LONG).show();
        }
    }

    private void showAnswerActivity(){
        unsetSocket();
        Intent intent = new Intent(this, AnswerActivity.class);
        startActivityForResult(intent, 0);
    }

    private void showResultActivity(){
        unsetSocket();
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("selecteds", selecteds);
        intent.putExtra("corrects", corrects);
        startActivity(intent);
        finish();
    }

    //Functions

    public void closeQuiz(){
       onBackPressed();
    }

    public void saveAnswer(String option){
        try {
            if (option == null){
                option = "";
            }
            JSONObject quizDetail = quizQuestions.getJSONObject(prefs.getInt(AppVariable.QUIZ_INDEX, 0));
            String correct_option = quizDetail.getString("correct_option");

            ArrayList<String> options = new ArrayList<>();

            options.add(quizDetail.getString("option_a"));
            options.add(quizDetail.getString("option_b"));
            options.add(quizDetail.getString("option_c"));
            options.add(quizDetail.getString("option_d"));

            String[] keys = {"a", "b", "c", "d"};

            selecteds.add(option);

            for (int i = 0; i < 4; i++){
                if (correct_option.equals(options.get(i))){
                    correct_option = keys[i];
                    corrects.add(correct_option);
                    break;
                }
            }

            if (option.equals(correct_option)){
                int countCorrect = prefs.getInt(AppVariable.QUIZ_CORRECT, 0);
                countCorrect += 1;
                prefs.edit().putInt(AppVariable.QUIZ_CORRECT, countCorrect).apply();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Network task

    private class GetQuizTask extends AsyncTask<String, Void, Integer> {

        private Exception exception;
        private String strJsonResponse;

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(String... params) {
            int flag = 0;
            try {
                URL url = new URL(Network.API_GET_QUIZ);

                //prepare json data
                JSONObject jsonUserData = new JSONObject();
                jsonUserData.put("token", token);
                jsonUserData.put("quiz_code", quiz_code);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                try {

                    connection.setReadTimeout(10000);
                    connection.setConnectTimeout(15000);
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    connection.setRequestProperty("Accept", "application/json");
                    connection.setDoInput(true);
                    connection.setDoOutput(true);

                    //write
                    OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                    writer.write(jsonUserData.toString());
                    writer.flush();

                    //check http response code
                    int status = connection.getResponseCode();
                    switch (status){
                        case HttpURLConnection.HTTP_OK:
                            //read response
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                            StringBuilder sb = new StringBuilder();
                            String line;

                            while ((line = bufferedReader.readLine()) != null) {
                                sb.append(line).append("\n");
                            }

                            bufferedReader.close();
                            strJsonResponse = sb.toString();

                            flag = HttpURLConnection.HTTP_OK;
                        default:
                            exception = new Exception(connection.getResponseMessage());
                    }
                }
                finally{
                    connection.disconnect();
                }
            }
            catch(Exception e) {
                exception = e;
            }
            return flag;
        }

        @Override
        protected void onPostExecute(Integer status) {
            if (status != HttpURLConnection.HTTP_OK){
                displayToast(exception.getMessage());
            }
            else {
                try{
                    JSONObject jsonObject = new JSONObject(strJsonResponse);
                    String result = jsonObject.getString("result");

                    if (result.equals("failure")){
                        String message = jsonObject.getString("message");
                        progressDialog.dismiss();
                        displayToast(message);
                        return;
                    }

                    quizConfig = jsonObject.getJSONObject("quiz");
                    quizQuestions = quizConfig.getJSONArray("questions");

                    prefs.edit().putInt(AppVariable.QUIZ_TOTAL, quizQuestions.length())
                            .putInt(AppVariable.QUIZ_TYPE, quizConfig.getInt("type"))
                            .putString(AppVariable.QUIZ_TITLE, quizConfig.getString("title")).apply();

                    progressDialog.dismiss();
                    return;

                } catch (JSONException e) {
                    e.printStackTrace();
                    displayToast(e.getMessage());
                }
            }
            progressDialog.dismiss();
        }
    }

    //Socket
    private void unsetSocket(){
        if (socket != null){
            socket.disconnect();
        }
    }

    private void reconnectSocket(){
        if (socket != null && !socket.connected()){
            socket.connect();
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

            }).on("quizQuestionReady", new Emitter.Listener() {

                @Override
                public void call(Object... args) {

                    JSONObject obj = (JSONObject)args[0];
                    String quiz_code;
                    try {
                        quiz_code = obj.getString("quiz_code");

                        String quiz_code_c = prefs.getString(AppVariable.QUIZ_CODE, null);

                        if (quiz_code.equals(quiz_code_c)){
                            changeMessage();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }).on("quizQuestionLoaded", new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    JSONObject obj = (JSONObject)args[0];
                    String quiz_code;
                    int question_index;
                    try {
                        quiz_code = obj.getString("quiz_code");

                        String quiz_code_c = prefs.getString(AppVariable.QUIZ_CODE, null);

                        if (quiz_code.equals(quiz_code_c)){
                            question_index = obj.getInt("question_index");

                            prefs.edit().
                                    putInt(AppVariable.QUIZ_INDEX, question_index)
                                    .apply();

                            showAnswerActivity();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }).on("quizEnded", new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    JSONObject obj = (JSONObject)args[0];
                    String quiz_code;
                    try {
                        quiz_code = obj.getString("quiz_code");

                        String quiz_code_c = prefs.getString(AppVariable.QUIZ_CODE, null);

                        if (quiz_code.equals(quiz_code_c)){

                            showResultActivity();
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
}
