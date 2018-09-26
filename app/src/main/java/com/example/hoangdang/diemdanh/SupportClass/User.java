package com.example.hoangdang.diemdanh.SupportClass;

public class User {
    private String strEmail;
    private String strFirstName;
    private Integer iID;
    private Integer iRole;
    private String strLastName;
    private String strToken;
    private String strPhone;
    public String avatar;

    public String getToken() {
        return strToken;
    }

    public void setToken(String strToken) {
        this.strToken = strToken;
    }

    public String getEmail() {
        return strEmail;
    }

    public void setEmail(String strEmail) {
        this.strEmail = strEmail;
    }

    public String getFirstName() {
        return strFirstName;
    }

    public void setFirstName(String strFirstName) {
        this.strFirstName = strFirstName;
    }

    public void setLastName(String strLastName) {
        this.strLastName = strLastName;
    }

    public String getLastName(){
        return this.strLastName;
    }

    public int getID() {
        return this.iID;
    }

    public void setID(Integer iID){
        this.iID = iID;
    }

    public int getRole(){
        return this.iRole;
    }

    public void setRole(Integer iRole){
        this.iRole = iRole;
    }

    public void setPhone(String strPhone){
        this.strPhone = strPhone;
    }

    public String getPhone(){
        return this.strPhone;
    }

    public User(String strEmail, String strToken) {
        this.strEmail = strEmail;
        this.strToken = strToken;
    }

    public User(Integer iID, Integer iRole, String strEmail, String strToken, String strLastName, String strFirstName, String strPhone, String avatar){
        this.iID = iID;
        this.iRole = iRole;
        this.strEmail = strEmail;
        this.strFirstName = strFirstName;
        this.strLastName = strLastName;
        this.strToken = strToken;
        this.strPhone = strPhone;
        this.avatar = avatar;
    }

    public User(){}
}
