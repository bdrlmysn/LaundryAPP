package com.example.laundryapp.data.model;

import java.io.Serializable;

public class Customer implements Serializable {
    public String name;
    public String phone;
    public String lastOrder;
    public String initials;

    public Customer(String name, String phone, String lastOrder, String initials) {
        this.name = name;
        this.phone = phone;
        this.lastOrder = lastOrder;
        this.initials = initials;
    }
}
