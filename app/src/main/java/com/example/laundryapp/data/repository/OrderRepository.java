package com.example.laundryapp.data.repository;

import com.example.laundryapp.data.model.HistoryOrder;

import java.util.ArrayList;
import java.util.List;

public class OrderRepository {

    public static List<HistoryOrder> historyList = new ArrayList<>();

    public static void add(HistoryOrder order) {
        historyList.add(0, order); // paling atas
    }

    public static List<HistoryOrder> getAll() {
        return historyList;
    }
}
