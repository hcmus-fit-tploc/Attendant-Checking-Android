package com.example.hoangdang.diemdanh.SupportClass;

public class AbsenceRequest {
    public int id;
    public String fromDate;
    public String toDate;
    public String reason;
    public int isAccepted;
    public String time;

    public AbsenceRequest(int id, String fromDate, String toDate, String reason, int isAccepted, String time){
        this.id = id;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.reason = reason;
        this.isAccepted = isAccepted;
        this.time = time;
    }
}
