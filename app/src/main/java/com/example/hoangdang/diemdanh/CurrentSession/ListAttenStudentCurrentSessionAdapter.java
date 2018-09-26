package com.example.hoangdang.diemdanh.CurrentSession;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.hoangdang.diemdanh.R;
import com.example.hoangdang.diemdanh.SupportClass.AppVariable;
import com.example.hoangdang.diemdanh.SupportClass.DatabaseHelper;
import com.example.hoangdang.diemdanh.SupportClass.Network;
import com.example.hoangdang.diemdanh.SupportClass.SecurePreferences;
import com.example.hoangdang.diemdanh.SupportClass.Student;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ListAttenStudentCurrentSessionAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<Student> list = new ArrayList<>();
    private Context context;

    public View view;
    public Button _changeButton;
    Socket socket;
    private JSONObject payload;
    public boolean isOffline;

    public ListAttenStudentCurrentSessionAdapter(ArrayList<Student> list, Context context, int class_id, int course_id, Socket socket){
        this.list = list;
        this.context = context;
        payload = new JSONObject();
        try {
            payload.put("course_id", course_id);
            payload.put("class_id", class_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.socket = socket;
        SharedPreferences prefs = new SecurePreferences(context);
        isOffline = prefs.getInt(AppVariable.CURRENT_IS_OFFLINE, 0) == 1;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).iID;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        view = convertView;
        if( view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.custom_atten_student_current_session, null);
        }

        TextView studName = (TextView)view.findViewById(R.id.current_session_stud_name_textView);
        studName.setText(list.get(position).strName);

        TextView studID = (TextView)view.findViewById(R.id.current_session_stud_id_textView);
        studID.setText(list.get(position).strCode);

        final DatabaseHelper db = new DatabaseHelper(context);

        _changeButton = (Button)view.findViewById(R.id.absence_button);
        _changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int attendance_id = ((CurrentSessionActivity)context).getAttendanceID();
                int student_id = list.get(position).iID;

                db.changeAttendanceStatus(student_id, attendance_id, AppVariable.ABSENCE_STATUS);
                if(Network.isOnline(context) && !isOffline){
                    SharedPreferences pref = new SecurePreferences(context);
                    new SyncTask().execute(pref.getString(AppVariable.USER_TOKEN, null), String.valueOf(attendance_id), String.valueOf(student_id));
                }

                list.remove(position);
                notifyDataSetChanged();
            }
        });

        return view;
    }

    private class SyncTask extends AsyncTask<String, Void, Integer> {

        private Exception exception;
        private String strJsonResponse;

        @Override
        protected void onPreExecute() {}

        @Override
        protected Integer doInBackground(String... params) {
            try {
                URL url = new URL(Network.API_ATTENDANCE_CHECKLIST);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                try {
                    JSONObject jsonUserData = new JSONObject();
                    jsonUserData.put("token", params[0]);
                    jsonUserData.put("student_id", params[2]);
                    jsonUserData.put("attendance_id", params[1]);
                    jsonUserData.put("attendance_type", 0);

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
            socket.emit("checkAttendanceUpdated", payload);
        }
    }

}
