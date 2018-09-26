package com.example.hoangdang.diemdanh.SupportClass;

public class Course {
    public int iID;
    public String strCode;
    public String strName;
    public int classID;
    public String classname;
    public String schedule;
    public int chcid;
    public int total_stud;
    public int open;
    public int attendid;
    public String office_hour;
    public String note;

    public Course(int iID, String strCode, String strName,
                  int classID, String classname, int chcid, int total_stud, String schedule, String office_hour, String note){
        this.iID = iID;
        this.strCode = strCode;
        this.strName = strName;
        this.classID = classID;
        this.classname = classname;
        this.chcid = chcid;
        this.total_stud = total_stud;
        this.schedule = schedule;
        this.office_hour = office_hour;
        this.note = note;
    }

    public Course(int iID, String strCode, String strName){
        this.iID = iID;
        this.strCode = strCode;
        this.strName = strName;
    }

    public Course(){}
}
