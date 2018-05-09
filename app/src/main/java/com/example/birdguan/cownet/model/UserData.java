package com.example.birdguan.cownet.model;

/**
 * Created by Gwg on 2016/8/31.
 */
public class UserData {
    private String mUserNo;
    private String mUserName;
    private String mPassword;
    private String mRoleName;
    public String mOwnerName;
    public String mOwnerPhone;

    public UserData(String userNo, String userName, String password, String roleName) {
        this.mUserNo = userNo;
        this.mUserName = userName;
        this.mPassword = password;
        this.mRoleName = roleName;
    }

    public void setUserNo(String userNo) {
        this.mUserNo = userNo;
    }

    public String getUserNo() {
        return mUserNo;
    }

    public void setUserName(String userName) {
        this.mUserName = userName;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setPassword(String password) {
        this.mPassword = password;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setRoleName(String roleName) {
        this.mRoleName = roleName;
    }

    public String getRoleName() {
        return mRoleName;
    }

}