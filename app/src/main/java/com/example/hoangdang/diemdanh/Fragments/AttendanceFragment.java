package com.example.hoangdang.diemdanh.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hoangdang.diemdanh.CurrentSession.CurrentSessionActivity;
import com.example.hoangdang.diemdanh.QRCode.QRCodeActivity;
import com.example.hoangdang.diemdanh.R;
import com.example.hoangdang.diemdanh.ScanQRActivity;
import com.example.hoangdang.diemdanh.SupportClass.AppVariable;
import com.example.hoangdang.diemdanh.SupportClass.Course;
import com.example.hoangdang.diemdanh.SupportClass.DatabaseHelper;
import com.example.hoangdang.diemdanh.SupportClass.Network;
import com.example.hoangdang.diemdanh.SupportClass.SecurePreferences;
import com.example.hoangdang.diemdanh.SupportClass.Student;
import com.example.hoangdang.diemdanh.classManagement.classManagementActivity;
import com.example.hoangdang.diemdanh.currentSessionImage.ApiAdapter;
import com.example.hoangdang.diemdanh.currentSessionImage.CurrentSessionImageActivity;
import com.example.hoangdang.diemdanh.currentSessionImage.GPSTracker;
import com.example.hoangdang.diemdanh.currentSessionImage.MyFaceDetect;
import com.example.hoangdang.diemdanh.currentSessionImage.VolleyCallBack;
import com.example.hoangdang.diemdanh.studentQuiz.StudentQuizActivity;
import com.example.hoangdang.diemdanh.teacherQuiz.TeacherQuizActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

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
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.example.hoangdang.diemdanh.currentSessionImage.MyFaceDetect.GET_GPS;
import static com.example.hoangdang.diemdanh.currentSessionImage.MyFaceDetect.removeAccent;

public class AttendanceFragment extends Fragment {
    @BindView(R.id.use_checklist_button)
    Button _use_checklistButton;

    @BindView(R.id.use_quiz_button)
    Button _use_quizButton;

    @BindView(R.id.use_face_rec_button)
    Button _use_face_recButton;

    @BindView(R.id.use_qr_button)
    Button _use_QRButton;

    @BindView(R.id.cancel_attendance)
    Button _cancel_button;

    @BindView(R.id.finish_attendance)
    Button _finish_button;

    @BindView(R.id.gridView_current_session)
    GridView _current_sessionGrip;

    @BindView(R.id.qllh_ll)
    LinearLayout _qllh_ll;

    @BindView(R.id.use_qllh_button)
    Button _use_qllh_button;

    @BindView(R.id.statistic_ll)
    LinearLayout _statistic_title;

    DatabaseHelper db;

    Socket socket;
    int totalStudent;
    int attendanceID;
    public static int user_role;
    public SharedPreferences prefs;
    boolean isOffline;

    ProgressDialog progressDialog;

    private OnFragmentInteractionListener mListener;

    public AttendanceFragment() {
        setHasOptionsMenu(true);
    }

    public static AttendanceFragment newInstance(String param1, String param2) {
        AttendanceFragment fragment = new AttendanceFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        prefs = new SecurePreferences(getActivity());

        user_role = prefs.getInt(AppVariable.USER_ROLE, 0);
        attendanceID = prefs.getInt(AppVariable.CURRENT_ATTENDANCE, 0);
        if (prefs.getInt(AppVariable.CURRENT_IS_OFFLINE, 0) == 0){
            isOffline = false;
        }

        if (attendanceID == 0 && user_role == AppVariable.TEACHER_ROLE){
            showChooseCourseFragment();
        }

        db = new DatabaseHelper(getActivity());

        if (user_role == AppVariable.TEACHER_ROLE){
            getActivity().setTitle(prefs.getString(AppVariable.CURRENT_COURSE_NAME, "EMPTY"));
            setSocket();
        }
        else {
            getActivity().setTitle("ATTENDANCE");
        }

        totalStudent = db.getTotalStudentOfClass(prefs.getInt(AppVariable.CURRENT_CLASS_HAS_COURSE_ID, 0));

        // prepare spinner
        progressDialog = new ProgressDialog(getActivity(), R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_attendance, container, false);
        ButterKnife.bind(this, view);

        if (user_role == AppVariable.TEACHER_ROLE){
            setCurrentSessionAdapter();
            _use_quizButton.setVisibility(View.GONE);
        }
        else {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)_use_checklistButton.getLayoutParams();
            params.setMargins(params.leftMargin, 50, params.rightMargin, params.bottomMargin); //left, top, right, bottom
            _use_checklistButton.setLayoutParams(params);
            _current_sessionGrip.setVisibility(View.GONE);
            _statistic_title.setVisibility(View.GONE);
        }

        setupButtonListener();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (user_role == AppVariable.TEACHER_ROLE){
            if (prefs.getInt(AppVariable.CURRENT_ATTENDANCE, 0) == 0){
                showChooseCourseFragment();
            }
            setCurrentSessionAdapter();
            socket.connect();
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
        if (socket != null){
            socket.disconnect();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                displayToast("Cancelled");
            } else {
                SharedPreferences pref = new SecurePreferences(getActivity());
                new SentAttendanceQRTask().execute(pref.getString(AppVariable.USER_TOKEN, null),result.getContents(), AppVariable.QR_FLAG);
                //displayToast(result.getContents());
            }
        }

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    // UI

    private void setupButtonListener() {
        if (user_role == AppVariable.TEACHER_ROLE) {
            _use_quizButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (socket != null){
                        socket.disconnect();
                    }
                    if (isOnline()){
                        startActivity(new Intent(getContext(), TeacherQuizActivity.class));
                    }
                    else {
                        displayToast("No connection");
                    }
                }
            });

            _use_checklistButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (socket != null){
                        socket.disconnect();
                    }
                    //startActivity(new Intent(getContext(), CurrentSessionActivity.class));
                    Log.d("Hiep","Testinggggggg");
                    startActivity(new Intent(getContext(), CurrentSessionImageActivity.class));
                }
            });

            _use_QRButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (socket != null){
                        socket.disconnect();
                    }
                    if (isOnline()){
                        startActivity(new Intent(getContext(), QRCodeActivity.class));
                    }
                    else {
                        displayToast("No connection");
                    }
                }
            });

            _use_qllh_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isOnline()){
                        startActivity(new Intent(getContext(), classManagementActivity.class));
                    }
                    else {
                        displayToast("No connection");
                    }
                }
            });

            _cancel_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Cancel attendance?")
                            .setMessage("All data will be deleted.")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    SharedPreferences prefs = new SecurePreferences(getActivity());
                                    // TODO: if ofline
                                    if (Network.isOnline(getActivity())) {
                                        socket.disconnect();
                                        new CancelTask().execute(
                                                prefs.getString(AppVariable.USER_TOKEN, null),
                                                prefs.getString(AppVariable.CURRENT_ATTENDANCE, null));

                                        JSONObject obj = new JSONObject();
                                        try {
                                            obj.put("class_id", prefs.getString(AppVariable.CURRENT_CLASS_ID, null));
                                            obj.put("course_id", prefs.getString(AppVariable.CURRENT_COURSE_ID, null));
                                            obj.put("message", "Canceled by " + prefs.getString(AppVariable.USER_NAME, null));
                                            socket.emit("checkAttendanceStopped", obj);
                                            socket.disconnect();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        new DatabaseHelper(getActivity()).updateCourseStatus(prefs.getString(AppVariable.CURRENT_CLASS_HAS_COURSE_ID, null),0, 0);

                                        prefs.edit()
                                                .putInt(AppVariable.CURRENT_CLASS_HAS_COURSE_ID, 0)
                                                .putString(AppVariable.CURRENT_COURSE_NAME, null)
                                                .putInt(AppVariable.CURRENT_ATTENDANCE, 0)
                                                .putInt(AppVariable.CURRENT_CLASS_ID, 0)
                                                .putInt(AppVariable.CURRENT_COURSE_ID, 0)
                                                .apply();

                                        ChooseCourseFragment chooseCourseFragment = new ChooseCourseFragment();
                                        getActivity().getSupportFragmentManager().beginTransaction()
                                                .remove(AttendanceFragment.this)
                                                .replace(R.id.content_frame, chooseCourseFragment)
                                                .commit();
                                    } else {
                                        //todo: ofline
                                        DatabaseHelper db = new DatabaseHelper(getActivity());
                                        db.updateCourseStatus(prefs.getString(AppVariable.CURRENT_CLASS_HAS_COURSE_ID, null),0, 0);
                                        db.removeAttendanceData(prefs.getInt(AppVariable.CURRENT_ATTENDANCE, 0));
                                        db.removeSyncTask(prefs.getInt(AppVariable.CURRENT_CLASS_ID, 0), prefs.getInt(AppVariable.CURRENT_COURSE_ID, 0));
                                        prefs.edit()
                                                .putInt(AppVariable.CURRENT_CLASS_HAS_COURSE_ID, 0)
                                                .putString(AppVariable.CURRENT_COURSE_NAME, null)
                                                .putInt(AppVariable.CURRENT_ATTENDANCE, 0)
                                                .putInt(AppVariable.CURRENT_CLASS_ID, 0)
                                                .putInt(AppVariable.CURRENT_COURSE_ID, 0)
                                                .putInt(AppVariable.CURRENT_IS_OFFLINE, 0)
                                                .apply();

                                        ChooseCourseFragment chooseCourseFragment = new ChooseCourseFragment();
                                        getActivity().getSupportFragmentManager().beginTransaction()
                                                .remove(AttendanceFragment.this)
                                                .replace(R.id.content_frame, chooseCourseFragment)
                                                .commit();
                                    }

                                }
                            }).setNegativeButton("No", null).show();
                }
            });

            _finish_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Finish attendance?")
                            .setMessage("This attendance can not be open again.")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    SharedPreferences prefs = new SecurePreferences(getActivity());
                                    //TODO: if offline
                                    if (Network.isOnline(getActivity())) {
                                        socket.disconnect();
                                        JSONObject obj = new JSONObject();

                                        new FinishTask().execute(
                                                prefs.getString(AppVariable.USER_TOKEN, null),
                                                prefs.getString(AppVariable.CURRENT_ATTENDANCE, null));

                                        try {
                                            obj.put("class_id", prefs.getString(AppVariable.CURRENT_CLASS_ID, null));
                                            obj.put("course_id", prefs.getString(AppVariable.CURRENT_COURSE_ID, null));
                                            obj.put("message", "Stopped by " + prefs.getString(AppVariable.USER_NAME, null));
                                            socket.emit("checkAttendanceStopped", obj);
                                            socket.disconnect();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        new DatabaseHelper(getActivity()).updateCourseStatus(prefs.getString(AppVariable.CURRENT_CLASS_HAS_COURSE_ID, null),0, 0);

                                        prefs.edit()
                                                .putInt(AppVariable.CURRENT_CLASS_HAS_COURSE_ID, 0)
                                                .putString(AppVariable.CURRENT_COURSE_NAME, null)
                                                .putInt(AppVariable.CURRENT_ATTENDANCE, 0)
                                                .putInt(AppVariable.CURRENT_CLASS_ID, 0)
                                                .putInt(AppVariable.CURRENT_COURSE_ID, 0)
                                                .apply();

                                        ChooseCourseFragment chooseCourseFragment = new ChooseCourseFragment();
                                        getActivity().getSupportFragmentManager().beginTransaction()
                                                .remove(AttendanceFragment.this)
                                                .replace(R.id.content_frame, chooseCourseFragment)
                                                .commit();
                                    }

                                }
                            }).setNegativeButton("No", null).show();
                }
            });
        }
        else {
            _use_QRButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isOnline()){
                        scanFromFragment();
                    }
                    else {
                        displayToast("No connection");
                    }
                }
            });

            _use_checklistButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isOnline()){
                        showInputDelegateDialog();
                    }
                    else {
                        displayToast("No connection");
                    }
                }
            });

            _use_quizButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isOnline()){
                        prefs.edit()
                                .putInt(AppVariable.QUIZ_CORRECT, 0)
                                .putInt(AppVariable.QUIZ_BLANK, 0)
                                .putInt(AppVariable.QUIZ_INDEX, -1).apply();
                        showInputQuizCodeDialog();
                        //startActivity(new Intent(getContext(), StudentQuizActivity.class));
                    }
                    else {
                        displayToast("No connection");
                    }
                }
            });

            _cancel_button.setVisibility(View.GONE);
            _finish_button.setVisibility(View.GONE);
            _qllh_ll.setVisibility(View.GONE);
        }
    }

    private void setCurrentSessionAdapter() {
        int absenceNumber = db.getNumberStudentOfClassByAttendance(attendanceID, AppVariable.ABSENCE_STATUS);
        int attendanceNumber = db.getNumberStudentOfClassByAttendance(attendanceID, AppVariable.ATTENDANCE_STATUS);

        _current_sessionGrip.setAdapter(new CurrentSessionAdapter(new int[] {totalStudent, attendanceNumber, absenceNumber}, getActivity()));
    }

    private void scanFromFragment() {
        IntentIntegrator scannerIntegrator = IntentIntegrator.forSupportFragment(AttendanceFragment.this);
        scannerIntegrator.setCaptureActivity(ScanQRActivity.class);
        scannerIntegrator.setPrompt("");
        scannerIntegrator.setOrientationLocked(false);
        scannerIntegrator.setBeepEnabled(true);
        scannerIntegrator.initiateScan();
    }

    private void showChooseCourseFragment() {
        SharedPreferences prefs = new SecurePreferences(getActivity());

        new DatabaseHelper(getActivity()).updateCourseStatus(prefs.getString(AppVariable.CURRENT_CLASS_HAS_COURSE_ID, null),0, 0);

        prefs.edit()
                .putInt(AppVariable.CURRENT_CLASS_HAS_COURSE_ID, 0)
                .putString(AppVariable.CURRENT_COURSE_NAME, null)
                .putInt(AppVariable.CURRENT_ATTENDANCE, 0)
                .putInt(AppVariable.CURRENT_CLASS_ID, 0)
                .putInt(AppVariable.CURRENT_COURSE_ID, 0)
                .apply();

        ChooseCourseFragment chooseCourseFragment = new ChooseCourseFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .remove(AttendanceFragment.this)
                .replace(R.id.content_frame, chooseCourseFragment)
                .commit();
    }

    private void showInputDelegateDialog() {
        LayoutInflater li = LayoutInflater.from(getContext());
        View promptsView = li.inflate(R.layout.input_delegate, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getContext());

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText delegate = (EditText) promptsView
                .findViewById(R.id.delegate_code_editText);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Submit",
                        new DialogInterface.OnClickListener() {
                            SharedPreferences pref = new SecurePreferences(getActivity());

                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                                new SubmitDelegateCodeTask().execute(pref.getString(AppVariable.USER_TOKEN, null), delegate.getText().toString().toUpperCase());
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void showInputQuizCodeDialog() {
        LayoutInflater li = LayoutInflater.from(getContext());
        View promptsView = li.inflate(R.layout.input_delegate, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getContext());

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText delegate = (EditText) promptsView
                .findViewById(R.id.delegate_code_editText);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Submit",
                        new DialogInterface.OnClickListener() {
                            SharedPreferences pref = new SecurePreferences(getActivity());

                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                                new SubmitQuizCodeTask().execute(pref.getString(AppVariable.USER_TOKEN, null), delegate.getText().toString());
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void showClosedDialog(String mes) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity())
                .setTitle("Attendance stopped")
                .setMessage(mes)
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showChooseCourseFragment();
                    }
                });


        new Thread() {
            public void run() {
                getActivity().runOnUiThread(new Runnable(){

                    @Override
                    public void run(){
                        dialog.show();
                    }
                });
            }
        }.start();
    }

    // Function

    private boolean isOnline(){
        return Network.isOnline(getActivity());
    }

    private void displayToast(String toast) {
        if(getActivity() != null && toast != null) {
            Toast.makeText(getActivity(), toast, Toast.LENGTH_LONG).show();
        }
    }

    private void saveStudentData(ArrayList<Student> students) {
        DatabaseHelper db = new DatabaseHelper(getActivity());
        SharedPreferences pref = new SecurePreferences(getActivity());
        db.addStudent(students,
                pref.getInt(AppVariable.CURRENT_ATTENDANCE, 0),
                pref.getInt(AppVariable.CURRENT_CLASS_HAS_COURSE_ID, 0));
    }

    // Network task

    private class CancelTask extends AsyncTask<String, Void, Integer> {

        private Exception exception;
        private String strJsonResponse;
        private String id;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Integer doInBackground(String... params) {
            int flag = 0;
            try {
                URL url = new URL(Network.API_CANCEL_ATTEND);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                try {
                    //prepare json data
                    JSONObject jsonUserData = new JSONObject();
                    jsonUserData.put("token", params[0]);
                    jsonUserData.put("attendance_id", params[1]);

                    id = params[1];

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
                //makeSyncTask(id, 0);
            }
            else {
                try{
                    JSONObject jsonObject = new JSONObject(strJsonResponse);
                    String result = jsonObject.getString("result");
                    if (result.equals("failure")){
                        //makeSyncTask(id, 0);
                    }

                    db.removeAttendanceData(attendanceID);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class FinishTask extends AsyncTask<String, Void, Integer> {

        private Exception exception;
        private String strJsonResponse;
        private String id;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Integer doInBackground(String... params) {
            int flag = 0;
            try {
                URL url = new URL(Network.API_FINISH_ATTEND);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                try {
                    //prepare json data
                    JSONObject jsonUserData = new JSONObject();
                    jsonUserData.put("token", params[0]);
                    jsonUserData.put("attendance_id", params[1]);

                    id = params[1];

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
                                sb.append(line+"\n");
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
                //makeSyncTask(id, 1);
            }
            else {
                try{
                    JSONObject jsonObject = new JSONObject(strJsonResponse);
                    String result = jsonObject.getString("result");
                    if (result.equals("failure")){
                        //makeSyncTask(id, 1);
                    }

                    db.removeAttendanceData(attendanceID);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class SentAttendanceQRTask extends AsyncTask<String, Void, Integer> {

        private Exception exception;
        private String strJsonResponse;
        private String id;

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Sending...");
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(String... params) {
            int flag = 0;
            try {
                URL url;
                int a = params[1].indexOf("localhost/");
                int b = a + 23;
                String c = Network.HOST + params[1].substring(b);

                url = new URL(params[1]);

                /*if(params[1].equals(AppVariable.QR_FLAG)){
                    url = new URL(params[1]);
                }
                else {
                    url = new URL(Network.API_ATTENDANCE_FACER);
                }*/

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                try {
                    //prepare json data
                    JSONObject jsonUserData = new JSONObject();
                    jsonUserData.put("token", params[0]);

                    id = params[1];

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
                displayToast("Server error");
                progressDialog.dismiss();
            }
            else {
                try{
                    JSONObject jsonObject = new JSONObject(strJsonResponse);
                    String result = jsonObject.getString("result");
                    if (result.equals("failure")){
                        String message = jsonObject.getString("message");
                        displayToast(message);
                        progressDialog.dismiss();
                    }
                    else {
                        displayToast("Done");
                        SharedPreferences pref = new SecurePreferences(getContext());

                        // Get GPS
                        Intent intent = new Intent(getContext(), GPSTracker.class);
                        startActivityForResult(intent, GET_GPS);
                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
                        String mylat = sharedPref.getString("Lat","Error");
                        String mylong = sharedPref.getString("Long","Error");
                        Date currentTime = Calendar.getInstance().getTime();
                        String log = "Toa do: (" + mylat + "," + mylong +") " + " - Method: QRCode" + " - Time: " + currentTime +"$";
                        Log.wtf("HiepQR",log );
                        //Update Log
                        final ApiAdapter apiAdapter = new ApiAdapter(getContext());
                        apiAdapter.UpdateLog(getContext(),log, removeAccent( pref.getString(AppVariable.USER_NAME, "EMPTY")), new VolleyCallBack() {
                            @Override
                            public void onSuccess(String result) {

                            }
                        });
                        progressDialog.dismiss();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    displayToast(e.getMessage());
                }
            }
        }
    }

    private class SubmitDelegateCodeTask extends AsyncTask<String, Void, Integer> {

        private Exception exception;
        private String strJsonResponse;
        private String id;

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Checking...");
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(String... params) {
            int flag = 0;
            try {
                URL url = new URL(Network.API_CHECK_DELEGATE_CODE);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                try {
                    //prepare json data
                    JSONObject jsonUserData = new JSONObject();
                    jsonUserData.put("token", params[0]);
                    jsonUserData.put("code", params[1]);

                    id = params[1];

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
                progressDialog.dismiss();
                displayToast("Server error");
            }
            else {
                try{
                    JSONObject jsonObject = new JSONObject(strJsonResponse);
                    String result = jsonObject.getString("result");
                    if (result.equals("failure")){
                        displayToast("Code not correct");
                        progressDialog.dismiss();
                    }
                    else {
                        JSONObject delegate_detail = jsonObject.getJSONObject("delegate_detail");
                        int course_id = delegate_detail.getInt("course_id");
                        int class_id = delegate_detail.getInt("class_id");
                        int attendance_id = delegate_detail.getInt("attendance_id");
                        Course course = db.getCourse(course_id, class_id);
                        SharedPreferences pref = new SecurePreferences(getActivity());
                        pref.edit()
                                .putInt(AppVariable.CURRENT_CLASS_HAS_COURSE_ID, course.chcid)
                                .putString(AppVariable.CURRENT_COURSE_NAME, course.strName)
                                .putInt(AppVariable.CURRENT_COURSE_ID, course_id)
                                .putInt(AppVariable.CURRENT_CLASS_ID, class_id)
                                .putInt(AppVariable.CURRENT_ATTENDANCE, attendance_id)
                                .apply();

                        new GetStudentTask().execute(
                                pref.getString(AppVariable.USER_TOKEN, null),
                                String.valueOf(attendance_id));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    displayToast(e.getMessage());
                }
            }
        }
    }

    private class SubmitQuizCodeTask extends AsyncTask<String, Void, Integer> {

        private Exception exception;
        private String strJsonResponse;
        private String quiz_code;

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Checking...");
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(String... params) {
            int flag = 0;
            try {
                URL url = new URL(Network.API_CHECK_QUIZ_CODE);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                try {
                    //prepare json data
                    JSONObject jsonUserData = new JSONObject();
                    jsonUserData.put("token", params[0]);
                    jsonUserData.put("code", params[1]);

                    quiz_code = params[1];

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
                progressDialog.dismiss();
                displayToast("Server error");
            }
            else {
                try{
                    JSONObject jsonObject = new JSONObject(strJsonResponse);
                    String result = jsonObject.getString("result");

                    if (result.equals("failure")){
                        String message = jsonObject.getString("message");
                        displayToast(message);
                        progressDialog.dismiss();
                    }
                    else {
                        SharedPreferences pref = new SecurePreferences(getActivity());
                        pref.edit()
                                .putString(AppVariable.QUIZ_CODE, quiz_code)
                                .apply();

                        progressDialog.dismiss();
                        startActivity(new Intent(getContext(), StudentQuizActivity.class));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    displayToast(e.getMessage());
                }
            }
        }
    }

    private class GetStudentTask extends AsyncTask<String, Void, Integer> {

        private Exception exception;
        private String strJsonResponse;

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Loading...");
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
                    progressDialog.dismiss();
                    //startActivity(new Intent(getContext(), CurrentSessionActivity.class));
                    startActivity(new Intent(getContext(), CurrentSessionImageActivity.class));
                    return;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            progressDialog.dismiss();
        }
    }

    private class UpdateStudentTask extends AsyncTask<String, Void, Integer> {
        private Exception exception;
        private String strJsonResponse;

        @Override
        protected void onPreExecute() {}

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
            }
            else {
                try{
                    JSONObject jsonObject = new JSONObject(strJsonResponse);
                    String result = jsonObject.getString("result");
                    if (result.equals("failure")){
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
                    setCurrentSessionAdapter();
                    return;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Socket

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

            }).on("checkAttendanceUpdated", new Emitter.Listener() {

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
                            new UpdateStudentTask().execute(prefs.getString(AppVariable.USER_TOKEN, null), prefs.getString(AppVariable.CURRENT_ATTENDANCE, null));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
                            socket.disconnect();
                            showClosedDialog(obj.getString("message"));
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
