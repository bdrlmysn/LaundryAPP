package com.example.laundryapp.data.db.model;

import java.io.Serializable;

public class CustomerEntity implements Serializable {
    public long id;
    public String name;
    public String phone;
    public String address;

    public CustomerEntity(long id, String name, String phone, String address) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.address = address;
    }
}
