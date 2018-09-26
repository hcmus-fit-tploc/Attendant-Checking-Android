package com.example.hoangdang.diemdanh.currentSessionImage;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hoangdang.diemdanh.R;
import com.example.hoangdang.diemdanh.SupportClass.AppVariable;
import com.example.hoangdang.diemdanh.SupportClass.DatabaseHelper;
import com.example.hoangdang.diemdanh.SupportClass.Network;
import com.example.hoangdang.diemdanh.SupportClass.SecurePreferences;
import com.example.hoangdang.diemdanh.SupportClass.Student;
import com.example.hoangdang.diemdanh.SupportClass.User;
import com.kairos.Kairos;
import com.kairos.KairosListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MyFaceDetect extends AppCompatActivity {

    @BindView(R.id.faceverify)
    Button verify;
    @BindView(R.id.facecheckall)
    Button checkall;
    @BindView(R.id.img_back_button)
    ImageView back_button;
    @BindView(R.id.img_next_button)
    ImageView next_button;
    @BindView(R.id.faceverify2)
    Button face_verify_single;
    private KairosListener verifylistener;
    private Kairos kairos;

    @BindView(R.id.imageView)
    ImageView imageview;
    private String galleryID;
    private static final int RC_CAMERA_PERMISSION = 100;
    private static final int RC_CAMERA_VERIFY = 102;
    private static final int GALLERY_REQUEST = 103;
    public static final int GET_GPS = 104;

    public static Socket socket;
    public boolean isOffline;
    public static int courseID;
    public static int attendanceID;
    public static int classHasCourseID;
    public static int user_role;
    public static int classID;
    public SharedPreferences prefs;

    Bitmap image;
    public DatabaseHelper db;
    public ArrayList<Student> studentDBList;

    public ProgressDialog dialog;
    ArrayList<DetectedPerson> mylist;

    public ArrayList<String> imagesEncodedList;
    public int position = 0;
    public String username;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_face_detect);

        ButterKnife.bind(this);
        kairos = new Kairos();
        kairos.setAuthentication(this, getString(R.string.app_id), getString(R.string.api_key));


        galleryID = "14ctt";

        db = new DatabaseHelper(this);
        prefs = new SecurePreferences(this);

        isOffline = prefs.getInt(AppVariable.CURRENT_IS_OFFLINE, 0) == 1;
        courseID = prefs.getInt(AppVariable.CURRENT_COURSE_ID, 0);
        attendanceID = prefs.getInt(AppVariable.CURRENT_ATTENDANCE, 0);
        classHasCourseID = prefs.getInt(AppVariable.CURRENT_CLASS_HAS_COURSE_ID, 0);
        classID = prefs.getInt(AppVariable.CURRENT_CLASS_ID, 0);
        user_role = prefs.getInt(AppVariable.USER_ROLE, 0);

        // TODO: if offline
        if (Network.isOnline(this) && !isOffline){
            setSocket();
        }
        studentDBList = db.getStudenListinClass();
        for(int i=0;i<studentDBList.size();i++){
            studentDBList.get(i).strName = removeAccent(studentDBList.get(i).strName);
            Log.wtf("Hiep",studentDBList.get(i).strName + " " + studentDBList.get(i).iID );
        }

        // Get user name
        db = new DatabaseHelper(this);
        User tempuser = db.getUser();
        Log.wtf("Hiepname",removeAccent(tempuser.getLastName() + " " + tempuser.getFirstName()));
        username = removeAccent(tempuser.getLastName() + " " + tempuser.getFirstName());

        // Hiep - 178


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    RC_CAMERA_PERMISSION);
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    GALLERY_REQUEST);
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    GALLERY_REQUEST);
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    GALLERY_REQUEST);
        }


        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder messagebox = new AlertDialog.Builder(MyFaceDetect.this);
                messagebox.setTitle("Choose your method");
                messagebox.setItems(new String[]{"Take Photo", "Choose from Gallery"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i==0)
                        {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, RC_CAMERA_VERIFY);
                        }
                        else if(i==1)
                        {
//                            Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
//                            photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//                            photoPickerIntent.setType("image/*");
//                            startActivityForResult(photoPickerIntent, GALLERY_REQUEST);

                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent,"Select Picture"), GALLERY_REQUEST);
                        }

                    }
                });
                messagebox.show();
            }
        });

        checkall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckAll(mylist);
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imagesEncodedList == null)
                    return;
                position--;
                if(position<=-1)
                    position = 0;
                image = BitmapFactory.decodeFile(imagesEncodedList.get(position));
                CorrectRotateImage(imagesEncodedList.get(position));
                imageview.setImageBitmap(image);
            }
        });
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imagesEncodedList == null)
                    return;
                position++;
                if(position>(imagesEncodedList.size()-1))
                    position = imagesEncodedList.size() - 1;

                image = BitmapFactory.decodeFile(imagesEncodedList.get(position));
                CorrectRotateImage(imagesEncodedList.get(position));
                imageview.setImageBitmap(image);
            }
        });
        face_verify_single.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new ProgressDialog(MyFaceDetect.this);
                dialog.setMessage("Verifying");
                dialog.show();
                final ApiAdapter apiAdapter = new ApiAdapter(MyFaceDetect.this);
                apiAdapter.CheckMyApi(image,galleryID, MyFaceDetect.this, new VolleyCallBack() {
                    @Override
                    public void onSuccess(String result) {
                        Log.wtf("HiepGallery",result);
                        DetectFaceAndDraw(result,2);
                    }
                });
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RC_CAMERA_PERMISSION:
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    finish();
                }
                break;
            case GALLERY_REQUEST:
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    finish();
                }
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            dialog = new ProgressDialog(this);
            switch (requestCode) {
                case GET_GPS:
                    String coordinate = String.valueOf(data.getDoubleExtra("Lat",12)+ "," + data.getDoubleExtra("Long",12));
                    Toast.makeText(MyFaceDetect.this,coordinate,Toast.LENGTH_LONG).show();
                    Log.wtf("HiepGPS",coordinate);
                    break;
                case RC_CAMERA_VERIFY:
                    dialog.setMessage("Verifying");
                    dialog.show();
                    image = (Bitmap) data.getExtras().get("data");
                    try {
                        final ApiAdapter apiAdapter = new ApiAdapter(MyFaceDetect.this);
                        apiAdapter.CheckMyApi(image,galleryID, this, new VolleyCallBack() {
                            @Override
                            public void onSuccess(String result) {
                                DetectFaceAndDraw(result,1);
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case GALLERY_REQUEST:

//                        Uri selectedPicture = data.getData();
//                        // Get and resize profile image
//                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
//                        Cursor cursor = getContentResolver().query(selectedPicture, filePathColumn, null, null, null);
//                        cursor.moveToFirst();
//
//                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                        String picturePath = cursor.getString(columnIndex);
//                        cursor.close();
//
//                        image = BitmapFactory.decodeFile(picturePath);
//
//                        ExifInterface exif = null;
//                        try {
//                            File pictureFile = new File(picturePath);
//                            exif = new ExifInterface(pictureFile.getAbsolutePath());
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//
//                        int orientation = ExifInterface.ORIENTATION_NORMAL;
//
//                        if (exif != null)
//                            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//
//                        switch (orientation) {
//                            case ExifInterface.ORIENTATION_ROTATE_90:
//                                image = rotateBitmap(image, 90);
//                                break;
//                            case ExifInterface.ORIENTATION_ROTATE_180:
//                                image = rotateBitmap(image, 180);
//                                break;
//
//                            case ExifInterface.ORIENTATION_ROTATE_270:
//                                image = rotateBitmap(image, 270);
//                                break;
//                        }
//                        //imageview.setImageBitmap(image);
//                        //image = Bitmap.createScaledBitmap(image, 120, 200, false);
//                        image = resize(image,400,400);
//                        final ApiAdapter apiAdapter = new ApiAdapter();
//                        apiAdapter.CheckMyApi(image,galleryID, this, new VolleyCallBack() {
//                            @Override
//                            public void onSuccess(String result) {
//                                Log.wtf("HiepGallery",result);
//                                DetectFaceAndDraw(result,2);
//                            }
//                        });






                    dialog.setMessage("Loading Images From Gallery");
                    dialog.show();

                    String[] filePathColumn = { MediaStore.Images.Media.DATA };

                    if(data.getData()!=null){

                        Uri mImageUri=data.getData();

                        // Get the cursor
                        Cursor cursor = getContentResolver().query(mImageUri,
                                filePathColumn, null, null, null);
                        // Move to first row
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String imageEncoded  = cursor.getString(columnIndex);
                        Log.wtf("HiepMultiple",imageEncoded);
                        image = BitmapFactory.decodeFile(imageEncoded);
                        CorrectRotateImage(imageEncoded);
                        imageview.setImageBitmap(image);
                        cursor.close();
                    } else {
                        if (data.getClipData() != null) {
                            imagesEncodedList = new ArrayList<String>();
                            ClipData mClipData = data.getClipData();
                            ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                            for (int i = 0; i < mClipData.getItemCount(); i++) {

                                ClipData.Item item = mClipData.getItemAt(i);
                                Uri uri = item.getUri();
                                mArrayUri.add(uri);
                                // Get the cursor
                                Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                                // Move to first row
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String imageEncoded = cursor.getString(columnIndex);
                                imagesEncodedList.add(imageEncoded);
                                cursor.close();

                            }

                            image = BitmapFactory.decodeFile(imagesEncodedList.get(0));
                            CorrectRotateImage(imagesEncodedList.get(0));
                            imageview.setImageBitmap(image);
                            Log.wtf("HiepMultiple", "Selected Images" + mArrayUri.size());
                        }
                    }
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    break;





            }
        }
    }
    private void CorrectRotateImage(String picturePath)
    {
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
                image = rotateBitmap(image, 90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                image = rotateBitmap(image, 180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                image = rotateBitmap(image, 270);
                break;
        }
        image = resize(image,400,400);
    }
    public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
    class DetectedPerson
    {
        String name;
        int x,y,height;

        public DetectedPerson(String name, int x, int y, int height) {
            this.height = height;
            this.name = name;
            this.x = x;
            this.y = y;
        }
    }

    public void DetectFaceAndDraw(String jsonResult,int mode)
    {
        Log.wtf("Hiep","Hhehe " + jsonResult);
        try {
            mylist = new ArrayList<DetectedPerson>();
            int x = 0, y = 0, height = 0;
            String temp = "";
            String name = "";
            JSONObject response = new JSONObject(jsonResult);
            //Toast.makeText(MyFaceDetect.this,response.toString(),Toast.LENGTH_SHORT).show();
            Log.d("Hiep", response.toString());
            //Log.d("Testing", String.valueOf(response.getJSONArray("images").length()));
            image = image.copy(Bitmap.Config.ARGB_8888, true);
            Canvas c = new Canvas(image);
            Paint myPaint = new Paint();
            if (response.toString().contains("Errors")) {
                Toast.makeText(MyFaceDetect.this, "Error Found !! " + response.getJSONArray("Errors").getJSONObject(0).getString("Message"), Toast.LENGTH_LONG).show();
                Log.d("Hiep", String.valueOf(response.getJSONArray("Errors").getJSONObject(0).getString("Message")));
                imageview.setImageBitmap(image);
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                return;
            }
            for (int i = 0; i < response.getJSONArray("images").length(); i++) {
                if (response.getJSONArray("images").getJSONObject(i).getJSONObject("transaction").getString("status").equals("success")) {
                    JSONArray array = response.getJSONArray("images").getJSONObject(i).getJSONArray("candidates");
                    Log.d("Hiep", array.toString());
                    temp += response.getJSONArray("images").getJSONObject(i).getJSONObject("transaction").getString("subject_id") + " ";
                    x = response.getJSONArray("images").getJSONObject(i).getJSONObject("transaction").getInt("topLeftX");
                    y = response.getJSONArray("images").getJSONObject(i).getJSONObject("transaction").getInt("topLeftY");
                    height = response.getJSONArray("images").getJSONObject(i).getJSONObject("transaction").getInt("height");
                    name = response.getJSONArray("images").getJSONObject(i).getJSONObject("transaction").getString("subject_id");

                    myPaint.setStyle(Paint.Style.FILL);
                    myPaint.setColor(Color.GREEN);
//                    myPaint.setTextSize(8);
                    if(mode==2)
                        myPaint.setTextSize((float) (height*0.1));
                    c.drawText(name, x - height / 8, y, myPaint);
                    Log.wtf("HiepHeight",height+"");


                    myPaint.setStyle(Paint.Style.STROKE);
                    myPaint.setStrokeWidth(1);
                    c.drawRect(x, y, x + height, y + height, myPaint);
                    mylist.add(new DetectedPerson(name, x, y, height));
                }
            }
            if (mylist.size() == 0) {
                Toast.makeText(MyFaceDetect.this, "Found face but not recognize ! Please try again !", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(MyFaceDetect.this, "Face found ! Please click on the face to check attendance !", Toast.LENGTH_SHORT).show();
            image = image.copy(Bitmap.Config.RGB_565, true);
            imageview.setImageBitmap(image);
            imageview.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    //Toast.makeText(MainActivity.this,String.valueOf(motionEvent.getRawX()) + " " + String.valueOf(motionEvent.getRawY()),Toast.LENGTH_SHORT).show();
                    Matrix inverse = new Matrix();
                    imageview.getImageMatrix().invert(inverse);
                    float[] pts = {
                            motionEvent.getX(), motionEvent.getY()
                    };
                    inverse.mapPoints(pts);
                    double xtouch = Math.floor(pts[0]);
                    double ytouch = Math.floor(pts[1]);
                    for (int i = 0; i < mylist.size(); i++) {
                        if (xtouch > mylist.get(i).x && xtouch < mylist.get(i).x + mylist.get(i).height && ytouch > mylist.get(i).y && ytouch < mylist.get(i).y + mylist.get(i).height) {
                            Toast.makeText(MyFaceDetect.this, "Da diem danh " + mylist.get(i).name, Toast.LENGTH_SHORT).show();
                            Diemdanh(mylist.get(i).name);
                        }
                    }
                    return false;
                }
            });

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void Diemdanh(String name)
    {

        // Get GPS
        Intent intent = new Intent(MyFaceDetect.this, GPSTracker.class);
        startActivityForResult(intent, GET_GPS);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MyFaceDetect.this);
        String mylat = sharedPref.getString("Lat","Error");
        String mylong = sharedPref.getString("Long","Error");
        Date currentTime = Calendar.getInstance().getTime();
        String log = "Diem danh: " + name + " - Toa do: (" + mylat + "," + mylong +") " + " - Method: Face Detection " + " - Time: " + currentTime +"$";
        Log.wtf("HiepPref",log);


        //Update Log
        final ApiAdapter apiAdapter = new ApiAdapter(MyFaceDetect.this);
        apiAdapter.UpdateLog(this,log, removeAccent(username), new VolleyCallBack() {
            @Override
            public void onSuccess(String result) {

            }
        });

        int id = 0;
        for(int i=0;i<studentDBList.size();i++)
        {
            if(name.equals(studentDBList.get(i).strName))
                id = studentDBList.get(i).iID;
        }
        Log.wtf("Hiep","Name la " + name + "Id la " + id);
        int attendanceID = prefs.getInt(AppVariable.CURRENT_ATTENDANCE, 0);
        db.changeAttendanceStatus(id, attendanceID, AppVariable.ATTENDANCE_STATUS);
        if(Network.isOnline(MyFaceDetect.this) && !isOffline){
            SharedPreferences pref = new SecurePreferences(MyFaceDetect.this);
            new SyncTask().execute(pref.getString(AppVariable.USER_TOKEN, null), String.valueOf(attendanceID), String.valueOf(id));
        }
    }
    private void CheckAll(final ArrayList<DetectedPerson> mylist)
    {
        if(mylist == null) {
            Toast.makeText(MyFaceDetect.this, "Please verify first !", Toast.LENGTH_SHORT).show();
            return;
        }
        String temp = "Here is the list of student: ";
        for (int i = 0; i < mylist.size(); i++)
        {
           temp += "\n" + mylist.get(i).name;
        }
        android.app.AlertDialog.Builder messagebox = new android.app.AlertDialog.Builder(MyFaceDetect.this);
        messagebox.setMessage(temp);
        messagebox.setPositiveButton("No" , new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        messagebox.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int i = 0; i < mylist.size(); i++)
                {
                    Toast.makeText(MyFaceDetect.this, "Da diem danh " + mylist.get(i).name, Toast.LENGTH_SHORT).show();
                    Diemdanh(mylist.get(i).name);
                }
            }
        });
        messagebox.show();

    }

    public class SyncTask extends AsyncTask<String, Void, Integer> {

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
                    jsonUserData.put("attendance_type", 1);

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
            JSONObject payload = new JSONObject();
            try {
                payload.put("course_id", courseID);
                payload.put("class_id", classID);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            socket.emit("checkAttendanceUpdated", payload);
        }
    }
    private void setSocket() {
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
                        prefs.edit().putInt(AppVariable.CURRENT_ATTENDANCE, 0).apply();
                        socket.disconnect();
                        showClosedDialog(obj.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
            }

        });
        socket.connect();
    }
    private void showClosedDialog(String mes) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.DialogeTheme)
                .setTitle("Attendance stopped")
                .setMessage(mes)
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });

        dialog.setCancelable(false);

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });


        new Thread() {
            public void run() {
                MyFaceDetect.this.runOnUiThread(new Runnable(){

                    @Override
                    public void run(){
                        dialog.show();
                    }
                });
            }
        }.start();
    }
    public static String removeAccent(String s) {
        s = s.replace("Đ","D");
        s = s.replace("đ","d");
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("");
    }
    private static Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > 1) {
                finalWidth = (int) ((float)maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float)maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }
}
