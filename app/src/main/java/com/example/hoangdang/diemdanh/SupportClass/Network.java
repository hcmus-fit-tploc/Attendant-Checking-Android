package com.example.hoangdang.diemdanh.SupportClass;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Network {

    /**
     * List of API
     */

    //public static final String HOST = "http://172.20.10.2:3000/"; //Heroku
    public static final String HOST = "http://hcmus-attendance.herokuapp.com/"; //Heroku

    //public static final String HOST = "http://192.168.0.105:3000/"; //May o nha


    //public static final String HOST = "http://192.168.21.22:3000/"; //May minh tret
    //public static final String HOST = "http://192.168.1.105:3000/"; //Vannguyen
    //public static final String HOST = "http://172.16.4.218:3000/"; //May Nghia
    //public static final String HOST = "http://192.168.22.22:3000/"; //May minh lau 1
    //public static final String HOST = "http://192.168.1.106:3000/"; //Hai Thanh
    //public static final String HOST = "http://192.168.1.162:3000/"; //ct
    //public static final String HOST = "http://172.16.7.57:3000/"; //APCS
    //public static final String HOST = "http://192.168.11.169:3000/"; //Gatsby

    public static final String API_LOGIN = HOST + "authenticate/login";
    public static final String API_LOGOUT = HOST + "authenticate/logout";

    public static final String API_RETRIEVE_STUDENT = HOST + "api/attendance/check-attendance";

    public static final String API_RETRIEVE_COURSE_LIST = HOST + "api/course/teaching";
    public static final String API_RETRIEVE_STUDYING_COURSE_LIST = HOST + "api/course/studying";
    public static final String API_RETRIEVE_OPENING_COURSE = HOST + "api/attendance/opening-by-teacher";
    public static final String API_RETRIEVE_STUDYING_OPENING_COURSE = HOST + "api/attendance/opening-for-student";

    public static final String API_SEND_STUDENT_ATTENDANCE_DATA = HOST + "api/attendance/update-attendance";

    public static final String API_CREATE_ATTEND = HOST + "api/attendance/create";
    public static final String API_CANCEL_ATTEND = HOST + "api/attendance/delete";
    public static final String API_FINISH_ATTEND = HOST + "api/attendance/close";

    public static final String API_ATTENDANCE_QR_CODE = HOST + "api/check-attendance/qr-code/";
    public static final String API_ATTENDANCE_FACER = HOST + "api/check-attendance/face";
    public static final String API_ATTENDANCE_CHECKLIST = HOST + "api/check-attendance/check-list";

    public static final String API_SEND_FEEDBACK = HOST + "api/feedback/send";
    public static final String API_GET_SENT_FEEDBACK_LIST = HOST + "api/feedback/history";

    public static final String API_SEND_ABSENCE_REQUEST = HOST + "api/absence-request/create";
    public static final String API_GET_SENT_ABSENCE_REQUEST_LIST = HOST + "api/absence-request/by-student";

    public static final String API_CHECK_DELEGATE_CODE = HOST + "api/attendance/check-delegate-code";
    public static final String API_GET_DELEGATE_CODE = HOST + "api/attendance/generate-delegate-code";

    public static final String API_CHECK_QUIZ_CODE = HOST + "api/quiz/join";
    public static final String API_GET_QUIZ = HOST + "api/quiz/published";
    public static final String API_SUBMIT_QUIZ = HOST + "api/quiz/submit";

    public static final String API_TEACHER_UPDATE_PROFILE = HOST + "api/teacher/update";
    public static final String API_STUDENT_UPDATE_PROFILE = HOST + "api/student/update";

    public static final String API_USER_CHANGE_PASSWORD = HOST + "api/user/change-password";
    public static final String API_USER_RESET_PASSWORD = HOST + "authenticate/forgot-password";
    public static final String API_SYNC = HOST + "api/attendance/update-attendance-offline";

    public static final String API_UPDATE_INTERACTION = HOST + "api/student/update-interaction";

    public static final String API_STUDENT_STATS = HOST + "api/attendance/list-by-student";

    /**
     * A function to check internet connection.
     * @param c classActivity.this
     * @return boolean
     */
    public static boolean isOnline(Context c) {
        try {
            ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
