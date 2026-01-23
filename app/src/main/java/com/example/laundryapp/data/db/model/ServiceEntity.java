package com.example.laundryapp.data.db.model;

import java.io.Serializable;

public class ServiceEntity implements Serializable {
    public long id;
    public String speed; // REGULER/KILAT/INSTANT
    public String type;  // CUCI_SETRIKA/CUCI_SAJA/SETRIKA_SAJA
    public int pricePerKg;
    public boolean active;

    public ServiceEntity(long id, String speed, String type, int pricePerKg, boolean active) {
        this.id = id;
        this.speed = speed;
        this.type = type;
        this.pricePerKg = pricePerKg;
        this.active = active;
    }
}
