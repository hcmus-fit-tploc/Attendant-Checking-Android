package com.example.hoangdang.diemdanh.CurrentSession;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.hoangdang.diemdanh.R;
import com.example.hoangdang.diemdanh.SupportClass.AppVariable;
import com.example.hoangdang.diemdanh.SupportClass.DatabaseHelper;
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
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class CurrentSessionActivity extends AppCompatActivity {

    @BindView(R.id.current_session_list_toolbar)
    Toolbar toolbar;
    @BindView(R.id.current_session_list_tabs)
    TabLayout tabLayout;
    @BindView(R.id.current_session_list_viewpager)
    ViewPager viewPager;

    public static int courseID;
    public static int attendanceID;
    public static int classHasCourseID;
    public static int user_role;
    public static int classID;
    private String token;
    public static Socket socket;
    public boolean isOffline;
    public DatabaseHelper db;
    public SharedPreferences prefs;
    public ProgressDialog progressDialog;

    Socket getSocket() {
        return socket;
    }

    int getAttendanceID(){
        return attendanceID;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_session);

        ButterKnife.bind(this);

        prefs = new SecurePreferences(this);
        isOffline = prefs.getInt(AppVariable.CURRENT_IS_OFFLINE, 0) == 1;
        token = prefs.getString(AppVariable.USER_TOKEN, null);
        courseID = prefs.getInt(AppVariable.CURRENT_COURSE_ID, 0);
        attendanceID = prefs.getInt(AppVariable.CURRENT_ATTENDANCE, 0);
        classHasCourseID = prefs.getInt(AppVariable.CURRENT_CLASS_HAS_COURSE_ID, 0);
        classID = prefs.getInt(AppVariable.CURRENT_CLASS_ID, 0);
        user_role = prefs.getInt(AppVariable.USER_ROLE, 0);

        toolbar.setTitle(prefs.getString(AppVariable.CURRENT_COURSE_NAME, "Student list"));
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        db = new DatabaseHelper(this);

        progressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);

        // TODO: if offline
        if (Network.isOnline(this) && !isOffline){
            new GetStudentTask().execute(
                    prefs.getString(AppVariable.USER_TOKEN, null),
                    String.valueOf(attendanceID));
            setSocket();
        }
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager){
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (Network.isOnline(this) && !isOffline){
            if (user_role == AppVariable.TEACHER_ROLE){
                commitData();
            }
        }

        if (socket != null){
            socket.disconnect();
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_checklist_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.get_delegate:
                if (Network.isOnline(this) && !isOffline){
                    if (user_role == AppVariable.TEACHER_ROLE){
                        new GetDelegateCodeTask().execute(token, String.valueOf(classID), String.valueOf(courseID));
                    }
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // UI

    private void setViewPager() {
        setViewPagerAdapter(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setViewPagerAdapter(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ListAbsenStudentCurrentSessionFragment(), "Absent");
        adapter.addFragment(new ListAttenStudentCurrentSessionFragment(), "Present");

        viewPager.setAdapter(adapter);
    }

    private void showClosedDialog(String mes) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.DialogeTheme)
                .setTitle("Attendance stopped")
                .setMessage(mes)
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });

        dialog.setCancelable(false);

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });


        new Thread() {
            public void run() {
                CurrentSessionActivity.this.runOnUiThread(new Runnable(){

                    @Override
                    public void run(){
                        dialog.show();
                    }
                });
            }
        }.start();
    }

    private void showDelegateCode(String message){
        AlertDialog.Builder dialog_code = new AlertDialog.Builder(this, R.style.DialogeTheme)
                .setTitle("Delegate code")
                .setMessage(message)
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        dialog_code.setCancelable(false);

        dialog_code.show();
    }

    // Functions

    private void commitData(){
        if (!Network.isOnline(this)){
            // TODO: offline
        }
        else {
            SharedPreferences pref = new SecurePreferences(this);
            new SendingDataTask().execute(pref.getString(AppVariable.USER_TOKEN, null));
        }
    }

    private void saveStudentData(ArrayList<Student> students) {
        DatabaseHelper db = new DatabaseHelper(this);
        SharedPreferences pref = new SecurePreferences(this);
        db.addStudent(students,
                pref.getInt(AppVariable.CURRENT_ATTENDANCE, 0),
                pref.getInt(AppVariable.CURRENT_CLASS_HAS_COURSE_ID, 0));
    }

    // Network task

    private class GetStudentTask extends AsyncTask<String, Void, Integer> {

        private Exception exception;
        private String strJsonResponse;

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(String... params) {
            int flag = 0;
            try {
                URL url = new URL(Network.API_RETRIEVE_STUDENT);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                try {
                    //prepare json data
                    JSONObject jsonUserData = new JSONObject();
                    jsonUserData.put("token", params[0]);
                    jsonUserData.put("attendance_id", params[1]);

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
                AppVariable.alert(getBaseContext(), exception.getMessage());
            }
            else {
                try{
                    JSONObject jsonObject = new JSONObject(strJsonResponse);
                    String result = jsonObject.getString("result");
                    if (result.equals("failure")){
                        progressDialog.dismiss();
                        AppVariable.alert(getBaseContext(), null);
                        return;
                    }

                    int length = Integer.valueOf(jsonObject.getString("length"));
                    JSONArray studentsJson = jsonObject.getJSONArray("check_attendance_list");

                    ArrayList<Student> students = new ArrayList<>();
                    for (int i = 0; i < length; i++){
                        JSONObject s = studentsJson.getJSONObject(i);
                        students.add(new Student(
                                s.getInt("id"),
                                s.getString("code"),
                                s.getString("name"),
                                s.getInt("status"),
                                s.getString("avatar"),
                                s.getString("answered_questions"),
                                s.getString("discussions"),
                                s.getString("presentations")
                        ));
                    }

                    saveStudentData(students);

                    setViewPager();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            progressDialog.dismiss();
        }
    }

    private class SendingDataTask extends AsyncTask<String, Void, Integer> {

        private Exception exception;
        private String strJsonResponse;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Integer doInBackground(String... params) {
            int flag = 0;
            try {
                //prepare json data
                JSONObject jsonUserData = new JSONObject();
                jsonUserData.put("token", params[0]);
                jsonUserData.put("attendance_id", attendanceID);
                jsonUserData.put("data", db.getAttendanceDataInJSON(attendanceID));

                URL url = new URL(Network.API_SEND_STUDENT_ATTENDANCE_DATA);
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
                //makeSyncTask();
            }
            else {
                try{
                    JSONObject jsonObject = new JSONObject(strJsonResponse);
                    String result = jsonObject.getString("result");
                    if (result.equals("failure")){
                        //makeSyncTask();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class GetDelegateCodeTask extends AsyncTask<String, Void, Integer> {

        private Exception exception;
        private String strJsonResponse;

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(String... params) {
            int flag = 0;
            try {
                URL url = new URL(Network.API_GET_DELEGATE_CODE);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                try {
                    //prepare json data
                    JSONObject jsonUserData = new JSONObject();
                    jsonUserData.put("token", params[0]);
                    jsonUserData.put("class_id", params[1]);
                    jsonUserData.put("course_id", params[2]);

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
                AppVariable.alert(getBaseContext(), exception.getMessage());
            }
            else {
                try{
                    JSONObject jsonObject = new JSONObject(strJsonResponse);
                    String result = jsonObject.getString("result");
                    if (result.equals("failure")){
                        progressDialog.dismiss();
                        AppVariable.alert(getBaseContext(), null);
                        return;
                    }

                    String code = String.valueOf(jsonObject.getString("code"));

                    showDelegateCode(code);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            progressDialog.dismiss();
        }
    }

    // Socket

    private void setSocket() {
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

        }).on("checkAttendanceStopped", new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                JSONObject obj = (JSONObject)args[0];
                int course_id;
                int class_id;
                try {
                    course_id = obj.getInt("course_id");
                    class_id = obj.getInt("class_id");

                    if (prefs.getInt(AppVariable.CURRENT_COURSE_ID, 0) == course_id &
                            prefs.getInt(AppVariable.CURRENT_CLASS_ID, 0) == class_id){
                        prefs.edit().putInt(AppVariable.CURRENT_ATTENDANCE, 0).apply();
                        socket.disconnect();
                        showClosedDialog(obj.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
            }

        });
        socket.connect();
    }
}
