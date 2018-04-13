package com.example.android.firechatapplication;

public class Message {
    private String username;
    private String message;
    private String imageUrl;

    Message(){
    }

    Message (String username, String message, String imageUrl) {
        this.username = username;
        this.message = message;
        this.imageUrl = imageUrl;

    }
    public void setUsername(String username) {
        this.username = username;
    }


    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }


    public String getImageUrl() {
        return imageUrl;
    }
}
