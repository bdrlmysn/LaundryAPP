package com.example.laundryapp.data.db.model;

public class User {
    public long id;
    public String username;
    public String role;

    public User(long id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }
}
