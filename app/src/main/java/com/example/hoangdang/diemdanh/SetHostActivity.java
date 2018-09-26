package com.example.hoangdang.diemdanh;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hoangdang.diemdanh.currentSessionImage.ApiAdapter;
import com.example.hoangdang.diemdanh.currentSessionImage.MyFaceDetect;
import com.example.hoangdang.diemdanh.currentSessionImage.VolleyCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SetHostActivity extends AppCompatActivity {
    @BindView(R.id.textview_host)
    TextView host_textview;
    @BindView(R.id.editText_inputhost)
    EditText editText_inputhost;
    @BindView(R.id.button_changehostbyhand)
    Button button_changehostbyhand;
    @BindView(R.id.button_changehostonline)
    Button button_changehostonline;

    public ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_host);
        ButterKnife.bind(this);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(SetHostActivity.this);
        String host = sharedPref.getString("baselink","Error");
        host_textview.setText(host);
        button_changehostbyhand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editText_inputhost.getText().length() == 0)
                    Toast.makeText(SetHostActivity.this,"Please input something",Toast.LENGTH_SHORT).show();
                else {
                    host_textview.setText(editText_inputhost.getText());
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(SetHostActivity.this);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("baselink", editText_inputhost.getText().toString());
                    editor.commit();
                }
            }
        });
        button_changehostonline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new ProgressDialog(SetHostActivity.this);
                dialog.setMessage("Getting Host online");
                dialog.show();
                final ApiAdapter apiAdapter = new ApiAdapter(SetHostActivity.this);
                apiAdapter.GetBaseURL(SetHostActivity.this, new VolleyCallBack() {
                    @Override
                    public void onSuccess(String result) throws JSONException {
                        Log.wtf("HiepTestURL",result);
                        JSONObject response = new JSONObject(result);
                        String link = response.getString("baselink");
                        Log.wtf("HiepBaseLink",link);
                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(SetHostActivity.this);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("baselink",link);
                        host_textview.setText(link);
                        editor.commit();
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });
            }
        });



    }
}
