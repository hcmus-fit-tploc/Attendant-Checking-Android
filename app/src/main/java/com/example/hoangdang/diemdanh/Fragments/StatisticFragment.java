package com.example.hoangdang.diemdanh.Fragments;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hoangdang.diemdanh.MainActivity;
import com.example.hoangdang.diemdanh.R;
import com.example.hoangdang.diemdanh.SupportClass.AppVariable;
import com.example.hoangdang.diemdanh.SupportClass.AttendanceDetail;
import com.example.hoangdang.diemdanh.SupportClass.Course;
import com.example.hoangdang.diemdanh.SupportClass.CourseStatistic;
import com.example.hoangdang.diemdanh.SupportClass.DatabaseHelper;
import com.example.hoangdang.diemdanh.SupportClass.Network;
import com.example.hoangdang.diemdanh.SupportClass.SecurePreferences;
import com.example.hoangdang.diemdanh.SupportClass.SyncTask;
import com.example.hoangdang.diemdanh.SupportClass.User;
import com.example.hoangdang.diemdanh.courseAttendanceDetail.AttendanceDetailActivity;
import com.example.hoangdang.diemdanh.feedback.FeedbackHistoryActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

public class StatisticFragment extends Fragment {

    String token;
    String user_id;

    DatabaseHelper db;
    ProgressDialog progressDialog;
    SharedPreferences pref;

    ArrayList<CourseStatistic> list;

    @BindView(R.id.choose_course_student_listView)
    ListView _choose_course_listView;

    public StatisticFragment() {}

    public static StatisticFragment newInstance(String param1, String param2) {
        StatisticFragment fragment = new StatisticFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("STATISTIC");

        // prepare spinner
        progressDialog = new ProgressDialog(getActivity(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);

        pref = new SecurePreferences(getActivity());

        user_id = pref.getString(AppVariable.USER_ID, null);
        token = pref.getString(AppVariable.USER_TOKEN, null);

        db = new DatabaseHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistic, container, false);

        ButterKnife.bind(this, view);

        new GetCoursesTask().execute();
        return view;
    }

    public void onButtonPressed(Uri uri) {}

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    //UI

    private void buildUI(){
        setListViewAdapter();

        setButtonListener();
    }

    private void setButtonListener(){
        _choose_course_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity(),AttendanceDetailActivity.class);
                i.putExtra("DATA_AD", list.get(position).attendanceDetails);
                startActivity(i);
            }
        });
    }

    private void setListViewAdapter() {
        _choose_course_listView.setAdapter(new CourseListStudentAdapter(list, getActivity()));
    }

    private void displayToast(String toast) {
        if(getActivity() != null && toast != null) {
            Toast.makeText(getActivity(), toast, Toast.LENGTH_LONG).show();
        }
    }

    //Network task

    private class GetCoursesTask extends AsyncTask<String, Void, Integer> {
        private String strJsonResponse;

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Retrieving data...");
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                URL url;
                //prepare json data
                JSONObject jsonUserData = new JSONObject();
                jsonUserData.put("token", token);
                jsonUserData.put("student_id", user_id);

                url = new URL(Network.API_STUDENT_STATS);

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
                            return 0;
                    }
                }
                finally{
                    connection.disconnect();
                }
            }
            catch(Exception e) {
                return 0;
            }
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
                        progressDialog.dismiss();
                        displayToast("Wrong Request");
                    }
                    else {
                        //int length = Integer.valueOf(jsonObject.getString("total_items"));
                        JSONArray coursesJson = jsonObject.getJSONArray("attendance_list_by_student");
                        int length = coursesJson.length();

                        list = new ArrayList<>();
                        for (int i = 0; i < length; i++){
                            JSONObject courseDetail = coursesJson.getJSONObject(i);
                            CourseStatistic courseStatistic = new CourseStatistic();

                            courseStatistic.name = courseDetail.getString("name");
                            courseStatistic.code = courseDetail.getString("code");
                            courseStatistic.attendanceCounts = courseDetail.getInt("attendance_count");
                            courseStatistic.absenceCounts = 0;

                            JSONArray attendanceDetails = courseDetail.getJSONArray("attendance_details");
                            int l = attendanceDetails.length();

                            courseStatistic.attendanceDetails = new ArrayList<>();

                            for (int j = 0; j < l; j++){
                                AttendanceDetail attendanceDetail1 = new AttendanceDetail();
                                JSONObject ad = attendanceDetails.getJSONObject(j);
                                attendanceDetail1.created_at = ad.getString("created_at");
                                attendanceDetail1.type = ad.getInt("attendance_type");
                                attendanceDetail1.edit_reason = ad.getString("edited_reason");
                                courseStatistic.attendanceDetails.add(attendanceDetail1);

                                if (attendanceDetail1.type == 0){
                                    courseStatistic.absenceCounts += 1;
                                }
                            }

                            list.add(courseStatistic);
                        }
                        progressDialog.dismiss();
                        buildUI();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    displayToast("Error when reading response in GetOpeningCourseTask");
                }
            }
        }
    }

    //Socket

}
