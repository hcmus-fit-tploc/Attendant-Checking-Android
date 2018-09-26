package com.example.hoangdang.diemdanh.Fragments;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hoangdang.diemdanh.R;
import com.example.hoangdang.diemdanh.SupportClass.AppVariable;
import com.example.hoangdang.diemdanh.SupportClass.DatabaseHelper;
import com.example.hoangdang.diemdanh.SupportClass.Network;
import com.example.hoangdang.diemdanh.SupportClass.SecurePreferences;
import com.example.hoangdang.diemdanh.SupportClass.User;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static com.example.hoangdang.diemdanh.currentSessionImage.UploadPhotoActivity.rotateBitmap;

public class ProfileFragment extends Fragment {
    @BindView(R.id.profile_img)
    ImageView _profile_img;

    @BindView(R.id.first_name_tv)
    TextView _first_name;

    @BindView(R.id.last_name_tv)
    TextView _last_name;

    @BindView(R.id.stud_id_tv)
    TextView _stud_id;

    @BindView(R.id.email_tv)
    TextView _email;

    @BindView(R.id.phone_tv)
    TextView _phone;

    @BindView(R.id.btn_changePassword)
    Button _changePassword;

    @BindView(R.id.stud_id_ll)
    LinearLayout stud_id_ll;

    private static final String CLIENT_ID = "Client-ID 56f531f985863ea";
    private static final String IMGUR_URL = "https://api.imgur.com/3/upload";

    String email;
    String phone;
    String name;
    String token;
    String user_id;

    String realemail;

    DatabaseHelper db;
    ProgressDialog progressDialog;
    SharedPreferences pref;
    Uri mImageCaptureUri;

    Bitmap squareAvatar;

    private OnFragmentInteractionListener mListener;

    public ProfileFragment() {}

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("PROFILE");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        ButterKnife.bind(this, view);

        progressDialog = new ProgressDialog(getActivity(),
                R.style.AppTheme_Dark_Dialog);

        db = new DatabaseHelper(getActivity());
        User user = db.getUser();
        pref = new SecurePreferences(getActivity());
        ImageLoader.getInstance().displayImage(pref.getString(AppVariable.USER_AVATAR, null), _profile_img);
        _first_name.setText(user.getFirstName());
        _last_name.setText(user.getLastName());
        if(user.getRole() == AppVariable.STUDENT_ROLE){
            stud_id_ll.setVisibility(View.VISIBLE);
            _stud_id.setText(user.getEmail().substring(0,7));
        }
        _email.setText(user.getEmail());
        realemail = user.getEmail();
        _phone.setText(user.getPhone());

        token = pref.getString(AppVariable.USER_TOKEN, null);
        user_id = pref.getString(AppVariable.USER_ID, null);
        name = pref.getString(AppVariable.USER_NAME, null);
        email = pref.getString(AppVariable.USER_EMAIL, null);
        phone = user.getPhone();

        setButtonListener();

        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 2:
                if(resultCode == RESULT_OK) {
                    final Bundle extras = imageReturnedIntent.getExtras();

                    if (extras != null) {
                        Bitmap photo = extras.getParcelable("data");
                        _profile_img.setImageBitmap(getRoundedShape(photo));
                    }

                    //Delete the temporary file
                    File f = new File(mImageCaptureUri.getPath());
                    if (f.exists()) {
                        f.delete();
                    }

                    InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.showSoftInput(_profile_img, InputMethodManager.SHOW_IMPLICIT);

                    new UploadToImgurTask().execute();
                }
                break;

            case 0:
                if(resultCode == RESULT_OK){
                    Intent cropApps = new Intent("com.android.camera.action.CROP");
                    cropApps.setType("image/*");

                    List<ResolveInfo> list = getActivity().getPackageManager().queryIntentActivities(cropApps, 0);
                    int size = list.size();

                    if (size == 0)
                    {
                        Toast.makeText(getActivity(), "Can not find image crop app", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    else
                    {
                        ResolveInfo res = list.get(0);

                        Intent intent = new Intent();
                        intent.setClassName(res.activityInfo.packageName, res.activityInfo.name);

                        intent.setData(mImageCaptureUri);
                        intent.putExtra("outputX", 500);
                        intent.putExtra("outputY", 500);
                        intent.putExtra("aspectX", 1);
                        intent.putExtra("aspectY", 1);
                        intent.putExtra("scale", true);
                        intent.putExtra("crop", "true");
                        intent.putExtra("return-data", true);

                        startActivityForResult(intent, 2);
                    }
                }

                break;
            case 1:
                if(resultCode == RESULT_OK){
                    Bundle extras2 = imageReturnedIntent.getExtras();

//                    Uri selectedImage = imageReturnedIntent.getData();
//                    Bitmap bitmap = null;
//                    try {
//                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }

                    Uri selectedPicture = imageReturnedIntent.getData();
                    // Get and resize profile image
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(selectedPicture, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();

                    Bitmap bitmap = BitmapFactory.decodeFile(picturePath);

                    ExifInterface exif = null;
                    try {
                        File pictureFile = new File(picturePath);
                        exif = new ExifInterface(pictureFile.getAbsolutePath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    int orientation = ExifInterface.ORIENTATION_NORMAL;

                    if (exif != null)
                        orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                    switch (orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            bitmap = rotateBitmap(bitmap, 90);
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            bitmap = rotateBitmap(bitmap, 180);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_270:
                            bitmap = rotateBitmap(bitmap, 270);
                            break;
                    }

                    _profile_img.setImageBitmap(getRoundedShape(bitmap));


                    Log.wtf("HiepAvatar","Vo day 1");
//                    if (extras2 != null) {
//                        Log.wtf("HiepAvatar","Vo day 2");
//                        Bitmap photo = extras2.getParcelable("data");
//                        _profile_img.setImageBitmap(getRoundedShape(photo));
//                    }

                    new UploadToImgurTask().execute();
                }
                break;
        }
    }

    //UI

    private void setButtonListener(){
        _changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangePasswordDialog();
            }
        });

        _profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChoosePictureSourceDialog();
            }
        });
    }

    private void showChangePasswordDialog() {
        LayoutInflater li = LayoutInflater.from(getContext());
        View promptsView = li.inflate(R.layout.change_password, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getContext());

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText currentPwd = (EditText) promptsView
                .findViewById(R.id.current_password_editText);

        final EditText newPwd = (EditText) promptsView
                .findViewById(R.id.new_password_editText);

        final CheckBox showPwd = (CheckBox) promptsView
                .findViewById(R.id.showPassword_chxbox);

        showPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    currentPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    newPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else {
                    currentPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    newPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                currentPwd.setSelection(currentPwd.getText().length());
                newPwd.setSelection(newPwd.getText().length());
            }
        });

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Change",
                        new DialogInterface.OnClickListener() {
                            SharedPreferences pref = new SecurePreferences(getActivity());

                            public void onClick(DialogInterface dialog,int id) {
                                new UpdatePasswordTask().execute(
                                        pref.getString(AppVariable.USER_TOKEN, null),
                                        pref.getString(AppVariable.USER_ID, null),
                                        currentPwd.getText().toString(),
                                        newPwd.getText().toString());
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

    private void showChoosePictureSourceDialog() {
        LayoutInflater li = LayoutInflater.from(getContext());
        View promptsView = li.inflate(R.layout.choose_picture_source, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getContext());

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final TextView openGallery = (TextView) promptsView
                .findViewById(R.id.open_gallery);

        final TextView openCamera = (TextView) promptsView
                .findViewById(R.id.open_camera);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

        openGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                openGallery();
            }
        });

        openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                openCamera();
            }
        });
    }

    private void openCamera(){
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                "tmp_contact_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));

        takePicture.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);

        try {
            takePicture.putExtra("return-data", true);
            startActivityForResult(takePicture, 0);
        } catch (ActivityNotFoundException e) {
            //Do nothing for now
        }
    }

    private void openGallery(){
        Log.wtf("HiepAvatar","Vo ham open");
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        pickPhoto.putExtra("outputX", 500);
        pickPhoto.putExtra("outputY", 500);
        pickPhoto.putExtra("aspectX", 1);
        pickPhoto.putExtra("aspectY", 1);
        pickPhoto.putExtra("scale", true);
        pickPhoto.putExtra("crop", "true");

        try {
            pickPhoto.putExtra("return-data", true);
            startActivityForResult(Intent.createChooser(pickPhoto,"Complete action using"), 1);
        } catch (ActivityNotFoundException e) {
            //Do nothing for now
        }
    }

    //Functions

    public void onChangePasswordFail(String message){
        new AlertDialog.Builder(getActivity())
                .setTitle("Alert")
                .setMessage("Fail to change password: " + message)
                .setNegativeButton("close", null).show();
    }

    private void displayToast(String toast) {
        if(getActivity() != null && toast != null) {
            Toast.makeText(getActivity(), toast, Toast.LENGTH_LONG).show();
        }
    }

    public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
        squareAvatar = scaleBitmapImage;
        int targetWidth = 500;
        int targetHeight = 500;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight,Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth),
                        ((float) targetHeight)) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        canvas.drawBitmap(scaleBitmapImage,
                new Rect(0, 0, scaleBitmapImage.getWidth(),
                        scaleBitmapImage.getHeight()),
                new Rect(0, 0, targetWidth, targetHeight), null);
        return targetBitmap;
    }

    //Network task

    private class UpdatePasswordTask extends AsyncTask<String, Void, Integer> {

        private Exception exception;
        private String strJsonResponse;

        @Override
        protected void onPreExecute() {
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Changing...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                URL url = new URL(Network.API_USER_CHANGE_PASSWORD);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                try {
                    JSONObject jsonUserData = new JSONObject();
                    jsonUserData.put("token", params[0]);
                    jsonUserData.put("confirm_password", params[3]);
                    jsonUserData.put("current_password", params[2]);
                    jsonUserData.put("new_password", params[3]);

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
                onChangePasswordFail(exception.getMessage());
            }
            else {
                try{
                    JSONObject jsonObject = new JSONObject(strJsonResponse);
                    String result = jsonObject.getString("result");
                    if (result.equals("failure")){
                        progressDialog.dismiss();
                        onChangePasswordFail("Wrong old password");
                        return;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    onChangePasswordFail(e.getMessage());
                }
            }
            progressDialog.dismiss();
        }
    }

    private class UploadToImgurTask extends AsyncTask<String, Void, Integer> {
        String strJsonResponse;
        Exception exception;
        Bitmap bitmap;

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Uploading");
            progressDialog.show();

            bitmap = squareAvatar;
        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                URL url = new URL(IMGUR_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                try {

                    ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArray);
                    byte[] byteImage = byteArray.toByteArray();
                    String dataImage = Base64.encodeToString(byteImage,Base64.DEFAULT);
                    String data = URLEncoder.encode("image", "UTF-8") + "="
                            + URLEncoder.encode(dataImage, "UTF-8");

                    connection.setReadTimeout(10000);
                    connection.setConnectTimeout(15000);
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type",
                            "application/x-www-form-urlencoded");
                    connection.setRequestProperty("Accept", "application/json");
                    connection.setRequestProperty("Authorization", CLIENT_ID);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);

                    //write
                    OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                    writer.write(data);
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
                displayToast(exception.getMessage());
                progressDialog.dismiss();
            }
            else {
                try{
                    JSONObject jsonObject = new JSONObject(strJsonResponse);
                    JSONObject result = jsonObject.getJSONObject("data");
                    String link = result.getString("link");

                    //displayToast(link);
                    Log.d("img", link);

                    //displayToast("Updated successful");
                    pref.edit().putString(AppVariable.USER_AVATAR, link).apply();

                    new UpdateProfileTask().execute(
                            token,
                            user_id,
                            name,
                            email,
                            phone,
                            link
                    );

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    displayToast(e.getMessage());
                }
            }
        }
    }

    private class UpdateProfileTask extends AsyncTask<String, Void, Integer> {
        private String strJsonResponse;

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Syncing...");
        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                URL url;

                if (pref.getInt(AppVariable.USER_ROLE, 0) == AppVariable.STUDENT_ROLE){
                    url = new URL(Network.API_STUDENT_UPDATE_PROFILE);
                }
                else {
                    url = new URL(Network.API_TEACHER_UPDATE_PROFILE);
                }

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                try {
                    JSONObject jsonUserData = new JSONObject();
                    jsonUserData.put("token", params[0]);
                    jsonUserData.put("id", params[1]);
                    jsonUserData.put("name", params[2]);
                    Log.w("HiepUpdateAva",realemail);
                    jsonUserData.put("email", realemail);
                    jsonUserData.put("phone", params[4]);
                    jsonUserData.put("avatar", params[5]);

                    connection.setReadTimeout(10000);
                    connection.setConnectTimeout(15000);
                    connection.setRequestMethod("PUT");
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
                displayToast("Server error");
            }
            else {
                try{
                    JSONObject jsonObject = new JSONObject(strJsonResponse);
                    String result = jsonObject.getString("result");
                    Log.wtf("HiepUpdateAva",result);
                    if (result.equals("failure")){
                        progressDialog.dismiss();
                        displayToast(jsonObject.getString("message"));
                        return;
                    }

                    displayToast("Updated successful");

                } catch (JSONException e) {
                    Log.wtf("HiepUpdateAva","Vo exception");
                    e.printStackTrace();
                    displayToast(e.getMessage());
                }
            }
            progressDialog.dismiss();
        }
    }

    //Socket

}
