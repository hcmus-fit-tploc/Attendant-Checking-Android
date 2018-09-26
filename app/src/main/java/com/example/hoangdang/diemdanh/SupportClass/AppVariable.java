package com.example.hoangdang.diemdanh.SupportClass;

import android.content.Context;
import android.support.v7.app.AlertDialog;

public class AppVariable {
    //UI
    //Functions
    //Network task
    //Socket
    public static final String USER_EMAIL = "user_email";
    public static final String USER_AUTHENTICATION = "user_authentication";
    public static final String USER_NAME = "user_fullname";
    public static final String USER_TOKEN = "user_token";
    public static final String USER_ID = "user_id";
    public static final String USER_ROLE = "user_role";
    public static final String USER_AVATAR = "user_avatar";

    public static final String CURRENT_ATTENDANCE = "current_attendance";
    public static final String CURRENT_COURSE_ID = "current_course_id";
    public static final String CURRENT_COURSE_NAME = "current_course_name";
    public static final String CURRENT_CLASS_ID = "current_class_id";
    public static final String CURRENT_IS_OFFLINE = "is_offline";

    public static final String CURRENT_QUIZ_ID = "current_quiz_id";

    public static final String CURRENT_CLASS_HAS_COURSE_ID = "current_class_has_course_id";
    public static final String LAST_LOAD_COURSE_LIST = "last_load_course_list";

    public static final String LAST_LOAD_COURSE = "last_load_course_";
    public static final int ABSENCE_STATUS = 0;
    public static final int ATTENDANCE_STATUS = 1;

    public static final int UNPROCESSED_STATUS = 5;
    public static final int STAFF_ROLE = 3;
    public static final int TEACHER_ROLE = 2;

    public static final int STUDENT_ROLE = 1;
    public static final String QR_FLAG = "1";
    public static final String FR_FLAG = "2";

    public static final int ANSWER_QUESTION_FLAG = 0;
    public static final int DISCUSSION_FLAG = 1;
    public static final int PRESENTATION_FLAG = 2;

    public static final String QUIZ_CODE = "quiz_code";
    public static final String QUIZ_MESSAGE = "quiz_message";
    public static final String QUIZ_INDEX = "quiz_index";
    public static final String QUIZ_TOTAL = "quiz_total";
    public static final String QUIZ_TITLE = "quiz_title";
    public static final String QUIZ_BLANK = "quiz_blank";
    public static final String QUIZ_TYPE = "quiz_type";
    public static final String QUIZ_CORRECT = "quiz_correct";

    public static void alert(Context context, String message){
        message = message == null ? "Server error! Please try again later" : message;
        new AlertDialog.Builder(context)
                .setTitle("ALERT")
                .setMessage(message)
                .setNegativeButton("CLOSE", null).show();
    }
}
