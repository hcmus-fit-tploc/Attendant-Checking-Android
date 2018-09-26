package com.example.hoangdang.diemdanh.SupportClass;

public class Feedback {
    public int id;
    public String title;
    public String content;
    public int isRead;
    public String time;

    public Feedback(int id, String title, String content, int isRead, String time){
        this.id = id;
        this.title = title;
        this.content = content;
        this.isRead = isRead;
        this.time = time;
    }
}
