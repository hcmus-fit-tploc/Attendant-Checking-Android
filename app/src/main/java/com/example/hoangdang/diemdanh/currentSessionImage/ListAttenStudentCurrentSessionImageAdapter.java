package com.example.hoangdang.diemdanh.currentSessionImage;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.hoangdang.diemdanh.CurrentSession.CurrentSessionActivity;
import com.example.hoangdang.diemdanh.R;
import com.example.hoangdang.diemdanh.SupportClass.AppVariable;
import com.example.hoangdang.diemdanh.SupportClass.DatabaseHelper;
import com.example.hoangdang.diemdanh.SupportClass.Network;
import com.example.hoangdang.diemdanh.SupportClass.SecurePreferences;
import com.example.hoangdang.diemdanh.SupportClass.Student;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

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

public class ListAttenStudentCurrentSessionImageAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<Student> list = new ArrayList<>();
    private Context context;

    public View view;
    public Button _changeButton;
    Socket socket;
    private JSONObject payload;
    public boolean isOffline;
	
	private LayoutInflater inflater;

	private DisplayImageOptions options;

    public ListAttenStudentCurrentSessionImageAdapter(ArrayList<Student> list, Context context, int class_id, int course_id, Socket socket){
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
    
		inflater = LayoutInflater.from(context);

		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.avatar)
				.showImageForEmptyUri(R.drawable.avatar)
				.showImageOnFail(R.drawable.avatar)
				.cacheOnDisk(true)
				.considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.build();
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
            view = inflater.inflate(R.layout.custom_list_student_image, null);
        }

        TextView stud_name = (TextView) view.findViewById(R.id.stud_name_image_textView);
		stud_name.setText(list.get(position).strCode);

        TextView code_name = (TextView) view.findViewById(R.id.stud_code_image_textView);
        code_name.setText(list.get(position).strName);
		
        ImageView imageView = (ImageView) view.findViewById(R.id.image);
		final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress);

        ImageView imageType = (ImageView) view.findViewById(R.id.type);

        int type = list.get(position).status;

        switch (type) {
            case 2:
                imageType.setImageResource(R.drawable.ic_qrcode_blue);
                break;
            case 3:
                imageType.setImageResource(R.drawable.ic_help_outline_blue_24dp);
                break;
            default:
                break;
        }

        imageType.setVisibility(View.VISIBLE);

		ImageLoader.getInstance()
					.displayImage(list.get(position).avatar, imageView, options, new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							progressBar.setProgress(0);
							progressBar.setVisibility(View.VISIBLE);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
							progressBar.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
							progressBar.setVisibility(View.GONE);
						}
					}, new ImageLoadingProgressListener() {
						@Override
						public void onProgressUpdate(String imageUri, View view, int current, int total) {
							progressBar.setProgress(Math.round(100.0f * current / total));
						}
					});
        final DatabaseHelper db = new DatabaseHelper(context);

        FrameLayout image_ll = (FrameLayout)view.findViewById(R.id.image_ll);
        image_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int attendance_id = ((CurrentSessionImageActivity)context).getAttendanceID();
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
