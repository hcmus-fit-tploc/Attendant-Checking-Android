package com.example.hoangdang.diemdanh.CurrentSession;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ListAbsenStudentCurrentSessionFragment extends Fragment {
    @BindView(R.id.current_session_listView) ListView listView;
    ArrayList<Student> list;
    int class_id;
    int course_id;
    Socket socket;

    public ListAbsenStudentCurrentSessionFragment(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SharedPreferences prefs = new SecurePreferences(getActivity());
        class_id = prefs.getInt(AppVariable.CURRENT_CLASS_ID, 0);
        course_id = prefs.getInt(AppVariable.CURRENT_COURSE_ID, 0);
        socket = ((CurrentSessionActivity)this.getActivity()).getSocket();

        socket.on("checkAttendanceUpdated", new Emitter.Listener() {

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

        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_student_current_session, container,false);

        ButterKnife.bind(this, view);

        setListViewAdapter();

        return view;
    }

    public void setListViewAdapter(){
        DatabaseHelper db = new DatabaseHelper(getActivity());
        list = db.getStudentByAttendanceStatus(((CurrentSessionActivity)this.getActivity()).getAttendanceID(), AppVariable.ABSENCE_STATUS);

        listView.setAdapter(new ListAbsenStudentCurrentSessionAdapter(list, getActivity(), class_id, course_id, ((CurrentSessionActivity) this.getActivity()).getSocket()));
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) {
            setListViewAdapter();
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
                    setListViewAdapter();
                    return;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void saveStudentData(ArrayList<Student> students) {
        DatabaseHelper db = new DatabaseHelper(getActivity());
        SharedPreferences pref = new SecurePreferences(getActivity());
        db.addStudent(students,
                pref.getInt(AppVariable.CURRENT_ATTENDANCE, 0),
                pref.getInt(AppVariable.CURRENT_CLASS_HAS_COURSE_ID, 0));
    }
}

