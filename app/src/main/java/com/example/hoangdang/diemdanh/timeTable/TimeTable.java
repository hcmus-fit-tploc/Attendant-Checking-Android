package com.example.hoangdang.diemdanh.timeTable;

import java.util.ArrayList;

public class TimeTable {
    public int[] index = {2,2,2,2,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

    public ArrayList<LessonInfo>[] lessonInfos = new ArrayList[]{
            new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()};

    public String getK(int pos){
        switch (pos){
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
                return "7:30 - 9:00";
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
                return "9:30 - 11:30";
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
                return "13:30 - 15:00";
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
                return "15:30 - 17:30";
        }
        return "EMPTY";
    }

    public TimeTable(){
        this.lessonInfos[0].add(new LessonInfo("MON"));
        this.lessonInfos[1].add(new LessonInfo("TUE"));
        this.lessonInfos[2].add(new LessonInfo("WED"));
        this.lessonInfos[3].add(new LessonInfo("THU"));
        this.lessonInfos[4].add(new LessonInfo("FRI"));
        this.lessonInfos[5].add(new LessonInfo("SAT"));
    }
}
