package com.example.hoangdang.diemdanh.classManagement;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hoangdang.diemdanh.LoginActivity;
import com.example.hoangdang.diemdanh.R;
import com.example.hoangdang.diemdanh.SupportClass.AppVariable;
import com.example.hoangdang.diemdanh.SupportClass.DatabaseHelper;
import com.example.hoangdang.diemdanh.SupportClass.Feedback;
import com.example.hoangdang.diemdanh.SupportClass.Network;
import com.example.hoangdang.diemdanh.SupportClass.SecurePreferences;
import com.example.hoangdang.diemdanh.SupportClass.Student;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class classManagementActivity extends AppCompatActivity {

    @BindView(R.id.class_management_listView)
    GridView listView;

    @BindView(R.id.class_management_toolbar)
    Toolbar toolbar;

    public DatabaseHelper db;
    public SharedPreferences prefs;
    public ProgressDialog progressDialog;

    TextView count_aq;
    TextView count_d;
    TextView count_p;

    ArrayList<Student> list;

    public int attendanceID;
    public int classID;
    public int courseID;
    public int stud_id;

    public String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_management);

        ButterKnife.bind(this);

        prefs = new SecurePreferences(this);
        db = new DatabaseHelper(this);

        toolbar.setTitle(prefs.getString(AppVariable.CURRENT_COURSE_NAME, "Present list"));
        setSupportActionBar(toolbar);

        attendanceID = prefs.getInt(AppVariable.CURRENT_ATTENDANCE, 0);
        classID = prefs.getInt(AppVariable.CURRENT_CLASS_ID, 0);
        courseID = prefs.getInt(AppVariable.CURRENT_COURSE_ID, 0);
        token = prefs.getString(AppVariable.USER_TOKEN, null);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        progressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);

        setListViewAdapter();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_class_management_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_instruction:
                showDialogInstruction();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //UI
    private void setListViewAdapter() {
        list = db.getStudentForManagementClass(attendanceID);
        listView.setAdapter(new listviewAdapter(list, this, 0, 0));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                count_aq = (TextView) view.findViewById(R.id.count_aq);

                count_d = (TextView) view.findViewById(R.id.count_d);

                count_p = (TextView) view.findViewById(R.id.count_p);

                showDialogInteraction(position);
            }
        });
    }

    private void showDialogInteraction(int pos){
        stud_id = list.get(pos).iID;
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.choose_action, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final Button _callAnswerQuestion = (Button) promptsView
                .findViewById(R.id.call_answer_question);

        final Button _callDiscussion = (Button) promptsView
                .findViewById(R.id.call_discuss);

        final Button _callPresentation = (Button) promptsView
                .findViewById(R.id.call_present);

        final TextView _close = (TextView) promptsView
                .findViewById(R.id.close_btn);

        // set dialog message

        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

        _callAnswerQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                callAnswerQuestion();
            }
        });

        _callDiscussion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                callDiscussion();
            }
        });

        _callPresentation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                callPresentation();
            }
        });

        _close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    private void showDialogInstruction(){
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.show_instruction, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final TextView _close_instruction = (TextView) promptsView
                .findViewById(R.id.close_instruction_btn);

        // set dialog message

        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

        _close_instruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    //Function
    private void callAnswerQuestion(){
        new UpdateStudentInteractionTask().execute(String.valueOf(AppVariable.ANSWER_QUESTION_FLAG));
    }

    private void callDiscussion(){
        new UpdateStudentInteractionTask().execute(String.valueOf(AppVariable.DISCUSSION_FLAG));
    }

    private void callPresentation(){
        new UpdateStudentInteractionTask().execute(String.valueOf(AppVariable.PRESENTATION_FLAG));
    }

    private void displayToast(String toast) {
        if(toast != null) {
            Toast.makeText(this, toast, Toast.LENGTH_LONG).show();
        }
    }

    //Network task
    private class UpdateStudentInteractionTask extends AsyncTask<String, Void, Integer> {
        private String strJsonResponse;
        private String type;

        @Override
        protected void onPreExecute(){
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                URL url = new URL(Network.API_UPDATE_INTERACTION);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                try {
                    //prepare json data
                    JSONObject jsonUserData = new JSONObject();
                    jsonUserData.put("token", token);
                    jsonUserData.put("interaction_type", params[0]);
                    jsonUserData.put("id", stud_id);
                    jsonUserData.put("class_id", classID);
                    jsonUserData.put("course_id", courseID);

                    type = params[0];

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

                            return HttpURLConnection.HTTP_OK;
                        default:
                            Log.d("error1", String.valueOf(status));
                            return 0;
                    }
                }
                finally{
                    connection.disconnect();
                }
            }
            catch(Exception e) {
                Log.d("error2", e.getMessage());
                return 0;
            }
        }

        @Override
        protected void onPostExecute(Integer status) {
            if (status != HttpURLConnection.HTTP_OK){
                progressDialog.dismiss();
                displayToast("No connection");
            }
            else {
                try{
                    JSONObject jsonObject = new JSONObject(strJsonResponse);
                    String result = jsonObject.getString("result");
                    if (result.equals("failure")){
                        progressDialog.dismiss();
                        displayToast("Server error");
                        return;
                    }

                    int int_type = Integer.valueOf(type);
                    String count;
                    Integer plusone;
                    switch (int_type){
                        case AppVariable.ANSWER_QUESTION_FLAG:
                            count = count_aq.getText().toString();
                            plusone = Integer.valueOf(count) + 1;
                            count_aq.setText(String.valueOf(plusone));
                            break;
                        case AppVariable.DISCUSSION_FLAG:
                            count = count_d.getText().toString();
                            plusone = Integer.valueOf(count) + 1;
                            count_d.setText(String.valueOf(plusone));
                            break;
                        case AppVariable.PRESENTATION_FLAG:
                            count = count_p.getText().toString();
                            plusone = Integer.valueOf(count) + 1;
                            count_p.setText(String.valueOf(plusone));
                            break;
                    }

                    db.updateClassManagement(stud_id, attendanceID, int_type);
                    progressDialog.dismiss();
                    displayToast("Updated data");

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
            }
        }
    }
}
