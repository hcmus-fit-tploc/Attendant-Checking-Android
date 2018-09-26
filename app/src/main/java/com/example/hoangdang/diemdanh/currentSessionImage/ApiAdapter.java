package com.example.hoangdang.diemdanh.currentSessionImage;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.Base64;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by TUNG HIEP on 2/6/2018.
 */

public class ApiAdapter {
    public String jsonResult;
    private String key = "tunghiep";
    //public String BaseUrl = "https://checkingattendance.000webhostapp.com/";
    public String BaseUrl ;
    public ApiAdapter(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        BaseUrl = sharedPref.getString("baselink","Error");
    }

    public void UpdateLog(Context context, final String log, final String name, final VolleyCallBack callBack)
    {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = BaseUrl + "LogAPI/updateLog.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    callBack.onSuccess(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("log",log);
                params.put("name",name);
                return params;
            }
        };;
        requestQueue.add(stringRequest);
    }
    public void TestImg(final Bitmap myimage,Context context,final VolleyCallBack callBack)
    {
        Log.wtf("HiepGalley","Vo trong API");
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = BaseUrl + "UploadAPI/imgur.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.wtf("HiepAPI",response);
                try {
                    callBack.onSuccess(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("image",base64FromBitmap(myimage));
                return params;
            }
        };;
        requestQueue.add(stringRequest);
    }
    public void GetLink(final String name, Context context, final VolleyCallBack callBack)
    {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = BaseUrl + "UploadAPI/getimageapi.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.wtf("HiepGetImg",response);
                try {
                    callBack.onSuccess(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("name",name);
                return params;
            }
        };;
        requestQueue.add(stringRequest);
    }
    public void UpdateApi(Context context, final String link,final String name,final VolleyCallBack callBack)
    {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = BaseUrl + "UploadAPI/updateimageapi.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    callBack.onSuccess(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("link",link);
                params.put("name",name);
                return params;
            }
        };;
        requestQueue.add(stringRequest);
    }

    public void CheckMyApi(final Bitmap myimage,final String gallery_name,Context context,final VolleyCallBack callBack)
    {

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = BaseUrl + "Kairos/recognize.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.wtf("HiepAPI",response);
                try {
                    callBack.onSuccess(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("key",key);
                params.put("image",base64FromBitmap(myimage));
                params.put("gallery_name",gallery_name);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
    public void GetBaseURL(Context context, final VolleyCallBack callBack)
    {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = "https://checkingattendance.000webhostapp.com/config.php";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.wtf("HiepGetBaseURL",response);
                try {
                    callBack.onSuccess(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                return params;
            }
        };;
        requestQueue.add(stringRequest);
    }
    protected String base64FromBitmap(Bitmap image) {
        //Log.wtf("HiepCompress","Vo day");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String encoded = Base64.encodeToString(byteArray, 0);
        Log.wtf("HiepCompress",encoded);
        return encoded;
    }

}

