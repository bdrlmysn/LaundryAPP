package com.example.laundryapp.data.model;

import java.io.Serializable;

public class HistoryOrder implements Serializable {

    public String orderId;
    public String customer;
    public String service;
    public String detail;
    public String price;
    public String status;
    public String paymentStatus;
    public String time;
    public String note;

    public HistoryOrder(
            String orderId,
            String customer,
            String service,
            String detail,
            String price,
            String status,
            String paymentStatus,
            String time,
            String note
    ) {
        this.orderId = orderId;
        this.customer = customer;
        this.service = service;
        this.detail = detail;
        this.price = price;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.time = time;
        this.note = note;
    }
}
