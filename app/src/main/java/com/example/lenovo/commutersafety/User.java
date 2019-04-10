package com.example.lenovo.commutersafety;

public class User {String userID , userName , userEmail , userPassword , userNumber , userCategory;

    public User() {
    }

    public User(String userID, String userName, String userEmail, String userPassword, String userNumber , String userCategory) {
        this.userID = userID;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.userNumber = userNumber;
        this.userCategory = userCategory;
    }

    public String getUserID() {
        return userID;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public String getUserNumber() {
        return userNumber;
    }

    public String getUserCategory() {
        return userCategory;
    }
}
