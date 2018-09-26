package com.example.hoangdang.diemdanh.classManagement;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
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

import com.example.hoangdang.diemdanh.R;
import com.example.hoangdang.diemdanh.SupportClass.AppVariable;
import com.example.hoangdang.diemdanh.SupportClass.DatabaseHelper;
import com.example.hoangdang.diemdanh.SupportClass.Network;
import com.example.hoangdang.diemdanh.SupportClass.SecurePreferences;
import com.example.hoangdang.diemdanh.SupportClass.Student;
import com.example.hoangdang.diemdanh.currentSessionImage.CurrentSessionImageActivity;
import com.example.hoangdang.diemdanh.currentSessionImage.ListAbsenStudentCurrentSessionImageAdapter;
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
import java.net.URL;
import java.util.ArrayList;

import io.socket.client.Socket;

public class listviewAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<Student> list = new ArrayList<>();
    private Context context;

    public View view;

    private LayoutInflater inflater;

    private DisplayImageOptions options;

    public listviewAdapter(ArrayList<Student> list, Context context, int class_id, int course_id){
        this.list = list;
        this.context = context;

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
    public View getView(final int position, View convertView, ViewGroup parent) {
        view = convertView;
        if( view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.custom_list_class_management_image, null);
        }

        Student student = list.get(position);

        TextView stud_name = (TextView) view.findViewById(R.id.stud_name_image_textView);
        stud_name.setText(student.strCode);

        TextView stud_id = (TextView) view.findViewById(R.id.stud_id_class_management_textView);
        stud_id.setText(String.valueOf(student.iID));

        TextView code_name = (TextView) view.findViewById(R.id.stud_code_image_textView);
        code_name.setText(student.strName);

        TextView count_aq = (TextView) view.findViewById(R.id.count_aq);
        count_aq.setText(String.valueOf(student.answered_questions));

        TextView count_d = (TextView) view.findViewById(R.id.count_d);
        count_d.setText(String.valueOf(student.discussions));

        TextView count_ap = (TextView) view.findViewById(R.id.count_p);
        count_ap.setText(String.valueOf(student.presentations));

        ImageView imageView = (ImageView) view.findViewById(R.id.image);
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress);

        ImageLoader.getInstance()
                .displayImage(student.avatar, imageView, options, new SimpleImageLoadingListener() {
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

        return view;
    }
}
