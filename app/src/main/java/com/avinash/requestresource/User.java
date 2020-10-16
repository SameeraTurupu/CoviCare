package com.avinash.requestresource;

import com.avinash.requestresource.Requests;

import java.util.ArrayList;

public class User {
    private String fullName;
    private String emailID;
    private String createdAt;
    private String userID;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmailID() {
        return emailID;
    }

    public void setEmailID(String emailID) {
        this.emailID = emailID;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public ArrayList<Requests> getCurrentUserRequests(String userID){
        return new ArrayList<Requests>();
    }
}
