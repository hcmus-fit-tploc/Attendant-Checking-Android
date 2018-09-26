package com.example.hoangdang.diemdanh.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hoangdang.diemdanh.R;
import com.example.hoangdang.diemdanh.SupportClass.AppVariable;
import com.example.hoangdang.diemdanh.SupportClass.Network;
import com.example.hoangdang.diemdanh.SupportClass.SecurePreferences;
import com.example.hoangdang.diemdanh.feedback.FeedbackHistoryActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SendFeedbackFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    @BindView(R.id.send_button)
    Button _send_Feedback;

    @BindView(R.id.title_editText)
    EditText _title;

    @BindView(R.id.content_editText)
    EditText _content;

    @BindView(R.id.isAnonymous_checkbox)
    CheckBox _isAnonymous;

    @BindView(R.id.des_feedback)
    TextView _desFeedback;

    private int user_role;
    private boolean isAnonymous;
    private ProgressDialog progressDialog;

    public SendFeedbackFragment() {}

    public static SendFeedbackFragment newInstance(String param1, String param2) {
        SendFeedbackFragment fragment = new SendFeedbackFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("SEND FEEDBACK");

        setHasOptionsMenu(true);

        // prepare spinner
        progressDialog = new ProgressDialog(getActivity(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Sending...");

        SharedPreferences pref = new SecurePreferences(getActivity());
        user_role = pref.getInt(AppVariable.USER_ROLE, 0);
    }

    private boolean validate() {
        boolean valid = true;

        String title = _title.getText().toString();
        String content = _content.getText().toString();

        if (title.isEmpty()) {
            _title.setError("missing title");
            valid = false;
        } else {
            _title.setError(null);
        }

        if (content.isEmpty()) {
            _content.setError("missing content");
            valid = false;
        } else {
            _content.setError(null);
        }

        return valid;

    }

    public void sendFeedback(){
        if (validate()){
            SharedPreferences pref = new SecurePreferences(getActivity());

            _title.clearFocus();
            _content.clearFocus();

            new SendFeedbackTask().execute(
                    pref.getString(AppVariable.USER_TOKEN, null),
                    _title.getText().toString(),
                    _content.getText().toString()
            );
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_send_feedback, container, false);

        ButterKnife.bind(this, view);


        if (user_role == AppVariable.STUDENT_ROLE){
            _isAnonymous.setVisibility(View.VISIBLE);
            _desFeedback.setVisibility(View.VISIBLE);
            _isAnonymous.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    isAnonymous = isChecked;
                }
            });
        }

        _send_Feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFeedback();
            }
        });

        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private class SendFeedbackTask extends AsyncTask<String, Void, Integer> {

        private Exception exception;
        private String strJsonResponse;

        @Override
        protected void onPreExecute() {
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);
        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                URL url = new URL(Network.API_SEND_FEEDBACK);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                try {
                    JSONObject jsonUserData = new JSONObject();
                    jsonUserData.put("token", params[0]);
                    jsonUserData.put("title", params[1]);
                    jsonUserData.put("content", params[2]);

                    if (user_role == AppVariable.STUDENT_ROLE){
                        jsonUserData.put("isAnonymous", isAnonymous);
                    }

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
                displayToast("Server error");
            }
            else {
                try{
                    JSONObject jsonObject = new JSONObject(strJsonResponse);
                    String result = jsonObject.getString("result");
                    if (result.equals("failure")){
                        Log.d("feedback", strJsonResponse);
                        progressDialog.dismiss();
                        displayToast("Server Error");
                        return;
                    }

                    progressDialog.dismiss();
                    onSentFeedback();
                    displayToast("Sent");

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    displayToast("Server error");
                }
            }
        }
    }

    public void onSentFeedback(){
        _title.setText("");
        _content.setText("");

        if(user_role == AppVariable.STUDENT_ROLE){
            _isAnonymous.setChecked(false);
        }
    }

    private void displayToast(String toast) {
        if(getActivity() != null) {
            Toast.makeText(getActivity(), toast, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.feedback_drawer, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
           case R.id.sent_history:
               startActivity(new Intent(getActivity(), FeedbackHistoryActivity.class));
               return true;
           default:
               return super.onOptionsItemSelected(item);
        }
    }
}
