package com.example.hoangdang.diemdanh.SupportClass;

public class Student {
    public int iID;
    public String strCode;
    public int iClass;
    public String strLastName;
    public String strFirstName;
    public String strName;
    public int status;
    public String avatar;
    public String answered_questions;
    public String discussions;
    public String presentations;

    public Student(int iID, String strCode, String strName, int status, String avatar, String answered_questions, String discussions, String presentations){
        this.iID = iID;
        this.strCode = strCode;
        this.strName = strName;
        this.status = status;
        this.avatar = avatar;
        this.answered_questions = answered_questions;
        this.discussions = discussions;
        this.presentations = presentations;
    }

    public Student(int iID, String strName) {
        this.iID = iID;
        this.strName = strName;
    }

    public Student(){}
}
