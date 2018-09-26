package com.example.hoangdang.diemdanh.timeTable;

import java.util.ArrayList;

public class LessonInfo {
    public String code;
    public String name;
    public String classname;
    public String office_hour;
    public String note;
    public String content;
    public int underline = 0;

    public LessonInfo(){};
    public LessonInfo(String code){
        this.code = code;
    }
}