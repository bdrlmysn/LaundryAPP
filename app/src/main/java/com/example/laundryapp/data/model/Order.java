package com.example.laundryapp.data.model;

public class Order {

    private String name;
    private String detail;
    private String price;
    private String type;

    public Order(String name, String detail, String price, String type) {
        this.name = name;
        this.detail = detail;
        this.price = price;
        this.type = type;
    }

    public String getName() { return name; }
    public String getDetail() { return detail; }
    public String getPrice() { return price; }
    public String getType() { return type; }
}