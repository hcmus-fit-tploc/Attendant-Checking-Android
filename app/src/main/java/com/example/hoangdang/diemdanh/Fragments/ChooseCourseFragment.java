package com.example.hoangdang.diemdanh.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hoangdang.diemdanh.R;
import com.example.hoangdang.diemdanh.SupportClass.AppVariable;
import com.example.hoangdang.diemdanh.SupportClass.Course;
import com.example.hoangdang.diemdanh.SupportClass.DatabaseHelper;
import com.example.hoangdang.diemdanh.SupportClass.Network;
import com.example.hoangdang.diemdanh.SupportClass.SecurePreferences;
import com.example.hoangdang.diemdanh.SupportClass.Student;
import com.example.hoangdang.diemdanh.SupportClass.SyncTask;

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

public class ChooseCourseFragment extends Fragment {
    private OnFragmentInteractionListener mListener;

    @BindView(R.id.choose_course_listView)
    ListView _choose_course_listView;

    ProgressDialog progressDialog;
    int user_role;
    Socket socket;
    SharedPreferences pref;
    DatabaseHelper db;

    public ChooseCourseFragment() {}

    public static ChooseCourseFragment newInstance(String param1, String param2) {
        ChooseCourseFragment fragment = new ChooseCourseFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("COURSE LIST");

        // prepare spinner
        progressDialog = new ProgressDialog(getActivity(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);

        pref = new SecurePreferences(getActivity());
        user_role = pref.getInt(AppVariable.USER_ROLE, 0);

        db = new DatabaseHelper(getActivity());

        setSocket();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_course, container, false);

        ButterKnife.bind(this, view);

        setListViewAdapter();

        setButtonListener();

        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        socket.disconnect();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    //UI

    private void setButtonListener(){
        _choose_course_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String courseName = ((TextView)view.findViewById(R.id.choose_course_name_textView)).getText().toString();
                final String class_has_course_id = ((TextView)view.findViewById(R.id.choose_class_has_course_id_textView)).getText().toString();
                final String attendanceID = ((TextView)view.findViewById(R.id.choose_attend_id_textView)).getText().toString();
                final String courseID = ((TextView)view.findViewById(R.id.choose_course_id_textView)).getText().toString();
                final String classID = ((TextView)view.findViewById(R.id.choose_class_id_textView)).getText().toString();

                final SharedPreferences prefs = new SecurePreferences(getActivity());
                prefs.edit()
                        .putInt(AppVariable.CURRENT_CLASS_HAS_COURSE_ID, Integer.valueOf(class_has_course_id))
                        .putString(AppVariable.CURRENT_COURSE_NAME, courseName)
                        .putInt(AppVariable.CURRENT_COURSE_ID, Integer.valueOf(courseID))
                        .putInt(AppVariable.CURRENT_CLASS_ID, Integer.valueOf(classID))
                        .apply();

                if(user_role == AppVariable.TEACHER_ROLE){
                    if (!attendanceID.equals("0")){
                        prefs.edit()
                                .putString(AppVariable.CURRENT_ATTENDANCE, attendanceID)
                                .apply();

                        if (Network.isOnline(getActivity())){
                            new GetStudentTask().execute(
                                prefs.getString(AppVariable.USER_TOKEN, null),
                                String.valueOf(attendanceID));
                        }
                        else {
                            prefs.edit()
                                    .putInt(AppVariable.CURRENT_IS_OFFLINE, 1).apply();
                            showAttendanceFragment();
                        }
                    }
                    else{
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Confirmation ")
                                .setMessage("Open attendance for this course")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //TODO: if offlinenn
                                        if (Network.isOnline(getActivity())){
                                            new CreateAttendIDTask().execute(
                                                    prefs.getString(AppVariable.USER_TOKEN, null),
                                                    classID,
                                                    courseID);
                                        }
                                        else {
                                            int attendanceID = db.innitStudent4Offline(class_has_course_id);
                                            prefs.edit().putInt(AppVariable.CURRENT_ATTENDANCE, attendanceID)
                                                    .putInt(AppVariable.CURRENT_IS_OFFLINE, 1).apply();
                                            SyncTask st = new SyncTask();
                                            st.tt = attendanceID;
                                            st.course_id = Integer.valueOf(courseID);
                                            st.class_id = Integer.valueOf(classID);
                                            db.addSyncTask(st);
                                            showAttendanceFragment();
                                        }
                                    }
                                }).setNegativeButton("No", null).show();
                    }
                }
                else {
                    AttendanceFragment attendanceFragment = new AttendanceFragment();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .remove(ChooseCourseFragment.this)
                            .replace(R.id.content_frame, attendanceFragment)
                            .commit();
                }

            }
        });
    }

    private void setListViewAdapter() {
        ArrayList<Course> listCourse = db.getAllCourse();
        _choose_course_listView.setAdapter(new CourseListAdapter(listCourse, getActivity()));
    }

    private void showAttendanceFragment(){
        if (socket != null){
            socket.disconnect();
        }
        AttendanceFragment attendanceFragment = new AttendanceFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .remove(ChooseCourseFragment.this)
                .replace(R.id.content_frame, attendanceFragment)
                .commit();
    }

    //Functions

    private void saveStudentData(ArrayList<Student> students) {
        DatabaseHelper db = new DatabaseHelper(getActivity());
        SharedPreferences pref = new SecurePreferences(getActivity());
        db.addStudent(students,
                pref.getInt(AppVariable.CURRENT_ATTENDANCE, 0),
                pref.getInt(AppVariable.CURRENT_CLASS_HAS_COURSE_ID, 0));
    }

    //Network task

    private class GetOpeningCourseTask extends AsyncTask<String, Void, Integer> {

        private Exception exception;
        private String strJsonResponse;

        @Override
        protected void onPreExecute() {
/*            progressDialog.setMessage(null);
            progressDialog.show();*/
        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                //prepare json data
                JSONObject jsonUserData = new JSONObject();
                jsonUserData.put("token", params[0]);
                jsonUserData.put("teacher_id", params[1]);
                jsonUserData.put("isMobile", 1);
                URL url = new URL(Network.API_RETRIEVE_OPENING_COURSE);

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
                                line = line + "\n";
                                sb.append(line);
                            }

                            bufferedReader.close();
                            strJsonResponse = sb.toString();

                            return HttpURLConnection.HTTP_OK;
                        default:
                            exception = new Exception(connection.getResponseMessage());
                            return 0;
                    }
                }
                finally{
                    connection.disconnect();
                }
            }
            catch(Exception e) {
                exception = e;
                return 0;
            }
        }

        @Override
        protected void onPostExecute(Integer status) {
            if (status != HttpURLConnection.HTTP_OK){
                progressDialog.dismiss();
            }
            else {
                try{
                    JSONObject jsonObject = new JSONObject(strJsonResponse);
                    String result = jsonObject.getString("result");
                    if (result.equals("failure")){
                        progressDialog.dismiss();
                    }
                    else {
                        int length = Integer.valueOf(jsonObject.getString("length"));
                        JSONArray coursesJson = jsonObject.getJSONArray("opening_attendances");

                        ArrayList<String> courses = new ArrayList<>();
                        ArrayList<String> attendance = new ArrayList<>();
                        for (int i = 0; i < length; i++){
                            courses.add(coursesJson.getJSONObject(i).getString("class_has_course_id"));
                            attendance.add(coursesJson.getJSONObject(i).getString("attendance_id"));
                        }
                        db.updateOpeningCourse(db.getAllCourse(), courses, attendance, 1);
                        setListViewAdapter();
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
            }
        }
    }

    private class CreateAttendIDTask extends AsyncTask<String, Void, Integer> {

        private Exception exception;
        private String strJsonResponse;

        @Override
        protected void onPreExecute() {
            // prepare spinner
            progressDialog.setMessage("Creating...");
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                URL url = new URL(Network.API_CREATE_ATTEND);
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

                            return HttpURLConnection.HTTP_OK;
                        default:
                            exception = new Exception(connection.getResponseMessage());
                            return 0;
                    }
                }
                finally{
                    connection.disconnect();
                }
            }
            catch(Exception e) {
                e.printStackTrace();
                return 0;
            }
        }

        @Override
        protected void onPostExecute(Integer status) {
            if (status != HttpURLConnection.HTTP_OK){
                AppVariable.alert(getActivity(), exception.getMessage());
            }
            else {
                try{
                    JSONObject jsonObject = new JSONObject(strJsonResponse);
                    String result = jsonObject.getString("result");
                    if (result.equals("failure")){
                        progressDialog.dismiss();
                        AppVariable.alert(getActivity(), null);
                        return;
                    }
                    socket.emit("checkAttendanceCreated", new JSONObject());
                    int attendID = Integer.valueOf(jsonObject.getString("attendance_id"));
                    SharedPreferences prefs = new SecurePreferences(getActivity());
                    prefs.edit().putInt(AppVariable.CURRENT_ATTENDANCE, attendID).apply();

                    new DatabaseHelper(getActivity()).updateCourseStatus(prefs.getString(AppVariable.CURRENT_CLASS_HAS_COURSE_ID, null), attendID, 1);

                    new GetStudentTask().execute(
                            prefs.getString(AppVariable.USER_TOKEN, null),
                            String.valueOf(jsonObject.getString("attendance_id")));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            progressDialog.dismiss();
        }
    }

    private class GetStudentTask extends AsyncTask<String, Void, Integer> {

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
                AppVariable.alert(getActivity(), exception.getMessage());
            }
            else {
                try{
                    JSONObject jsonObject = new JSONObject(strJsonResponse);
                    String result = jsonObject.getString("result");
                    if (result.equals("failure")){
                        progressDialog.dismiss();
                        AppVariable.alert(getActivity(), null);
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
                    socket.disconnect();
                    AttendanceFragment attendanceFragment = new AttendanceFragment();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .remove(ChooseCourseFragment.this)
                            .replace(R.id.content_frame, attendanceFragment)
                            .commit();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            progressDialog.dismiss();
        }
    }

    //Socket

    private void setSocket(){
        if (Network.isOnline(getActivity())){
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

            }).on("checkAttendanceCreated", new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    new GetOpeningCourseTask().execute(pref.getString(AppVariable.USER_TOKEN, null), pref.getString(AppVariable.USER_ID, null));
                }

            }).on("checkAttendanceStopped", new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    new GetOpeningCourseTask().execute(pref.getString(AppVariable.USER_TOKEN, null), pref.getString(AppVariable.USER_ID, null));
                }

            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {}

            });
            socket.connect();
        }
    }
}
