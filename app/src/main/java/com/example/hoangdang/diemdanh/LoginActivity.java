package com.example.hoangdang.diemdanh;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hoangdang.diemdanh.SupportClass.AppVariable;
import com.example.hoangdang.diemdanh.SupportClass.Course;
import com.example.hoangdang.diemdanh.SupportClass.DatabaseHelper;
import com.example.hoangdang.diemdanh.SupportClass.Network;
import com.example.hoangdang.diemdanh.SupportClass.User;
import com.example.hoangdang.diemdanh.SupportClass.SecurePreferences;
import com.example.hoangdang.diemdanh.currentSessionImage.ApiAdapter;
import com.example.hoangdang.diemdanh.currentSessionImage.VolleyCallBack;
import com.example.hoangdang.diemdanh.studentQuiz.DetailActivity;

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

import static com.example.hoangdang.diemdanh.currentSessionImage.MyFaceDetect.removeAccent;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.email_editText) EditText _email_editText;
    @BindView(R.id.password_editText) EditText _password_editText;
    @BindView(R.id.login_button) Button _login_button;
    @BindView(R.id.forgot_pw_textView) TextView _forgot_pw_textView;
    @BindView(R.id.setting_host) TextView sethost_textView;

    protected ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(isLoggedIn()){
            startMainActivity();
        }

        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        setupButtonListener();

        // prepare spinner
        progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.setCanceledOnTouchOutside(false);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        String host = sharedPref.getString("baselink","Error");

        if(host.equals("Error")) {
            final ApiAdapter apiAdapter = new ApiAdapter(this);
            apiAdapter.GetBaseURL(this, new VolleyCallBack() {
                @Override
                public void onSuccess(String result) throws JSONException {
                    Log.wtf("HiepTestURL", result);
                    JSONObject response = new JSONObject(result);
                    String link = response.getString("baselink");
                    Log.wtf("HiepBaseLink", link);
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("baselink", link);
                    editor.commit();
                }
            });
        }
        else
        {
            //Toast.makeText(LoginActivity.this,"Host setted ! " + host,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    //UI blinding

    private void setupButtonListener(){
        SharedPreferences pref = new SecurePreferences(this);

        _email_editText.setText(pref.getString(AppVariable.USER_EMAIL, null));

        _login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        _forgot_pw_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(getBaseContext());
                View promptsView = li.inflate(R.layout.input_email, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(LoginActivity.this, R.style.AppTheme_Dialog_Login));

                alertDialogBuilder.setView(promptsView);

                final EditText email = (EditText) promptsView
                        .findViewById(R.id.email_reset_editText);

                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Request",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                        new RequestNewPasswordTask().execute(email.getText().toString());
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                AlertDialog alertDialog = alertDialogBuilder.create();

                alertDialog.show();
            }
        });
        sethost_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SetHostActivity.class);
                startActivity(intent);
            }
        });
    }

    private void startMainActivity(){
        Intent mainActivity = new Intent(this, MainActivity.class);
        startActivity(mainActivity);
        finish();
    }

    //Function

    private boolean validate() {
        boolean valid = true;

        String email = _email_editText.getText().toString();
        String password = _password_editText.getText().toString();

        if (email.isEmpty()) {
            _email_editText.setError("enter a valid username");
            valid = false;
        } else {
            _email_editText.setError(null);
        }

        if (password.isEmpty()) {
            _password_editText.setError("missing password");
            valid = false;
        } else {
            _password_editText.setError(null);
        }

        return valid;
    }

    private boolean isLoggedIn() {
        SharedPreferences prefs = new SecurePreferences(this);
        return prefs.getString(AppVariable.USER_TOKEN, null) != null;
    }

    private void login() {
        if(!Network.isOnline(LoginActivity.this)){
            onLoginFailed("No internet");
            return;
        }

        if (!validate()){
            onLoginFailed("Validation fail");
            return;
        }

        new AuthenticationTask().execute(
                _email_editText.getText().toString(),
                _password_editText.getText().toString());
    }

    private void onLoginSuccess(User user) {
        SharedPreferences prefs = new SecurePreferences(this);
        String id = _email_editText.getText().toString();
        prefs.edit()
                .putString(AppVariable.USER_AUTHENTICATION, _password_editText.getText().toString())
                .putString(AppVariable.USER_AVATAR, user.avatar)
                .putString(AppVariable.USER_EMAIL, id)
                .putString(AppVariable.USER_TOKEN, user.getToken())
                .putInt(AppVariable.USER_ID, user.getID())
                .putInt(AppVariable.USER_ROLE, user.getRole())
                .putString(AppVariable.USER_NAME, user.getLastName() + " " + user.getFirstName())
                .apply();

        DatabaseHelper db = new DatabaseHelper(this);
        db.addUser(user);
    }

    private void onLoginFailed(String strMessage) {
        progressDialog.dismiss();
        Toast.makeText(getBaseContext(), strMessage != null ? strMessage : "Cannot connect to server" , Toast.LENGTH_LONG).show();
    }

    private void saveCourseData(ArrayList<Course> courses) {
        DatabaseHelper db = new DatabaseHelper(this);
        db.addCourse(courses);
    }

    //Network task

    private class AuthenticationTask extends AsyncTask<String, Void, Integer> {

        private Exception exception;
        private String strJsonResponse;

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                URL url = new URL(Network.API_LOGIN);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                try {
                    JSONObject jsonUserData = new JSONObject();
                    jsonUserData.put("username", params[0]);
                    jsonUserData.put("password", params[1]);

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
                onLoginFailed(exception.getMessage());
            }
            else {
                try{
                    JSONObject jsonObject = new JSONObject(strJsonResponse);
                    String result = jsonObject.getString("result");
                    if (result.equals("failure")){
                        progressDialog.dismiss();
                        onLoginFailed("Wrong email or password");
                        return;
                    }

                    JSONObject jsonUserData = new JSONObject(jsonObject.getString("user"));

                    onLoginSuccess(
                            new User(
                                    jsonUserData.getInt("id"),
                                    jsonUserData.getInt("role_id"),
                                    jsonUserData.getString("email"),
                                    jsonObject.getString("token"),
                                    jsonUserData.getString("first_name"),
                                    jsonUserData.getString("last_name"),
                                    jsonUserData.getString("phone"),
                                    jsonUserData.getString("avatar")
                            ));

                    switch (jsonUserData.getInt("role_id")){
                        case AppVariable.STAFF_ROLE:
                            break;
                        case AppVariable.STUDENT_ROLE:
                            new RetrieveStudyingCourseTask().execute(
                                    jsonObject.getString("token"), jsonUserData.getString("id"));
                            break;
                        case AppVariable.TEACHER_ROLE:
                            new RetrieveTeachingCourseTask().execute(
                                    jsonObject.getString("token"), jsonUserData.getString("id"));
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    onLoginFailed(e.getMessage());
                }
            }
        }
    }

    private class RequestNewPasswordTask extends AsyncTask<String, Void, Integer> {
        private String strJsonResponse;

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Sending...");
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                URL url = new URL(Network.API_USER_RESET_PASSWORD);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                try {
                    JSONObject jsonUserData = new JSONObject();
                    jsonUserData.put("email", params[0]);

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
                            Toast.makeText(getBaseContext(), connection.getResponseMessage(), Toast.LENGTH_LONG).show();
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
            progressDialog.dismiss();
            if (status != HttpURLConnection.HTTP_OK){
                Toast.makeText(getBaseContext(), "Server Error", Toast.LENGTH_LONG).show();
            }
            else {
                try{
                    JSONObject jsonObject = new JSONObject(strJsonResponse);
                    String result = jsonObject.getString("result");
                    if (result.equals("failure")){
                        Toast.makeText(getBaseContext(), "Wrong Email", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(getBaseContext(), "Check your email", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getBaseContext(), "Server Error", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private class RetrieveTeachingCourseTask extends AsyncTask<String, Void, Integer> {

        private Exception exception;
        private String strJsonResponse;

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Retrieving data...");
        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                URL url = new URL(Network.API_RETRIEVE_COURSE_LIST);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                try {
                    //prepare json data
                    JSONObject jsonUserData = new JSONObject();
                    jsonUserData.put("token", params[0]);

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
                            Toast.makeText(getBaseContext(), connection.getResponseMessage(), Toast.LENGTH_LONG).show();
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
                onLoginFailed(exception.getMessage());
            }
            else {
                try{
                    JSONObject jsonObject = new JSONObject(strJsonResponse);
                    String result = jsonObject.getString("result");
                    if (result.equals("failure")){
                        progressDialog.dismiss();
                        AppVariable.alert(LoginActivity.this, null);
                        return;
                    }

                    int length = Integer.valueOf(jsonObject.getString("total_items"));
                    JSONArray coursesJson = jsonObject.getJSONArray("courses");

                    ArrayList<Course> courses = new ArrayList<>();
                    for (int i = 0; i < length; i++){
                        JSONObject c = coursesJson.getJSONObject(i);

                        courses.add(new Course(
                                c.getInt("id"),
                                c.getString("code"),
                                c.getString("name"),
                                c.getInt("class"),
                                c.getString("class_name"),
                                c.getInt("chcid"),
                                c.getInt("total_stud"),
                                c.getString("schedule"),
                                c.getString("office_hour"),
                                c.getString("note")
                        ));
                    }

                    saveCourseData(courses);

                    progressDialog.dismiss();
                    startMainActivity();

                } catch (JSONException e) {
                    e.printStackTrace();
                    onLoginFailed(e.getMessage());
                }
            }
        }
    }

    private class RetrieveStudyingCourseTask extends AsyncTask<String, Void, Integer> {

        private Exception exception;
        private String strJsonResponse;

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Retrieving data...");
        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                URL url = new URL(Network.API_RETRIEVE_STUDYING_COURSE_LIST);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                try {
                    //prepare json data
                    JSONObject jsonUserData = new JSONObject();
                    jsonUserData.put("token", params[0]);

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
                exception = e;
                return 0;
            }
        }

        @Override
        protected void onPostExecute(Integer status) {
            if (status != HttpURLConnection.HTTP_OK){
                onLoginFailed(exception.getMessage());
            }
            else {
                try{
                    JSONObject jsonObject = new JSONObject(strJsonResponse);
                    String result = jsonObject.getString("result");
                    if (result.equals("failure")){
                        progressDialog.dismiss();
                        AppVariable.alert(LoginActivity.this, null);
                        return;
                    }

                    int length = Integer.valueOf(jsonObject.getString("total_items"));
                    JSONArray coursesJson = jsonObject.getJSONArray("courses");

                    ArrayList<Course> courses = new ArrayList<>();
                    for (int i = 0; i < length; i++){
                        JSONObject c = coursesJson.getJSONObject(i);

                        courses.add(new Course(
                                c.getInt("id"),
                                c.getString("code"),
                                c.getString("name"),
                                c.getInt("class"),
                                c.getString("class_name"),
                                c.getInt("chcid"),
                                c.getInt("total_stud"),
                                c.getString("schedule"),
                                c.getString("office_hour"),
                                c.getString("note")
                        ));
                    }

                    saveCourseData(courses);

                    progressDialog.dismiss();
                    startMainActivity();

                } catch (JSONException e) {
                    e.printStackTrace();
                    onLoginFailed(e.getMessage());
                }
            }
        }
    }
}
