package com.example.laundryapp.data.db.model;

import java.io.Serializable;

public class OrderEntity implements Serializable {
    public long id;
    public String orderCode;

    public long customerId;
    public String customerName;
    public String customerPhone;
    public String customerAddress;

    public long serviceId;
    public String speed;
    public String type;
    public int pricePerKg;

    public double weight;
    public String parfum;
    public String note;

    public int subtotal;
    public int tax;
    public int total;

    public String paymentStatus; // PAID/UNPAID
    public String status;        // DITERIMA..DIAMBIL

    public long createdAt;
    public long updatedAt;

    public OrderEntity() {}
}
