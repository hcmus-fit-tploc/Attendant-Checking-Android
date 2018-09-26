package com.example.hoangdang.diemdanh.currentSessionImage;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.hoangdang.diemdanh.R;
import com.example.hoangdang.diemdanh.SupportClass.DatabaseHelper;
import com.example.hoangdang.diemdanh.SupportClass.User;
import com.google.zxing.common.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UploadPhotoActivity extends AppCompatActivity {
    @BindView (R.id.myuploadlistview)
    ListView UploadListView;
    @BindView (R.id.UploadButton)
    Button UploadButton;
    UploadPhotoAdapter uploadPhotoAdapter;
    ArrayList<String> imagelinks;

    List<String> test ;
    public DatabaseHelper db;

    private static final int RC_CAMERA_PERMISSION = 100;
    private static final int RC_CAMERA_VERIFY = 102;
    private static final int GALLERY_REQUEST = 103;

    public String original_links;
    public String myname;

    public int numberofpictures;

    public ProgressDialog dialog;
    public Bitmap image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo);
        ButterKnife.bind(this);
        //UploadListView = (ListView) findViewById(R.id.myuploadlistview);
        dialog = new ProgressDialog(this);

        // Get user name
        db = new DatabaseHelper(this);
        User tempuser = db.getUser();
        Log.wtf("Hiepname",removeAccent(tempuser.getLastName() + " " + tempuser.getFirstName()));
        myname = removeAccent(tempuser.getLastName() + " " + tempuser.getFirstName());

        dialog.setMessage("Loading pictures");
        dialog.show();

        ApiAdapter apiAdapter = new ApiAdapter(UploadPhotoActivity.this);
        apiAdapter.GetLink(myname, this, new VolleyCallBack() {
            @Override
            public void onSuccess(String result) throws JSONException {
                JSONArray response = new JSONArray(result);
                Log.wtf("HiepJson",response.getJSONObject(0).getString("link"));
                original_links = response.getJSONObject(0).getString("link");
                //imagelinks = new ArrayList<String>(Arrays.asList(original_links.split("$"))) ;

                test = new ArrayList<String>(Arrays.asList(original_links.split("\\$")));
                for(int i=0;i<test.size();i++)
                    Log.wtf("HiepString2",test.get(i));

                //String str = "https://i.imgur.com/66EJu46.png$https://i.imgur.com/SDF9MR0.png$";
//                List<String> elephantList = Arrays.asList(str.split(","));
//                for(int i=0;i<elephantList.size();i++)
//                       Log.wtf("HiepString",elephantList.get(i));
                //test.add(response.getJSONObject(0).getString("link"));
                numberofpictures = CountPictures(original_links);
                if(numberofpictures>0) {
                    uploadPhotoAdapter = new UploadPhotoAdapter(UploadPhotoActivity.this, R.layout.lineforuploadimage, test);
                    UploadListView.setAdapter(uploadPhotoAdapter);
                }
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

        UploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(numberofpictures >= 5)
                    Toast.makeText(UploadPhotoActivity.this,"You can only upload 5 images ! Please delete some pictures !", Toast.LENGTH_LONG).show();
                else {
                    android.support.v7.app.AlertDialog.Builder messagebox = new android.support.v7.app.AlertDialog.Builder(UploadPhotoActivity.this);
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
                                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                                photoPickerIntent.setType("image/*");
                                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
                            }

                        }
                    });
                    messagebox.show();
                }
            }
        });

        UploadListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                AlertDialog.Builder messagebox = new AlertDialog.Builder(UploadPhotoActivity.this);
                messagebox.setMessage("Are you sure you want to delete this picture ?");
                messagebox.setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                messagebox.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RemoveItem(i);
                        //((MainActivity)context).ParseJSOn("http://192.168.56.1/android/getdata.php");
                    }
                });
                messagebox.show();
            }
        });

        // Request for your camera
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    RC_CAMERA_PERMISSION);
        }
        // Request to read image
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    GALLERY_REQUEST);
        }
    }
    public void RemoveItem(int position)
    {
        dialog.setMessage("Removing");
        dialog.show();
        test.remove(position);
        String temp = "";
        for(int i=0;i<test.size();i++)
            temp += test.get(i) + "$";
        Log.wtf("HiepRemove",temp);
        original_links = temp;
        final ApiAdapter apiAdapter = new ApiAdapter(UploadPhotoActivity.this);
        apiAdapter.UpdateApi(this, original_links, myname, new VolleyCallBack() {
            @Override
            public void onSuccess(String result) throws JSONException {
                numberofpictures --;
                uploadPhotoAdapter.notifyDataSetChanged();
                if(dialog.isShowing())
                    dialog.dismiss();
            }
        });
    }
    public int CountPictures(String str)
    {
        // return number of $ in the string so we know how many pictures
        return str.length() - str.replace("$", "").length();
    }
    public void UpdatedListView()
    {
        ApiAdapter apiAdapter = new ApiAdapter(UploadPhotoActivity.this);
        Log.wtf("HiepTest","co vo day khong");
        apiAdapter.GetLink(myname, this, new VolleyCallBack() {
            @Override
            public void onSuccess(String result) throws JSONException {
                JSONArray response = new JSONArray(result);
                Log.wtf("HiepTest","co vo day khong 2");
                original_links = response.getJSONObject(0).getString("link");
                test = new ArrayList<String>(Arrays.asList(original_links.split("\\$")));
                for(int i=0;i<test.size();i++)
                    Log.wtf("HiepString3",test.get(i));
                numberofpictures = CountPictures(original_links);
                if(numberofpictures>0)
                {
                    uploadPhotoAdapter = new UploadPhotoAdapter(UploadPhotoActivity.this, R.layout.lineforuploadimage, test);
                    UploadListView.setAdapter(uploadPhotoAdapter);
                }
                if(dialog.isShowing())
                    dialog.dismiss();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RC_CAMERA_VERIFY:
                    image = (Bitmap) data.getExtras().get("data");
                    try {
                        dialog.setMessage("Uploading");
                        dialog.show();
                        final ApiAdapter apiAdapter = new ApiAdapter(this);
                        apiAdapter.TestImg(image, this, new VolleyCallBack() {
                            @Override
                            public void onSuccess(String result) {
                                Log.wtf("HiepNewImg",result);
                                original_links += result + "$";
                                apiAdapter.UpdateApi(UploadPhotoActivity.this, original_links, myname, new VolleyCallBack() {
                                    @Override
                                    public void onSuccess(String result) throws JSONException {
                                        UpdatedListView();
                                    }
                                });

                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case GALLERY_REQUEST:
                    Uri selectedPicture = data.getData();
                    // Get and resize profile image
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedPicture, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();

                    image = BitmapFactory.decodeFile(picturePath);

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
                    image = resize(image,250,250);
                    try {
                        dialog.setMessage("Uploading");
                        dialog.show();
                        final ApiAdapter apiAdapter = new ApiAdapter(this);
                        apiAdapter.TestImg(image, this, new VolleyCallBack() {
                            @Override
                            public void onSuccess(String result) {
                                Log.wtf("HiepNewImg",result);
                                original_links += result + "$";
                                apiAdapter.UpdateApi(UploadPhotoActivity.this, original_links, myname, new VolleyCallBack() {
                                    @Override
                                    public void onSuccess(String result) throws JSONException {
                                        UpdatedListView();
                                    }
                                });

                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RC_CAMERA_PERMISSION:
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    finish();
                }
                break;
        }
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
    public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
